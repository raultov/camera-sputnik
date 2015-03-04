package com.ayoza.camera_sputnik.camerasputnik.views;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.ayoza.camera_sputnik.camerasputnik.arduino.managers.BluetoothMgr;

import java.io.Serializable;

import static android.widget.LinearLayout.LayoutParams;

/**
 * Created by raul on 11/02/15.
 */
public class ItemDeviceBluetooth extends TextView implements Serializable {
    
    public static final long serialVersionUID = -698708377638738438L;
    
    private final BluetoothMgr bluetoothMgr;
    
    public ItemDeviceBluetooth(Integer id, final BluetoothDevice bluetoothDevice, final Activity activity) {
        super(activity);

        bluetoothMgr = BluetoothMgr.getInstance(activity);

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(lp);
        this.setGravity(Gravity.CENTER);
        this.setId(id);
        this.setText(bluetoothDevice.getName());
        this.setTextSize(25.0f);
        this.setTextColor(Color.BLACK);
        this.setPadding(0, 0, 0, 15);
        this.setBackgroundColor(Color.CYAN);
        this.setClickable(true);
        
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothMgr.connect(bluetoothDevice, activity);
            }
        });
    }
    

}
