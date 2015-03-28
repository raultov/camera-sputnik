package com.ayoza.camera_sputnik.camerasputnik.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ayoza.camera_sputnik.camerasputnik.arduino.managers.BluetoothMgr;

/**
 * This class downloads images on background
 * Created by raultov on 14/03/15.* 
 */
public class DownloadingImageActivity extends AsyncTask<Void, Integer, Boolean> {

    private BluetoothMgr bluetoothMgr = null;
    private Activity activity;
    private Integer publishedProgress = null;

    private Boolean stopDownloading = false;
    
    public DownloadingImageActivity(Activity activity) {
        this.activity = activity;
        bluetoothMgr = BluetoothMgr.getInstance(activity);
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        boolean ret = false;
        if (bluetoothMgr != null && bluetoothMgr.getConnected()) {
            ret = bluetoothMgr.receiveImage(this);
        }
        
        do {
            if (bluetoothMgr != null) {
                bluetoothMgr.connect();
                ret = bluetoothMgr.receiveImage(this);

                // restart progress bar to zero position
                publishedProgress = 0;
                Intent intent = new Intent(MainActivity.DOWNLOAD_IMAGE_PROGRESS_EVENT);
                intent.putExtra("progress", 0);
                LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
            } else {
                setStopDownloading(true);
            }
        } while (!stopDownloading);

        return ret;
    }

    public final void publishDownloadProgress(Integer value, Integer length) {
        int currentProgress = (value * 100) / length;
        if (currentProgress > publishedProgress) {
            publishedProgress = currentProgress;
            publishProgress(publishedProgress);
        }
    }

    public void setStopDownloading(Boolean stopDownloading) {
        this.stopDownloading = stopDownloading;
    }
    
    @Override
    protected void onProgressUpdate(Integer... values) {
        Intent intent = new Intent(MainActivity.DOWNLOAD_IMAGE_PROGRESS_EVENT);
        intent.putExtra("progress", values[0].intValue());
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    @Override
    protected void onPreExecute() {
        publishedProgress = 0;
        Intent intent = new Intent(MainActivity.DOWNLOAD_IMAGE_PROGRESS_EVENT);
        intent.putExtra("progress", publishedProgress);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
/*
        pDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                MiTareaAsincronaDialog.this.cancel(true);
            }
        });

        pDialog.setProgress(0);
        pDialog.show();*/
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            //pDialog.dismiss();
            //Toast.makeText(MainActivity.class, "Task finished!", Toast.LENGTH_SHORT).show();
            Log.d(this.getClass().getCanonicalName(), "Image downloaded successfully");
        } else {
            Log.d(this.getClass().getCanonicalName(), "There was a problem downloading image");
        }
    }

    @Override
    protected void onCancelled() {
        //Toast.makeText(MainHilos.this, "Tarea cancelada!", Toast.LENGTH_SHORT).show();
    }
    
}
