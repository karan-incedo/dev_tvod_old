package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.viewlift.models.data.appcms.ui.android.NavigationUser;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.activity.AppCmsHomeActivity;
import com.viewlift.tv.views.fragment.ClearDialogFragment;
import com.viewlift.views.customviews.VideoPlayerView;

import java.util.Date;
import java.util.List;

import rx.functions.Action1;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_READY;

/**
 * Created by viewlift on 5/31/17.
 */

public class CustomVideoPlayerView
        extends VideoPlayerView
        implements AdErrorEvent.AdErrorListener,
        AdEvent.AdEventListener,
        VideoPlayerView.ErrorEventListener {

    private static final String TAG = CustomVideoPlayerView.class.getSimpleName();
    final String[] videoImageUrl = new String[1];
    private Context mContext;
    private AppCMSPresenter appCMSPresenter;
    private LinearLayout custonLoaderContaineer;
    private TextView loaderMessageView;
    private LinearLayout customMessageContaineer;
    private TextView customMessageView;
    private boolean shouldRequestAds = false;
    private boolean isADPlay;
    private ImaSdkFactory sdkFactory;
    private AdsLoader adsLoader;
    private AdsManager adsManager;
    private String adsUrl;
    private boolean isAdsDisplaying;
    private boolean isAdDisplayed;
    private View imageViewContainer;
    private ImageView imageView;
    private long beaconMsgTimeoutMsec;
    private long beaconBufferingTimeoutMsec;
    private long mTotalVideoDuration;
    private boolean sentBeaconFirstFrame;
    private long mStopBufferMilliSec;
    private long mStartBufferMilliSec;
    private double ttfirstframe;
    private int currentPlayingIndex = 0;
    private List<String> relatedVideoId;
    private String parentScreenName;
    private String mStreamId;
    private int apod;
    private ContentDatum videoData = null;
    private BeaconBufferingThread beaconBufferingThread;
    private BeaconPingThread beaconMessageThread;
    private boolean sentBeaconPlay;

    public CustomVideoPlayerView(Context context) {
        super(context);
        getPlayerView().setUseController(false);
        mContext = context;
        appCMSPresenter = ((AppCMSApplication) mContext.getApplicationContext()).getAppCMSPresenterComponent().appCMSPresenter();
        createLoader();
        createCustomMessageView();
        imageViewContainer = findViewById(R.id.videoPlayerThumbnailImageContainer);
        imageView = (ImageView) findViewById(R.id.videoPlayerThumbnailImage);
        setListener(this);
        parentScreenName = mContext.getString(R.string.app_cms_beacon_video_player_parent_screen_name);
    }


    public void requestFocusOnLogin(){
        if(customMessageContaineer.getVisibility() == View.VISIBLE){
            loginButton.requestFocus();
        }
    }

    public void setupAds(String adsUrl) {
        this.adsUrl = adsUrl;
        if (adsUrl != null && !TextUtils.isEmpty(adsUrl)) {
            shouldRequestAds = true;
        }
        sdkFactory = ImaSdkFactory.getInstance();
        adsLoader = sdkFactory.createAdsLoader(getContext());
        adsLoader.addAdErrorListener(this);
        adsLoader.addAdsLoadedListener(adsManagerLoadedEvent -> {
            adsManager = adsManagerLoadedEvent.getAdsManager();
            adsManager.addAdErrorListener(CustomVideoPlayerView.this);
            adsManager.addAdEventListener(CustomVideoPlayerView.this);
            adsManager.init();
        });
    }

    public void setVideoUri(String videoId) {
        showProgressBar("Loading...");
        appCMSPresenter.refreshVideoData(videoId, new Action1<ContentDatum>() {
            @Override
            public void call(ContentDatum contentDatum) {
                if (!contentDatum.getGist().getFree()) {
                    //check login and subscription first.
                    if (!appCMSPresenter.isUserLoggedIn()) {
                        setBackgroundImage(contentDatum.getGist().getVideoImageUrl());
                        showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                    } else {
                        //check subscription data
                        appCMSPresenter.getSubscriptionData(appCMSUserSubscriptionPlanResult -> {
                            try {
                                if (appCMSUserSubscriptionPlanResult != null) {
                                    String subscriptionStatus = appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus();
                                    if (subscriptionStatus.equalsIgnoreCase("COMPLETED") ||
                                            subscriptionStatus.equalsIgnoreCase("DEFERRED_CANCELLATION")) {
                                        videoData = contentDatum;
                                        if (shouldRequestAds) requestAds(adsUrl);
                                        playVideos(0, contentDatum);
                                    } else {
                                        setBackgroundImage(contentDatum.getGist().getVideoImageUrl());
                                        showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                                    }
                                } else {
                                    setBackgroundImage(contentDatum.getGist().getVideoImageUrl());
                                    showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                                }
                            } catch (Exception e) {
                                setBackgroundImage(contentDatum.getGist().getVideoImageUrl());
                                showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                            }
                        });
                    }
                } else {
                    videoData = contentDatum;
                    if (shouldRequestAds) requestAds(adsUrl);
                    playVideos(0, contentDatum);
                }
            }
        });
    }

    private void playVideos(int currentIndex, ContentDatum contentDatum) {
        try {
            mStreamId = appCMSPresenter.getStreamingId(videoData.getGist().getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }
        releaseBeaconsThread();
        startBeaconsThread();
        hideRestrictedMessage();
        String url = null;
        if (null != contentDatum && null != contentDatum.getStreamingInfo() && null != contentDatum.getStreamingInfo().getVideoAssets()) {
            url = getVideoUrl(contentDatum.getStreamingInfo().getVideoAssets());
        }


        Log.d(TAG , "Url is = "+url);
        if (contentDatum != null && contentDatum.getGist() != null) {
            videoImageUrl[0] = contentDatum.getGist().getVideoImageUrl();
        }

        if (null != url) {
            setUri(Uri.parse(url), null);
            if (null != appCMSPresenter.getCurrentActivity() &&
                    appCMSPresenter.getCurrentActivity() instanceof AppCmsHomeActivity) {
                if (((AppCmsHomeActivity) appCMSPresenter.getCurrentActivity()).isActive) {
                    getPlayerView().getPlayer().setPlayWhenReady(true);
                } else {
                    getPlayerView().getPlayer().setPlayWhenReady(false);
                }
            }

            if (currentIndex == 0) {
                relatedVideoId = contentDatum.getContentDetails().getRelatedVideoIds();
            }
            currentPlayingIndex = currentIndex;
            hideProgressBar();
        }
    }

    private String getVideoUrl(VideoAssets videoAssets) {
        String defaultVideoResolution = mContext.getResources().getString(R.string.default_video_resolution);
        String videoUrl = videoAssets.getHls();

        if (TextUtils.isEmpty(videoUrl)) {
            if (videoAssets.getMpeg() != null && !videoAssets.getMpeg().isEmpty()) {
                for (int i = 0; i < videoAssets.getMpeg().size() && TextUtils.isEmpty(videoUrl); i++) {
                    if (videoAssets.getMpeg().get(i) != null &&
                            videoAssets.getMpeg().get(i).getRenditionValue() != null &&
                            videoAssets.getMpeg().get(i).getRenditionValue().contains(defaultVideoResolution)) {
                        videoUrl = videoAssets.getMpeg().get(i).getUrl();
                    }
                }
                if (videoAssets.getMpeg().get(0) != null && TextUtils.isEmpty(videoUrl)) {
                    videoUrl = videoAssets.getMpeg().get(0).getUrl();
                }
            }
        }
        return videoUrl;
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch (playbackState) {
            case STATE_ENDED:
//                getPlayerView().getPlayer().setPlayWhenReady(false);
                currentPlayingIndex++;
                Log.d(TAG, "appCMSPresenter.getAutoplayEnabledUserPref(mContext): " +
                        appCMSPresenter.getAutoplayEnabledUserPref(mContext));
                if (null != relatedVideoId
                        && currentPlayingIndex <= relatedVideoId.size() - 1) {
                    if (appCMSPresenter.getAutoplayEnabledUserPref(mContext)) {

                        showProgressBar("Loading Next Video...");
                        appCMSPresenter.refreshVideoData(relatedVideoId.get(currentPlayingIndex), new Action1<ContentDatum>() {
                            @Override
                            public void call(ContentDatum contentDatum) {
                                videoImageUrl[0] = contentDatum.getGist().getVideoImageUrl();
                                imageViewContainer.setVisibility(GONE);
                                if (!contentDatum.getGist().getFree()) {
                                    //check login and subscription first.
                                    if (!appCMSPresenter.isUserLoggedIn()) {
                                        showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                                        setBackgroundImage(videoImageUrl[0]);
                                    } else /*User not logged in */ {
                                        //check subscription data
                                        appCMSPresenter.getSubscriptionData(appCMSUserSubscriptionPlanResult -> {
                                            try {
                                                if (appCMSUserSubscriptionPlanResult != null) {
                                                    String subscriptionStatus = appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus();
                                                    if (subscriptionStatus.equalsIgnoreCase("COMPLETED") ||
                                                            subscriptionStatus.equalsIgnoreCase("DEFERRED_CANCELLATION")) {
                                                        if (shouldRequestAds) requestAds(adsUrl);
                                                        playVideos(currentPlayingIndex, contentDatum);
                                                    } else /*user not subscribed*/ {
                                                        setBackgroundImage(videoImageUrl[0]);
                                                        showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                                                    }
                                                } else /*received null result from API in appCMSUserSubscriptionPlanResult*/ {
                                                    setBackgroundImage(videoImageUrl[0]);
                                                    showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                                                }
                                            } catch (Exception e) {
                                                setBackgroundImage(videoImageUrl[0]);
                                                showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                                            }
                                        });
                                    }
                                } else /*Video is free*/ {
                                    if (shouldRequestAds) requestAds(adsUrl);
                                    playVideos(currentPlayingIndex, contentDatum);
                                    imageViewContainer.setVisibility(GONE);
                                }
                            }
                        });
                    } else /*Autoplay is turned-off*/ {
                        setBackgroundImage(videoImageUrl[0]);
                        showRestrictMessage(getResources().getString(R.string.autoplay_off_msg));
                    }
                } else {
                    setBackgroundImage(videoImageUrl[0]);
                    showRestrictMessage(getResources().getString(R.string.no_more_videos_in_queue));
                }
                break;
            case STATE_BUFFERING:
                showProgressBar("buffering...");

                if (beaconMessageThread != null) {
                    beaconMessageThread.sendBeaconPing = false;
                }
                if (beaconBufferingThread != null) {
                    beaconBufferingThread.sendBeaconBuffering = true;
                    if (!beaconBufferingThread.isAlive()) {
                        beaconBufferingThread.start();
                    }
                }

                break;
            case STATE_READY:
                hideProgressBar();

                if (beaconBufferingThread != null) {
                    beaconBufferingThread.sendBeaconBuffering = false;
                }
                if (beaconMessageThread != null) {
                    beaconMessageThread.sendBeaconPing = true;
                    if (!beaconMessageThread.isAlive()) {
                        beaconMessageThread.start();
                        mTotalVideoDuration = getDuration() / 1000;
                        mTotalVideoDuration -= mTotalVideoDuration % 4;
                    }
                    if (!sentBeaconFirstFrame) {
                        mStopBufferMilliSec = new Date().getTime();
                        ttfirstframe = mStartBufferMilliSec == 0l ? 0d : ((mStopBufferMilliSec - mStartBufferMilliSec) / 1000d);
                        appCMSPresenter.sendBeaconMessage(videoData.getGist().getId(),
                                videoData.getGist().getPermalink(),
                                parentScreenName,
                                getCurrentPosition(),
                                false,
                                AppCMSPresenter.BeaconEvent.FIRST_FRAME,
                                "Video",
                                getBitrate() != 0 ? String.valueOf(getBitrate()) : null,
                                String.valueOf(getVideoHeight()),
                                String.valueOf(getVideoWidth()),
                                mStreamId,
                                ttfirstframe,
                                0,
                                false);
                        sentBeaconFirstFrame = true;

                    }
                }
                break;
            default:
                hideProgressBar();
        }
    }

    private void setBackgroundImage(String videoImageUrl) {
        if (!TextUtils.isEmpty(videoImageUrl)) {
            imageViewContainer.setVisibility(VISIBLE);
            Glide.with(mContext)
                    .load(videoImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);
        }
    }

    public void pausePlayer() {
        super.pausePlayer();


        if (beaconMessageThread != null) {
            beaconMessageThread.sendBeaconPing = false;
        }
        if (beaconBufferingThread != null) {
            beaconBufferingThread.sendBeaconBuffering = false;
        }

    }

    public void resumePlayer() {
        if (null != getPlayer() && !getPlayer().getPlayWhenReady()) {
            if (appCMSPresenter.getCurrentActivity() != null && appCMSPresenter.getCurrentActivity() instanceof AppCmsHomeActivity) {
                if (((AppCmsHomeActivity) appCMSPresenter.getCurrentActivity()).isActive) {
                    getPlayer().setPlayWhenReady(true);

                    if (beaconMessageThread != null) {
                        beaconMessageThread.sendBeaconPing = true;
                    }
                    if (beaconBufferingThread != null) {
                        beaconBufferingThread.sendBeaconBuffering = true;
                    }
                }
            }
        }
    }

    private void createLoader() {
        custonLoaderContaineer = new LinearLayout(mContext);
        custonLoaderContaineer.setOrientation(LinearLayout.VERTICAL);
        custonLoaderContaineer.setGravity(Gravity.CENTER);
        ProgressBar progressBar = new ProgressBar(mContext);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().
                setColorFilter(Color.parseColor(Utils.getFocusColor(mContext, appCMSPresenter)),
                        PorterDuff.Mode.MULTIPLY
                );
        LinearLayout.LayoutParams progressbarParam = new LinearLayout.LayoutParams(50, 50);
        progressBar.setLayoutParams(progressbarParam);
        custonLoaderContaineer.addView(progressBar);
        loaderMessageView = new TextView(mContext);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loaderMessageView.setLayoutParams(textViewParams);
        custonLoaderContaineer.addView(loaderMessageView);
        this.addView(custonLoaderContaineer);
    }

    private Button loginButton ,  cancelButton;
    private void createCustomMessageView() {
        customMessageContaineer = new LinearLayout(mContext);
        customMessageContaineer.setOrientation(LinearLayout.VERTICAL);
        customMessageContaineer.setGravity(Gravity.CENTER);
        customMessageView = new TextView(mContext);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        customMessageView.setLayoutParams(textViewParams);
        customMessageView.setPadding(20, 20, 20, 20);
        if (customMessageView.getParent() != null) {
            ((ViewGroup) customMessageView.getParent()).removeView(customMessageView);
        }
        customMessageContaineer.addView(customMessageView);
        customMessageContaineer.setVisibility(View.INVISIBLE);


        loginButton = new Button(mContext);
        loginButton.setText("Login");
        loginButton.setLayoutParams(textViewParams);
        loginButton.setPadding(5,5,5,5);
        loginButton.setBackground(getResources().getDrawable(R.drawable.st_subscriber_module_color_selector));
        customMessageContaineer.addView(loginButton);
        loginButton.setVisibility(View.VISIBLE);

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appCMSPresenter.isNetworkConnected()) {
                    NavigationUser navigationUser = appCMSPresenter.getLoginNavigation();
                    appCMSPresenter.setLaunchType(AppCMSPresenter.LaunchType.NAVIGATE_TO_HOME_FROM_LOGIN_DIALOG);
                    appCMSPresenter.navigateToTVPage(
                            navigationUser.getPageId(),
                            navigationUser.getTitle(),
                            navigationUser.getUrl(),
                            false,
                            Uri.EMPTY,
                            false,
                            false,
                            true);
                }
            }
        });


/*

        cancelButton = new Button(mContext);
        cancelButton.setText("Cancel");
        cancelButton.setPadding(5,5,5,5);
        customMessageContaineer.addView(cancelButton);
        cancelButton.setLayoutParams(textViewParams);
        cancelButton.setBackground(getResources().getDrawable(R.drawable.st_subscriber_module_color_selector));
        cancelButton.setVisibility(View.VISIBLE);

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"Cancel ",Toast.LENGTH_SHORT).show();
            }
        });
*/


        this.addView(customMessageContaineer);
    }

    private void showProgressBar(String text) {
        if (null != custonLoaderContaineer && null != loaderMessageView) {
            loaderMessageView.setText(text);
            custonLoaderContaineer.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (null != custonLoaderContaineer) {
            custonLoaderContaineer.setVisibility(View.INVISIBLE);
        }
    }

     private void showRestrictMessage(String message) {
        if (null != customMessageContaineer && null != customMessageView) {
            hideProgressBar();
            customMessageView.setText(message);
            customMessageContaineer.setVisibility(View.VISIBLE);
            loginButton.requestFocus();
        }
    }

    private void hideRestrictedMessage() {
        if (null != customMessageContaineer) {
            customMessageContaineer.setVisibility(View.INVISIBLE);
        }
    }

    private void requestAds(String adTagUrl) {
        if (!TextUtils.isEmpty(adTagUrl) && adsLoader != null) {
            Log.d(TAG, "Requesting ads: " + adTagUrl);
            AdDisplayContainer adDisplayContainer = sdkFactory.createAdDisplayContainer();
            adDisplayContainer.setAdContainer(this);

            AdsRequest request = sdkFactory.createAdsRequest();
            request.setAdTagUrl(adTagUrl);
            request.setAdDisplayContainer(adDisplayContainer);
            /*request.setContentProgressProvider(new ContentProgressProvider() {
                @Override
                public VideoProgressUpdate getContentProgress() {
                    if (isAdDisplayed || videoPlayerView.getDuration() <= 0) {
                        return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                    }
                    return new VideoProgressUpdate(videoPlayerView.getCurrentPosition(),
                            videoPlayerView.getDuration());
                }
            });*/

            adsLoader.requestAds(request);
            isAdsDisplaying = true;

            apod += 1;
            if (appCMSPresenter != null) {
                appCMSPresenter.sendBeaconMessage(videoData.getGist().getId(),
                        videoData.getGist().getPermalink(),
                        parentScreenName,
                        getCurrentPosition(),
                        false,
                        AppCMSPresenter.BeaconEvent.AD_REQUEST,
                        "Video",
                        getBitrate() != 0 ? String.valueOf(getBitrate()) : null,
                        String.valueOf(getVideoHeight()),
                        String.valueOf(getVideoWidth()),
                        mStreamId,
                        0d,
                        apod,
                        false);
            }
        }
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        Log.d(TAG, "OnAdError: " + adErrorEvent.getError().getMessage());
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        Log.i(TAG, "onAdEvent: " + adEvent.getType());

        switch (adEvent.getType()) {
            case LOADED:
                adsManager.start();
                isAdsDisplaying = true;
                break;
            case CONTENT_PAUSE_REQUESTED:
                isAdDisplayed = true;
                if (beaconMessageThread != null) {
                    beaconMessageThread.sendBeaconPing = false;
                }

                if (appCMSPresenter != null) {
                    appCMSPresenter.sendBeaconMessage(videoData.getGist().getId(),
                            videoData.getGist().getPermalink(),
                            parentScreenName,
                            getCurrentPosition(),
                            false,
                            AppCMSPresenter.BeaconEvent.AD_IMPRESSION,
                            "Video",
                            getBitrate() != 0 ? String.valueOf(getBitrate()) : null,
                            String.valueOf(getVideoHeight()),
                            String.valueOf(getVideoWidth()),
                            mStreamId,
                            0d,
                            apod,
                            false);
                }
                getPlayer().setPlayWhenReady(false);
                break;
            case CONTENT_RESUME_REQUESTED:
                isAdDisplayed = false;
                this.startPlayer();
                if (beaconMessageThread != null) {
                    beaconMessageThread.sendBeaconPing = true;
                }

                if (appCMSPresenter != null) {
                    mStopBufferMilliSec = new Date().getTime();
                    ttfirstframe = mStartBufferMilliSec == 0l ? 0d : ((mStopBufferMilliSec - mStartBufferMilliSec) / 1000d);
                    appCMSPresenter.sendBeaconMessage(videoData.getGist().getId(),
                            videoData.getGist().getPermalink(),
                            parentScreenName,
                            getCurrentPosition(),
                            false,
                            AppCMSPresenter.BeaconEvent.FIRST_FRAME,
                            "Video",
                            getBitrate() != 0 ? String.valueOf(getBitrate()) : null,
                            String.valueOf(getVideoHeight()),
                            String.valueOf(getVideoWidth()),
                            mStreamId,
                            ttfirstframe,
                            0,
                            false);
                }
                if (beaconMessageThread != null && !beaconMessageThread.isAlive()) {
                    beaconMessageThread.start();

                }
                break;
            case ALL_ADS_COMPLETED:
                if (adsManager != null) {
                    adsManager.destroy();
                    adsManager = null;
                }
                isAdsDisplaying = false;
                getPlayer().setPlayWhenReady(true);
                /*if (isVisible() && isAdded()) {
                    preparePlayer();
                }
                videoPlayerInfoContainer.setVisibility(View.VISIBLE);*/ //show player controlls.
                break;
            default:
                break;
        }
    }

    public void startBeaconsThread() {
        beaconMsgTimeoutMsec = mContext.getResources().getInteger(R.integer.app_cms_beacon_timeout_msec);
        beaconBufferingTimeoutMsec = mContext.getResources().getInteger(R.integer.app_cms_beacon_buffering_timeout_msec);

        if (!sentBeaconPlay) {
            appCMSPresenter.sendBeaconMessage(videoData.getGist().getId(),
                    videoData.getGist().getPermalink(),
                    parentScreenName,
                    getCurrentPosition(),
                    false,
                    AppCMSPresenter.BeaconEvent.PLAY,
                    "Video",
                    getBitrate() != 0 ? String.valueOf(getBitrate()) : null,
                    String.valueOf(getVideoHeight()),
                    String.valueOf(getVideoWidth()),
                    mStreamId,
                    0d,
                    0,
                    false);
            sentBeaconPlay = true;
            mStartBufferMilliSec = new Date().getTime();
        }


        beaconMessageThread = new BeaconPingThread(
                beaconMsgTimeoutMsec,
                appCMSPresenter,
                videoData.getGist().getId(),
                videoData.getGist().getPermalink(),
                false,
                parentScreenName,
                this,
                mStreamId);

        beaconBufferingThread = new BeaconBufferingThread(
                beaconBufferingTimeoutMsec,
                appCMSPresenter,
                videoData.getGist().getId(),
                videoData.getGist().getPermalink(),
                parentScreenName,
                this,
                mStreamId);
    }


    public void releaseBeaconsThread() {
        try {
            if (null != beaconMessageThread) {
                beaconMessageThread.sendBeaconPing = false;
                beaconMessageThread.runBeaconPing = false;
                beaconMessageThread.videoPlayerView = null;
                beaconMessageThread = null;
            }

            if (null != beaconBufferingThread) {
                beaconBufferingThread.sendBeaconBuffering = false;
                beaconBufferingThread.runBeaconBuffering = false;
                beaconBufferingThread.videoPlayerView = null;
                beaconBufferingThread = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRefreshTokenCallback() {

    }

    @Override
    public void onFinishCallback(String message) {

        AppCMSPresenter.BeaconEvent event;
        if (message.contains("Unable")) {// If video position is something else then 0 It is dropped in between playing
            event = AppCMSPresenter.BeaconEvent.DROPPED_STREAM;
        } else if (message.contains("Response")) {
            event = AppCMSPresenter.BeaconEvent.FAILED_TO_START;
        } else {
            event = AppCMSPresenter.BeaconEvent.FAILED_TO_START;
        }

        appCMSPresenter.sendBeaconMessage(videoData.getGist().getId(),
                videoData.getGist().getPermalink(),
                parentScreenName,
                getCurrentPosition(),
                false,
                event,
                "Video",
                String.valueOf(getBitrate()),
                String.valueOf(getHeight()),
                String.valueOf(getWidth()),
                mStreamId,
                0d,
                0,
                false);
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }

    private static class BeaconPingThread extends Thread {
        final long beaconMsgTimeoutMsec;
        final AppCMSPresenter appCMSPresenter;
        final String filmId;
        final String permaLink;
        final String parentScreenName;
        final String mStreamId;
        VideoPlayerView videoPlayerView;
        boolean runBeaconPing;
        boolean sendBeaconPing;
        boolean isTrailer;
        int playbackState;


        public BeaconPingThread(long beaconMsgTimeoutMsec,
                                AppCMSPresenter appCMSPresenter,
                                String filmId,
                                String permaLink,
                                boolean isTrailer,
                                String parentScreenName,
                                VideoPlayerView videoPlayerView,
                                String mStreamId) {
            this.beaconMsgTimeoutMsec = beaconMsgTimeoutMsec;
            this.appCMSPresenter = appCMSPresenter;
            this.filmId = filmId;
            this.permaLink = permaLink;
            this.parentScreenName = parentScreenName;
            this.videoPlayerView = videoPlayerView;
            this.isTrailer = isTrailer;
            this.mStreamId = mStreamId;
        }

        @Override
        public void run() {
            runBeaconPing = true;
            while (runBeaconPing) {
                try {
                    Thread.sleep(beaconMsgTimeoutMsec);
                    if (sendBeaconPing) {

                        long currentTime = videoPlayerView.getCurrentPosition() / 1000;
                        playbackState = videoPlayerView.getPlayer().getPlaybackState();
                        boolean pingCondition = appCMSPresenter != null && videoPlayerView != null
                                && 30 <= (videoPlayerView.getCurrentPosition() / 1000)
                                && playbackState == ExoPlayer.STATE_READY && currentTime % 30 == 0;
                        if (pingCondition) { // For not to sent PIN in PAUSE mode
                            appCMSPresenter.sendBeaconMessage(filmId,
                                    permaLink,
                                    parentScreenName,
                                    videoPlayerView.getCurrentPosition(),
                                    false,
                                    AppCMSPresenter.BeaconEvent.PING,
                                    "Video",
                                    videoPlayerView.getBitrate() != 0 ? String.valueOf(videoPlayerView.getBitrate()) : null,
                                    String.valueOf(videoPlayerView.getVideoHeight()),
                                    String.valueOf(videoPlayerView.getVideoWidth()),
                                    mStreamId,
                                    0d,
                                    0,
                                    false);

                            if (!isTrailer && videoPlayerView != null) {
                                appCMSPresenter.updateWatchedTime(filmId,
                                        videoPlayerView.getCurrentPosition() / 1000);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "BeaconPingThread sleep interrupted");
                }
            }
        }
    }

    private static class BeaconBufferingThread extends Thread {
        final long beaconBufferTimeoutMsec;
        final AppCMSPresenter appCMSPresenter;
        final String filmId;
        final String permaLink;
        final String parentScreenName;
        final String mStreamId;
        VideoPlayerView videoPlayerView;
        boolean runBeaconBuffering;
        boolean sendBeaconBuffering;
        int bufferCount = 0;

        public BeaconBufferingThread(long beaconBufferTimeoutMsec,
                                     AppCMSPresenter appCMSPresenter,
                                     String filmId,
                                     String permaLink,
                                     String parentScreenName,
                                     VideoPlayerView videoPlayerView,
                                     String mStreamId) {
            this.beaconBufferTimeoutMsec = beaconBufferTimeoutMsec;
            this.appCMSPresenter = appCMSPresenter;
            this.filmId = filmId;
            this.permaLink = permaLink;
            this.parentScreenName = parentScreenName;
            this.videoPlayerView = videoPlayerView;
            this.mStreamId = mStreamId;
        }

        public void run() {
            runBeaconBuffering = true;
            while (runBeaconBuffering) {
                try {
                    Thread.sleep(beaconBufferTimeoutMsec);
                    if (sendBeaconBuffering) {
                        if (appCMSPresenter != null && videoPlayerView != null &&
                                videoPlayerView.getPlayer().getPlayWhenReady() &&
                                videoPlayerView.getPlayer().getPlaybackState() == ExoPlayer.STATE_BUFFERING) { // For not to sent PIN in PAUSE mode
                            bufferCount++;
                            if (bufferCount >= 5) {
                                appCMSPresenter.sendBeaconMessage(filmId,
                                        permaLink,
                                        parentScreenName,
                                        videoPlayerView.getCurrentPosition(),
                                        false,
                                        AppCMSPresenter.BeaconEvent.BUFFERING,
                                        "Video",
                                        videoPlayerView.getBitrate() != 0 ? String.valueOf(videoPlayerView.getBitrate()) : null,
                                        String.valueOf(videoPlayerView.getVideoHeight()),
                                        String.valueOf(videoPlayerView.getVideoWidth()),
                                        mStreamId,
                                        0d,
                                        0,
                                        false);
                                bufferCount = 0;
                            }

                        }
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "beaconBufferingThread sleep interrupted");
                }
            }
        }
    }

}
