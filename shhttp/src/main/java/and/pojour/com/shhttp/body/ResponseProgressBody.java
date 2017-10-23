package and.pojour.com.shhttp.body;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

/**
 * Created by "Shake" on 2017/10/17.
 * 重写ResponseBody。这部分应该是为了能够获取进度吧！！！
 */
public class ResponseProgressBody extends ResponseBody {

    private ResponseBody mResponseBody;
    private BufferedSource mBufferedSource;

    public ResponseProgressBody(ResponseBody responseBody){
        this.mResponseBody = responseBody;
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if(mBufferedSource == null){
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }

        return mBufferedSource;
    }

    private ForwardingSource source(BufferedSource source) {

        return new ForwardingSource(source) {

            long totalBytesRead;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                //这个进度是读取response每次内容的进度，在写文件之前，所以读取进度以写完文件的进度为准
                long bytesRead = super.read(sink,byteCount);
                totalBytesRead += (bytesRead!=-1)?bytesRead:0;
                return totalBytesRead;
            }
        };
    }
}
