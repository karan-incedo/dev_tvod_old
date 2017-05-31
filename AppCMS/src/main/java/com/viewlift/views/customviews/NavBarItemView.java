package com.viewlift.views.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/26/17.
 */

public class NavBarItemView extends LinearLayout {
    private static final String TAG = "NavBarItemView";
    private ImageView navImage;
    private TextView navLabel;

    public NavBarItemView(Context context) {
        super(context);
        init();
    }

    public NavBarItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavBarItemView(Context context,
                          @Nullable AttributeSet attrs,
                          int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NavBarItemView(Context context,
                          AttributeSet attrs,
                          int defStyleAttr,
                          int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        setOrientation(VERTICAL);
        createChildren(getContext());
    }

    public void createChildren(Context context) {
        navImage = new ImageView(context);
        LinearLayout.LayoutParams navImageLayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        navImageLayoutParams.gravity = Gravity.CENTER;
        navImage.setLayoutParams(navImageLayoutParams);
        navLabel = new TextView(context);
        LinearLayout.LayoutParams navLabelLayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        navLabelLayoutParams.gravity = Gravity.CENTER;
        navLabel.setLayoutParams(navLabelLayoutParams);
        navLabel.setTextColor(ContextCompat.getColor(context, R.color.colorNavBarText));
        addView(navImage);
        addView(navLabel);
    }

    public void setImage(String drawableName) {
        Resources resources = getResources();
        int drawableId = resources.getIdentifier(drawableName,
                "drawable",
                getContext().getPackageName());
        navImage.setBackgroundResource(drawableId);
    }

    public void setLabel(String label) {
        navLabel.setText(label);
    }
}
