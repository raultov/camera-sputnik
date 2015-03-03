package com.ayoza.camera_sputnik.camerasputnik.activities;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.ayoza.camera_sputnik.camerasputnik.R;
import com.ayoza.camera_sputnik.camerasputnik.arduino.managers.BluetoothMgr;
import com.ayoza.camera_sputnik.camerasputnik.views.ItemDeviceBluetooth;

import java.util.List;

/**
 * Created by raul on 23/01/15.
 */
public class BluetoothDevicesListActivity extends Activity {

    private BluetoothMgr bluetoothMgr;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_devices_list);

        bluetoothMgr = BluetoothMgr.getInstance();
        List<BluetoothDevice> devicesFound = bluetoothMgr.getListDevicesFound();
        
        // Get the message from the intent
      //  Intent intent = getIntent();
      //  String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Create the text view
     //   TextView textView = (TextView) findViewById(R.id.devicesListLabel);
     //   textView.setTextSize(20);
     //   textView.setText(message);
        
        LinearLayout m_vwJokeLayout=(LinearLayout) this.findViewById(R.id.linearDeviceList);
        int i = 0;
        for (BluetoothDevice bluetoothDevice : devicesFound) {
            ItemDeviceBluetooth itemDeviceBluetooth = new ItemDeviceBluetooth(i, bluetoothDevice, this);
            m_vwJokeLayout.addView(itemDeviceBluetooth);
            i++;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO nothing to do 
        // Instead implement a back button to load main activity and call finish(); to kill current activity
    }

}
