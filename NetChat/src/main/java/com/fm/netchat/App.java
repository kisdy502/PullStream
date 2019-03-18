package com.fm.netchat;

import android.app.Application;

/**
 * Created by Administrator on 2019/3/18.
 */

public class App extends Application {
    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
