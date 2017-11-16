package com.viewlift.tv.views.presenter;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.viewlift.R;
import com.viewlift.tv.views.customviews.CustomVideoVideoPlayerView;
import com.viewlift.tv.views.fragment.AppCMSPlayVideoFragment;
import com.viewlift.views.customviews.VideoPlayerView;

/**
 * Created by nitin.tyagi on 11/2/2017.
 */

public class PlayerPresenter extends Presenter {

    private static int DEVICE_HEIGHT , DEVICE_WIDTH= 0;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        DEVICE_WIDTH = parent.getContext().getResources().getDisplayMetrics().widthPixels;
        DEVICE_HEIGHT = parent.getContext().getResources().getDisplayMetrics().heightPixels;
        //Log.d("Presenter" , " CardPresenter onCreateViewHolder******");
        final FrameLayout frameLayout = new FrameLayout(parent.getContext());
        FrameLayout.LayoutParams layoutParams;
        layoutParams = new FrameLayout.LayoutParams(DEVICE_WIDTH,
                    DEVICE_HEIGHT);
        frameLayout.setLayoutParams(layoutParams);
        frameLayout.setFocusable(true);
        return new ViewHolder(frameLayout);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        FrameLayout cardView = (FrameLayout) viewHolder.view;
        CustomVideoVideoPlayerView videoPlayerView = null;
        if(null != cardView && cardView.getChildCount() > 0){
            videoPlayerView = (CustomVideoVideoPlayerView)cardView.getChildAt(0);
        }else {
            videoPlayerView = playerView(cardView.getContext());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            videoPlayerView.setLayoutParams(layoutParams);

            cardView.addView(videoPlayerView);
            videoPlayerView.setVideoUri(mVideoId);
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
      /*  try {
            FrameLayout cardView = (FrameLayout) viewHolder.view;
            VideoPlayerView videoPlayerView = (VideoPlayerView)cardView.getChildAt(0);
            videoPlayerView.pausePlayer();

            if (null != viewHolder && null != viewHolder.view) {
                ((FrameLayout) viewHolder.view).removeAllViews();
            }

        }catch (Exception e){
            e.printStackTrace();
        }*/
    }


    public static CustomVideoVideoPlayerView playerView(Context context) {
        CustomVideoVideoPlayerView videoPlayerView = new CustomVideoVideoPlayerView(context);
        videoPlayerView.init(context);
        videoPlayerView.getPlayerView().hideController();
        videoPlayerView.getPlayerView().setControllerVisibilityListener(new PlaybackControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int i) {
                if (i == 0) {
                   videoPlayerView.getPlayerView().hideController();
                }
            }
        });
        return videoPlayerView;
    }

    private String mVideoId;
    public void setVideoId(String id) {
        mVideoId = id;
    }
}
