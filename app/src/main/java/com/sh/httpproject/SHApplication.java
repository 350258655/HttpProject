package com.sh.httpproject;

import android.app.Application;

import java.util.concurrent.TimeUnit;

import and.pojour.com.shhttp.SHHttpUtils;
import okhttp3.OkHttpClient;

/**
 * Created by "Shake" on 2017/10/13.
 */
public class SHApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        SHHttpUtils.setOkHttpClient(okHttpClient);
        SHHttpUtils.setDebug(true);

    }
}
