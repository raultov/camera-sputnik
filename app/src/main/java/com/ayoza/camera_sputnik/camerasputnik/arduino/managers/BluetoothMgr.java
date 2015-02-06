package com.ayoza.camera_sputnik.camerasputnik.arduino.managers;

/**
 * Created by raul on 18/01/15.
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.TextView;

import com.ayoza.camera_sputnik.camerasputnik.storage.entities.BDeviceSputnik;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * This class is responsible for creating and managing connections between Arduino and Android
 *
 */
public final class BluetoothMgr {

    private static BluetoothMgr instance = null;

    public static final int REQUEST_ENABLE_BT = 6666;
    private static final String UUID_HC06 = "00001101-0000-1000-8000-00805F9B34FB";

    private final BroadcastReceiver mReceiver;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket bs = null;
    private Boolean isBluetoothEnabled = null;
    private ConfigurationMgr configurationMgr;
    private static Context context;
    private Boolean connected = false;
    private TextView bluetoothStatus;

    private BluetoothMgr() {

        configurationMgr = ConfigurationMgr.getInstance(context);

        final BDeviceSputnik bDeviceSputnik = configurationMgr.getPairedBluetoothDevice();

        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BDeviceSputnik object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView

                    try {
                        if (bDeviceSputnik != null) {
                            String name = bDeviceSputnik.getName();

                            if (device.getName().equals(name)) {
                                mBluetoothAdapter.cancelDiscovery();
                                bs = device.createRfcommSocketToServiceRecord(UUID.fromString(UUID_HC06));
                                bs.connect();
                                connected = true;

                                OutputStream os = bs.getOutputStream();
                                os.write("Hola Arduino!".getBytes());
                            }
                        }
                    } catch (IOException e) {
                        try {
                            bs.close();
                        } catch (IOException closeException) { }
                        e.printStackTrace();
                        return;
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                    Log.d(BluetoothMgr.class.getSimpleName(), "El Discovery terminó");
                    
                }
            }
        };

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            throw new RuntimeException("No device bluetooth was found");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            isBluetoothEnabled = false;
        } else {
            isBluetoothEnabled = true;
        }

        // FIXME creo que aquí debería de poner un if con isBluetoothEnabled para no lanzar
        // startDiscovery cuando el Bluetooth no está habilitado
        boolean ret = mBluetoothAdapter.startDiscovery();
        if (ret == true) {
            Log.d(BluetoothMgr.class.getSimpleName(), "El Discovery comenzó");
        } else {
            Log.d(BluetoothMgr.class.getSimpleName(), "El Discovery no pudo comenzar");
        }
    }

    public Boolean getConnected() {
        return connected;
    }

    public static BluetoothMgr getInstance(Context context) {
        BluetoothMgr.context = context;
        
        if (instance == null) {
            instance = new BluetoothMgr();
        }

        return instance;
    }

    public Boolean isBluetoothEnabled() {
        return isBluetoothEnabled;
    }

    public void registerReceiver(Activity activity) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(mReceiver, filter);
    }

    public void unRegisterReceiver(Activity activity) {
        activity.unregisterReceiver(mReceiver);
    }

    public void showBluetoothTurnOnRequest(Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    public boolean startDiscovery() {
        return mBluetoothAdapter.startDiscovery();
    }

    public void close() {
        if (bs != null) {
            try {
                bs.close();
            } catch (IOException e) {}
        }
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
