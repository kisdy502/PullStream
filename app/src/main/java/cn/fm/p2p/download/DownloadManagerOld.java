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
import okhttp3.ResponseBody;


/**
 * @desc http下载管理器
 * @created 通过读取流返回下载进度，实测在下载过程中会出现，下一个进度小于前一个进度的情况，
 * @createdDate 2019/3/11 14:11
 * @updated
 * @updatedDate 2019/3/11 14:11
 **/
@Deprecated
public class DownloadManagerOld {

    private final static String TAG = "DownloadManagerOld";

    public static final long TOTAL_ERROR = -1L;
    public static final long REQUEST_EXCEPTION = -8888L;
    public static final long RESPONSE_CODE_ERROR = -9999L;

    private HashMap<String, Call> mCallMap;
    private OkHttpClient mClient;

    private DownloadManagerOld() {
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
        private final static DownloadManagerOld instance = new DownloadManagerOld();
    }

    public static DownloadManagerOld getInstance() {
        return Holder.instance;
    }


    public boolean startDownload(final DownloadInfo downloadInfo, DownloadCallback callback) {
        if (callback == null) {
            callback = mCallback;
        }
        if (mCallMap.containsKey(downloadInfo.getDownloadUrl())) {
            callback.downloadFailed(downloadInfo, DownloadCallback.FAILED_EXIST);
            return false;
        }
        File file = new File(downloadInfo.getSaveDir(), downloadInfo.getSaveFileName());// 设置路径
        long contentLength = getContentLength(downloadInfo.getDownloadUrl());
        if (contentLength == RESPONSE_CODE_ERROR) {  //资源存在问题,提前返回，后面都不需处理了
            callback.downloadFailed(downloadInfo, DownloadCallback.FAILED_RESPONSE_CODE);
            return false;
        }
        if (contentLength == REQUEST_EXCEPTION) {  //请求长度发生异常,提前返回，后面都不需处理了
            callback.downloadFailed(downloadInfo, DownloadCallback.FAILED_IO_EXCEPTION);
            return false;
        }
        if (file.exists() && file.length() == contentLength && contentLength != TOTAL_ERROR) {
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
            Log.i("dm", "response:" + response.code());
            if (response.code() == 200 || response.code() == 206) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    is = responseBody.byteStream();          //获取输入流
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
                        callback.downloadFailed(downloadInfo, DownloadCallback.FAILED_EMPTY_STREAM);
                    }
                } else {
                    callback.downloadFailed(downloadInfo, DownloadCallback.FAILED_EMPTY_RESPONSE);
                }

            } else {
                callback.downloadFailed(downloadInfo, DownloadCallback.FAILED_RESPONSE_CODE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            callback.downloadFailed(downloadInfo, DownloadCallback.FAILED_IO_EXCEPTION);
        } finally {
            IOUtils.close(fos);
            IOUtils.close(is);
            mCallMap.remove(downloadInfo.getDownloadUrl());
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
            Log.d("dm", "response:" + response.code());
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.close();
                return contentLength == 0 ? TOTAL_ERROR : contentLength;
            } else {
                return RESPONSE_CODE_ERROR;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return REQUEST_EXCEPTION;
        }
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
        public void downloadFailed(DownloadInfo downloadInfo, int failedCode) {

        }


    };
}
