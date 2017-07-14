package com.viewlift.views.customviews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by viewlift on 6/22/17.
 */

public class AlwaysSelectedTextView extends AppCompatTextView {
    public AlwaysSelectedTextView(Context context) {
        super(context);
    }

    public AlwaysSelectedTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlwaysSelectedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setSelected(true);
    }
}
