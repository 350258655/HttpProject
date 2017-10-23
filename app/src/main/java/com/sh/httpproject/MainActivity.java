package com.sh.httpproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import and.pojour.com.shhttp.SHHttpUtils;
import and.pojour.com.shhttp.response.IResponseDownloadListener;
import and.pojour.com.shhttp.response.JsonResponseListener;
import and.pojour.com.shhttp.response.StringResponseListener;

public class MainActivity extends AppCompatActivity {


    /**
     * 遇到的问题一。一开始总以为在是访问127.0.0.1，但其实127.0.0.1是模拟器本身，所以应该得用下面这个固定的IP
     * 详细参考这篇文章 http://blog.csdn.net/xuanyuanyelian/article/details/54313240
     */
    public String BASE_URL = "http://10.0.3.2:8080/web/HelloServlet";
    public String UPLOAD_URL = "http://10.0.3.2:8080/web/UploadServlet";
    public String DOWNLOAD_URL = "http://10.0.3.2:8080/web/DownloadServlet";
//    public String DOWNLOAD_URL = "http://cache.hdg123.cn/index.php?r=cache/index&name=tmp_yyb";

    // 要上传文件的路径
    public String UPLOAD_FILE_PATH = "/storage/emulated/0/Download/x.mp3";
    File UPLOAD_FILE = new File(UPLOAD_FILE_PATH);

    //    String DOWNLOAD_FILE_PATH ="/storage/emulated/0/Download/u.zip";
    String DOWNLOAD_FILE_PATH = "/storage/emulated/0/Download/x.mp3";

    private long mCompleteBytes = 0l;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_get = (Button) findViewById(R.id.btn_get);
        Button btn_post = (Button) findViewById(R.id.btn_post);
        final Button btn_upload = (Button) findViewById(R.id.btn_upload);
        Button btn_download = (Button) findViewById(R.id.btn_download);
        final Button btn_cancle = (Button) findViewById(R.id.btn_cancle);


        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SHHttpUtils.getInstance().get().url(BASE_URL).enqueue(new StringResponseListener() {
                    @Override
                    public void onSuccess(int statusCode, String response) {
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int statusCode, String errMsg) {
                        Toast.makeText(MainActivity.this, "fail : " + errMsg, Toast.LENGTH_SHORT).show();
                        Log.i(SHHttpUtils.sDebugTag, "onFailed: statusCode-->" + statusCode + ", mag : " + errMsg);
                    }

                });

            }
        });


        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", "shake");
                params.put("age", "25");
                SHHttpUtils.getInstance().post().url(BASE_URL).params(params).enqueue(new JsonResponseListener() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject jsonObjectResponse, JSONArray jsonArrayResponse) {
                        if (jsonArrayResponse == null && jsonObjectResponse != null) {
                            Toast.makeText(MainActivity.this, "jsonObject : " + jsonObjectResponse, Toast.LENGTH_SHORT).show();
                        } else if (jsonObjectResponse == null && jsonArrayResponse != null) {
                            Toast.makeText(MainActivity.this, "jsonArray : " + jsonArrayResponse, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(int statusCode, String errMsg) {
                        Toast.makeText(MainActivity.this, "fail : " + errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SHHttpUtils.getInstance().upload().url(UPLOAD_URL).addFile("adsdk_0.1.7.0.jar", UPLOAD_FILE).enqueue(new StringResponseListener() {
                    @Override
                    public void onSuccess(int statusCode, String response) {
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int statusCode, String errMsg) {
                        Toast.makeText(MainActivity.this, "fail : " + errMsg, Toast.LENGTH_SHORT).show();
                        Log.i(SHHttpUtils.sDebugTag, "onFailed: " + errMsg);
                    }

                    @Override
                    public void onProgress(int progress, long readBytes, long total) {
                        Log.i(SHHttpUtils.sDebugTag, "progress: " + progress);
                    }
                });
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i(SHHttpUtils.sDebugTag, "文件路径 : " + DOWNLOAD_FILE_PATH);
                SHHttpUtils.getInstance()
                        .download()
                        .url(DOWNLOAD_URL)
                        .filePath(DOWNLOAD_FILE_PATH)
                        .tag(MainActivity.class)
                        .enqueue(new IResponseDownloadListener() {
                            @Override
                            public void onFinish(File downliadFile) {
                                Log.i(SHHttpUtils.sDebugTag, "下载成功！");
                                Toast.makeText(MainActivity.this, "下载成功！", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailed(int statusCode, String errMsg) {
                                Log.i(SHHttpUtils.sDebugTag, "下载失败 : " + errMsg);
                                Toast.makeText(MainActivity.this, "下载失败 ： " + errMsg, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onProgress(int progress, long readBytes, long total) {
                                Log.i(SHHttpUtils.sDebugTag, progress + "-" + readBytes + "-" + total);
                                mCompleteBytes = readBytes;
                            }

                            @Override
                            public void onCancel() {

                            }

                        });
            }
        });


        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SHHttpUtils.isCancel) {
                    Log.i(SHHttpUtils.sDebugTag, "文件路径 : " + DOWNLOAD_FILE_PATH);
                    SHHttpUtils.getInstance()
                            .download()
                            .url(DOWNLOAD_URL)
                            .filePath(DOWNLOAD_FILE_PATH)
                            .tag(MainActivity.class)
                            .completeBytes(mCompleteBytes)
                            .enqueue(new IResponseDownloadListener() {
                                @Override
                                public void onFinish(File downliadFile) {
                                    Log.i(SHHttpUtils.sDebugTag, "下载成功！");
                                    Toast.makeText(MainActivity.this, "下载成功！", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailed(int statusCode, String errMsg) {
                                    Log.i(SHHttpUtils.sDebugTag, "下载失败 : " + errMsg);
                                    Toast.makeText(MainActivity.this, "下载失败 ： " + errMsg, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onProgress(int progress, long readBytes, long total) {
                                    Log.i(SHHttpUtils.sDebugTag, progress + "-" + readBytes + "-" + total);
                                    mCompleteBytes = readBytes;
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                    btn_upload.setText("取消下载");
                } else {
                    // 取消下载
                    SHHttpUtils.getInstance().cancel(MainActivity.class);
                    btn_cancle.setText("重新下载");
                }
            }
        });




    }


}
