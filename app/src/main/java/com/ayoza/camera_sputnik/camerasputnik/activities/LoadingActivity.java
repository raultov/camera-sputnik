package com.ayoza.camera_sputnik.camerasputnik.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.ayoza.camera_sputnik.camerasputnik.R;
import com.ayoza.camera_sputnik.camerasputnik.interfaces.OnBackgroundListener;

public class LoadingActivity extends Activity {
    //A ProgressDialog object
    private ProgressDialog progressDialog;
    private Context context = null;
    
    private static OnBackgroundListener onBackgroundListenerStatic;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        //Initialize a LoadViewTask object and call the execute() method
        new LoadViewTask().execute();
    }
    
    public static void setOnBackgroundListener(OnBackgroundListener onBackgroundListener) {
        onBackgroundListenerStatic = onBackgroundListener;
    }

    //To use the AsyncTask, it must be subclassed
    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
        
        private boolean connected = false;
        
        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(LoadingActivity.this,"Loading...",
                    "Loading application View, please wait...", false, false);
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {
            
            if (onBackgroundListenerStatic != null) {
                connected = onBackgroundListenerStatic.onBackground();
            }
            
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
            //close the progress dialog
            progressDialog.dismiss();
            //initialize the View
            //setContentView(R.layout.activity_devices_list);
            //getFragmentManager().popBackStack();
            
            if (connected == false) {
                Log.d(LoadingActivity.class.getSimpleName(), "Starting Bluetooth Devices scan");
                Intent intent = new Intent(context, BluetoothDevicesListActivity.class);
                startActivity(intent);
            } else {
                Log.d(LoadingActivity.class.getSimpleName(), "Starting Main Activity");
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        }
    }
}