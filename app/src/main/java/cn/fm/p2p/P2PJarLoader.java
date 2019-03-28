package cn.fm.p2p;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import cn.fm.udp.Constant;
import dalvik.system.DexClassLoader;


/**
 * @desc 反射加载p2pjar包中的代码
 * @created
 * @createdDate 2019/3/27 14:38
 * @updated
 * @updatedDate 2019/3/27 14:38
 **/

public class P2PJarLoader {

    public static boolean copyAssetsFileToCacheFile(Context context) {
        File jarDir = new File(App.getInstance().getFilesDir(), "adlib");
        if (!jarDir.exists()) {
            jarDir.mkdirs();
        }
        File jarFile = new File(jarDir, "p2pnat.jar");
        try {
            InputStream is = context.getAssets().open("p2pnat.jar");
            FileOutputStream fos = new FileOutputStream(jarFile);
            byte[] buffer = new byte[4096];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void cpAssetToFile() {
        try {
            InputStream is = App.getInstance().getResources().getAssets().open("p2pnat.jar");
            File jarDir = new File(App.getInstance().getFilesDir(), "adlib");
            if (!jarDir.exists()) {
                jarDir.mkdirs();
            }
            File jarFile = new File(jarDir, "p2pnat.jar");
            if (!jarFile.exists()) {
                readToFile(is, jarFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readToFile(InputStream is, File tagetFile) {
        byte[] buffer = new byte[1024];
        int byteCount = 0;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tagetFile);
            while ((byteCount = is.read(buffer)) != -1) {         // 循环从输入流读取
                fos.write(buffer, 0, byteCount);              // 将读取的输入流写入到输出流
            }
            fos.flush();                                          // 刷新缓冲区
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void startLogin() {
        final Context context = App.getInstance();
        File jarDir = new File(context.getFilesDir(), "adlib");
        File jarFile = new File(jarDir, "p2pnat.jar");
        if (jarFile.exists()) {
            DexClassLoader classLoader = new DexClassLoader(jarFile.getAbsolutePath(), context.getFilesDir()
                    .getPath(), null, ClassLoader.getSystemClassLoader());
            login(classLoader);
        }
    }

    private static void login(DexClassLoader classLoader) {
        try {
            Class clsCallback = classLoader.loadClass("com.fengmang.p2p.udp.Callback");
            Class clsUdpClient = classLoader.loadClass("com.fengmang.p2p.udp.UDPClient");
            Constructor udpClientConstructor = clsUdpClient.getConstructor();
            CallbackHandler callbackHandler = new CallbackHandler();
            Object mObj = Proxy.newProxyInstance(classLoader, new Class[]{clsCallback}, callbackHandler);
            Method method = clsUdpClient.getMethod("startClient",
                    new Class[]{
                            String.class,
                            String.class,
                            String.class,
                            int.class,
                            String.class,
                            clsCallback
                    });
            String logDir = App.getInstance().getExternalFilesDir("p2pLog").getAbsolutePath();
            String serverHost = "gt.beevideo.tv";
            String saveDir = App.getInstance().getExternalFilesDir("p2pFiles").getAbsolutePath();
            int code = (int) getFileValue(classLoader, "SC_LOGIN");
            String strfiles = Constant.FILE_MP4 + "," + Constant.FILE_ZIP;
            Object[] param = new Object[]{
                    logDir,
                    serverHost,
                    saveDir,
                    code,
                    strfiles,
                    mObj
            };
            Log.d("loader", "login...");
            method.invoke(udpClientConstructor.newInstance(), param);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public static void startDownload() {
        final Context context = App.getInstance();
        File jarDir = new File(context.getFilesDir(), "adlib");
        File jarFile = new File(jarDir, "p2pnat.jar");
        if (jarFile.exists()) {
            DexClassLoader classLoader = new DexClassLoader(jarFile.getAbsolutePath(), context.getFilesDir()
                    .getPath(), null, ClassLoader.getSystemClassLoader());
            download(classLoader);
        }
    }

    private static void download(DexClassLoader classLoader) {
        try {
            Class clsCallback = classLoader.loadClass("com.fengmang.p2p.udp.Callback");
            Class clsUdpClient = classLoader.loadClass("com.fengmang.p2p.udp.UDPClient");
            Constructor udpClientConstructor = clsUdpClient.getConstructor();
            CallbackHandler callbackHandler = new CallbackHandler();
            Object mObj = Proxy.newProxyInstance(classLoader, new Class[]{clsCallback}, callbackHandler);
            Method method = clsUdpClient.getMethod("startClient",
                    new Class[]{
                            String.class,
                            String.class,
                            String.class,
                            int.class,
                            String.class,
                            clsCallback
                    });
            String logDir = App.getInstance().getExternalFilesDir("p2pLog").getAbsolutePath();
            String serverHost = "gt.beevideo.tv";
            String saveDir = App.getInstance().getExternalFilesDir("p2pFiles").getAbsolutePath();
            int code = (int) getFileValue(classLoader, "SC_DOWNLOAD");
            String strfiles = Constant.FILE_MP4;
            //String strfiles = Constant.FILE_ZIP ;
            Object[] param = new Object[]{
                    logDir,
                    serverHost,
                    saveDir,
                    code,
                    strfiles,
                    mObj
            };
            method.invoke(udpClientConstructor.newInstance(), param);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static Object getFileValue(DexClassLoader classLoader, String fileName) {
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

    static class CallbackHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (args.length == 2) {
                int code = (int) args[0];
                String text = (String) args[1];
                Log.e("callback", "callback,code:" + code);
                Log.e("callback", "callback,text:" + text);
                Intent intent = new Intent();
                intent.setPackage(App.getInstance().getPackageName());

                Bundle bundle = new Bundle();
                bundle.putInt("code", code);
                bundle.putString("filename", text);
                intent.putExtra("params", bundle);
                intent.setAction(P2PReceiver.ACTION_P2P_CALLBACK);
                App.getInstance().sendBroadcast(intent);
            }

            return null;
        }
    }
}
