package com.ayoza.camera_sputnik.camerasputnik.interfaces;

/**
 * Created by raul on 8/02/15.
 */
public interface OnDiscoveryFinishedListener {

    /**
     * Depending on the status connected or not the behaviour must be defined
     * in this method
     *
     * @param connected
     */
    public void onDiscoveryFinished(Boolean connected);
}
