package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.tv.utility.Utils;

/**
 * Created by anas.azeem on 10/9/2017.
 * Owned by ViewLift, NYC
 */

public class AppCMSTVAutoplayCustomLoader extends TVBaseView {

    private final Context mContext;
    private final Component mComponent;
    private int DEFAULT_HEIGHT = LayoutParams.WRAP_CONTENT;
    private int DEFAULT_WIDTH = LayoutParams.WRAP_CONTENT;

    public AppCMSTVAutoplayCustomLoader(@NonNull Context context,
                                        Component component) {
        super(context);
        mComponent = component;
        mContext = context;
        init();
    }

    @Override
    public void init() {
        float viewHeight = Utils.getViewHeight(mContext, mComponent.getLayout(), DEFAULT_HEIGHT);
        float viewWidth = Utils.getViewWidth(mContext, mComponent.getLayout(), DEFAULT_WIDTH);
        LayoutParams layoutParams = new LayoutParams((int) viewWidth, (int) viewHeight);
        layoutParams.gravity = Gravity.CENTER;
        setLayoutParams(layoutParams);
        setFocusable(false);
        createLoaderView();
    }

    private void createLoaderView() {
        ImageView loader = new ImageView(mContext);
        loader.setImageResource(R.drawable.autoplay_loader);
        addView(loader);

        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE);
        loader.startAnimation(rotate);

        TextView tvTimer = new TextView(mContext);
        tvTimer.setGravity(Gravity.CENTER);
        addView(tvTimer);

        tvTimer.setId(R.id.countdown_id);
    }

    @Override
    protected Component getChildComponent(int index) {
        return null;
    }

    @Override
    protected Layout getLayout() {
        return mComponent.getLayout();
    }
}
