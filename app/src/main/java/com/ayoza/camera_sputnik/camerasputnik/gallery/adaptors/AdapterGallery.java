package com.ayoza.camera_sputnik.camerasputnik.gallery.adaptors;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.ayoza.camera_sputnik.camerasputnik.gallery.entities.GalleryItem;

import java.util.ArrayList;

/**
 * Created by raultov on 26/04/15.
 */
public class AdapterGallery extends PagerAdapter {

    private Activity ctx;
    private ArrayList<GalleryItem> dataList;

    public AdapterGallery(Activity act, ArrayList<GalleryItem> list) {
        ctx = act;
        dataList = list;
    }
    
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return false;
    }
}
