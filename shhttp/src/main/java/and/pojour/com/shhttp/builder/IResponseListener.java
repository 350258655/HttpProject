package and.pojour.com.shhttp.builder;

import okhttp3.Response;

/**
 * Created by "Shake" on 2017/10/11.
 * Response响应回调接口
 */
public interface IResponseListener {

    void onSuccess(Response response);

    void onFailed(int statusCode,String errMsg);

    void onProgress(int progress,long readBytes,long total);
}
