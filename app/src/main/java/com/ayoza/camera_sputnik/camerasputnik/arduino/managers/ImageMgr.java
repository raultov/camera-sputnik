package com.ayoza.camera_sputnik.camerasputnik.arduino.managers;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.ayoza.camera_sputnik.camerasputnik.exceptions.ImageException;

import java.io.File;
import java.io.IOException;

/**
 * This manager handles with images and storage
 * Created by raultov on 15/03/15.
 */
public class ImageMgr {
    private static final String DIRECTORY_IMAGES = "CAMERA_SPUTNIK_IMAGES";
    private static final long SAFE_FREE_SPACE_AMOUNT = 10000L; // 10000 bytes
    
    private ConfigurationMgr configurationMgr = null;
    private static ImageMgr instance;
    private File currentImage = null;
    
    private ImageMgr(Activity activity) {
        configurationMgr = ConfigurationMgr.getInstance(activity);
    }
    
    public static ImageMgr getInstance(Activity activity) {
        if (instance == null) {
            instance = new ImageMgr(activity);
        }
        
        return instance;
    }
    
    public void startNewImage(long lengthFile) throws ImageException {
        if (currentImage != null) {
            throw new ImageException(ImageException.ANOTHER_IMAGE_IS_BEEING_PROCESSED);
        }
        
        if (!isExternalStorageWritable()) {
            throw new ImageException(ImageException.EXTERNAL_STORAGE_NOT_AVAILABLE);
        }

        File directory = getAlbumStorageDir();
        long freeSpace = directory.getFreeSpace();
        if ((freeSpace - lengthFile) < SAFE_FREE_SPACE_AMOUNT) {
            throw  new ImageException(ImageException.NOT_ENOUGH_FREE_SPACE);
        }

        String lastImageNameDownloaded = configurationMgr.getLastImageNameDownloaded();
        // TODO concatenate date to image file name
        currentImage = new File(directory, "");


    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        
        return false;
    }

    private File getAlbumStorageDir() throws ImageException {
        // Get the directory for the user's public pictures directory. 
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), DIRECTORY_IMAGES);
        
        if (!file.mkdirs() && !file.isDirectory()) {
            throw new ImageException(ImageException.DIRECTORY_COULD_NOT_BE_CREATED);
        }
        
        return file;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        throw new CloneNotSupportedException();
    }
}
