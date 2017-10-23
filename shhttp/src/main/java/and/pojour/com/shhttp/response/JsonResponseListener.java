package and.pojour.com.shhttp.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

import and.pojour.com.shhttp.SHHttpUtils;
import and.pojour.com.shhttp.builder.IResponseListener;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by "Shake" on 2017/10/16.
 */
public abstract class JsonResponseListener implements IResponseListener {

    @Override
    public void onSuccess(final Response response) {
        ResponseBody responseBody = response.body();
        String responseBodyStr = "";
        try {
            responseBodyStr = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            SHHttpUtils.sHandler.post(new Runnable() {
                @Override
                public void run() {
                    onFailed(response.code(),"read response body faild");
                }
            });
            return;
        }finally {
            responseBody.close();
        }


        final String finalResponseBodyStr = responseBodyStr;
        try {
            // JSONTokener.nextValue()会给出一个对象，然后可以动态的转换为适当的类型。
            final Object result = new JSONTokener(finalResponseBodyStr).nextValue();
            if(result instanceof JSONObject){
                SHHttpUtils.sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onSuccess(response.code(), (JSONObject) result,null);
                    }
                });
            }else if(result instanceof JSONArray){
                SHHttpUtils.sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onSuccess(response.code(),null, (JSONArray) result);
                    }
                });
            }else {
                SHHttpUtils.sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onFailed(response.code(),"faild parse jsonObject,body = " + finalResponseBodyStr);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
            SHHttpUtils.sHandler.post(new Runnable() {
                @Override
                public void run() {
                    onFailed(response.code(),"faild parse jsonObject,body = " + finalResponseBodyStr);
                }
            });
        }
    }

    // 子类必须重写
    public abstract void onSuccess(int statusCode, JSONObject jsonObjectResponse,JSONArray jsonArrayResponse);


    @Override
    public void onProgress(int progress, long readBytes, long total) {

    }
}
