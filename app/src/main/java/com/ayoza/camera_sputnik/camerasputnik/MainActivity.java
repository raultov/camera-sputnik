package com.ayoza.camera_sputnik.camerasputnik;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ayoza.camera_sputnik.camerasputnik.activities.BluetoothDevicesListActivity;
import com.ayoza.camera_sputnik.camerasputnik.arduino.managers.BluetoothMgr;
import com.ayoza.camera_sputnik.camerasputnik.interfaces.OnDiscoveryFinishedListener;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.ayoza.camera_sputnik.MESSAGE";

    private BluetoothMgr bluetoothMgr;
    TextView bluetoothStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothMgr = BluetoothMgr.getInstance(this);

        if (!bluetoothMgr.isBluetoothEnabled()) {
            bluetoothMgr.showBluetoothTurnOnRequest(this);
        }

        bluetoothStatus = (TextView) findViewById(R.id.bluetoothStatus);

        bluetoothMgr.registerReceiver(this);

        bluetoothMgr.setDiscoveryFinishedListener(new OnDiscoveryFinishedListener() {
            @Override
            public void onDiscoveryFinished(Boolean connected) {
                if (connected == false) {
                    bluetoothStatus.setBackgroundResource(R.drawable.rounded_corner_red);
                    bluetoothStatus.setPadding(20, 20, 20, 20);
                    bluetoothStatus.setText(getResources().getString(R.string.desconectado_str_es));
                } else {
                    bluetoothStatus.setBackgroundResource(R.drawable.rounded_corner_green);
                    bluetoothStatus.setPadding(20, 20, 20, 20);
                    bluetoothStatus.setText(getResources().getString(R.string.conectado_str_es));
                }
            }
        });
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothMgr.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
                Log.d(MainActivity.class.getSimpleName(), "El usuario activó el Bluetooth");

                boolean ret = bluetoothMgr.startDiscovery();
                if (ret == true) {
                    Log.d(MainActivity.class.getSimpleName(), "El Discovery comenzó");
                } else {
                    Log.d(MainActivity.class.getSimpleName(), "El Discovery no pudo comenzar");
                }
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
        Log.d(MainActivity.class.getSimpleName(), "Starting Bluetooth Devices scan");

        Intent intent = new Intent(this, BluetoothDevicesListActivity.class);
        String message = "hola nueva ventanita2";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }
}
