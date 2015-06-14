package com.ayoza.camera_sputnik.camerasputnik.gallery.entities;

import android.content.Context;
import android.media.Image;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by raul on 13/06/15.
 * This class contains a Image and a Text
 */
public class ImageText extends RelativeLayout {

    private ViewPager imageView;
    private TextView textView;

    public ImageText(Context context) {
        super(context);
    }

    public ImageText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //Disable clipping of children so non-selected pages are visible
        setClipChildren(false);

        //Child clipping doesn't work with hardware acceleration in Android 3.x/4.x
        //You need to set this value here if using hardware acceleration in an
        // application targeted at these releases.
        //setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onFinishInflate() {
        try {
            imageView = (ViewPager) getChildAt(0);
            textView = (TextView) getChildAt(1);
        } catch (Exception e) {
            throw new IllegalStateException("The first child of PagerContainer must be a ViewPager and the second one a TextView");
        }
    }

    public ViewPager getImageView() {
        return imageView;
    }

    public TextView getTextView() {
        return textView;
    }

}
