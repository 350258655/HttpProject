package and.pojour.com.shhttp.body;

import java.io.IOException;

import and.pojour.com.shhttp.SHHttpUtils;
import and.pojour.com.shhttp.builder.IResponseListener;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by "Shake" on 2017/10/17.
 * 重写RequestBody
 */
public class RequestProgressBody extends RequestBody {

    private IResponseListener mResponseHandler;
    private RequestBody mRequestBody;
    private BufferedSink mBufferedSink;

    public RequestProgressBody(RequestBody requestBody, IResponseListener responseHandler) {
        this.mRequestBody = requestBody;
        this.mResponseHandler = responseHandler;
    }


    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink bufferedSink) throws IOException {
        if (mBufferedSink == null) {
            mBufferedSink = Okio.buffer(sink(bufferedSink));
        }
        // 写入
        mRequestBody.writeTo(mBufferedSink);
        //必须调用flush，否则最后一部分数据可能不会被写入
        mBufferedSink.flush();
    }

    /**
     * 这是为了上传文件用的？
     *
     * @param sink
     * @return
     */
    private Sink sink(BufferedSink sink) {

        return new ForwardingSink(sink) {
            //当前写入字节数
            long bytesWritten = 0l;
            //总字节长度
            long contentLength = 0l;
            // 进度
            int progress=0;
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                progress = (int) (bytesWritten*100/contentLength);
                SHHttpUtils.sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mResponseHandler.onProgress(progress,bytesWritten, contentLength);
                    }
                });

            }
        };
    }


}
