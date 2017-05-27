package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by viewlift on 5/26/17.
 */

public class NavBarItemView extends LinearLayout {
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
    }

    public void createChildren(Context context,
                               int width,
                               int height,
                               float weight) {
        navImage = new ImageView(context);
        LinearLayout.LayoutParams navImageLayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        navImageLayoutParams.weight = 2;
        navImage.setLayoutParams(navImageLayoutParams);
        navLabel = new TextView(context);
        LinearLayout.LayoutParams navLabelLayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        navLabel.setLayoutParams(navLabelLayoutParams);
        navLabelLayoutParams.weight = 1;
        addView(navImage);
        addView(navLabel);
    }

    public void setImage(String drawableName) {
        navImage.setBackground(Drawable.createFromPath(drawableName));
    }

    public void setLabel(String label) {
        navLabel.setText(label);
    }
}
