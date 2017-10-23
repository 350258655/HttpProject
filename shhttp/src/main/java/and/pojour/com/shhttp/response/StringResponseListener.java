package and.pojour.com.shhttp.response;

import java.io.IOException;

import and.pojour.com.shhttp.SHHttpUtils;
import and.pojour.com.shhttp.builder.IResponseListener;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by "Shake" on 2017/10/13.
 * 定义成抽象方法，即有些方法不用去做具体实现
 */
public abstract class StringResponseListener implements IResponseListener {
    @Override
    public void onSuccess(final Response response) {
        ResponseBody responseBody = response.body();
        String resBodyStr = "";
        try {
            resBodyStr = responseBody.string();
        } catch (final IOException e) {
            e.printStackTrace();
            SHHttpUtils.sHandler.post(new Runnable() {
                @Override
                public void run() {
                    onFailed(response.code(), e.toString());
                }
            });
        } finally {
            response.close();
        }

        // 把成功的响应结果提交出去
        final String finalResBodyStr = resBodyStr;
        SHHttpUtils.sHandler.post(new Runnable() {
            @Override
            public void run() {
                onSuccess(response.code(), finalResBodyStr);
            }
        });


    }

    /**
     * 抽象的回调方法
     *
     * @param statusCode
     * @param response
     */
    public abstract void onSuccess(int statusCode, String response);

    /**
     * 不需要子类再去实现了
     *
     * @param progress
     * @param total
     */
    @Override
    public void onProgress(int progress,long readBytes, long total) {

    }
}
