package com.ayoza.camera_sputnik.camerasputnik.restclient;

import android.app.Activity;
import android.os.AsyncTask;

import com.ayoza.camera_sputnik.camerasputnik.exceptions.RestException;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.PointSputnikRest;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.TrackSputnik;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages communications between Android Application and Rest Server.
 * This is a Rest Client.
 *
 * Created by raul on 18/01/15.
 */
public class RestClient {

    private static volatile RestClient instance;
    private static final Object lock = new Object();

    private Activity activity;
    private volatile Boolean uploading = false;
    private List<PointSputnikRest> points;

    private RestClient(Activity activity) {
        this.activity = activity;
    }

    public static RestClient getInstance(Activity activity) {
        RestClient r = instance;

        if(r == null) {
            synchronized(lock) {    // while we were waiting for the lock, another
                r = instance;       // thread may have instantiated the object
                if(r == null) {
                    r = new RestClient(activity);
                    instance = r;
                }
            }
        }

        return r;
    }

    /**
     * This method starts sending new track to Rest Server
     *
     */
    public void startUploading(TrackSputnik trackSputnik) throws RestException {
        if (trackSputnik == null) {
            throw new RestException(RestException.ERROR_UPLOADING_NULL_TRACK,
                                    RestException.ERROR_UPLOADING_NULL_TRACK_MSG);
        }

        points = new ArrayList<>();
        uploading = true;

        // A task which uploads points is executed in background
        new UploadPointsTask().execute();
    }

    /**
     * This method stops uploading
     *
     */
    public void stopUploading() {
        points = null;
        uploading = false;
    }

    private class UploadPointsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            while (uploading) {

                try {
                    // Sleep Thread for 5000 ms to minimize system load
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // TODO upload points
                
            }

            return null;
        }
    }
}


















