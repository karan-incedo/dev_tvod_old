package com.viewlift.tv.views.presenter;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.customviews.CustomVideoVideoPlayerView;
import com.viewlift.tv.views.fragment.AppCMSPlayVideoFragment;
import com.viewlift.views.customviews.VideoPlayerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by nitin.tyagi on 11/2/2017.
 */

public class PlayerPresenter extends Presenter {

    private static int DEVICE_HEIGHT , DEVICE_WIDTH= 0;
    private final Context context;
    private final AppCMSPresenter appCmsPresenter;

    public PlayerPresenter(Context context , AppCMSPresenter appCMSPresenter){
        this.context = context;
        this.appCmsPresenter = appCMSPresenter;
    }
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
        BrowseFragmentRowData rowData = (BrowseFragmentRowData)item;
        ContentDatum contentData = rowData.contentData;

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
            videoPlayerView.setVideoUri(contentData.getGist().getId());
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
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

}
