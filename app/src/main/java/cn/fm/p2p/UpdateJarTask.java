package cn.fm.p2p;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import cn.fm.p2p.download.DownloadManager;
import dalvik.system.DexClassLoader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateJarTask implements Runnable {

    private Context context;

    public UpdateJarTask(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        File jarDir = new File(App.getInstance().getFilesDir(), "adlib");
        File jarFile = new File(jarDir, "p2pnat.jar");
        if (jarFile.exists()) {
            String version = readJarVerison(jarFile, context);
            UpdateJarData updateJarData = requestJar(version);
            if (checkData(updateJarData)) {
                jarFile.delete();
                downloadJar(updateJarData, jarDir.getAbsolutePath(), jarFile.getName());
            }
        } else {
            Log.d("update", "本地无jar包");
        }
    }

    private boolean downloadJar(UpdateJarData updateJarData, String saveDir, String saveName) {
        String url = updateJarData.getUrl();
        boolean result = DownloadManager.getInstance().download(url, saveDir, saveName,
                null);
        Log.d("update", "p2pjar包更新成功");
        return result;
    }


    private String readJarVerison(File jarFile, Context context) {
        DexClassLoader classLoader = new DexClassLoader(jarFile.getAbsolutePath(), context.getFilesDir()
                .getPath(), null, ClassLoader.getSystemClassLoader());
        String version = (String) getFileValue(classLoader, "version");
        return version != null ? version : "1.0";
    }

    private Object getFileValue(DexClassLoader classLoader, String fileName) {
        try {
            Class clsConstant = classLoader.loadClass("com.fengmang.p2p.udp.Constant");
            Field f = clsConstant.getDeclaredField(fileName);
            f.setAccessible(true);
            return f.get(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UpdateJarData requestJar(String version) {
        String url = "http://gt.beevideo.tv/p2pstat/api/getJarInfo?version=" + version;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            if (response != null && response.code() == 200) {
                String text = response.body().string();
                UpdateJarData updateJarData = new Gson().fromJson(text, UpdateJarData.class);
                return updateJarData;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean checkData(UpdateJarData updateJarData) {
        return updateJarData.getStatus() == 0 &&
                !TextUtils.isEmpty(updateJarData.getUrl()) &&
                !TextUtils.isEmpty(updateJarData.getMd5()) &&
                !TextUtils.isEmpty(updateJarData.getVersion());
    }


    public class UpdateJarData {

        private int status;
        private String url;
        private String md5;
        private String version;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
