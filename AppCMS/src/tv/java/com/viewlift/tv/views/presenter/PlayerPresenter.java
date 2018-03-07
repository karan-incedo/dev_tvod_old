package com.viewlift.tv.views.presenter;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.customviews.CustomTVVideoPlayerView;

/**
 * Created by nitin.tyagi on 11/2/2017.
 */

public class PlayerPresenter extends Presenter {

    private static int DEVICE_HEIGHT , DEVICE_WIDTH= 0;
    private final Context context;
    private final AppCMSPresenter appCmsPresenter;
    private int mHeight = -1;
    private int mWidth = -1;

    public PlayerPresenter(Context context , AppCMSPresenter appCMSPresenter ,
                           int height , int width){
        this.context = context;
        this.appCmsPresenter = appCMSPresenter;
        mHeight = height;
        mWidth = width;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        DEVICE_WIDTH = parent.getContext().getResources().getDisplayMetrics().widthPixels;
        DEVICE_HEIGHT = parent.getContext().getResources().getDisplayMetrics().heightPixels;
        //Log.d("Presenter" , " CardPresenter onCreateViewHolder******");
        final FrameLayout frameLayout = new FrameLayout(parent.getContext());

        if(mCustomVideoPlayerView == null){
            mCustomVideoPlayerView = playerView(context);
            setVideoPlayerView(mCustomVideoPlayerView , true);
        }

        FrameLayout.LayoutParams layoutParams;
        layoutParams = new FrameLayout.LayoutParams(Utils.getViewXAxisAsPerScreen(context , mWidth),
                Utils.getViewYAxisAsPerScreen(context,mHeight));
        frameLayout.setLayoutParams(layoutParams);


        FrameLayout.LayoutParams playerParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        mCustomVideoPlayerView.setLayoutParams(playerParams);
        if(mCustomVideoPlayerView != null && mCustomVideoPlayerView.getParent() != null){
            ((ViewGroup)mCustomVideoPlayerView.getParent()).removeView(mCustomVideoPlayerView);
        }

//        mCustomVideoPlayerView.setPadding(10,10,10,10);
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
            mCustomVideoPlayerView.setVideoUri(contentData.getGist().getId());
            shouldStartPlayer = false;
        }

        mCustomVideoPlayerView.requestFocusOnLogin();

        cardView.setBackground(Utils.getGradientTrayBorder(
                        context,
                        Utils.getPrimaryHoverColor(context, appCmsPresenter),
                        Utils.getSecondaryHoverColor(context, appCmsPresenter)));
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



    public CustomTVVideoPlayerView playerView(Context context) {
        CustomTVVideoPlayerView videoPlayerView = new CustomTVVideoPlayerView(context,
                appCmsPresenter);
        videoPlayerView.init(context);
        videoPlayerView.getPlayerView().hideController();
        return videoPlayerView;
    }

    private CustomTVVideoPlayerView mCustomVideoPlayerView;
    private boolean shouldStartPlayer;
    public void setVideoPlayerView(CustomTVVideoPlayerView customVideoPlayerView , boolean shouldStartPlayer){
        this.mCustomVideoPlayerView = customVideoPlayerView;
        this.shouldStartPlayer = shouldStartPlayer;
    }

}
