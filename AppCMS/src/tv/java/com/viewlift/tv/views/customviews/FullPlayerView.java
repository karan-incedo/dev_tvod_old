package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.views.customviews.CustomVideoPlayerView;

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

        lpView = new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        //lpVideoView = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        setLayoutParams(lpView);
        setBackgroundColor(Color.BLACK);

        appCMSPresenter.videoPlayerView.setLayoutParams(lpView);

        if(appCMSPresenter.videoPlayerView.getParent()!=null){
            appCMSPresenter.videoPlayerViewParent=(ViewGroup)appCMSPresenter.videoPlayerView.getParent();
            ((ViewGroup) appCMSPresenter.videoPlayerView.getParent()).removeView(appCMSPresenter.videoPlayerView);
        }
        //appCMSPresenter.videoPlayerView.updateFullscreenButtonState(appCMSPresenter.getCurrentActivity().getRequestedOrientation());
       // appCMSPresenter.videoPlayerView.updateFullscreenButtonState(Configuration.ORIENTATION_LANDSCAPE);
        setVisibility(VISIBLE);
        addView(appCMSPresenter.videoPlayerView);
    }



}