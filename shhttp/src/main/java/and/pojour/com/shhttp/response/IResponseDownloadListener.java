package and.pojour.com.shhttp.response;

import java.io.File;

import and.pojour.com.shhttp.builder.IResponseListener;
import okhttp3.Response;

/**
 * Created by "Shake" on 2017/10/17.
 */
public abstract class IResponseDownloadListener implements IResponseListener {

    // 需要子类重写
    public abstract void onFinish(File downliadFile);

    // 开始
    public void onStart(long total){}

    // 取消
    public void onCancel(){}


    @Override
    public void onSuccess(Response response) {

    }
}
