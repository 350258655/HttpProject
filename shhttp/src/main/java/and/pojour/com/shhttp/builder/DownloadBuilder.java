package and.pojour.com.shhttp.builder;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import and.pojour.com.shhttp.SHHttpUtils;
import and.pojour.com.shhttp.body.ResponseProgressBody;
import and.pojour.com.shhttp.callback.DownloadCallback;
import and.pojour.com.shhttp.response.IResponseDownloadListener;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by "Shake" on 2017/10/17.
 */
public class DownloadBuilder extends BaseRequestBuilder<DownloadBuilder> {

    private String mFilePath; //文件路径(如果设置该字段，以上两个就无需设置)
    private long mCompleteBytes = 0l; // 已经下载完成的字节数，用于断点续传

    public DownloadBuilder(SHHttpUtils shHttpUtils) {
        super(shHttpUtils);
    }

    /**
     * 设置已经下载的字节数，用于断点下载的
     *
     * @param completeBytes
     * @return
     */
    public DownloadBuilder completeBytes(long completeBytes) {
        if (completeBytes > 0l) {
            this.mCompleteBytes = completeBytes;
            addHeader("RANGE", completeBytes+"");
            Log.i(SHHttpUtils.sDebugTag, "completeBytes : " + completeBytes);
        }
        return this;
    }


    /**
     * 设置文件路径
     *
     * @param filePath
     * @return
     */
    public DownloadBuilder filePath(String filePath) {
        this.mFilePath = filePath;
        return this;
    }


    @Override
    public void enqueue(final IResponseListener responseHandler) {
        try {
            if (TextUtils.isEmpty(mUrl)) {
                throw new IllegalArgumentException("url can not be null");
            }
            if (TextUtils.isEmpty(mFilePath)) {
                throw new IllegalArgumentException("filepath can not be null");
            }
            // 检查文件路径
            checkFilePath();
            Request.Builder builder = new Request.Builder().url(mUrl);
            appendHeadersToBuilder(builder);
            if (mTag != null) {
                builder.tag(mTag);
            }
            Request request = builder.build();

            // 这里要加一个拦截器。可以参考这里 http://blog.csdn.net/KevinsCSDN/article/details/51934274
            Call call = SHHttpUtils.getInstance()
                                    .getOkHttpClient()
                                    .newBuilder()
                                    .addNetworkInterceptor(new Interceptor() {
                                        @Override
                                        public Response intercept(Chain chain) throws IOException {
                                            Response response = chain.proceed(chain.request());
                                            return response.newBuilder().body(new ResponseProgressBody(response.body())).build();
                                        }
                                    })
                                    .build()
                                    .newCall(request);
            // 发起异步请求
            call.enqueue(new DownloadCallback((IResponseDownloadListener) responseHandler,mFilePath,mCompleteBytes));


        } catch (final Exception e) {
            e.printStackTrace();
            SHHttpUtils.sHandler.post(new Runnable() {
                @Override
                public void run() {
                    responseHandler.onFailed(-1, e.getMessage());
                }
            });
        }


    }

    /**
     * 检查FilePath是否有效
     */
    private void checkFilePath() throws Exception {
        File file = new File(mFilePath);
        if (file.exists()) {
            Log.i(SHHttpUtils.sDebugTag, "file is exist ");
            return;
        }
        if (mCompleteBytes > 0l) {
            throw new Exception("断点续传文件 ： " + mFilePath + " 不存在");
        }
        if (mFilePath.endsWith(File.separator)) {
            throw new Exception("创建文件 ： " + mFilePath + " 失败。目标文件不能为空目录");
        }
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new Exception("创建目标文件所在目录失败!");
            }
        }

    }
}
