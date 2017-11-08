package com.viewlift.tv.views.presenter;

import android.content.Context;
import android.net.Uri;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.ui.PlaybackControlView;
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
        VideoPlayerView videoPlayerView = null;
        if(null != cardView && cardView.getChildCount() > 0){
            videoPlayerView = (VideoPlayerView)cardView.getChildAt(0);
        }else {
            videoPlayerView = playerView(cardView.getContext());
            cardView.addView(videoPlayerView);
            videoPlayerView.setUri(Uri.parse("https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"),
                    null);
            videoPlayerView.getPlayerView().getPlayer().setPlayWhenReady(true);
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



    public static VideoPlayerView playerView(Context context) {

        VideoPlayerView videoPlayerView = new VideoPlayerView(context);
        videoPlayerView.init(context);
        // it should be dynamic when live url come from api
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

}
