package com.viewlift.views.customviews;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

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

    /**
     * this Constructor is for TV.
     * @param context
     * @param videoPlayerView
     */
    public FullPlayerView(Context context,
                          TVVideoPlayerView videoPlayerView) {
        super(context);
        this.context = context;
        this.tvVideoPlayerView = videoPlayerView;
        init();
    }

    /**
     * this Constructor is for Mobile.
     * @param context
     * @param videoPlayerView
     */
    public FullPlayerView(Context context,
                          CustomVideoPlayerView videoPlayerView) {
        super(context);
        this.context = context;
        this.videoPlayerView = videoPlayerView;
        init();
    }

    /**
     * this is a genralize constructor . Mobile or TV can use it.
     * @param context
     * @param appCMSPresenter
     */
    public FullPlayerView(Context context,
                          AppCMSPresenter appCMSPresenter) {
        super(context);
        this.context = context;
        this.appCMSPresenter = appCMSPresenter;
        init();
    }

    public void init() {
        lpView = new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        setLayoutParams(lpView);
        setBackgroundColor(Color.BLACK);
        if(appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV) {
            appCMSPresenter.tvVideoPlayerView.setLayoutParams(lpView);
            if (appCMSPresenter.tvVideoPlayerView.getParent() != null) {
                appCMSPresenter.videoPlayerViewParent = (ViewGroup) appCMSPresenter.tvVideoPlayerView.getParent();
                ((ViewGroup) appCMSPresenter.tvVideoPlayerView.getParent()).removeView(appCMSPresenter.tvVideoPlayerView);
            }
            setVisibility(VISIBLE);
            addView(appCMSPresenter.tvVideoPlayerView);
        }else{
            appCMSPresenter.videoPlayerView.setLayoutParams(lpView);
            if(appCMSPresenter.videoPlayerView.getParent()!=null){
                appCMSPresenter.videoPlayerViewParent=(ViewGroup)appCMSPresenter.videoPlayerView.getParent();
                ((ViewGroup) appCMSPresenter.videoPlayerView.getParent()).removeView(appCMSPresenter.videoPlayerView);
            }
            appCMSPresenter.videoPlayerView.updateFullscreenButtonState(Configuration.ORIENTATION_LANDSCAPE);
            appCMSPresenter.videoPlayerView.getPlayerView().getController().show();
            setVisibility(VISIBLE);
            appCMSPresenter.videoPlayerView.setClickable(true);
            addView(appCMSPresenter.videoPlayerView);
        }
    }
}