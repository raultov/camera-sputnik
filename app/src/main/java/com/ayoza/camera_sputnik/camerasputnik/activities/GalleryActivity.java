package com.ayoza.camera_sputnik.camerasputnik.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Gallery;
import android.widget.ImageView;

import com.ayoza.camera_sputnik.camerasputnik.R;

import java.util.List;

/**
 * Created by raultov on 26/04/15.
 */
public class GalleryActivity extends Activity {

    //variable for selection intent
    private final int PICKER = 1;
    //variable to store the currently selected image
    private int currentPic = 0;
    //gallery object
    private ViewPager picGallery;
    //image view for larger display
    private ImageView picView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        //get the large image view
        picView = (ImageView) findViewById(R.id.picture);

        //get the gallery view
        picGallery = (ViewPager) findViewById(R.id.gallery);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
