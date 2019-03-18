package com.fm.netchat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fm.netchat.udp.ReceiveDeviceTask;
import com.fm.netchat.udp.SendDeviceTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2019/3/18.
 */

public class UdpService extends Service {

    ExecutorService cachePool;

    @Override
    public void onCreate() {
        super.onCreate();
        cachePool = Executors.newCachedThreadPool();
        cachePool.submit(new SendDeviceTask());
        cachePool.submit(new ReceiveDeviceTask());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
