package com.ayoza.camera_sputnik.camerasputnik.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.ayoza.camera_sputnik.camerasputnik.arduino.managers.BluetoothMgr;

/**
 * This class downloads images on background
 * Created by raultov on 14/03/15.* 
 */
public class DownloadingImageActivity extends AsyncTask<Void, Integer, Boolean> {

    private BluetoothMgr bluetoothMgr;
    
    public DownloadingImageActivity(Activity activity) {
        bluetoothMgr = BluetoothMgr.getInstance(activity);
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        if (bluetoothMgr.getConnected()) {
            bluetoothMgr.receiveImage(ProgressBar progressBar);
        }

        for(int i = 0; i < 10; i++) {
            //tareaLarga();

            //publishProgress(i*10);

            if(isCancelled())
                return false;
        }
        
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //int progreso = values[0].intValue();

        //pDialog.setProgress(progreso);
    }

    @Override
    protected void onPreExecute() {

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
            //Toast.makeText(MainActivity.class, "Tarea finalizada!", Toast.LENGTH_SHORT).show();
            System.out.println("hola");
        }
    }

    @Override
    protected void onCancelled() {
        //Toast.makeText(MainHilos.this, "Tarea cancelada!", Toast.LENGTH_SHORT).show();
    }
    
}
