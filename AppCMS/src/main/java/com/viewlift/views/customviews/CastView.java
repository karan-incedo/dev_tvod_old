package com.viewlift.views.customviews;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by viewlift on 6/7/17.
 */

public class CastView extends LinearLayout {
    public CastView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LinearLayout headingColumn = new LinearLayout(getContext());
        headingColumn.setOrientation(VERTICAL);
        LinearLayout.LayoutParams headingColumnLayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        headingColumnLayoutParams.gravity = Gravity.CENTER;
        headingColumn.setLayoutParams(headingColumnLayoutParams);

        LinearLayout contentColumn = new LinearLayout(getContext());
        contentColumn.setOrientation(VERTICAL);
        LinearLayout.LayoutParams contentColumnLayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        contentColumnLayoutParams.gravity = Gravity.CENTER;
        contentColumn.setLayoutParams(contentColumnLayoutParams);
    }
}
