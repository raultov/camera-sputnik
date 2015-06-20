package com.ayoza.camera_sputnik.camerasputnik.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ayoza.camera_sputnik.camerasputnik.R;
import com.ayoza.camera_sputnik.camerasputnik.arduino.managers.ImageMgr;
import com.ayoza.camera_sputnik.camerasputnik.arduino.managers.TrackMgr;
import com.ayoza.camera_sputnik.camerasputnik.exceptions.ImageException;
import com.ayoza.camera_sputnik.camerasputnik.exceptions.TrackException;
import com.ayoza.camera_sputnik.camerasputnik.gallery.entities.ImageText;
import com.ayoza.camera_sputnik.camerasputnik.gallery.entities.PagerContainer;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.ImageSputnik;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by raultov on 26/04/15.
 * This activity shows a gallery of pictures received previously through bluetooth
 */
public class GalleryActivity extends Activity {

    private ImageMgr imageMgr;
    private List<ImageSputnik> currentImages = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        TrackMgr trackMgr = TrackMgr.getInstance(this);

        imageMgr = ImageMgr.getInstance(this);

        try {
            // FIXME
            //currentImages = trackMgr.getAllImagesFromCurrentTrack();
            currentImages = trackMgr.getAllImagesFromLastTrack();
        } catch (TrackException e) {
            e.printStackTrace();
        }

        if (currentImages != null) {
            for (ImageSputnik image : currentImages) {

                System.out.println("image filename: " + image.getFilename());
            }
        }

        PagerContainer mContainer = (PagerContainer) findViewById(R.id.pager_container);

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

    /** Called when user clicks Scan Devices button */
    public void enlargeImage(View view) {

        ImageSputnik imageSputnik = currentImages.get(view.getId());

        if (imageMgr != null) {
            try {
                File folder = imageMgr.getAlbumStorageDir();
                String fullPath = folder.getAbsolutePath() + "/" + imageSputnik.getFilename();
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bm = BitmapFactory.decodeFile(fullPath, options);
                ImageView largePicture = (ImageView) findViewById(R.id.pictureLarge);
                largePicture.setImageBitmap(bm);
            } catch (ImageException ie) {
                ie.printStackTrace();
            }
        }
    }

    //Nothing special about this adapter, just throwing up colored views for demo
    private class GalleryAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageSputnik imageSputnik = currentImages.get(position);

            BitmapFactory.Options options = new BitmapFactory.Options();

            ImageText imageText = new ImageText(GalleryActivity.this);

            options.inSampleSize = 2;

            if (imageMgr != null) {
                try {
                    File folder = imageMgr.getAlbumStorageDir();
                    String fullPath = folder.getAbsolutePath() + "/" + imageSputnik.getFilename();

                    Bitmap bm = BitmapFactory.decodeFile(fullPath, options);

                    imageText.getImageView().setImageBitmap(bm);
                    imageText.getImageView().setId(position);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
                    imageText.getTextView().setText(imageSputnik.getId().toString() + " - " + sdf.format(imageSputnik.getCreateDate()));

                    container.addView(imageText);
                } catch (ImageException e) {
                    e.printStackTrace();
                }
            }

            return imageText;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return currentImages != null ? currentImages.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }
}
