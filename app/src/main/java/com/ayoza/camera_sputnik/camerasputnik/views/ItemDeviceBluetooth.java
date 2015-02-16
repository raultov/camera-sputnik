package com.ayoza.camera_sputnik.camerasputnik.views;

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
    
    public ItemDeviceBluetooth(Integer id, String text, Context context) {
        super(context);

        bluetoothMgr = BluetoothMgr.getInstance(context);

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(lp);
        this.setGravity(Gravity.CENTER);
        this.setId(id);
        this.setText(text);
        this.setTextSize(25.0f);
        this.setTextColor(Color.BLACK);
        this.setPadding(0, 0, 0, 15);
        this.setBackgroundColor(Color.CYAN);
        this.setClickable(true);
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                
                System.out.println("Click on device item");
            }
        });
    }
    

}
