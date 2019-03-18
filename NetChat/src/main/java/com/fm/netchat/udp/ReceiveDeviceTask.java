package com.fm.netchat.udp;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.fm.netchat.App;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;

/**
 * Created by Administrator on 2019/3/18.
 */

public class ReceiveDeviceTask implements Runnable {

    private DatagramSocket datagramSocket;
    private boolean isRun;
    private WifiManager.MulticastLock lock;

    public ReceiveDeviceTask() {
        WifiManager manager = (WifiManager) App.getInstance().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        assert manager != null;
        this.lock = manager.createMulticastLock("UDP_WIFI");
        isRun = true;
    }

    @Override
    public void run() {
        try {
            startListen();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stopThread();
        }
    }

    private void startListen() throws IOException {
        byte[] msg = new byte[256];

        datagramSocket = new DatagramSocket(Common.PORT);
        datagramSocket.setBroadcast(true);
        DatagramPacket packet = new DatagramPacket(msg, msg.length);

        String localIp = NetUtils.getLocalhostIp(App.getInstance());
        Log.d("Receive", "localIp:" + localIp);
        while (isRun) {
            lock.acquire();

            datagramSocket.receive(packet);
            String remoteIp = packet.getAddress().getHostAddress();
            if (!remoteIp.equalsIgnoreCase(localIp)) {
                String strMsg = new String(packet.getData(), 0, packet.getLength(), Charset.forName("UTF-8"));
                Log.d("Receive", packet.getAddress().getHostAddress().toString());
                Log.d("Receive", "remote message:" + strMsg);

            }

            lock.release();

        }

    }

    public void stopThread() {
        isRun = false;
        if (datagramSocket != null) {
            datagramSocket.close();
        }
        // 释放锁
        if (lock.isHeld()) {
            lock.release();
        }

    }
}
