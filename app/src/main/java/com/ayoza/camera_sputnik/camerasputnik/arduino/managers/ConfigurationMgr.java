package com.ayoza.camera_sputnik.camerasputnik.arduino.managers;

/**
 * Created by raul on 28/01/15.
 */

import android.content.Context;

import com.ayoza.camera_sputnik.camerasputnik.storage.dao.ConfigurationDao;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.BDeviceSputnik;

/**
 * This class is responsible for creating and managing settings
 *
 */
public final class ConfigurationMgr {
    
    private static ConfigurationMgr instance = null;
    private ConfigurationDao configurationDao;
    
    private ConfigurationMgr() {

    }

    public static ConfigurationMgr getInstance(Context context) {
        if (instance == null) {
            instance = new ConfigurationMgr();
            instance.configurationDao = new ConfigurationDao(context);
        }

        return instance;
    }
    
    public BDeviceSputnik getPairedBluetoothDevice() {
        configurationDao.open();
        BDeviceSputnik bDeviceSputnik = configurationDao.getFirstPairedBluetoothDevice();
        configurationDao.close();
        
        return bDeviceSputnik;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
