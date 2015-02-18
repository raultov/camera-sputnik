package com.ayoza.camera_sputnik.camerasputnik.storage.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ayoza.camera_sputnik.camerasputnik.storage.entities.BDeviceSputnik;
import com.ayoza.camera_sputnik.camerasputnik.storage.helpers.ConfigurationHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raul on 3/02/15.
 */
public class ConfigurationDao {

    // Database fields
    private SQLiteDatabase database;
    private ConfigurationHelper configurationHelper;

    private String[] allColumnsBluetoothDevice = { 
            ConfigurationHelper.BLUETOOTH_DEVICE_ID,
            ConfigurationHelper.BLUETOOTH_DEVICE_NAME, 
            ConfigurationHelper.BLUETOOTH_DEVICE_MAC,
            ConfigurationHelper.BLUETOOTH_DEVICE_PAIRED
            };

    public ConfigurationDao(Context context) {
        configurationHelper = new ConfigurationHelper(context);
    }

    public void open() throws SQLException {
        database = configurationHelper.getWritableDatabase();
    }

    public void close() {
        configurationHelper.close();
    }

    public BDeviceSputnik createBluetoothDevice(String name, String mac, Boolean paired) {
        ContentValues values = new ContentValues();
        values.put(ConfigurationHelper.BLUETOOTH_DEVICE_NAME, name);
        values.put(ConfigurationHelper.BLUETOOTH_DEVICE_MAC, mac);
        values.put(ConfigurationHelper.BLUETOOTH_DEVICE_PAIRED, paired);
        long insertId = database.insert(ConfigurationHelper.TABLE_BLUETOOTH_DEVICE, null,
                values);
        
        Cursor cursor = database.query(ConfigurationHelper.TABLE_BLUETOOTH_DEVICE,
                allColumnsBluetoothDevice, 
                ConfigurationHelper.BLUETOOTH_DEVICE_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        BDeviceSputnik bDeviceSputnik = cursorToBluetoothDevice(cursor);
        cursor.close();
        return bDeviceSputnik;
    }

    public void deleteBluetoothDevice(BDeviceSputnik bDeviceSputnik) {
        long id = bDeviceSputnik.getId();
        Log.d(ConfigurationDao.class.getName(), "Bluetooth Device deleted with id: " + id);
        database.delete(ConfigurationHelper.TABLE_BLUETOOTH_DEVICE, ConfigurationHelper.BLUETOOTH_DEVICE_ID
                + " = " + id, null);
    }

    public List<BDeviceSputnik> getAllBluetoothDevices() {
        List<BDeviceSputnik> devices = new ArrayList<BDeviceSputnik>();

        Cursor cursor = database.query(ConfigurationHelper.TABLE_BLUETOOTH_DEVICE,
                allColumnsBluetoothDevice, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            BDeviceSputnik bDeviceSputnik = cursorToBluetoothDevice(cursor);
            devices.add(bDeviceSputnik);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return devices;
    }

    /**
     * Return the first paired BDeviceSputnik existing in Database Configuration
     *
     * @return
     */
    public BDeviceSputnik getFirstPairedBluetoothDevice() {

        BDeviceSputnik bDeviceSputnik = null;

        Cursor cursor = database.query(ConfigurationHelper.TABLE_BLUETOOTH_DEVICE,
                allColumnsBluetoothDevice, ConfigurationHelper.BLUETOOTH_DEVICE_PAIRED + "=1",
                null, null, null, null);

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            bDeviceSputnik = cursorToBluetoothDevice(cursor);
        }
        cursor.close();
        
        return bDeviceSputnik;
    }

    private BDeviceSputnik cursorToBluetoothDevice(Cursor cursor) {
        BDeviceSputnik bDeviceSputnik = new BDeviceSputnik();
        bDeviceSputnik.setId(cursor.getLong(0));
        bDeviceSputnik.setName(cursor.getString(1));
        bDeviceSputnik.setMac(cursor.getString(2));
        bDeviceSputnik.setPaired(cursor.getInt(3) == 1);
        return bDeviceSputnik;
    }
}
