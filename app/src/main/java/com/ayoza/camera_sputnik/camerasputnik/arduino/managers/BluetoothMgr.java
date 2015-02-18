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

import com.ayoza.camera_sputnik.camerasputnik.interfaces.OnDiscoveryFinishedListener;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.BDeviceSputnik;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class is responsible for creating and managing connections between Arduino and Android
 *
 */
public final class BluetoothMgr {

    private static BluetoothMgr instance = null;

    public static final int REQUEST_ENABLE_BT = 6666;
    private static final String UUID_HC06 = "00001101-0000-1000-8000-00805F9B34FB";
    private static final String MAGIC_NUMBER = "5FRK14U0JKMTY71";

    private final BroadcastReceiver mReceiver;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket bs = null;
    private Boolean isBluetoothEnabled = null;
    private ConfigurationMgr configurationMgr = null;
    private static Context context;
    
    private Boolean connected = false;
    private OnDiscoveryFinishedListener discoveryFinishedListener;
    private List<BluetoothDevice> listDevicesFound;

    private BluetoothMgr() {

        final BDeviceSputnik bDeviceSputnik;

        if (context != null) {
            configurationMgr = ConfigurationMgr.getInstance(context);
            bDeviceSputnik = configurationMgr.getPairedBluetoothDevice();
        } else {
            bDeviceSputnik = null;
        }
        
        listDevicesFound = new ArrayList<BluetoothDevice>();

        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BDeviceSputnik object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView

                    // Store bluetooth device to be used later
                    listDevicesFound.add(device);


                    if (bDeviceSputnik != null) {
                        String name = bDeviceSputnik.getName();

                        if (device.getName().equals(name)) {
                            mBluetoothAdapter.cancelDiscovery();
                            connect(device);
                        }
                    }

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    Log.d(BluetoothMgr.class.getSimpleName(), "Discovery finished");

                    discoveryFinishedListener.onDiscoveryFinished(connected);
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
            Log.d(BluetoothMgr.class.getSimpleName(), "Discovery started");
        } else {
            Log.d(BluetoothMgr.class.getSimpleName(), "Discovery could not start");
        }
    }
    
    public Boolean connect(BluetoothDevice device) {
        
        if (device != null) {
            try {
                bs = device.createRfcommSocketToServiceRecord(UUID.fromString(UUID_HC06));
                bs.connect();

                InputStream is = bs.getInputStream();
                byte[] buffer = null;
                is.read(buffer, 0, MAGIC_NUMBER.length());
                if (buffer.toString().equals(MAGIC_NUMBER)) {
                    connected = true;
                    // insert device in DB
                    BDeviceSputnik bDeviceSputnik = new BDeviceSputnik();
                    bDeviceSputnik.setName(device.getName());
                    bDeviceSputnik.setMac(device.getAddress());
                    bDeviceSputnik.setPaired(true);
                    configurationMgr.insertPairedBluetoothDevice(bDeviceSputnik);
                } else {
                    // TODO inform device incompatible
                    connected = false;
                    bs.close();
                    bs = null;
                }
                
            } catch (IOException e) {
                
            }
        }

        discoveryFinishedListener.onDiscoveryFinished(connected);
        
        return connected;
    }
    
    public void disconnect() {
        try {
            if (bs != null) {
                bs.close();
            }
        } catch (IOException closeException) { 
        
        } finally {
            bs = null;
        }
        
    }

    public List<BluetoothDevice> getListDevicesFound() {
        return listDevicesFound;
    }


    public Boolean getConnected() {
        return connected;
    }

    public static BluetoothMgr getInstance(Context context) {
        if (instance == null) {
            BluetoothMgr.context = context;
            instance = new BluetoothMgr();
        }

        return instance;
    }

    public static BluetoothMgr getInstance() {
        return getInstance(null);
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

    public void setDiscoveryFinishedListener(OnDiscoveryFinishedListener discoveryFinishedListener) {
        this.discoveryFinishedListener = discoveryFinishedListener;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
