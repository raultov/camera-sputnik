package com.ayoza.camera_sputnik.camerasputnik.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.ayoza.camera_sputnik.camerasputnik.R;
import com.ayoza.camera_sputnik.camerasputnik.interfaces.OnBackgroundListener;

/**
 * Created by raultov on 5/03/15.
 */
public class ScanningDevicesActivity extends Activity {

    public static final int SCANNING_ENDED = 1;
    private static final long TIMEOUT = 50000L;
    
    //A ProgressDialog object
    private ProgressDialog progressDialog;
    private Context context = null;

    private static boolean scanningEnded = false;
    private static Handler mHandlerStatic = null; 
    static {
        mHandlerStatic = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SCANNING_ENDED:
                        scanningEnded = true;
                        break;
                }
            }
        };
        
    }

    public static Handler getmHandlerStatic() {
        return mHandlerStatic;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        //Initialize a LoadViewTask object and call the execute() method
        new LoadViewTask().execute();
    }

    //To use the AsyncTask, it must be subclassed
    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {

        private boolean connected = false;

        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ScanningDevicesActivity.this, context.getString(R.string.scanning),
                    context.getString(R.string.scanning_devices), false, false);
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {
            long initial = System.currentTimeMillis();
            
            do {
                
            } while ((System.currentTimeMillis() - initial) < TIMEOUT && !scanningEnded);

            // reset variable
            scanningEnded = false;
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values) {
            //set the current progress of the progress dialog
            progressDialog.setProgress(values[0]);
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //close the progress dialog
            progressDialog.dismiss();
            finish();
        }
    }
}
