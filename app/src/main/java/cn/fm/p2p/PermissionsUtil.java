package cn.fm.p2p;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Android 23+ 动态读取权限
 * Created by Administrator on 2018/7/3.
 */

public class PermissionsUtil {
    //读写权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * 检查应用程序是否有权写入设备存储
     * 如果应用程序没有权限，则会提示用户授予权限
     *
     * @param activity 所在的Activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        //检查应用程序是否有权写入设备存储
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            //如果应用程序没有权限，则会提示用户授予权限
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}


