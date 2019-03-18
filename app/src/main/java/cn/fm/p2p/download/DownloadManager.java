package cn.fm.p2p.download;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import cn.fm.udp.LogWriter;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * @desc http下载管理器
 * @created
 * @createdDate 2019/3/11 14:11
 * @updated
 * @updatedDate 2019/3/11 14:11
 **/
public class DownloadManager {

    private final static String TAG = "DownloadManager";

    private HashMap<String, Call> mCallMap;
    private OkHttpClient mClient;

    private DownloadManager() {
        mCallMap = new HashMap<>();
        mClient = new OkHttpClient();
    }

    public void cancel(String downloadUrl) {
        Call call = mCallMap.get(downloadUrl);
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

    private final static class Holder {
        private final static DownloadManager instance = new DownloadManager();
    }

    public static DownloadManager getInstance() {
        return Holder.instance;
    }


    public boolean startDownload(final DownloadInfo downloadInfo, DownloadCallback callback) {
        if (callback == null) {
            callback = mCallback;
        }
        File file = new File(downloadInfo.getSaveDir(), downloadInfo.getSaveFileName());// 设置路径
        long contentLength = getContentLength(downloadInfo.getDownloadUrl());
        if (file.exists() && file.length() == contentLength && contentLength != -1) {
            callback.downloadSuccess(downloadInfo);
            return true; //文件存在，且完整，直接返回，不下载了
        }
        long process = 0;
        Request.Builder builder = new Request.Builder();
        if (file.exists() && contentLength != -1) {
            process = file.length();
            builder.addHeader("RANGE", "bytes=" + file.length() + "-" + contentLength);
        }
        Request request = builder.url(downloadInfo.getDownloadUrl()).build();
        Call call = mClient.newCall(request);
        mCallMap.put(downloadInfo.getDownloadUrl(), call);
        InputStream is = null;//输入流
        FileOutputStream fos = null;//输出流
        try {

            Response response = call.execute();
            if (response.code() == 200 || response.code() == 206) {
                is = response.body().byteStream();          //获取输入流
                if (is != null) {
                    fos = new FileOutputStream(file);
                    byte[] buf = new byte[1024 * 8];
                    int len = -1;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        process += len;
                        Log.d(TAG, "progress:" + 100f * process / contentLength);
                        callback.downloadProgress(downloadInfo, 100f * process / contentLength);
                    }
                    fos.flush();
                    LogWriter.getInstance().info("downloaded:" + downloadInfo.toString());
                    callback.downloadSuccess(downloadInfo);
                    return true;
                } else {
                    callback.downloadFailed(downloadInfo);
                }
            } else {
                callback.downloadFailed(downloadInfo);
            }
        } catch (IOException ex) {
            boolean isCanceled = call.isCanceled();
            boolean isExecuted = call.isExecuted();
            LogWriter.getInstance().info("isCanceled:" + isCanceled);
            LogWriter.getInstance().info("isExecuted:" + isExecuted);
            ex.printStackTrace();
            callback.downloadFailed(downloadInfo);
        } finally {
            IOUtils.close(fos);
            IOUtils.close(is);
            mCallMap.remove(call);
        }
        return false;
    }


    /**
     * 并不做数据请求，只是获取长度
     *
     * @param downloadUrl
     * @return
     */
    private long getContentLength(String downloadUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.close();
                return contentLength == 0 ? DownloadInfo.TOTAL_ERROR : contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return DownloadInfo.TOTAL_ERROR;
    }


    private DownloadCallback mCallback = new DownloadCallback() {
        @Override
        public void downloadStart(DownloadInfo downloadInfo) {

        }

        @Override
        public void downloadProgress(DownloadInfo downloadInfo, float progress) {

        }

        @Override
        public void downloadSuccess(DownloadInfo downloadInfo) {

        }

        @Override
        public void downloadFailed(DownloadInfo downloadInfo) {

        }
    };
}
