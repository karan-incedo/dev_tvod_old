package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;

/**
 * Created by sandeep.singh on 11/16/2017.
 */

public class FullPlayerView extends RelativeLayout {

    private TVVideoPlayerView tvVideoPlayerView;
    private CustomVideoPlayerView videoPlayerView;
    private AppCMSPresenter appCMSPresenter;
    private Context context;
    private LayoutParams lpView;
    private FrameLayout.LayoutParams lpVideoView;

    public FullPlayerView(Context context,
                          TVVideoPlayerView videoPlayerView) {
        super(context);
        this.context = context;
        this.tvVideoPlayerView = videoPlayerView;
        init();
    }


    public FullPlayerView(Context context,
                          CustomVideoPlayerView videoPlayerView) {
        super(context);
        this.context = context;
        this.videoPlayerView = videoPlayerView;
        init();
    }

    public FullPlayerView(Context context,
                          AppCMSPresenter appCMSPresenter) {
        super(context);
        this.context = context;
        this.appCMSPresenter = appCMSPresenter;
        init();
    }

    public void init() {

        lpView = new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        //lpVideoView = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        setLayoutParams(lpView);
        setBackgroundColor(Color.BLACK);

        appCMSPresenter.tvVideoPlayerView.setLayoutParams(lpView);

        if(appCMSPresenter.tvVideoPlayerView.getParent()!=null){
            appCMSPresenter.videoPlayerViewParent=(ViewGroup)appCMSPresenter.tvVideoPlayerView.getParent();
            ((ViewGroup) appCMSPresenter.tvVideoPlayerView.getParent()).removeView(appCMSPresenter.tvVideoPlayerView);
        }
        //appCMSPresenter.tvVideoPlayerView.updateFullscreenButtonState(appCMSPresenter.getCurrentActivity().getRequestedOrientation());
        // appCMSPresenter.tvVideoPlayerView.updateFullscreenButtonState(Configuration.ORIENTATION_LANDSCAPE);
        setVisibility(VISIBLE);
        addView(appCMSPresenter.tvVideoPlayerView);
    }



}