package com.viewlift.views.customviews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by viewlift on 10/26/17.
 */

public class ResponsiveButton extends android.support.v7.widget.AppCompatImageButton {
    private static final String TAG = "ResponsiveButton";

    public ResponsiveButton(Context context) {
        super(context);
    }

    public ResponsiveButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResponsiveButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            setPressed(true);
//            return performClick();
//        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }
}
