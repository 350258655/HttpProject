package and.pojour.com.shhttp.callback;

import android.util.Log;

import java.io.IOException;

import and.pojour.com.shhttp.SHHttpUtils;
import and.pojour.com.shhttp.builder.IResponseListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by "Shake" on 2017/10/11.
 */
public class SHCallback implements Callback {

    // 回调接口实例
    private IResponseListener mResponseHandler;

    public SHCallback(IResponseListener responseHandler){
        this.mResponseHandler = responseHandler;
    }



    @Override
    public void onFailure(Call call, final IOException e) {
        if(SHHttpUtils.isDebug){
            Log.e(SHHttpUtils.sDebugTag, "request fail : " + e.getMessage());
            e.printStackTrace();
        }

        // 回调失败的结果
        SHHttpUtils.sHandler.post(new Runnable() {
            @Override
            public void run() {
                mResponseHandler.onFailed(-1,e.toString());
            }
        });


    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        if(SHHttpUtils.isDebug){
            Log.i(SHHttpUtils.sDebugTag, "response state : " + response.code() + ", msg : " + response.message() + ", response body : " + response.body());
        }
        // 回调成功
        if(response.isSuccessful()){
            mResponseHandler.onSuccess(response);
        }else {
            // 回调失败
            SHHttpUtils.sHandler.post(new Runnable() {
                @Override
                public void run() {
                    mResponseHandler.onFailed(response.code(),"response fail msg : " + response.message());
                }
            });
        }
    }
}
