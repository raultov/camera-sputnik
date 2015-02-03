package com.ayoza.camera_sputnik.camerasputnik.storage.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ayoza.camera_sputnik.camerasputnik.storage.entities.BluetoothDevice;
import com.ayoza.camera_sputnik.camerasputnik.storage.helpers.ConfigurationHelper;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raul on 3/02/15.
 */
public class ConfigurationDao {

    // Database fields
    private SQLiteDatabase database;
    private ConfigurationHelper configurationHelper;

    private String[] allColumnsBluetoothDevice = { ConfigurationHelper.BLUETOOTH_DEVICE_ID,
            ConfigurationHelper.BLUETOOTH_DEVICE_NAME, ConfigurationHelper.BLUETOOTH_DEVICE_MAC };

    public ConfigurationDao(Context context) {
        configurationHelper = new ConfigurationHelper(context);
    }

    public void open() throws SQLException {
        database = configurationHelper.getWritableDatabase();
    }

    public void close() {
        configurationHelper.close();
    }

    public BluetoothDevice createBluetoothDevice(String name, String mac) {
        ContentValues values = new ContentValues();
        values.put(ConfigurationHelper.BLUETOOTH_DEVICE_NAME, name);
        values.put(ConfigurationHelper.BLUETOOTH_DEVICE_MAC, mac);
        long insertId = database.insert(ConfigurationHelper.TABLE_BLUETOOTH_DEVICE, null,
                values);
        
        Cursor cursor = database.query(ConfigurationHelper.TABLE_BLUETOOTH_DEVICE,
                allColumnsBluetoothDevice, 
                ConfigurationHelper.BLUETOOTH_DEVICE_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        BluetoothDevice bluetoothDevice = cursorToBluetoothDevice(cursor);
        cursor.close();
        return bluetoothDevice;
    }

    public void deleteBluetoothDevice(BluetoothDevice bluetoothDevice) {
        long id = bluetoothDevice.getId();
        Log.d(ConfigurationDao.class.getName(), "Comment deleted with id: " + id);
        database.delete(ConfigurationHelper.TABLE_BLUETOOTH_DEVICE, ConfigurationHelper.BLUETOOTH_DEVICE_ID
                + " = " + id, null);
    }

    public List<BluetoothDevice> getAllComments() {
        List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

        Cursor cursor = database.query(ConfigurationHelper.TABLE_BLUETOOTH_DEVICE,
                allColumnsBluetoothDevice, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            BluetoothDevice bluetoothDevice = cursorToBluetoothDevice(cursor);
            devices.add(bluetoothDevice);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return devices;
    }

    private BluetoothDevice cursorToBluetoothDevice(Cursor cursor) {
        BluetoothDevice bluetoothDevice = new BluetoothDevice();
        bluetoothDevice.setId(cursor.getLong(0));
        bluetoothDevice.setName(cursor.getString(1));
        bluetoothDevice.setMac(cursor.getString(2));
        return bluetoothDevice;
    }
}
