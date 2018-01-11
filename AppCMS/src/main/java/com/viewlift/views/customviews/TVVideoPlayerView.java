package com.viewlift.views.customviews;

import android.content.Context;

/**
 * Created by viewlift on 5/31/17.
 */

public class TVVideoPlayerView
        extends VideoPlayerView
         {

    private boolean isHardPause;
    public boolean isLiveStream;

    public TVVideoPlayerView(Context context) {
        super(context);
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
