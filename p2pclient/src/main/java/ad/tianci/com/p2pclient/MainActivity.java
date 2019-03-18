package ad.tianci.com.p2pclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pixmob.httpclient.HttpClient;
import org.pixmob.httpclient.HttpClientException;
import org.pixmob.httpclient.HttpResponse;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import de.javawi.jstun.attribute.MappedAddress;
import de.javawi.jstun.util.UtilityException;

public class MainActivity extends Activity implements StunClient.StunClientListener {
    public static final String TAG = "MainActivity";

    private static final int MSG_REMOTE_DEVICE_REQUEST = 1;
    private static final int MSG_HOLE_PUNCHING_SUCCEED = 2;
    private static final int MSG_MESSAGE_RECEIVED = 3;
    private static final int MSG_REGISTRATION_SUCCEED = 4;

    private static final int OPTION_MENU_ITEM_REGISTER_DEVICE = Menu.FIRST;
    private static final int OPTION_MENU_ITEM_UPDATE_DEVICE_LIST = Menu.FIRST + 1;
    private static final int OPTION_MENU_ITEM_SETTINGS = Menu.FIRST + 2;


    private static final int CONTEXT_MENU_ITEM_HOLE_PUNCHING = Menu.FIRST;
    private static final int CONTEXT_MENU_ITEM_SEND_MSG = Menu.FIRST + 1;

    private TextView tvDeviceInfo;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tvDeviceInfo = findViewById(R.id.device_info);
        mDeviceListView = (ListView) findViewById(R.id.device_list);
        mDeviceListView.setOnCreateContextMenuListener(this);

        StunClient.getInstance().setStunClientListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences(SettingsActivity.SHARED_PREFERENCE_NAME, MODE_PRIVATE);

        mStunServerAddress = sp.getString(SettingsActivity.SETTINGS_STUN_SERVER_ADDRESS, SettingsActivity.DEFAULT_STUN_SERVER_ADDRESS);
        mStunServerPort = sp.getInt(SettingsActivity.SETTINGS_STUN_SERVER_PORT, SettingsActivity.DEFAULT_STUN_SERVER_PORT);

        int registrationServerPort = sp.getInt(SettingsActivity.SETTINGS_REGISTRATION_SERVER_PORT, SettingsActivity.DEFAULT_REGISTRATION_SERVER_PORT);
        if (registrationServerPort == -1) {
            mRegistrationServer = "http://" + sp.getString(SettingsActivity.SETTINGS_REGISTRATION_SERVER_ADDRESS, SettingsActivity.DEFAULT_REGISTRATION_SERVER_ADDRESS);
        } else {
            mRegistrationServer = "http://" + sp.getString(SettingsActivity.SETTINGS_REGISTRATION_SERVER_ADDRESS, SettingsActivity.DEFAULT_REGISTRATION_SERVER_ADDRESS) + ":" + registrationServerPort;
        }

        Log.d("test", "mStunServerAddress:" + mStunServerAddress);
        Log.d("test", "mStunServerPort:" + mStunServerPort);
        Log.d("test", "mRegistrationServer:" + mRegistrationServer);
        Log.d("test", "registrationServerPort:" + registrationServerPort);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StunClient.getInstance().close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, OPTION_MENU_ITEM_REGISTER_DEVICE, 0, "Register device");
        menu.add(0, OPTION_MENU_ITEM_UPDATE_DEVICE_LIST, 0, "Update device list");
        menu.add(0, OPTION_MENU_ITEM_SETTINGS, 0, "Settings");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case OPTION_MENU_ITEM_REGISTER_DEVICE:
                new GetMappedAddressTask().execute();
                break;
            case OPTION_MENU_ITEM_UPDATE_DEVICE_LIST:
                new GetDeviceListTask().execute(mDevice);
                break;
            case OPTION_MENU_ITEM_SETTINGS:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return true;
    }

    public void clickMenu(View view) {
        int id = view.getId();
        if (id == R.id.menu1) {
            new GetMappedAddressTask().execute();
        } else if (id == R.id.menu2) {
            new GetDeviceListTask().execute(mDevice);
        } else if (id == R.id.menu3) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, CONTEXT_MENU_ITEM_HOLE_PUNCHING, 0, "Hole punching");
        menu.add(0, CONTEXT_MENU_ITEM_SEND_MSG, 0, "Send test message");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Device device = ((Device) mDeviceListView.getAdapter().getItem(info.position));

        switch (item.getItemId()) {
            case CONTEXT_MENU_ITEM_HOLE_PUNCHING:
                new ConnectToRemoteDeviceTask().execute(device);
                break;
            case CONTEXT_MENU_ITEM_SEND_MSG:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Input the message");
                final EditText editText = (EditText) LayoutInflater.from(this).inflate(R.layout.input, null);
                builder.setView(editText);
                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread() {
                            @Override
                            public void run() {
                                StunClient.getInstance().testP2P(device, editText.getText().toString());
                            }
                        }.start();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.create().show();
                break;
        }
        return true;
    }

    @Override
    public void onMappedAddressReceived(final MappedAddress mappedAddress) {
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            tvDeviceInfo.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        tvDeviceInfo.setText("stun 服务器返回本设备公网地址:"
                                + mappedAddress.getAddress().getInetAddress().getHostAddress()
                                + ":" + mappedAddress.getPort());
                    } catch (UtilityException e) {
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
            });

            Device device = new Device(androidId, mappedAddress.getAddress().getInetAddress().getHostAddress(), mappedAddress.getPort());
            new RegisterDeviceTask().execute(device);
        } catch (UtilityException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRemoteDeviceRequestReceived(MappedAddress mappedAddress) {
        Message msg = new Message();
        msg.what = MSG_REMOTE_DEVICE_REQUEST;
        msg.obj = mappedAddress;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onHolePunchingSucceed() {
        mHandler.sendEmptyMessage(MSG_HOLE_PUNCHING_SUCCEED);
    }

    @Override
    public void onMessageReceived(String content) {
        Message msg = new Message();
        msg.what = MSG_MESSAGE_RECEIVED;
        msg.obj = content;
        mHandler.sendMessage(msg);
    }

    private class GetMappedAddressTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            StunClient.getInstance().requestMappedAddress(mStunServerAddress, mStunServerPort);
            return null;
        }
    }

    private class RegisterDeviceTask extends AsyncTask<Device, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Device... devices) {
            mDevice = devices[0];

            try {
                HttpClient hc = new HttpClient(MainActivity.this);
                HttpResponse response = hc.get(String.format(mRegistrationServer + "/register?name=%s&ip=%s&port=%d",
                        mDevice.name, mDevice.ip, mDevice.port)).execute();
                StringBuilder buf = new StringBuilder();
                response.read(buf);

                JSONObject result = new JSONObject(buf.toString());
                String status = result.getString("status");

                if (status.equals("success")) {
                    return true;
                }
            } catch (HttpClientException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Log.d(TAG, "registered result = " + result);

            Message msg = new Message();
            msg.what = MSG_REGISTRATION_SUCCEED;
            msg.obj = result;
            mHandler.sendMessage(msg);
            // new GetDeviceListTask().execute(mDevice);
        }
    }

    private class GetDeviceListTask extends AsyncTask<Device, Void, List<Device>> {

        @Override
        protected List<Device> doInBackground(Device... localDevices) {
            Device localDevice = localDevices[0];
            ArrayList<Device> deviceList = null;

            try {
                HttpClient hc = new HttpClient(MainActivity.this);
                HttpResponse response = hc.get(mRegistrationServer + "/get").execute();
                StringBuilder buf = new StringBuilder();
                response.read(buf);

                JSONObject result = new JSONObject(buf.toString());
                JSONArray deviceListObj = result.getJSONArray("devices");
                deviceList = new ArrayList<Device>();
                for (int i = 0; i < deviceListObj.length(); i++) {
                    JSONObject deviceObj = deviceListObj.getJSONObject(i);
                    if (!localDevice.name.equals(deviceObj.getString("name"))) {
                        deviceList.add(new Device(deviceObj.getString("name"), deviceObj.getString("ip"), Integer.valueOf(deviceObj.getString("port"))));
                    }
                }
            } catch (HttpClientException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return deviceList;
        }

        @Override
        protected void onPostExecute(List<Device> devices) {
            if (devices != null) {
                mDeviceListView.setAdapter(new ArrayAdapter<Device>(MainActivity.this, R.layout.device_list_item, R.id.device_list_item, devices));
            }
        }
    }

    private class ConnectToRemoteDeviceTask extends AsyncTask<Device, Void, Void> {

        @Override
        protected Void doInBackground(Device... devices) {
            Device remoteDevice = devices[0];
            StunClient.getInstance().connectToRemoteDevice(remoteDevice, mStunServerAddress, mStunServerPort);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REMOTE_DEVICE_REQUEST:
                    Toast.makeText(MainActivity.this, "remote device wants to communication!", Toast.LENGTH_LONG).show();
                    break;
                case MSG_HOLE_PUNCHING_SUCCEED:
                    Toast.makeText(MainActivity.this, "Hole punching succeed!", Toast.LENGTH_LONG).show();
                    break;
                case MSG_MESSAGE_RECEIVED:
                    Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case MSG_REGISTRATION_SUCCEED:
                    Toast.makeText(MainActivity.this, "Registration status : " + (Boolean) msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    Device mDevice;
    private ListView mDeviceListView;
    private String mRegistrationServer;
    private String mStunServerAddress;
    private int mStunServerPort;
}
