package and.pojour.com.shhttp.builder;

import android.text.TextUtils;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import and.pojour.com.shhttp.SHHttpUtils;
import and.pojour.com.shhttp.body.RequestProgressBody;
import and.pojour.com.shhttp.callback.SHCallback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by "Shake" on 2017/10/16.
 */
public class UploadBuilder extends BaseRequestBuilder<UploadBuilder> {

    // 文件和路径的集合
    private Map<String, File> mFileMap;
    // MultipartBody 表示分块请求
    private List<MultipartBody.Part> mParts;

    public UploadBuilder(SHHttpUtils shHttpUtils) {
        super(shHttpUtils);
    }

    /**
     * 添加文件集
     *
     * @param files
     * @return
     */
    public UploadBuilder files(Map<String, File> files) {
        this.mFileMap = files;
        return this;
    }

    /**
     * 添加单个文件
     *
     * @param path
     * @param file
     * @return
     */
    public UploadBuilder addFile(String path, File file) {
        if (mFileMap == null) {
            mFileMap = new HashMap<>();
        }
        mFileMap.put(path, file);
        return this;
    }

    /**
     * 添加文件
     *
     * @param key
     * @param fileName
     * @param fileContent
     * @return
     */
    public UploadBuilder addFile(String key, String fileName, byte[] fileContent) {
        if (this.mParts == null) {
            this.mParts = new ArrayList<>();
        }
        // 创建RequestBody
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), fileContent);
        // 创建 MultipartBody.Part
        MultipartBody.Part part = MultipartBody.Part.create(Headers.of("Content-Disposition",
                "form-data; name=\"" + key + "\"; filename=\"" + fileName + "\""), fileBody);
        this.mParts.add(part);
        return this;
    }


    @Override
    public void enqueue(IResponseListener responseHandler) {
        if (TextUtils.isEmpty(mUrl)) {
            throw new IllegalArgumentException("url can not be null");
        }
        Request.Builder builder = new Request.Builder().url(mUrl);
        appendHeadersToBuilder(builder);
        if (mTag != null) {
            builder.tag(mTag);
        }

        //创建一个分块请求的builder
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        appendParams(multipartBodyBuilder);
        appendFiles(multipartBodyBuilder);
        appendParts(multipartBodyBuilder);

        // post一个重写的RequestBuilder
        builder.post(new RequestProgressBody(multipartBodyBuilder.build(), responseHandler));

        // 创建Request
        Request request = builder.build();
        SHHttpUtils.getInstance().getOkHttpClient().newCall(request).enqueue(new SHCallback(responseHandler));
    }

    /**
     * 添加Parts
     *
     * @param multipartBodyBuilder
     */
    private void appendParts(MultipartBody.Builder multipartBodyBuilder) {
        if (mParts != null && mParts.size() > 0) {
            for (int i = 0; i < mParts.size(); i++) {
                multipartBodyBuilder.addPart(mParts.get(i));
            }
        }
    }

    /**
     * 添加文件
     *
     * @param multipartBodyBuilder
     */
    private void appendFiles(MultipartBody.Builder multipartBodyBuilder) {
        if (mFileMap != null && !mFileMap.isEmpty()) {
            RequestBody fileBody;
            File file = null;
            String fileName = null;
            for (String key : mFileMap.keySet()) {
                file = mFileMap.get(key);
                if (file != null) {
                    fileName = file.getName();
                    fileBody = RequestBody.create(MediaType.parse(getMimeType(fileName)), file);
                    multipartBodyBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\"; filename=\"" + fileName + "\""), fileBody);
                }
            }
        }
    }

    private String getMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentType = fileNameMap.getContentTypeFor(fileName);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return contentType;
    }

    /**
     * 添加参数
     *
     * @param multipartBodyBuilder
     */
    private void appendParams(MultipartBody.Builder multipartBodyBuilder) {
        if (mParams != null && !mParams.isEmpty()) {
            for (String key : mParams.keySet()) {
                if (mParams.get(key) != null) {
                    multipartBodyBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                            RequestBody.create(null, mParams.get(key)));
                }
            }
        }
    }
}
