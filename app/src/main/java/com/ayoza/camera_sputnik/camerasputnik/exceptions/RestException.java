package com.ayoza.camera_sputnik.camerasputnik.exceptions;

/**
 * Created by raul on 30/07/15.
 */
public class RestException extends Exception {

    public static final int ERROR_UPLOADING_NULL_TRACK = 1;
    public static final String ERROR_UPLOADING_NULL_TRACK_MSG = "Track cannot be null";

    private int error;
    private String message;

    public RestException(int error, String message) {
        super();
        this.error = error;
        this.message = message;
    }

    public int getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
