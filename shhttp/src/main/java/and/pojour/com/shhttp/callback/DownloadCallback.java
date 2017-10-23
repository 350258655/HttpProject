package and.pojour.com.shhttp.callback;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import and.pojour.com.shhttp.SHHttpUtils;
import and.pojour.com.shhttp.response.IResponseDownloadListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by "Shake" on 2017/10/17.
 */
public class DownloadCallback implements Callback {

    private IResponseDownloadListener mDownloadHandler;
    private String mFilePath;
    private long mCompleteBytes;

    public DownloadCallback(IResponseDownloadListener handler, String filePath, long completeBytes) {
        this.mDownloadHandler = handler;
        this.mFilePath = filePath;
        this.mCompleteBytes = completeBytes;
    }


    @Override
    public void onFailure(Call call, final IOException e) {
        if (SHHttpUtils.isDebug) {
            Log.e(SHHttpUtils.sDebugTag, "download faild : " + e.getMessage());
            e.printStackTrace();
        }
        SHHttpUtils.sHandler.post(new Runnable() {
            @Override
            public void run() {
                mDownloadHandler.onFailed(-1, e.getMessage());
            }
        });
    }

    @Override
    public void onResponse(Call call, final Response response) {

        if (response.isSuccessful()) {

            // 回调开始
            SHHttpUtils.isCancel =false;
            SHHttpUtils.sHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mDownloadHandler != null) {
                        mDownloadHandler.onStart(response.body().contentLength());
                    }
                }
            });

            // 这个判断是为了看是否是断点下载的
            if (response.header("Content-Range") == null || response.header("Content-Range").length() == 0) {
                mCompleteBytes = 0;
            }
            try {
                // 保存文件
                saveFile(response);

                // 回调成功
                final File file = new File(mFilePath);
                SHHttpUtils.sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mDownloadHandler != null) {
                            mDownloadHandler.onFinish(file);
                        }
                    }
                });
            } catch (IOException e) {
               if(call.isCanceled()){
                   // 主动取消
                   SHHttpUtils.sHandler.post(new Runnable() {
                       @Override
                       public void run() {
                            if(mDownloadHandler != null){
                                mDownloadHandler.onCancel();
                            }
                       }
                   });
               }else {
                   if(SHHttpUtils.isDebug){
                       Log.i(SHHttpUtils.sDebugTag, "save file failed : " + e.getMessage());
                        SHHttpUtils.sHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(mDownloadHandler!=null){
                                    mDownloadHandler.onCancel();
                                }
                            }
                        });
                   }
               }
            }


        }else {
            SHHttpUtils.sHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mDownloadHandler != null){
                        mDownloadHandler.onFailed(response.code(),"failed meg : " + response.message());
                    }
                }
            });
        }

    }


    /**
     * 保存文件
     *
     * @param response
     */
    private void saveFile(Response response) throws IOException {
        InputStream is = null;
        // 每次读取3kb
        byte[] buf = new byte[3 * 1024];
        int len;
        RandomAccessFile file = null;

        try {
            is = response.body().byteStream();
            file = new RandomAccessFile(mFilePath, "rwd");
            if (mCompleteBytes > 0) {
                file.seek(mCompleteBytes);
            }

           // long completeLen = mCompleteBytes;
            final long totalLen = response.body().contentLength();
            Log.i(SHHttpUtils.sDebugTag, "totalLen: " + totalLen);

            int currentProgress = (int)(mCompleteBytes*100/totalLen);
            int preProgress =0;
            while ((len = is.read(buf)) != -1) {
                file.write(buf, 0, len);
                mCompleteBytes += len;

                // 假如当前已经下载的字节数大于总字节数，就退出
                if(mCompleteBytes>totalLen){
                    break;
                }

                // 已经读取的文件字节数
                final long finalCompleteLen = mCompleteBytes;
                // 计算进度
                currentProgress = (int) (finalCompleteLen*100/totalLen);

                // 只有进度加1的时候，才回调数据
                if(preProgress!=currentProgress){
                    final int finalProgress = currentProgress;
                    SHHttpUtils.sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mDownloadHandler != null) {
                                mDownloadHandler.onProgress(finalProgress,finalCompleteLen, totalLen);
                            }
                        }
                    });
                }

                // 用于记录上一个进度
                preProgress = currentProgress;

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
            if (file != null) {
                file.close();
            }
        }
    }

}
