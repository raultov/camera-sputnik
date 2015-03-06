package com.ayoza.camera_sputnik.camerasputnik.activities;

import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ayoza.camera_sputnik.camerasputnik.R;
import com.ayoza.camera_sputnik.camerasputnik.arduino.managers.BluetoothMgr;

import android.os.Handler;


public class MainActivity extends ActionBarActivity {

    public final static int CONNECTION_STATUS_MSG = 1;

    public static final int SCAN_DEVICES_LIST = 0;

    private BluetoothMgr bluetoothMgr;
    private TextView bluetoothStatus;
    private Button devicesScanButton;
    public static Handler mHandlerStatic = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothMgr = BluetoothMgr.getInstance(this);

        if (!bluetoothMgr.isBluetoothEnabled()) {
            bluetoothMgr.showBluetoothTurnOnRequest(this);
        }

        bluetoothStatus = (TextView) findViewById(R.id.bluetoothStatus);
        devicesScanButton = (Button) findViewById(R.id.scanDevicesButtonId);

        bluetoothMgr.registerReceiver(this);

        //paintComponents(bluetoothMgr.getConnected());

        mHandlerStatic = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CONNECTION_STATUS_MSG:
                        Boolean connected = (Boolean) msg.obj;

                        paintComponents(connected);
                        break;
                }
            }
        };
    }
    
    private void paintComponents(boolean connected) {
        if (connected == false) {
            bluetoothStatus.setBackgroundResource(R.drawable.rounded_corner_red);
            bluetoothStatus.setPadding(20, 20, 20, 20);
            bluetoothStatus.setText(getResources().getString(R.string.desconectado_str_es));

            devicesScanButton.setEnabled(true);
            devicesScanButton.setClickable(true);
            //devicesScanButton.setBackgroundColor(Color.argb(255,10,17,255));
            devicesScanButton.setBackgroundColor(getResources().getColor(R.color.list_devices_activated));
            devicesScanButton.setTextColor(getResources().getColor(R.color.list_devices_text_activated));
            //FF0A11FF
        } else {
            bluetoothStatus.setBackgroundResource(R.drawable.rounded_corner_green);
            bluetoothStatus.setPadding(20, 20, 20, 20);
            bluetoothStatus.setText(getResources().getString(R.string.conectado_str_es));

            devicesScanButton.setEnabled(false);
            devicesScanButton.setClickable(false);
            //devicesScanButton.setBackgroundColor(Color.argb(255,194,192,188));
            devicesScanButton.setBackgroundColor(getResources().getColor(R.color.deactivated));
            devicesScanButton.setTextColor(getResources().getColor(R.color.list_devices_text_deactivated));
            //FFC2C0BC
        }
    }

    public static Handler getmHandlerStatic() {
        return mHandlerStatic;
    }
    
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putString(KEY_TEXT_VALUE, mTextView.getText());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        bluetoothMgr.close();

        bluetoothMgr.unRegisterReceiver(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothMgr.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
                Log.d(MainActivity.class.getSimpleName(), "El usuario activó el Bluetooth");

                boolean ret = bluetoothMgr.startDiscovery();
                if (ret == true) {
                    Log.d(MainActivity.class.getSimpleName(), "Discovery started");
                    Log.d(MainActivity.class.getSimpleName(), "Starting ScanningDevicesActivity");
                    Intent intent = new Intent(this, ScanningDevicesActivity.class);
                    startActivityForResult(intent, 1);
                    Log.d(MainActivity.class.getSimpleName(), "ScanningDevicesActivity started");
                } else {
                    Log.d(MainActivity.class.getSimpleName(), "El Discovery no pudo comenzar");
                }
            }
        } else  if (requestCode == SCAN_DEVICES_LIST) {
            if (resultCode == RESULT_OK) {
                // Come back from scanned devices list

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the Scan Devices button */
    public void scanDevices(View view) {
        Log.d(MainActivity.class.getSimpleName(), "Starting Bluetooth Devices list");
        //finish();
        Intent intent = new Intent(this, BluetoothDevicesListActivity.class);
        //startActivity(intent);

        startActivityForResult(intent, SCAN_DEVICES_LIST);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
