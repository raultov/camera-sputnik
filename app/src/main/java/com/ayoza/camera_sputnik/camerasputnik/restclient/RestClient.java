package com.ayoza.camera_sputnik.camerasputnik.restclient;

import com.ayoza.camera_sputnik.camerasputnik.storage.entities.TrackSputnik;

/**
 * This class manages communications between Android Application and Rest Server.
 * This is a Rest Client.
 *
 * Created by raul on 18/01/15.
 */
public class RestClient {

    private static volatile RestClient instance;
    private static final Object lock = new Object();

    private RestClient() {
    }

    public static RestClient getInstance() {
        RestClient r = instance;

        if(r == null) {
            synchronized(lock) {    // while we were waiting for the lock, another
                r = instance;       // thread may have instantiated the object
                if(r == null) {
                    r = new RestClient();
                    instance = r;
                }
            }
        }

        return r;
    }

    /**
     * This method starts sending new track to Rest Client
     *
     */
    public void startUploading(TrackSputnik trackSputnik) {

    }

}
