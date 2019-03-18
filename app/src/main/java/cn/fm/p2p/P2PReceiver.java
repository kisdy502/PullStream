package cn.fm.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


/**
 * @desc p2p相关处理，接受消息
 * @created
 * @createdDate 2019/3/11 17:12
 * @updated
 * @updatedDate 2019/3/11 17:12
 **/
public class P2PReceiver extends BroadcastReceiver {

    public final static String ACTION_UPLOAD_RESPONSE = "fm.p2p.action.LOG_UPLOAD";

    public final static String ACTION_HTTP_DOWNLOAD = "fm.p2p.action.HTTP_DOWNLOAD";

    public final static String ACTION_P2P_LOGIN = "fm.p2p.action.P2P_LOGIN";


    public final static String ACTION_P2P_NATA = "fm.p2p.action.P2P_NATA";


    public final static String ACTION_P2P_NATB = "fm.p2p.action.P2P_NATB";


    public final static String ACTION_P2P_DOWNLOAD = "fm.p2p.action.P2P_DOWNLOAD";


    public final static String ACTION_P2P_CALLBACK = "fm.p2p.action.P2P_CALLBACK";


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
        switch (intent.getAction()) {
            case ACTION_HTTP_DOWNLOAD:
                Toast.makeText(context, "File downloaded:" + intent.getStringExtra("fileName"),
                        Toast.LENGTH_SHORT).show();
                break;
            case ACTION_UPLOAD_RESPONSE:
                break;
            case ACTION_P2P_LOGIN:
                Toast.makeText(context, "P2P Login Successed!", Toast.LENGTH_SHORT).show();
                break;
            case ACTION_P2P_NATA:
                break;
            case ACTION_P2P_NATB:
                break;
            case ACTION_P2P_DOWNLOAD:
                break;
            case ACTION_P2P_CALLBACK:
                Bundle bundle = intent.getBundleExtra("params");
                int code = bundle.getInt("code", -1);
                String filename = bundle.getString("filename");
                if (code == 9999) {
                    Toast.makeText(context, "code:" + code + ",本机公网地址:" + filename, Toast
                            .LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "code:" + code + ",filename:" + filename, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
