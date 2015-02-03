package com.ayoza.camera_sputnik.camerasputnik.arduino.managers;

/**
 * Created by raul on 28/01/15.
 */

import android.content.Context;

import com.ayoza.camera_sputnik.camerasputnik.storage.dao.ConfigurationDao;

/**
 * This class is responsible for creating and managing settings
 *
 */
public class ConfigurationMgr {
    
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
    
    public ConfigurationDao getConfigurationDao() {
        return configurationDao;
    }
}
