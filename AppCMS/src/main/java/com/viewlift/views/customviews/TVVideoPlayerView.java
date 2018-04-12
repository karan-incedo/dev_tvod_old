package com.viewlift.views.customviews;

import android.content.Context;

import com.viewlift.presenters.AppCMSPresenter;

/**
 * Created by viewlift on 5/31/17.
 */

public class TVVideoPlayerView
        extends VideoPlayerView
         {

    private boolean isHardPause;
    public boolean isLiveStream;

    public TVVideoPlayerView(Context context, AppCMSPresenter presenter) {
        super(context, presenter);
    }


    public boolean isHardPause() {
        return isHardPause;
    }

    public void setHardPause(boolean hardPause) {
        isHardPause = hardPause;
    }

    public boolean isLiveStream(){
        return isLiveStream;
    }

}
