package and.pojour.com.shhttp;

import android.os.Handler;
import android.os.Looper;

import and.pojour.com.shhttp.builder.DownloadBuilder;
import and.pojour.com.shhttp.builder.GetBuilder;
import and.pojour.com.shhttp.builder.PostBuilder;
import and.pojour.com.shhttp.builder.UploadBuilder;
import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

/**
 * Created by "Shake" on 2017/10/10.
 */
public class SHHttpUtils {

    // 加上volatile，可以保证这个变量的唯一性
    private static volatile SHHttpUtils sSHHttpUtils;
    private static OkHttpClient sOkHttpClient;
    // 创建一个主线程的Handler
    public static Handler sHandler = new Handler(Looper.getMainLooper());
    // 是否是debug模式
    public static boolean isDebug = false;
    // 日志的过滤器
    public static final String sDebugTag = "SHTAG";
    // 请求是否取消了
    public static boolean isCancel = false;

    private SHHttpUtils() {

    }

    public static SHHttpUtils getInstance() {
        if (sOkHttpClient == null) {
            throw new IllegalArgumentException("Should be call setOkHttpClient() before call getInstance()");
        }

        if (sSHHttpUtils == null) {
            synchronized (SHHttpUtils.class) {
                if (sSHHttpUtils == null) {
                    sSHHttpUtils = new SHHttpUtils();
                }
            }
        }
        return sSHHttpUtils;
    }


    /**
     * 设置OkHttpClient对象
     *
     * @param okHttpClient
     */
    public static void setOkHttpClient(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            sOkHttpClient = new OkHttpClient();
        } else {
            sOkHttpClient = okHttpClient;
        }
    }


    /**
     * 获取OkHttpClient对象
     *
     * @return
     */
    public OkHttpClient getOkHttpClient() {
        return sOkHttpClient;
    }

    /**
     * debug模式则开启日志
     *
     * @param isDebug
     */
    public static void setDebug(boolean isDebug) {
        isDebug = isDebug;
    }


    /**
     * 返回一个get请求的builder
     *
     * @return
     */
    public GetBuilder get() {
        return new GetBuilder(this);
    }

    /**
     * 返回一个post请求的builder
     *
     * @return
     */
    public PostBuilder post() {
        return new PostBuilder(this);
    }

    /**
     * 返回一个用于upload的builder
     *
     * @return
     */
    public UploadBuilder upload() {
        return new UploadBuilder(this);
    }


    /**
     * 返回一个用于download的builder
     *
     * @return
     */
    public DownloadBuilder download() {
        return new DownloadBuilder(this);
    }


    /**
     * 根据tag来取消Request
     *
     * @param tag
     */
    public void cancel(Object tag) {
        Dispatcher dispatcher = sOkHttpClient.dispatcher();
        for (Call call : dispatcher.queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : dispatcher.runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        isCancel = true;
    }


}
