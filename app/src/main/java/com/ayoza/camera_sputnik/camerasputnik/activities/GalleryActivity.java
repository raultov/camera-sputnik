package com.ayoza.camera_sputnik.camerasputnik.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ayoza.camera_sputnik.camerasputnik.R;
import com.ayoza.camera_sputnik.camerasputnik.arduino.managers.TrackMgr;
import com.ayoza.camera_sputnik.camerasputnik.exceptions.TrackException;
import com.ayoza.camera_sputnik.camerasputnik.gallery.entities.PagerContainer;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.ImageSputnik;

import java.util.List;

/**
 * Created by raultov on 26/04/15.
 * This activity shows a gallery of pictures received previously through bluetooth
 */
public class GalleryActivity extends Activity {

    //gallery object
   // private ViewPager picGallery;
    //image view for larger display
    //private ImageView picView;

    private TrackMgr trackMgr;

    PagerContainer mContainer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        trackMgr = TrackMgr.getInstance(this);

        List<ImageSputnik> currentImages = null;
        try {
            currentImages = trackMgr.getAllImagesFromCurrentTrack();
        } catch (TrackException e) {
            e.printStackTrace();

        }

        // TODO continue here

        if (currentImages != null) {
            for (ImageSputnik image : currentImages) {

                System.out.println("image filename: " + image.getFilename());
            }
        }

        mContainer = (PagerContainer) findViewById(R.id.pager_container);

        ViewPager pager = mContainer.getViewPager();
        PagerAdapter adapter = new GalleryAdapter();
        pager.setAdapter(adapter);
        //Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(adapter.getCount());
        //A little space between pages
        pager.setPageMargin(15);

        //If hardware acceleration is enabled, you should also remove
        // clipping on the pager for its children.
        pager.setClipChildren(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                System.out.println("nothing");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //Nothing special about this adapter, just throwing up colored views for demo
    private class GalleryAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView view = new TextView(GalleryActivity.this);
            view.setText("Item "+position);
            view.setGravity(Gravity.CENTER);
            view.setBackgroundColor(Color.argb(255, position * 50, position * 10, position * 50));

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }
}
