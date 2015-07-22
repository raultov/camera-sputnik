package com.ayoza.camera_sputnik.camerasputnik.arduino.managers;

import android.content.Context;

import com.ayoza.camera_sputnik.camerasputnik.storage.dao.ConfigurationDao;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.BDeviceSputnik;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.ImageSputnik;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.PointSputnik;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.TrackSputnik;

import java.util.Date;
import java.util.List;

/**
 * This class is responsible for creating and managing settings
 *
 * Created by raul on 28/01/15
 */
public final class ConfigurationMgr {
    
    private static ConfigurationMgr instance = null;
    private ConfigurationDao configurationDao;
    
    private ConfigurationMgr() {
    }

    public static synchronized ConfigurationMgr getInstance(Context context) {
        if (instance == null) {
            instance = new ConfigurationMgr();
            instance.configurationDao = new ConfigurationDao(context);
        }

        return instance;
    }
    
    /*
    
     _     _            _              _   _     
    | |__ | |_   _  ___| |_ ___   ___ | |_| |__  
    | '_ \| | | | |/ _ \ __/ _ \ / _ \| __| '_ \ 
    | |_) | | |_| |  __/ || (_) | (_) | |_| | | |
    |_.__/|_|\__,_|\___|\__\___/ \___/ \__|_| |_|
                                                 
         _            _               
      __| | _____   _(_) ___ ___  ___ 
     / _` |/ _ \ \ / / |/ __/ _ \/ __|
    | (_| |  __/\ V /| | (_|  __/\__ \
     \__,_|\___| \_/ |_|\___\___||___/

    
     */
    
    public BDeviceSputnik getPairedBluetoothDevice() {
        configurationDao.open();
        BDeviceSputnik bDeviceSputnik = configurationDao.getFirstPairedBluetoothDevice();
        configurationDao.close();
        
        return bDeviceSputnik;
    }
    
    public void insertPairedBluetoothDevice(BDeviceSputnik bDeviceSputnik) {
        configurationDao.open();
        configurationDao.createBluetoothDevice(bDeviceSputnik.getName(), bDeviceSputnik.getMac(), bDeviceSputnik.getPaired());
        configurationDao.close();
    }

    /**
      _
     (_)_ __ ___   __ _  __ _  ___  ___
     | | '_ ` _ \ / _` |/ _` |/ _ \/ __|
     | | | | | | | (_| | (_| |  __/\__ \
     |_|_| |_| |_|\__,_|\__, |\___||___/
     |___/

     */
    
    
    public ImageSputnik getLastImageNameDownloaded() {
        configurationDao.open();
        ImageSputnik imageSputnik = configurationDao.getLastInsertedDownloadedImage();
        configurationDao.close();
        
        return imageSputnik;
    }
    
    public Long insertImageSputnik(ImageSputnik imageSputnik) {
        configurationDao.open();
        ImageSputnik imageSputnik1 = configurationDao.createDownloadedImage(imageSputnik.getFilename());
        configurationDao.close();

        return imageSputnik1.getId();
    }

    public List<ImageSputnik> getAllImagesFromTrack(Long idTrack) {
        configurationDao.open();
        List<ImageSputnik> images = configurationDao.getImagesFromTrack(idTrack);
        configurationDao.close();

        return images;
    }

    public List<ImageSputnik> getAllImagesFromLastTrack() {
        configurationDao.open();
        List<ImageSputnik> images = configurationDao.getImagesFromLastTrack();
        configurationDao.close();

        return images;
    }

    public List<TrackSputnik> getAllTracksFromDay(Date day) {
        configurationDao.open();
        List<TrackSputnik> tracks = configurationDao.getAllTracksFromDay(day);
        configurationDao.close();

        return tracks;
    }
    
    /*
     _                  _        
    | |_ _ __ __ _  ___| | _____ 
    | __| '__/ _` |/ __| |/ / __|
    | |_| | | (_| | (__|   <\__ \
     \__|_|  \__,_|\___|_|\_\___/                               
    
     */
    
    public TrackSputnik createTrackSputnik() {
        configurationDao.open();
        TrackSputnik trackSputnik = configurationDao.createTrack();
        configurationDao.close();
        
        return trackSputnik;
    }
    
    /*
                 _       _       
     _ __   ___ (_)_ __ | |_ ___ 
    | '_ \ / _ \| | '_ \| __/ __|
    | |_) | (_) | | | | | |_\__ \
    | .__/ \___/|_|_| |_|\__|___/
    |_|       
      
     */
    
    public PointSputnik createPointSputnik(Long trackId, Long imageId,
                                   Double latitude, Double longitude) {
        configurationDao.open();
        PointSputnik pointSputnik = configurationDao.createPoint(trackId, imageId,latitude, longitude);
        configurationDao.close();
        
        return pointSputnik;
    }
    

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
