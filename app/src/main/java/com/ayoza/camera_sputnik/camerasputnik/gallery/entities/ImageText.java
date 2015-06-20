package com.ayoza.camera_sputnik.camerasputnik.gallery.entities;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ayoza.camera_sputnik.camerasputnik.R;

/**
 * Created by raul on 13/06/15.
 * This class contains a Image and a Text
 */
public class ImageText extends LinearLayout {

    private ImageView imageView;

    private TextView textView;

    private LayoutInflater mInflater;

    public ImageText(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public ImageText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public ImageText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInflater = LayoutInflater.from(context);
        init();
    }

    private void init() {
        mInflater.inflate(R.layout.image_text_view, this, true);
        imageView = (ImageView) this.findViewById(R.id.imageTextImageId);
        textView = (TextView) this.findViewById(R.id.imageTextTextId);
        textView.setText(" Custom RelativeLayout");
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
            imageView = (ImageView) getChildAt(0);
            textView = (TextView) getChildAt(1);
        } catch (Exception e) {
            throw new IllegalStateException("The first child of PagerContainer must be a ViewPager and the second one a TextView");
        }
    }

    public TextView getTextView() {
        return textView;
    }

    public ImageView getImageView() {
        return imageView;
    }


}
