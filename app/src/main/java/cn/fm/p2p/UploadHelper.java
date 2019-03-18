package cn.fm.p2p;

import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import cn.fm.udp.LogWriter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadHelper {

    public static void upload() {

        File dir = LogWriter.getInstance().createLogWriteDir();
        File[] logFiles = dir.listFiles();

        String url = "http://gt.beevideo.tv/p2pstat/api/uploadLogFile";

        if (logFiles != null && logFiles.length > 0) {

            OkHttpClient client = new OkHttpClient();

            MultipartBody.Builder builder = new MultipartBody.Builder();

            builder.setType(MultipartBody.FORM);

            String TYPE = "application/octet-stream";

            for (File file : logFiles) {

                if (file.isFile()) {
                    Log.d("upload", "file:" + file.getName());
                    RequestBody fileBody = RequestBody.create(MediaType.parse(TYPE), file);
                    builder.addFormDataPart("fileName", file.getName(), fileBody);
                } else {
                    Log.d("upload", "ignore Directory:" + file.getName());
                }
            }

            RequestBody requestBody = builder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                Log.d("upload", "response:" + response.code());

                Intent intent = new Intent();
                intent.putExtra("code", response.code());
                if (response.code() == 200) {
                    String text = response.body().string();
                    Log.d("upload", "response:" + text);
                    intent.putExtra("response", text);
                }

                intent.setAction(P2PReceiver.ACTION_UPLOAD_RESPONSE);
                intent.setPackage(App.getInstance().getPackageName());
                App.getInstance().sendBroadcast(intent);
            } catch (IOException e) {

            }

        } else {
            Log.d("upload", "upload no log file");
        }
    }
}
