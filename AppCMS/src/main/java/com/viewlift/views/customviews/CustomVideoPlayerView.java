package com.viewlift.views.customviews;

/**
 * Created by viewlift on 11/17/2017.
 */


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ClosedCaptions;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.exoplayerview.CustomPlaybackControlView;

import java.util.List;
import rx.functions.Action1;
import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_READY;

public class CustomVideoPlayerView extends VideoPlayerView {

    private Context mContext;
    private AppCMSPresenter appCMSPresenter;
    private  FrameLayout.LayoutParams baseLayoutParms;
    private LinearLayout customLoaderContainer;
    private TextView loaderMessageView;
    private LinearLayout customMessageContainer;
    private TextView customMessageView;
    private LinearLayout customPlayBack;
    private String videoDataId;
    private ToggleButton mFullScreenButton;



    public CustomVideoPlayerView(Context context, String videoId) {
        super(context);
        mContext = context;
        this.videoDataId = videoId;
        appCMSPresenter = ((AppCMSApplication) mContext.getApplicationContext()).getAppCMSPresenterComponent().appCMSPresenter();
        createLoader();
        //createPlaybackFullScreen();
        createCustomMessageView();




        mFullScreenButton = createFullScreenToggleButton();
        ((RelativeLayout) getPlayerView().findViewById(R.id.exo_controller_container)).addView(mFullScreenButton);



    }


    int currentPlayingIndex = 0;
    List<String> relatedVideoId;

    public void setVideoUri(String videoId, int resIdMessage) {

        showProgressBar(getResources().getString(resIdMessage));
        releasePlayer();
        init(mContext);
        getPlayerView().hideController();

        appCMSPresenter.refreshVideoData(videoId, new Action1<ContentDatum>() {
            @Override
            public void call(ContentDatum contentDatum) {
                if (!contentDatum.getGist().getFree()) {
                    //check login and subscription first.
                    if (!appCMSPresenter.isUserLoggedIn()) {
                        showRestrictMessage(getResources().getString(R.string.app_cms_subscribe_text_message));
                    } else {
                        //check subscription data
                        appCMSPresenter.getSubscriptionData(appCMSUserSubscriptionPlanResult -> {
                            try {
                                if (appCMSUserSubscriptionPlanResult != null) {
                                    String subscriptionStatus = appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus();
                                    if (subscriptionStatus.equalsIgnoreCase("COMPLETED") ||
                                            subscriptionStatus.equalsIgnoreCase("DEFERRED_CANCELLATION")) {
                                        playVideos(0, contentDatum);
                                    } else {
                                        showRestrictMessage(getResources().getString(R.string.app_cms_subscribe_text_message));
                                    }
                                } else {
                                    showRestrictMessage(getResources().getString(R.string.app_cms_subscribe_text_message));
                                }
                            } catch (Exception e) {
                                showRestrictMessage(getResources().getString(R.string.app_cms_subscribe_text_message));
                            }
                        });
                    }
                } else {
                    playVideos(0, contentDatum);
                }
                System.out.println(" JSON for Video details " + new Gson().toJson(contentDatum));
            }
        });

    }

//    public void showFullScreenController(){
//        if(getPlayerView()!=null) {
//            getPlayerView().showController();
//
//            if(getPlayerView()!=null) {
////                getPlayerView().getController();
//                PlaybackControlView playbackControlView = getPlayerView().getController();
//                FrameLayout mFullScreenButton = (FrameLayout) playbackControlView.findViewById(R.id.exo_fullscreen_button);
//                ImageView mFullScreenIcon = (ImageView) playbackControlView.findViewById(R.id.exo_fullscreen_icon);
//                mFullScreenIcon.setImageResource(R.drawable.ic_fullscreen_expand);
//                mFullScreenButton.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        appCMSPresenter.launchFullScreenStandalonePlayer(videoDataId);
//                    }
//                });
//            }
//        }
//    }

    private void playVideos(int currentIndex, ContentDatum contentDatum) {
        hideRestrictedMessage();

        if (null != customPlayBack)
            customPlayBack.setVisibility(View.VISIBLE);

        String url = null;
        String closedCaptionUrl = null;
        if (null != contentDatum && null != contentDatum.getStreamingInfo() && null != contentDatum.getStreamingInfo().getVideoAssets()) {
            if (null != contentDatum.getStreamingInfo().getVideoAssets().getHls()) {
                url = contentDatum.getStreamingInfo().getVideoAssets().getHls();
            } else if (null != contentDatum.getStreamingInfo().getVideoAssets().getMpeg()
                    && contentDatum.getStreamingInfo().getVideoAssets().getMpeg().size() > 0) {
                url = contentDatum.getStreamingInfo().getVideoAssets().getMpeg().get(0).getUrl();
            }
            if (contentDatum.getContentDetails() != null
                    && contentDatum.getContentDetails().getClosedCaptions() != null
                    && !contentDatum.getContentDetails().getClosedCaptions().isEmpty()) {
                for (ClosedCaptions cc : contentDatum.getContentDetails().getClosedCaptions()) {
                    if (cc.getUrl() != null &&
                            !cc.getUrl().equalsIgnoreCase(getContext().getString(R.string.download_file_prefix)) &&
                            cc.getFormat() != null &&
                            cc.getFormat().equalsIgnoreCase("SRT")) {
                        closedCaptionUrl = cc.getUrl();
                    }
                }
            }
            if (playerView!=null && playerView.getController()!=null ){
                playerView.getController().setPlayingLive(contentDatum.getStreamingInfo().getIsLiveStream());
            }
        }

        if (null != url) {
            setUri(Uri.parse(url), closedCaptionUrl == null ? null : Uri.parse(closedCaptionUrl));
            getPlayerView().getPlayer().setPlayWhenReady(true);
            if (currentIndex == 0) {
                relatedVideoId = contentDatum.getContentDetails().getRelatedVideoIds();
            }
            currentPlayingIndex = currentIndex;
            hideProgressBar();
        }
    }


    private boolean checkVideoSubscriptionStatus(ContentDatum contentDatum) {
        final boolean[] isSubscribe = {false};
        if (!contentDatum.getGist().getFree()) {
            //check login and subscription first.
            if (!appCMSPresenter.isUserLoggedIn()) {
                isSubscribe[0] = false;
            } else {
                //check subscription data
                appCMSPresenter.getSubscriptionData(appCMSUserSubscriptionPlanResult -> {
                    try {
                        if (appCMSUserSubscriptionPlanResult != null) {
                            String subscriptionStatus = appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus();
                            if (subscriptionStatus.equalsIgnoreCase("COMPLETED") ||
                                    subscriptionStatus.equalsIgnoreCase("DEFERRED_CANCELLATION")) {
                                isSubscribe[0] = true;
                            } else {
                                isSubscribe[0] = false;
                            }
                        } else {
                            isSubscribe[0] = false;
                        }
                    } catch (Exception e) {
                        isSubscribe[0] = false;
                    }
                });
            }
        } else {
            isSubscribe[0] = true;
        }
        return isSubscribe[0];
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch (playbackState) {
            case STATE_ENDED:
                getPlayerView().getPlayer().setPlayWhenReady(false);
                if (null != relatedVideoId && currentPlayingIndex <= relatedVideoId.size() - 1) {
                    //showProgressBar("Loading Next Video...");
                    setVideoUri(relatedVideoId.get(++currentPlayingIndex),R.string.loading_next_video_text);

                    /*appCMSPresenter.refreshVideoData(relatedVideoId.get(currentPlayingIndex), new Action1<ContentDatum>() {
                        @Override
                        public void call(ContentDatum contentDatum) {
                            if (!checkVideoSubscriptionStatus(contentDatum)) {
                                showRestrictMessage("This video is only available to Monumental Sports Network subscribers");
                                return;
                            }
                            hideRestrictedMessage();
                            setUri(Uri.parse(contentDatum.getStreamingInfo().getVideoAssets().getHls()), null);
                            getPlayerView().getPlayer().setPlayWhenReady(true);
                            hideProgressBar();
                        }
                    });*/
                }else{
                    showRestrictMessage(getResources().getString(R.string.app_cms_video_ended_text_message));
                }
                break;
            case STATE_BUFFERING:
                showProgressBar("Streaming...");
                break;
            case STATE_READY:
                hideProgressBar();
                break;
            default:
                hideProgressBar();
        }
    }

    public void pausePlayer() {
        super.pausePlayer();
        //super.releasePlayer();
    }


    public void resumePlayer() {
        if (null != getPlayer() && !getPlayer().getPlayWhenReady()) {
            getPlayer().setPlayWhenReady(true);
        }
    }

    public void releasePlayer() {
        if (getPlayer() != null) {
            getPlayer().release();
        }
    }


    private void createLoader() {
        customLoaderContainer = new LinearLayout(mContext);
        customLoaderContainer.setOrientation(LinearLayout.VERTICAL);
        customLoaderContainer.setGravity(Gravity.CENTER);
        ProgressBar progressBar = new ProgressBar(mContext);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().
                setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent),
                        PorterDuff.Mode.MULTIPLY
                );
        LinearLayout.LayoutParams progressbarParam = new LinearLayout.LayoutParams(50, 50);
        progressBar.setLayoutParams(progressbarParam);
        customLoaderContainer.addView(progressBar);
        loaderMessageView = new TextView(mContext);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loaderMessageView.setLayoutParams(textViewParams);
        customLoaderContainer.addView(loaderMessageView);
        this.addView(customLoaderContainer);
    }

    private void createPlaybackFullScreen() {
        customPlayBack = new LinearLayout(mContext);
        customPlayBack.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams llLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        customPlayBack.setLayoutParams(llLinear);

        customPlayBack.setGravity(Gravity.BOTTOM | Gravity.RIGHT);

        ImageView imgFullScreen = new ImageView(mContext);
        imgFullScreen.setScaleType(ImageView.ScaleType.FIT_XY);
        imgFullScreen.setBackground(mContext.getDrawable(R.drawable.full_screen_player_icon));
        LinearLayout.LayoutParams paramsImgFullScreen = new LinearLayout.LayoutParams(BaseView.dpToPx(R.dimen.full_screen_item_min_width, mContext), BaseView.dpToPx(R.dimen.full_screen_item_min_width, mContext));

        paramsImgFullScreen.setMargins(0, 0, 32, 32);

        imgFullScreen.setLayoutParams(paramsImgFullScreen);
        imgFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                appCMSPresenter.launchFullScreenStandalonePlayer(videoDataId);
            }
        });
        customPlayBack.addView(imgFullScreen);
        customPlayBack.setVisibility(View.INVISIBLE);

        this.addView(customPlayBack);
    }


    private void createCustomMessageView() {
        customMessageContainer = new LinearLayout(mContext);
        customMessageContainer.setOrientation(LinearLayout.HORIZONTAL);
        customMessageContainer.setGravity(Gravity.CENTER);
        customMessageContainer.setBackgroundColor(Color.parseColor("#d4000000"));
        customMessageView = new TextView(mContext);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.gravity = Gravity.CENTER;
        customMessageView.setLayoutParams(textViewParams);
        customMessageView.setTextColor(Color.parseColor("#ffffff"));
        customMessageView.setTextSize(15);
        customMessageView.setPadding(20, 20, 20, 20);

        customMessageContainer.addView(customMessageView);
        customMessageContainer.setVisibility(View.INVISIBLE);
        this.addView(customMessageContainer);
    }

    private void showProgressBar(String text) {
        if (null != customLoaderContainer && null != loaderMessageView) {
            loaderMessageView.setText(text);
            loaderMessageView.setTextColor(getResources().getColor(android.R.color.white));
            customLoaderContainer.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (null != customLoaderContainer) {
            customLoaderContainer.setVisibility(View.INVISIBLE);
        }
    }

    private void showRestrictMessage(String message) {
        if (null != customMessageContainer && null != customMessageView) {
            hideProgressBar();
            loaderMessageView.setTextColor(getResources().getColor(android.R.color.white));
            customMessageView.setText(message);
            customMessageContainer.setVisibility(View.VISIBLE);

        }


    }

    private void hideRestrictedMessage() {
        if (null != customMessageContainer) {
            customMessageContainer.setVisibility(View.INVISIBLE);
        }


    }

    protected ToggleButton createFullScreenToggleButton() {
        ToggleButton mToggleButton = new ToggleButton(getContext());
        RelativeLayout.LayoutParams toggleLP = new RelativeLayout.LayoutParams(BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_width, getContext()), BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_width, getContext()));
        toggleLP.addRule(RelativeLayout.CENTER_VERTICAL);
        toggleLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        toggleLP.setMarginStart(BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_left_margin, getContext()));
        mToggleButton.setLayoutParams(toggleLP);
        mToggleButton.setChecked(false);
        mToggleButton.setTextOff("");
        mToggleButton.setTextOn("");
        mToggleButton.setText("");
        mToggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.full_screen_toggle_selector, null));
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //todo work on maximizing the player on this event
                /*if (isChecked){
                    FrameLayout.LayoutParams frameLayoutParams=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    setLayoutParams(frameLayoutParams);
                }else{
                    setLayoutParams(baseLayoutParms);
                }*/
                appCMSPresenter.launchFullScreenStandalonePlayer(videoDataId);
            }
        });

        return mToggleButton;
    }
}

