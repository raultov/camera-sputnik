package com.ayoza.camera_sputnik.camerasputnik.activities;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ayoza.camera_sputnik.camerasputnik.R;
import com.ayoza.camera_sputnik.camerasputnik.arduino.managers.BluetoothMgr;
import com.ayoza.camera_sputnik.camerasputnik.views.ItemDeviceBluetooth;

import android.widget.LinearLayout.LayoutParams;

import java.util.List;

/**
 * Created by raul on 23/01/15.
 */
public class BluetoothDevicesListActivity extends Activity {

    private BluetoothMgr bluetoothMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);

        bluetoothMgr = BluetoothMgr.getInstance();
        List<BluetoothDevice> devicesFound = bluetoothMgr.getListDevicesFound();
        
        // Get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Create the text view
        TextView textView = (TextView) findViewById(R.id.devicesListLabel);
        textView.setTextSize(20);
        textView.setText(message);
        
        LinearLayout m_vwJokeLayout=(LinearLayout) this.findViewById(R.id.linearDeviceList);
        int i = 0;
        for (BluetoothDevice bluetoothDevice : devicesFound) {
            ItemDeviceBluetooth itemDeviceBluetooth = new ItemDeviceBluetooth(i, bluetoothDevice.getName(), this);
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


    /**
     * A placeholder fragment containing a simple view.
     */
    /*
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_main, container, false);
            return rootView;
        }
    }
    */
}
