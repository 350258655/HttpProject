package and.pojour.com.shhttp.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import and.pojour.com.shhttp.SHHttpUtils;
import okhttp3.Headers;
import okhttp3.Request;

/**
 * Created by "Shake" on 2017/10/10.
 */
public abstract class BaseRequestBuilder<T extends BaseRequestBuilder> {

    protected String mUrl;
    protected Object mTag;
    protected Map<String, String> mHeaders;
    protected Map<String, String> mParams;
    protected SHHttpUtils mSHHttpUtils;

    public BaseRequestBuilder(SHHttpUtils shHttpUtils) {
        this.mSHHttpUtils = shHttpUtils;
    }

    /**
     * 设置URL
     *
     * @param url
     * @return
     */
    public T url(String url) {
        this.mUrl = url;
        return (T) this;
    }


    /**
     * 设置TAG
     *
     * @param tag
     * @return
     */
    public T tag(Object tag) {
        this.mTag = tag;
        return (T) this;
    }

    /**
     * 添加请求头
     *
     * @param headers
     * @return
     */
    public T headers(Map<String, String> headers) {
        this.mHeaders = headers;
        return (T) this;
    }


    /**
     * 添加单个请求头
     *
     * @param key
     * @param value
     * @return
     */
    public T addHeader(String key, String value) {
        if (this.mHeaders == null) {
            this.mHeaders = new LinkedHashMap<>();
        }
        mHeaders.put(key, value);
        return (T) this;
    }


    /**
     * 将请求头添加到RequestBuilder中
     *
     * @param builder
     */
    protected void appendHeadersToBuilder(Request.Builder builder) {
        if (mHeaders == null || mHeaders.isEmpty()) {
            return;
        }
        // 创建okhttp的headers容器的Builder
        Headers.Builder headersBuilder = new Headers.Builder();
        for (String key : mHeaders.keySet()) {
            headersBuilder.add(key, mHeaders.get(key));
        }

        // 将Headers添加到Builder
        builder.headers(headersBuilder.build());
    }


    /**
     * 设置请求参数
     *
     * @param params
     * @return
     */
    public T params(Map<String, String> params) {
        this.mParams = params;
        return (T) this;
    }

    /**
     * 添加单个参数
     *
     * @param key
     * @param value
     * @return
     */
    public T addParam(String key, String value) {
        if (this.mParams == null) {
            this.mParams = new LinkedHashMap<>();
        }
        this.mParams.put(key, value);
        return (T) this;
    }


    /**
     * 异步请求
     *
     * @param responseHandler
     */
    public abstract void enqueue(IResponseListener responseHandler);


}
