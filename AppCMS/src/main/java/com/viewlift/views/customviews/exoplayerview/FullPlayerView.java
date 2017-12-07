package com.viewlift.views.customviews.exoplayerview;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.CustomVideoPlayerView;

/**
 * Created by sandeep.singh on 11/16/2017.
 */

public class FullPlayerView extends RelativeLayout {

    private CustomVideoPlayerView videoPlayerView;
    private AppCMSPresenter appCMSPresenter;
    private Context context;
    private LayoutParams lpView;
    private FrameLayout.LayoutParams lpVideoView;

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

        lpView = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //lpVideoView = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        setLayoutParams(lpView);
        setBackgroundColor(Color.BLACK);




        AppCMSPresenter.videoPlayerView.setLayoutParams(lpView);

        if(AppCMSPresenter.videoPlayerView.getParent()!=null){
            AppCMSPresenter.videoPlayerViewParent=(ViewGroup)AppCMSPresenter.videoPlayerView.getParent();
            ((ViewGroup) AppCMSPresenter.videoPlayerView.getParent()).removeView(AppCMSPresenter.videoPlayerView);
        }
        //AppCMSPresenter.videoPlayerView.updateFullscreenButtonState(appCMSPresenter.getCurrentActivity().getRequestedOrientation());
        AppCMSPresenter.videoPlayerView.updateFullscreenButtonState(Configuration.ORIENTATION_LANDSCAPE);
        addView(AppCMSPresenter.videoPlayerView);




    }



}
