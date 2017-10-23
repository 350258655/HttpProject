package and.pojour.com.shhttp.builder;

import android.text.TextUtils;

import and.pojour.com.shhttp.SHHttpUtils;
import and.pojour.com.shhttp.callback.SHCallback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by "Shake" on 2017/10/16.
 */
public class PostBuilder extends BaseRequestBuilder<PostBuilder> {
    public PostBuilder(SHHttpUtils shHttpUtils) {
        super(shHttpUtils);
    }

    @Override
    public void enqueue(IResponseListener responseHandler) {
        // 检查URL是否为空
        if (TextUtils.isEmpty(mUrl)) {
            throw new IllegalArgumentException("url can not be null");
        }
        // 创建ReuqestBuilder
        Request.Builder builder = new Request.Builder().url(mUrl);
        // 添加请求头
        appendHeadersToBuilder(builder);
        // 添加标记
        if (mTag != null) {
            builder.tag(mTag);
        }
        if (mParams != null) {
            // 创建一个装载参数的容器
            FormBody.Builder paramsContainer = new FormBody.Builder();
            // 将参数装进去容器
            appendParams(paramsContainer);
            // 将容器设置到 RequestBuilder上
            builder.post(paramsContainer.build());
        }else {
            //default is string request body ... 假如没有post参数的时候
            RequestBody body = RequestBody.create(MediaType.parse("text/plain; charset=utf-8"),"");
            builder.post(body);
        }

        // 创建请求对象
        Request request = builder.build();
        // 发起请求
        mSHHttpUtils.getOkHttpClient()
                    .newCall(request)
                    .enqueue(new SHCallback(responseHandler));
    }

    /**
     * 添加post的参数
     *
     * @param paramsContainer
     */
    private void appendParams(FormBody.Builder paramsContainer) {
        if (mParams != null && !mParams.isEmpty()) {
            for (String key : mParams.keySet()) {
                paramsContainer.add(key, mParams.get(key));
            }
        }
    }
}
