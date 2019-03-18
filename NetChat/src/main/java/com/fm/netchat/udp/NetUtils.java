package com.fm.netchat.udp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.FileInputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetUtils {

    private static final String NOT_CONNECTED_IP = "0.0.0.0";

    /**
     * 检查当前网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = null;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检查当有线是否插入
     *
     * @return
     */
    public static boolean isCablePlugin() {
        final String netFile = "/sys/class/net/eth0/operstate";
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(netFile);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer, "UTF-8");
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //ELog.d("isCablePlugin : " + res);
        if (null != res) {
            if ("up".equals(res.trim()) || "unknown".equals(res.trim())) {
                return true;
            }
        }
        return false;
    }


    public static String getWIFILocalIpAdress(Context context) {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(
                Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = formatIpAddress(ipAddress);
        return ip;
    }


    private static String formatIpAddress(int ipAdress) {
        return (ipAdress & 0xFF) + "." +
                ((ipAdress >> 8) & 0xFF) + "." +
                ((ipAdress >> 16) & 0xFF) + "." +
                (ipAdress >> 24 & 0xFF);
    }

    /**
     * 获取以太网ip
     *
     * @return
     */
    public static String getEthernetIpAddress() {
        String localIp = "0.0.0.0";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address && !inetAddress.getHostAddress().toString().equals("0.0.0.0")) {
                        localIp = inetAddress.getHostAddress().toString();
                        if (!localIp.equals("192.168.43.1")) {
                            return localIp;
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localIp;
        //修复了部分盒子上面，既有连接网线，有开启了热点导致获取的ip地址不正确的问题修复
        //192.168.43.1  开启wifi热点时固定就是会获取到这个ip，因此要排除这个ip
    }


    public static int getNetType(ConnectivityManager connectivityManager, Context context) {
        NetworkInfo ethnetNetWorkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (ethnetNetWorkInfo != null) {
            NetworkInfo.State ethnetState = ethnetNetWorkInfo.getState();
            if (ethnetState != null) {
                if (ethnetState == NetworkInfo.State.CONNECTED) {
                    return ConnectivityManager.TYPE_ETHERNET;
                }
            }
        }
        NetworkInfo wifiNetWorkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetWorkInfo != null) {
            NetworkInfo.State wifiState = wifiNetWorkInfo.getState();
            if (wifiState != null) {
                if (wifiState == NetworkInfo.State.CONNECTED) {
                    return ConnectivityManager.TYPE_WIFI;
                }
            }
        }
        return -1;
    }

    //Android电视设备、机顶盒获取ip方式，只有连接网线和wifi两种方式
    public static String getLocalhostIp(Context context) {
        String ip = null;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        int netType = (getNetType(connectivityManager, context));
        if (ConnectivityManager.TYPE_ETHERNET == netType) {
            ip = getEthernetIpAddress();
        } else {
            ip = getWIFILocalIpAdress(context);
        }
        return ip;
    }


}
