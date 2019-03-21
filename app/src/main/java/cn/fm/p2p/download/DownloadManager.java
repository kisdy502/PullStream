package cn.fm.p2p.download;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;


public class DownloadManager {

    private final static byte[] MIN_BUFF = new byte[2 * 1024];
    private final static byte[] MIDDLE_BUFF = new byte[8 * 1024];
    private final static byte[] BIG_BUFF = new byte[32 * 1024];
    private byte[] buff = MIDDLE_BUFF;

    private final static long LENGTH_1MB = 1024 * 1024;
    private final static long LENGTH_128MB = LENGTH_1MB * 128;
    private final static long LENGTH_1GB = LENGTH_1MB * 1024;

    private final static String TAG = "DownloadManager";
    private static final long TOTAL_ERROR = -1L;
    private static final long REQUEST_EXCEPTION = -8888L;
    private static final long RESPONSE_CODE_ERROR = -9999L;

    private HashMap<String, Call> mCallMap;

    private DownloadManager() {
        mCallMap = new HashMap<>();
    }

    public void cancel(String downloadUrl) {
        Call call = mCallMap.get(downloadUrl);
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
        mCallMap.remove(downloadUrl);
    }

    public boolean download(String downloadUrl, String saveDir, String saveFileName, @Nullable
            ProgressListener
            listener) {
        if (listener == null) {
            listener = mListener;
        }
        if (mCallMap.containsKey(downloadUrl)) {
            listener.onFailed(downloadUrl, ProgressListener.FAILED_CALL_EXIST, "下载任务已经存在");
            return false;
        }
        long contentLength = getContentLength(downloadUrl);
        if (contentLength < TOTAL_ERROR) {
            listener.onFailed(downloadUrl, contentLength == REQUEST_EXCEPTION ? ProgressListener
                    .FAILED_IO_EXCEPTION : ProgressListener.FAILED_RESPONSE_CODE, "");
            return false;
        }
        File file = new File(saveDir, saveFileName); // 设置路径
        long start = 0L;
        if (file.exists()) {
            start = file.length();
        }
        if (start == contentLength) {
            listener.onUpdate(downloadUrl, start, contentLength, true);
            return true;
        }
        Request.Builder builder = new Request.Builder();

        if (contentLength == TOTAL_ERROR) {
            Log.i(TAG, "bytes=" + start + "-");
            builder.addHeader("RANGE", "bytes=" + start + "-");
        } else {
            Log.i(TAG, "bytes=" + start + "-" + contentLength);
            builder.addHeader("RANGE", "bytes=" + start + "-" + contentLength);
        }
        Request request = builder.url(downloadUrl).build();
        OkHttpClient client = new OkHttpClient.Builder().addNetworkInterceptor(new ProgressInterceptor
                (listener, start, downloadUrl)).build();
        Call call = client.newCall(request);

        mCallMap.put(downloadUrl, call);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                long length = response.body().contentLength();
                if (length == 0) {
                    listener.onUpdate(downloadUrl, start, contentLength, true);
                    return true;     // 说明文件已经下载完
                }
                setBuff(contentLength);
                return saveFile(response, file, start);
            } else {
                Log.e(TAG, "download failed");
            }
        } catch (IOException e) {
            Log.w(TAG, "http request exception" + e.getMessage());
            e.printStackTrace();
        } finally {
            mCallMap.remove(downloadUrl);
        }
        return false;
    }

    private void setBuff(long contentLength) {
        if (contentLength < 0L) {
            buff = MIN_BUFF;
        } else if (contentLength < LENGTH_1MB) {
            buff = MIN_BUFF;
        } else if (contentLength < LENGTH_128MB) {
            buff = MIDDLE_BUFF;
        } else if (contentLength < LENGTH_1GB) {
            buff = BIG_BUFF;
        } else {
            buff = BIG_BUFF;
        }
    }

    private void assetBuff(byte[] bytes) {
        if (bytes.length == MIN_BUFF.length) {
            Log.i(TAG, "MIN_BUFF");
        } else if (bytes.length == MIDDLE_BUFF.length) {
            Log.i(TAG, "MIDDLE_BUFF");
        } else if (bytes.length == BIG_BUFF.length) {
            Log.i(TAG, "BIG_BUFF");
        }
    }

    private boolean saveFile(Response response, File saveFile, long start) {
        assetBuff(buff);
        InputStream is = null;
        RandomAccessFile randomAccessFile = null;
        BufferedInputStream bis = null;
        try {
            int len = 0;
            is = response.body().byteStream();
            bis = new BufferedInputStream(is);
            // 随机访问文件，可以指定断点续传的起始位置
            randomAccessFile = new RandomAccessFile(saveFile, "rwd");
            randomAccessFile.seek(start);
            while ((len = bis.read(buff)) != -1) {
                randomAccessFile.write(buff, 0, len);
            }
            return true;
        } catch (IOException e) {
            Log.w(TAG, "save file exception" + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            close(bis);
            close(is);
            close(randomAccessFile);
        }
    }

    private long getContentLength(String downloadUrl) {
        Request request = new Request.Builder().url(downloadUrl).build();
        try {
            Response response = new OkHttpClient.Builder().build().newCall(request).execute();
            Log.d("dm", "response:" + response.code());
            if (response.isSuccessful()) {
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

    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;
        private long start;
        private String downloadUrl;

        ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener, long
                start, String downloadUrl) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
            this.start = start;
            this.downloadUrl = downloadUrl;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {

            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.onUpdate(downloadUrl, totalBytesRead + start, responseBody
                                    .contentLength() + start,
                            bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }


    private static class ProgressInterceptor implements Interceptor {

        private ProgressListener listener;
        private long start;
        private String downloadUrl;

        private ProgressInterceptor(ProgressListener listener, long start, String downloadUrl) {
            this.listener = listener;
            this.start = start;
            this.downloadUrl = downloadUrl;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), listener,
                            start, downloadUrl)).build();
        }
    }

    public interface ProgressListener {
        int FAILED_IO_EXCEPTION = 1;
        int FAILED_RESPONSE_CODE = 2;
        int FAILED_CALL_EXIST = 3;

        void onFailed(String downloadUrl, int result, String desc);

        void onUpdate(String downloadUrl, long bytesRead, long contentLength, boolean done);
    }

    private ProgressListener mListener = new ProgressListener() {
        @Override
        public void onFailed(String downloadUrl, int result, String desc) {
        }

        @Override
        public void onUpdate(String downloadUrl, long bytesRead, long contentLength, boolean done) {
        }
    };

    private final static class Holder {
        private final static DownloadManager instance = new DownloadManager();
    }

    public static DownloadManager getInstance() {
        return Holder.instance;
    }

    private void close(Closeable... closeable) {
        for (Closeable c : closeable) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
