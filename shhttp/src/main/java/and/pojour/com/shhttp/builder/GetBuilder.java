package and.pojour.com.shhttp.builder;

import android.text.TextUtils;

import java.util.Map;

import and.pojour.com.shhttp.SHHttpUtils;
import and.pojour.com.shhttp.callback.SHCallback;
import okhttp3.Request;

/**
 * Created by "Shake" on 2017/10/10.
 */
public class GetBuilder extends BaseRequestBuilder<GetBuilder> {
    public GetBuilder(SHHttpUtils shHttpUtils) {
        super(shHttpUtils);
    }

    @Override
    public void enqueue(IResponseListener responseHandler) {

        // 检查URL是否为空
        if (TextUtils.isEmpty(mUrl)) {
            throw new IllegalArgumentException("url can not be null");
        }
        // 拼接请求参数
        if (mParams != null && mParams.size() > 0) {
            mUrl = appendParams(mUrl, mParams);
        }

        // 创建ReuqestBuilder
        Request.Builder builder = new Request.Builder().url(mUrl).get();
        // 添加请求头
        appendHeadersToBuilder(builder);

        // 添加标记
        if(mTag != null){
            builder.tag(mTag);
        }
        // 创建请求对象
        Request request  = builder.build();

        // 发起请求
        mSHHttpUtils.getOkHttpClient()
                    .newCall(request)
                    .enqueue(new SHCallback(responseHandler));
    }

    /**
     * 拼接请求参数
     *
     * @param url
     * @param params
     * @return
     */
    private String appendParams(String url, Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        sb.append(url + "?");
        // 遍历参数
        for (String key : params.keySet()) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        // 删除最后一位 "&"
        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
