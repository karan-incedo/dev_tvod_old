package com.viewlift.views.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by viewlift on 10/26/17.
 */

public class ResponsiveButton extends android.support.v7.widget.AppCompatImageButton {
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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setPressed(true);
            return true;
        }
        return super.onTouchEvent(event);
    }


}
