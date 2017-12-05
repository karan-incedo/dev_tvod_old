package com.viewlift.tv.views.presenter;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.views.customviews.CustomVideoPlayerView;

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




        if(mCustomVideoPlayerView == null){
            mCustomVideoPlayerView = playerView(context);
            setVideoPlayerView(mCustomVideoPlayerView , true);
        }

        FrameLayout.LayoutParams playerParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        mCustomVideoPlayerView.setLayoutParams(playerParams);
        if(mCustomVideoPlayerView != null && mCustomVideoPlayerView.getParent() != null){
            ((ViewGroup)mCustomVideoPlayerView.getParent()).removeView(mCustomVideoPlayerView);
        }
        frameLayout.addView(mCustomVideoPlayerView);

        frameLayout.setFocusable(true);

        return new ViewHolder(frameLayout);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        BrowseFragmentRowData rowData = (BrowseFragmentRowData)item;
        ContentDatum contentData = rowData.contentData;

        FrameLayout cardView = (FrameLayout) viewHolder.view;

        if(shouldStartPlayer){
            boolean requestAds = true;
            String adsUrl = appCmsPresenter.getAdsUrl(appCmsPresenter.getPermalinkCompletePath(contentData.getGist().getPermalink()));
            if(adsUrl == null) {
                requestAds = false;
            }
            mCustomVideoPlayerView.setupAds(requestAds ? adsUrl : null);
            mCustomVideoPlayerView.setVideoUri(contentData.getGist().getId());
        }

        mCustomVideoPlayerView.requestFocusOnLogin();
       //CustomVideoPlayerView videoPlayerView = null;
       /* if(null != cardView && cardView.getChildCount() > 0){
            videoPlayerView = (CustomVideoPlayerView)cardView.getChildAt(0);
        }else {
            videoPlayerView = playerView(cardView.getContext());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            videoPlayerView.setLayoutParams(layoutParams);

            cardView.addView(videoPlayerView);
            boolean svodServiceType = appCmsPresenter.getAppCMSMain().getServiceType().equals(
                    context.getString(R.string.app_cms_main_svod_service_type_key));

            boolean requestAds = !svodServiceType;
            String adsUrl = appCmsPresenter.getAdsUrl(appCmsPresenter.getPermalinkCompletePath(contentData.getGist().getPermalink()));
            if(adsUrl == null) {
                requestAds = false;
            }
            videoPlayerView.setupAds(requestAds ? adsUrl : null);
            videoPlayerView.setVideoUri(contentData.getGist().getId());
        }*/
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
      /*  try {
            if (null != viewHolder && null != viewHolder.view) {
                ((FrameLayout) viewHolder.view).removeAllViews();
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }



    public CustomVideoPlayerView playerView(Context context) {
        CustomVideoPlayerView videoPlayerView = new CustomVideoPlayerView(context);
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

    private CustomVideoPlayerView mCustomVideoPlayerView;
    private boolean shouldStartPlayer;
    public void setVideoPlayerView(CustomVideoPlayerView customVideoPlayerView , boolean shouldStartPlayer){
        this.mCustomVideoPlayerView = customVideoPlayerView;
        this.shouldStartPlayer = shouldStartPlayer;
    }

}
