package com.ayoza.camera_sputnik.camerasputnik.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ayoza.camera_sputnik.camerasputnik.R;
import com.ayoza.camera_sputnik.camerasputnik.arduino.managers.ImageMgr;
import com.ayoza.camera_sputnik.camerasputnik.arduino.managers.TrackMgr;
import com.ayoza.camera_sputnik.camerasputnik.exceptions.ImageException;
import com.ayoza.camera_sputnik.camerasputnik.exceptions.TrackException;
import com.ayoza.camera_sputnik.camerasputnik.gallery.entities.ImageText;
import com.ayoza.camera_sputnik.camerasputnik.gallery.entities.PagerContainer;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.ImageSputnik;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.TrackSputnik;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by raultov on 26/04/15.
 * This activity shows a gallery of pictures received previously through bluetooth
 * You can enlarge pictures by clicking on them
 */
public class GalleryActivity extends ActionBarActivity {

    private ImageMgr imageMgr;
    private TrackMgr trackMgr;
    private List<ImageSputnik> currentImages = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        trackMgr = TrackMgr.getInstance(this);

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

    /** Called when user clicks on a picture */
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.show_calendar) {
            System.out.println("Shows calendar");

            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            final Context thisActivity = this;

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        private boolean firstTime = true;

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            if (!firstTime) {
                                // just to avoid show date picker more than once with same click
                                return;
                            }

                            firstTime = false;

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, monthOfYear);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            List<TrackSputnik> tracks = trackMgr.getAllTracksFromDay(calendar.getTime());

                            System.out.println(dayOfMonth + "-"
                                    + (monthOfYear + 1) + "-" + year);

                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                    thisActivity);
                            builderSingle.setIcon(R.drawable.ic_launcher);
                            builderSingle.setTitle("Select One Name:-");
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                    thisActivity,
                                    android.R.layout.select_dialog_singlechoice);

                            arrayAdapter.add("Hardik");
                            arrayAdapter.add("Archit");
                            arrayAdapter.add("Jignesh");
                            arrayAdapter.add("Umang");
                            arrayAdapter.add("Gatti");

                            builderSingle.setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builderSingle.setAdapter(arrayAdapter,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String strName = arrayAdapter.getItem(which);
                                            /*AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                                    thisActivity);
                                            builderInner.setMessage(strName);
                                            builderInner.setTitle("Your Selected Item is");
                                            builderInner.setPositiveButton("Ok",
                                                    new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            builderInner.show();
                                            */
                                            System.out.println(strName);
                                        }
                                    });
                            builderSingle.show();

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
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
