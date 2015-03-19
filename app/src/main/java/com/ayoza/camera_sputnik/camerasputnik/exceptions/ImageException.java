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
    public static final int COULD_NOT_PARSE_IMAGE_NAME = 6;
    public static final int NO_CURRENT_IMAGE_OPENED = 7;
    public static final int PROBLEM_CLOSING_IMAGE_STREAM = 8;
    public static final int PROBLEM_WRITING_IMAGE = 9;
    public static final int PROBLEM_REMOVING_IMAGE = 10;
    
    private int error;
    
    public ImageException(int error) {
        super();
        this.error = error;
    }
    
    public int getError() {
        return error;
    }
}
