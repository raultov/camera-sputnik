package com.ayoza.camera_sputnik.camerasputnik.storage.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.util.Log;

import com.ayoza.camera_sputnik.camerasputnik.storage.entities.BDeviceSputnik;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.ImageSputnik;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.PointSputnik;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.TrackSputnik;
import com.ayoza.camera_sputnik.camerasputnik.storage.helpers.ConfigurationHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private String[] allColumnsImageDownloaded = {
            ConfigurationHelper.IMAGES_DOWNLOADED_ID,
            ConfigurationHelper.IMAGES_DOWNLOADED_FILENAME,
            ConfigurationHelper.IMAGES_DOWNLOADED_DATE
    };

    private String[] allColumnsTrack = {
            ConfigurationHelper.TRACK_ID,
            ConfigurationHelper.TRACK_DATE
    };

    private String[] allColumnsPoint = {
            ConfigurationHelper.POINT_ID,
            ConfigurationHelper.POINT_DATE,
            ConfigurationHelper.POINT_IMAGES_DOWNLOADED_ID,
            ConfigurationHelper.POINT_LATITUDE,
            ConfigurationHelper.POINT_LONGITUDE,
            ConfigurationHelper.POINT_TRACK_ID
    };

    public ConfigurationDao(Context context) {
        configurationHelper = new ConfigurationHelper(context);

        /*SQLiteDatabase YourDatabaseName;
        YourDatabaseName = configurationHelper.getWritableDatabase();
        configurationHelper.onUpgrade(YourDatabaseName, 1, 2);*/
    }

    public void open() throws SQLException {
        database = configurationHelper.getWritableDatabase();
    }

    public void close() {
        configurationHelper.close();
    }

    ////////////////////***********************************************////////////////////////
    //////////////////// BLUETOOTH DEVICE SECTION  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
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

    ////////////////////***********************************************////////////////////////
    //////////////////// IMAGES DOWNLOADED SECTION  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////    

    public ImageSputnik createDownloadedImage(String filename) {
        ContentValues values = new ContentValues();
        values.put(ConfigurationHelper.IMAGES_DOWNLOADED_FILENAME, filename);
        long insertId = database.insert(ConfigurationHelper.TABLE_IMAGES_DOWNLOADED, null,
                values);

        Cursor cursor = database.query(ConfigurationHelper.TABLE_IMAGES_DOWNLOADED,
                allColumnsImageDownloaded,
                ConfigurationHelper.IMAGES_DOWNLOADED_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        ImageSputnik imageSputnik = cursorToImage(cursor);
        cursor.close();
        
        return imageSputnik;
    }
    
    public ImageSputnik getLastInsertedDownloadedImage() {
        ImageSputnik imageSputnik = null;

        Cursor cursor = null;
        try {
            cursor = database.query(ConfigurationHelper.TABLE_IMAGES_DOWNLOADED,
                    allColumnsImageDownloaded,
                    null,
                    null,
                    null,
                    null,
                    ConfigurationHelper.IMAGES_DOWNLOADED_DATE + " DESC");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            imageSputnik = cursorToImage(cursor);
        }
        cursor.close();
        
        return imageSputnik;
    }
    
    public ImageSputnik getImageById(Long imageId) {

        Cursor cursor = database.query(ConfigurationHelper.TABLE_IMAGES_DOWNLOADED,
                allColumnsImageDownloaded,
                ConfigurationHelper.IMAGES_DOWNLOADED_ID + " = " + imageId, null,
                null, null, null);

        cursor.moveToFirst();
        ImageSputnik imageSputnik = cursorToImage(cursor);
        cursor.close();

        return imageSputnik;
    }

    private ImageSputnik cursorToImage(Cursor cursor) {
        ImageSputnik imageSputnik = new ImageSputnik();
        imageSputnik.setId(cursor.getLong(0));
        imageSputnik.setFilename(cursor.getString(1));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            imageSputnik.setCreateDate(simpleDateFormat.parse(cursor.getString(2)));
        } catch (ParseException pe) {}
        
        return imageSputnik;
    }

    ////////////////////***********************************************////////////////////////
    //////////////////// TRACKS AND GPS-POINTS SECTION  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////// TRACKS ///////////////////////////////////////////////////
    
    public TrackSputnik createTrack() {
        ContentValues values = new ContentValues();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        values.put(ConfigurationHelper.TRACK_DATE, simpleDateFormat.format(new Date()));
        long insertId = database.insert(ConfigurationHelper.TABLE_TRACK, null, values);
        Cursor cursor = database.query(ConfigurationHelper.TABLE_TRACK,
                allColumnsTrack,
                ConfigurationHelper.TRACK_ID + " = " + insertId, null,
                null, null, null);

        cursor.moveToFirst();
        TrackSputnik trackSputnik = cursorToTrack(cursor);
        cursor.close();
        
        return trackSputnik;
    }
    
    public TrackSputnik getTrackById(Long trackId) {
        Cursor cursor = database.query(ConfigurationHelper.TABLE_TRACK,
                allColumnsTrack,
                ConfigurationHelper.TRACK_ID + " = " + trackId, null,
                null, null, null);

        cursor.moveToFirst();
        TrackSputnik trackSputnik = cursorToTrack(cursor);
        cursor.close();

        return trackSputnik;
    }

    public List<ImageSputnik> getImagesFromTrack(Long trackId) {

        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ");
        query.append("image_downloaded i ");
        query.append("INNER JOIN point p ON i._id = p.id_image ");
        query.append("WHERE p.id_track = ? ORDER BY i.created_date ASC");

        Cursor cursor = database.rawQuery(query.toString(), new String[]{String.valueOf(trackId)});

        List<ImageSputnik> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursorToImage(cursor));
            cursor.moveToNext();
        }

        return list;
    }

    private TrackSputnik cursorToTrack(Cursor cursor) {
        TrackSputnik trackSputnik = new TrackSputnik();
        trackSputnik.setIdTrackSputnik(cursor.getLong(0));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            trackSputnik.setDate(simpleDateFormat.parse(cursor.getString(1)));
        } catch (ParseException pe) {}

        return trackSputnik;
    }

    ////////////////////////////////// POINTS /////////////////////////////////////////////////
    
    public PointSputnik createPoint(Long trackId, Long imageId,
                                        Double latitude, Double longitude) {
        ContentValues values = new ContentValues();
        values.put(ConfigurationHelper.POINT_TRACK_ID, trackId);
        values.put(ConfigurationHelper.POINT_IMAGES_DOWNLOADED_ID, imageId);
        values.put(ConfigurationHelper.POINT_LATITUDE, latitude);
        values.put(ConfigurationHelper.POINT_LONGITUDE, longitude);
        
        long insertId = database.insert(ConfigurationHelper.TABLE_POINT, null,
                values);

        Cursor cursor = database.query(ConfigurationHelper.TABLE_POINT,
                allColumnsPoint,
                ConfigurationHelper.POINT_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        PointSputnik pointSputnik = cursorToPoint(cursor);
        cursor.close();

        return pointSputnik;
    }

    public List<PointSputnik> getAllPointsFromTrack(Long trackId) {
        List<PointSputnik> list = new ArrayList<>();

        Cursor cursor = database.query(ConfigurationHelper.TABLE_POINT,
                allColumnsPoint,
                ConfigurationHelper.TRACK_ID + " = " + trackId,
                null,
                null,
                null,
                ConfigurationHelper.POINT_DATE + " ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursorToPoint(cursor));
            cursor.moveToNext();
        }

        return list;
    }
    
    private PointSputnik cursorToPoint(Cursor cursor) {
        PointSputnik pointSputnik = new PointSputnik();
        pointSputnik.setIdPointSputnik(cursor.getLong(0));
        pointSputnik.setLatitude(cursor.getDouble(1));
        pointSputnik.setLongitude(cursor.getDouble(2));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            pointSputnik.setDate(simpleDateFormat.parse(cursor.getString(3)));
        } catch (ParseException pe) {}
        
        Long imageId = cursor.getLong(4);
        ImageSputnik imageSputnik = new ImageSputnik();
        imageSputnik.setId(imageId);
        pointSputnik.setImageSputnik(imageSputnik);
        
        Long trackId = cursor.getLong(5);
        TrackSputnik trackSputnik = new TrackSputnik();
        trackSputnik.setIdTrackSputnik(trackId);
        pointSputnik.setTrackSputnik(trackSputnik);
        
        return pointSputnik;
    }
    
    
}











































