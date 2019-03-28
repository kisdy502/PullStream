package cn.fm.udp;

import android.content.Intent;
import android.os.Bundle;

import java.io.File;
import java.util.logging.Logger;

import cn.fm.p2p.App;
import cn.fm.p2p.HttpTool;
import cn.fm.p2p.P2PReceiver;

class Callback {

    private static Logger logger = Logger.getLogger(Callback.class.getName());

    void callback(int code, String filename) {
        if (code == Constant.CALL_BACK_SUCCESS) {
            File file = new File(App.getInstance().getExternalFilesDir(""), filename);
            String md5 = HttpTool.getFileMD5String(file);
            String logStr = "p2p 下载" + filename + " success,md5 is:" + md5;
        } else if (code == Constant.CALL_BACK_FAIL) {
            logger.info("下载失败：" + code + " " + filename);
        } else if (code == Constant.CALL_BACK_NAT_FAIL) {
            logger.info("打洞失败：" + code + " " + filename);
        } else if (code == Constant.CALL_BACK_NO_SOURCE) {
            logger.info("没有可下载的源：" + code + " " + filename);
        } else if (code == Constant.CALL_BACK_RELOGIN) {
            App.getInstance().setP2pLogin(false);
            logger.info("请重新登录：" + code + " " + filename);
        } else if (code == 9999) {
            logger.info("登录成功");
            App.getInstance().setP2pLogin(true);
        }else if (code == Constant.CALL_BACK_NAT_TYPE) {
            logger.info("nat类型：" + code + " " + filename);
        }
        Intent intent = new Intent();
        intent.setPackage(App.getInstance().getPackageName());

        Bundle bundle = new Bundle();
        bundle.putInt("code", code);
        bundle.putString("filename", filename);
        intent.putExtra("params", bundle);
        intent.setAction(P2PReceiver.ACTION_P2P_CALLBACK);
        App.getInstance().sendBroadcast(intent);
    }
}
