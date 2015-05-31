package com.ayoza.camera_sputnik.camerasputnik.exceptions;

/**
 * This class represents exceptions occurred when dealing with tracks
 * Created by raultov on 27/05/15.
 */
public class TrackException extends Exception {

    public static final int ANOTHER_TRACK_IS_CURRENTLY_OPEN = 1;
    public static final int NO_CURRENT_TRACK_AVAILABLE = 2;

    private int error;

    public TrackException(int error) {
        super();
        this.error = error;
    }

    public int getError() {
        return error;
    }
}
