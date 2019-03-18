package ad.tianci.com.p2pclient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity {

    public static final String SHARED_PREFERENCE_NAME = "settings";

    public static final String SETTINGS_STUN_SERVER_ADDRESS = "stun_server_address";
    public static final String SETTINGS_STUN_SERVER_PORT = "stun_server_port";
    public static final String SETTINGS_REGISTRATION_SERVER_ADDRESS = "registration_server_address";
    public static final String SETTINGS_REGISTRATION_SERVER_PORT = "registration_server_port";

    public static final String DEFAULT_STUN_SERVER_ADDRESS = "stun.ekiga.net";
    public static final int DEFAULT_STUN_SERVER_PORT = 3478;
    public static final String DEFAULT_REGISTRATION_SERVER_ADDRESS = "usermgr.jd-app.com";
    public static final int DEFAULT_REGISTRATION_SERVER_PORT = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        mStunServerEdit = (EditText)findViewById(R.id.stun_server_edit);
        mRegistrationServerEdit = (EditText)findViewById(R.id.registration_server_edit);

        Button btnReset = (Button)findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStunServerEdit.setText(DEFAULT_STUN_SERVER_ADDRESS + ":" + DEFAULT_STUN_SERVER_PORT);
                mRegistrationServerEdit.setText(DEFAULT_REGISTRATION_SERVER_ADDRESS);
            }
        });

        SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE);

        String stunServerAddress = sp.getString(SETTINGS_STUN_SERVER_ADDRESS, DEFAULT_STUN_SERVER_ADDRESS);
        int stunServerPort = sp.getInt(SETTINGS_STUN_SERVER_PORT, DEFAULT_STUN_SERVER_PORT);

        String registrationServerAddress = sp.getString(SETTINGS_REGISTRATION_SERVER_ADDRESS, DEFAULT_REGISTRATION_SERVER_ADDRESS);
        int registrationServerPort = sp.getInt(SETTINGS_REGISTRATION_SERVER_PORT, DEFAULT_REGISTRATION_SERVER_PORT);

        mStunServerEdit.setText(stunServerAddress + ":" + stunServerPort);

        if (registrationServerPort == -1) {
            mRegistrationServerEdit.setText(registrationServerAddress);
        } else {
            mRegistrationServerEdit.setText(registrationServerAddress + ":" + registrationServerPort);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String[] mStunServer = mStunServerEdit.getText().toString().split(":");
        editor.putString(SETTINGS_STUN_SERVER_ADDRESS, mStunServer[0]);
        editor.putInt(SETTINGS_STUN_SERVER_PORT, Integer.valueOf(mStunServer[1]));

        String[] mRegistrationServer = mRegistrationServerEdit.getText().toString().split(":");
        editor.putString(SETTINGS_REGISTRATION_SERVER_ADDRESS, mRegistrationServer[0]);
        if (mRegistrationServer.length > 1) {
            editor.putInt(SETTINGS_REGISTRATION_SERVER_PORT, Integer.valueOf(mRegistrationServer[1]));
        } else {
            editor.putInt(SETTINGS_REGISTRATION_SERVER_PORT, -1);
        }

        editor.commit();
    }

    private EditText mStunServerEdit;
    private EditText mRegistrationServerEdit;
}
