package com.ayoza.camera_sputnik.camerasputnik.arduino.managers;

import android.app.Activity;
import android.os.Environment;

import com.ayoza.camera_sputnik.camerasputnik.exceptions.ImageException;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.ImageSputnik;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private String currentFilename = "";
    private FileOutputStream currentOutputStream = null;
    
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

        ImageSputnik lastImageNameDownloaded = configurationMgr.getLastImageNameDownloaded();
        try {
            int startDot = lastImageNameDownloaded.getFilename().indexOf('.');
            String lastNumberStr = lastImageNameDownloaded.getFilename().substring(startDot);
            Integer lastNumber = Integer.valueOf(lastNumberStr);
            lastNumber++;
            currentFilename = lastNumber.toString() + ".jpg";
        } catch (Exception e) {
            throw new ImageException(ImageException.COULD_NOT_PARSE_IMAGE_NAME);
        }
        
        currentImage = new File(directory, currentFilename);

        try {
            currentOutputStream = new FileOutputStream(currentImage);
        } catch (FileNotFoundException e) {
            throw new ImageException(ImageException.IMAGE_FILE_COULD_NOT_BE_CREATED);
        }
    }
    
    public void deleteCurrentImage() throws ImageException {
        if (currentOutputStream != null) {
            try {
                currentOutputStream.close();
            } catch (IOException e) {
                throw new ImageException(ImageException.PROBLEM_CLOSING_IMAGE_STREAM);
            }
        }

        if (currentImage == null) {
            throw new ImageException(ImageException.NO_CURRENT_IMAGE_OPENED);
        }

        currentOutputStream = null;
        
        if (!currentImage.delete()) {
            currentImage = null;
            throw new ImageException(ImageException.PROBLEM_REMOVING_IMAGE);
        }

        currentImage = null;
    }
    
    public void storeBytes(byte []buffer, int length) throws ImageException {
        if (currentImage == null || currentOutputStream == null) {
            throw new RuntimeException("There is no image opened",
                    new ImageException(ImageException.NO_CURRENT_IMAGE_OPENED));
        }

        try {
            currentOutputStream.write(buffer, 0, length);
        } catch (IOException ioe) {
            throw new ImageException(ImageException.PROBLEM_WRITING_IMAGE);
        }
    }
    
    public void closeCurrentImage() throws ImageException {
        if (currentOutputStream != null) {
            try {
                currentOutputStream.close();
            } catch (IOException e) {
                throw new ImageException(ImageException.PROBLEM_CLOSING_IMAGE_STREAM);
            }
        }

        if (currentImage == null) {
            throw new ImageException(ImageException.NO_CURRENT_IMAGE_OPENED);
        }

        currentImage = null;
        currentOutputStream = null;
        
        // TODO insert image data in DB
        ImageSputnik imageSputnik = new ImageSputnik();
        imageSputnik.setFilename(currentFilename);
        configurationMgr.insertImageSputnik(imageSputnik);
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
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
