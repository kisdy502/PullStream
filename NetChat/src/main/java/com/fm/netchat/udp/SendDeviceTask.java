package com.fm.netchat.udp;

import com.fm.netchat.App;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 * Created by Administrator on 2019/3/18.
 */

public class SendDeviceTask implements Runnable {
    private boolean isRun;
    private String localIp;

    public SendDeviceTask() {
        isRun = true;
    }

    @Override
    public void run() {
        localIp = NetUtils.getLocalhostIp(App.getInstance());
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket();
            datagramSocket.setReuseAddress(true);
            datagramSocket.setTrafficClass(255);
            SocketAddress broadcastAddress = new InetSocketAddress("255.255.255.255", Common.PORT);
            String myName = localIp;
            DatagramPacket sendPacket = new DatagramPacket(myName.getBytes(), myName.length(), broadcastAddress);
            while (isRun) {
                datagramSocket.send(sendPacket);
                Thread.sleep(3000);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            datagramSocket.close();
            isRun = false;
        }

    }

    private void sendMsg() {
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket();

        } catch (Exception e) {
            e.printStackTrace();
        }
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getByName("255.255.255.255");

        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] msgByte = localIp.getBytes();
        DatagramPacket packet = new DatagramPacket(msgByte, msgByte.length,
                localAddress, Common.PORT);
        try {
            socket.setTrafficClass(255);
            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
