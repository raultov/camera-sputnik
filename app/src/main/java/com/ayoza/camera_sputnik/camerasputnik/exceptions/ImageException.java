package com.ayoza.camera_sputnik.camerasputnik.exceptions;

/**
 * This class represents exceptions occurred when dealing with images
 * Created by raultov on 15/03/15.
 */
public class ImageException extends  Exception {
    public static final int EXTERNAL_STORAGE_NOT_AVAILABLE = 1;
    public static final int DIRECTORY_COULD_NOT_BE_CREATED = 2;
    public static final int NOT_ENOUGH_FREE_SPACE = 3;
    public static final int ANOTHER_IMAGE_IS_BEEING_PROCESSED = 4;
    public static final int IMAGE_FILE_COULD_NOT_BE_CREATED = 5;
    
    private int error;
    
    public ImageException(int error) {
        super();
        this.error = error;
    }
    
    public int getError() {
        return error;
    }
}
