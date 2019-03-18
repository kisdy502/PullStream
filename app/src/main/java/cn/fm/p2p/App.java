package cn.fm.p2p;

import android.app.Application;
import android.content.Intent;

import cn.fm.p2p.log.LogService;

/**
 * @desc Application
 * @created
 * @createdDate 2019/3/11 14:15
 * @updated
 * @updatedDate 2019/3/11 14:15
 **/
public class App extends Application {

    private static App instance;

    private boolean isP2pLogin = false;

    public boolean isP2pLogin() {
        return isP2pLogin;
    }

    public void setP2pLogin(boolean p2pLogin) {
        isP2pLogin = p2pLogin;
    }

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //startService(new Intent(this, LogService.class));

    }
}
