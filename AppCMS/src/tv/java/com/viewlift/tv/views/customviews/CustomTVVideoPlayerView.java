package com.viewlift.tv.views.customviews;

        import android.content.Context;
        import android.graphics.Color;
        import android.graphics.PorterDuff;
        import android.graphics.Typeface;
        import android.net.Uri;
        import android.support.annotation.NonNull;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.FrameLayout;
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
        import com.google.android.exoplayer2.ExoPlaybackException;
        import com.google.android.exoplayer2.ExoPlayer;
        import com.viewlift.AppCMSApplication;
        import com.viewlift.R;
        import com.viewlift.models.data.appcms.api.ContentDatum;
        import com.viewlift.models.data.appcms.api.VideoAssets;
        import com.viewlift.models.data.appcms.ui.android.NavigationUser;
        import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
        import com.viewlift.presenters.AppCMSPresenter;
        import com.viewlift.tv.utility.Utils;
        import com.viewlift.tv.views.activity.AppCmsHomeActivity;
        import com.viewlift.views.customviews.TVVideoPlayerView;
        import com.viewlift.views.customviews.VideoPlayerView;

        import java.util.Date;
        import java.util.List;
        import java.util.Timer;
        import java.util.TimerTask;

        import rx.functions.Action1;

        import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
        import static com.google.android.exoplayer2.Player.STATE_ENDED;
        import static com.google.android.exoplayer2.Player.STATE_READY;

/**
 * Created by nitin.tyagi on 1/8/2018.
 */

public class CustomTVVideoPlayerView
        extends TVVideoPlayerView
        implements AdErrorEvent.AdErrorListener,
    AdEvent.AdEventListener,
    VideoPlayerView.ErrorEventListener {
        protected static final String TAG = TVVideoPlayerView.class.getSimpleName();
    private final AppCMSPresenter appCMSPresenter;
    private Context mContext;
    private LinearLayout custonLoaderContaineer;
    private TextView loaderMessageView;
    private LinearLayout customMessageContaineer;
    private TextView customMessageView;
    protected boolean shouldRequestAds = false;
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
    private int currentPlayingIndex = -1;
    protected List<String> relatedVideoId;
    private String parentScreenName;
    protected String mStreamId;
    private int apod;
    protected ContentDatum videoData = null;
    private BeaconBufferingThread beaconBufferingThread;
    private BeaconPingThread beaconMessageThread;
    private boolean sentBeaconPlay;
    private Timer timer;
    private TimerTask timerTask;
    private ContentDatum contentDatum;
    private boolean isHardPause;


    public CustomTVVideoPlayerView(Context context) {
        super(context);
        getPlayerView().setUseController(false);
        mContext = context;
        appCMSPresenter = ((AppCMSApplication) mContext.getApplicationContext()).getAppCMSPresenterComponent().appCMSPresenter();
        createLoader();
        createCustomMessageView();
        createTitleView();
        imageViewContainer = findViewById(R.id.videoPlayerThumbnailImageContainer);
        imageView = (ImageView) findViewById(R.id.videoPlayerThumbnailImage);
        setListener(this);
        parentScreenName = mContext.getString(R.string.app_cms_beacon_video_player_parent_screen_name);
        setupAds();
        getPlayerView().hideController();
        setOnPlayerControlsStateChanged(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                headerTitleContaineer.setVisibility(integer);
            }
        });
    }


    private String getAdsUrl(ContentDatum contentDatum){
        shouldRequestAds = false;
        String adsUrl = appCMSPresenter.getAdsUrl(appCMSPresenter.getPermalinkCompletePath(contentDatum.getGist().getPermalink()));
        if(adsUrl != null && !contentDatum.getStreamingInfo().getIsLiveStream()
                && (!appCMSPresenter.isUserSubscribed() )) {
            shouldRequestAds = true;
        }
        return adsUrl;
    }

    public void requestFocusOnLogin(){
        if(customMessageContaineer.getVisibility() == View.VISIBLE){
            //loginButton.requestFocus();
        }
    }

    public void setupAds() {
        sdkFactory = ImaSdkFactory.getInstance();
        adsLoader = sdkFactory.createAdsLoader(getContext());
        adsLoader.addAdErrorListener(this);
        adsLoader.addAdsLoadedListener(adsManagerLoadedEvent -> {
            adsManager = adsManagerLoadedEvent.getAdsManager();
            adsManager.addAdErrorListener(CustomTVVideoPlayerView.this);
            adsManager.addAdEventListener(CustomTVVideoPlayerView.this);
            adsManager.init();
        });
    }

    public void playVideos(int currentPlayingIndex, ContentDatum contentDatum) {
        try {
            mStreamId = appCMSPresenter.getStreamingId(videoData.getGist().getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }
        releaseBeaconsThread();
        startBeaconsThread();
        hideRestrictedMessage();
        String url = null;
        if (null != contentDatum && null != contentDatum.getStreamingInfo()) {
            shouldRequestAds = !contentDatum.getStreamingInfo().getIsLiveStream();
            if (null != contentDatum.getStreamingInfo().getVideoAssets()){
                url = getVideoUrl(contentDatum.getStreamingInfo().getVideoAssets());
            }
        }

        Log.d(TAG , "Url is = "+url);
        if (null != url) {
            lastUrl = url;
            setUri(Uri.parse(url), null);
            if (null != appCMSPresenter.getCurrentActivity() &&
                    appCMSPresenter.getCurrentActivity() instanceof AppCmsHomeActivity) {
                if (((AppCmsHomeActivity) appCMSPresenter.getCurrentActivity()).isActive
                        && !isHardPause()) {
                    getPlayerView().getPlayer().setPlayWhenReady(true);
                } else {
                    getPlayerView().getPlayer().setPlayWhenReady(false);
                }
            }

            if (currentPlayingIndex == -1) {
                relatedVideoId = contentDatum.getContentDetails().getRelatedVideoIds();
            }
            hideProgressBar();
        }

    }

    public void setVideoUri(String videoId) {
        showProgressBar("Loading...");
        appCMSPresenter.refreshVideoData(videoId, contentDatum -> {
            this.contentDatum = contentDatum;
            if (contentDatum.getStreamingInfo() != null) {
                isLiveStream = contentDatum.getStreamingInfo().getIsLiveStream();
            }
            setTitle();
            adsUrl = getAdsUrl(contentDatum);
            Log.d(TAG, "CVP Free : " + contentDatum.getGist().getFree());
            if (!contentDatum.getGist().getFree()) {
                //check login and subscription first.
                if (!appCMSPresenter.isUserLoggedIn()) {
                    if (userFreePlayTimeExceeded()) {
                        setBackgroundImage();
                        showRestrictMessage(getUnSubscribeOvelayText());
                        toggleLoginButtonVisibility(true);
                        exitFullScreenPlayer();
                    } else {
                        videoData = contentDatum;
                        if (shouldRequestAds)
                        {
                            requestAds(adsUrl);
                        }
                        else{
                            playVideos(0, contentDatum);
                            startFreePlayTimer();
                        }

                    }
                } else {
                    //check subscription data
                    appCMSPresenter.getSubscriptionData(appCMSUserSubscriptionPlanResult -> {
                        try {
                            if (appCMSUserSubscriptionPlanResult != null) {
                                String subscriptionStatus = appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus();
                                if ((subscriptionStatus.equalsIgnoreCase("COMPLETED") ||
                                        subscriptionStatus.equalsIgnoreCase("DEFERRED_CANCELLATION"))) {
                                    videoData = contentDatum;
                                    //  if (shouldRequestAds) requestAds(adsUrl);
                                    playVideos(0, contentDatum);
                                    // start free play time timer
                                } else if (!userFreePlayTimeExceeded()){
                                    videoData = contentDatum;
                                    if (shouldRequestAds){
                                        requestAds(adsUrl);
                                    }
                                    else {
                                        playVideos(0, contentDatum);
                                        startFreePlayTimer();
                                    }

                                } else {
                                    setBackgroundImage();
                                    showRestrictMessage(getUnSubscribeOvelayText());
                                    toggleLoginButtonVisibility(false);
                                    exitFullScreenPlayer();
                                }
                            } else {
                                setBackgroundImage();
                                showRestrictMessage(getUnSubscribeOvelayText());
                                toggleLoginButtonVisibility(false);
                                exitFullScreenPlayer();
                            }
                        } catch (Exception e) {
                            setBackgroundImage();
                            showRestrictMessage(getUnSubscribeOvelayText());
                            toggleLoginButtonVisibility(false);
                            exitFullScreenPlayer();
                        }
                    });
                }
            } else {
              /*  videoData = contentDatum;
              //  if (shouldRequestAds) requestAds(adsUrl);
                playVideos(0, contentDatum);*/
                videoData = contentDatum;
                if (shouldRequestAds){
                    requestAds(adsUrl);
                }else{
                    playVideos(0, contentDatum);
                }
            }
        });
    }


    private void stopTimer(){
        if (timer != null) {
            timer.cancel();
            timer = null;
            Log.d(TAG, "CVP timer cancelled");
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
            Log.d(TAG, "CVP timerTask cancelled");
        }
    }
    private void startFreePlayTimer() {
        if (timer != null || timerTask != null) {
            /*Means timer is already running*/
            return;
        }
        if(contentDatum != null
                && contentDatum.getGist() != null
                && contentDatum.getGist().getFree()){
            /*The video is free*/
            Log.d(TAG, "CVP Free : " + contentDatum.getGist().getFree());
            return;
        }
        Log.d(TAG, "CVP starting timer");
        final int totalFreePreviewTimeInMillis = getTotalFreePreviewTime();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (appCMSPresenter.getUserFreePlayTimePreference() >= totalFreePreviewTimeInMillis) {
                    stopTimer();
                    appCMSPresenter.getCurrentActivity().runOnUiThread(() -> {
                        String message = getUnSubscribeOvelayText();
                        if (appCMSPresenter.isUserLoggedIn()) {
                            String finalMessage = message;
                            appCMSPresenter.getSubscriptionData(appCMSUserSubscriptionPlanResult -> {
                                try {
                                    if (appCMSUserSubscriptionPlanResult != null) {
                                        String subscriptionStatus = appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus();
                                        if (!(subscriptionStatus.equalsIgnoreCase("COMPLETED") ||
                                                subscriptionStatus.equalsIgnoreCase("DEFERRED_CANCELLATION"))) {
                                            pausePlayer();
                                            setBackgroundImage();
                                            showRestrictMessage(finalMessage);
                                            toggleLoginButtonVisibility(false);
                                            exitFullScreenPlayer();
                                        }
                                    } else /*Unsubscribed*/{
                                        pausePlayer();
                                        setBackgroundImage();
                                        showRestrictMessage(finalMessage);
                                        toggleLoginButtonVisibility(false);
                                        exitFullScreenPlayer();
                                    }
                                } catch (Exception e) {
                                    pausePlayer();
                                    setBackgroundImage();
                                    showRestrictMessage(finalMessage);
                                    toggleLoginButtonVisibility(false);
                                    exitFullScreenPlayer();
                                }
                            });
                        } else {
                            pausePlayer();
                            showRestrictMessage(message);
                            setBackgroundImage();
                            toggleLoginButtonVisibility(true);
                            exitFullScreenPlayer();
                        }
                    });

                    return;
                }
                if (null != getPlayer() &&
                        getPlayer().getPlayWhenReady()) {
                    Log.d(TAG, "CVP Timer called off. " + (appCMSPresenter.getUserFreePlayTimePreference() + 1000));
                    appCMSPresenter.setUserFreePlayTimePreference(appCMSPresenter.getUserFreePlayTimePreference() + 1000);
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    @NonNull
    private String getUnSubscribeOvelayText() {
        String message = null;
        if (appCMSPresenter.getAppCMSAndroid() != null
                && appCMSPresenter.getAppCMSAndroid().getSubscriptionFlowContent() != null
                && appCMSPresenter.getAppCMSAndroid().getSubscriptionFlowContent().getOverlayMessage() != null) {
            message = appCMSPresenter.getAppCMSAndroid().getSubscriptionFlowContent().getOverlayMessage();
        }
        if (message == null) {
            message = getResources().getString(R.string.unsubscribe_text);
        }
        return message;
    }

    private boolean userFreePlayTimeExceeded() {
        final long userFreePlayTime = appCMSPresenter.getUserFreePlayTimePreference();
        final int maxPreviewSecs = getTotalFreePreviewTime();
        return userFreePlayTime >= maxPreviewSecs;
    }


    /**
     * Checks the value of the AppCMSMain > Features > Free Preview > Length > Unit > Multiplier and
     * return the value in milliseconds
     *
     * @return returns the value of free preview in milliseconds
     */
    private int getTotalFreePreviewTime() {
        AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
        int entitlementCheckMultiplier = 0;
        if (appCMSMain != null &&
                appCMSMain.getFeatures() != null &&
                appCMSMain.getFeatures().getFreePreview() != null &&
                appCMSMain.getFeatures().getFreePreview().isFreePreview() &&
                appCMSMain.getFeatures().getFreePreview().getLength() != null &&
                appCMSMain.getFeatures().getFreePreview().getLength().getUnit().equalsIgnoreCase("Minutes")) {
            try {
                entitlementCheckMultiplier = Integer.parseInt(appCMSMain.getFeatures().getFreePreview().getLength().getMultiplier());
            } catch (Exception e) {
                Log.e(TAG, "Error parsing free preview multiplier value: " + e.getMessage());
            }
        }
        return entitlementCheckMultiplier * 60 * 1000;
    }

    String lastUrl = null;

    protected String getVideoUrl(VideoAssets videoAssets) {
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
                currentPlayingIndex++;
//                getPlayerView().getPlayer().setPlayWhenReady(false);
                Log.d(TAG, "appCMSPresenter.getAutoplayEnabledUserPref(mContext): " +
                        appCMSPresenter.getAutoplayEnabledUserPref(mContext));
                if (null != relatedVideoId
                        && currentPlayingIndex <= relatedVideoId.size() - 1) {
                    if (appCMSPresenter.getAutoplayEnabledUserPref(mContext)) {
                        showProgressBar("Loading Next Video...");
                        setVideoUri(relatedVideoId.get(currentPlayingIndex));
                    } else /*Autoplay is turned-off*/ {
                        setBackgroundImage();
                        showRestrictMessage(getResources().getString(R.string.autoplay_off_msg));
                        toggleLoginButtonVisibility(false);
                        exitFullScreenPlayer();
                    }
                } else {
                    setBackgroundImage();
                    showRestrictMessage(getResources().getString(R.string.no_more_videos_in_queue));
                    toggleLoginButtonVisibility(false);
                    exitFullScreenPlayer();
                }
                break;
            case STATE_BUFFERING:
                showProgressBar(getResources().getString(R.string.buffering_text));

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
                        appCMSPresenter.sendBeaconMessage(contentDatum.getGist().getId(),
                                contentDatum.getGist().getPermalink(),
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

    private void setBackgroundImage() {
        if (mContext instanceof AppCmsHomeActivity) {
            if (((AppCmsHomeActivity) mContext).isFinishing()) {
                return;
            }
        }
        String videoImageUrl = null;
        if (contentDatum != null
                && contentDatum.getGist() != null
                && contentDatum.getGist().getVideoImageUrl() != null) {
            videoImageUrl = contentDatum.getGist().getVideoImageUrl();
        }
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
        stopTimer();

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
                    startFreePlayTimer();
                    if(!isHardPause()) {
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
        customMessageView.setGravity(Gravity.CENTER);
        customMessageView.setTextSize(20);
        customMessageView.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(Utils.getViewXAxisAsPerScreen(mContext , 1400), ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.setMargins(0, 0,0, 50);
        customMessageView.setLayoutParams(textViewParams);
        customMessageView.setPadding(20, 20, 20, 20);
        if (customMessageView.getParent() != null) {
            ((ViewGroup) customMessageView.getParent()).removeView(customMessageView);
        }
        customMessageContaineer.addView(customMessageView);
        customMessageContaineer.setVisibility(View.INVISIBLE);


        loginButton = new Button(mContext);
        String loginButtonText;

        if (appCMSPresenter.getAppCMSAndroid() != null
                && appCMSPresenter.getAppCMSAndroid().getSubscriptionFlowContent() != null
                && appCMSPresenter.getAppCMSAndroid().getSubscriptionFlowContent().getLoginButtonText() != null) {
            loginButtonText = appCMSPresenter.getAppCMSAndroid().getSubscriptionFlowContent().getLoginButtonText();
        } else {
            loginButtonText = mContext.getString(R.string.app_cms_login);
        }

        loginButton.setText(loginButtonText);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(300, 75);
        loginButton.setLayoutParams(buttonParams);
        loginButton.setPadding(50,0,50,0);
        loginButton.setBackgroundColor(getResources().getColor(R.color.colorAccent)/*getResources().getDrawable(R.drawable.st_subscriber_module_color_selector)*/);
        customMessageContaineer.addView(loginButton);
        loginButton.setVisibility(View.VISIBLE);

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appCMSPresenter.isNetworkConnected()) {
                    NavigationUser navigationUser = appCMSPresenter.getLoginNavigation();
                    if (null != navigationUser) {
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

    protected void hideProgressBar() {
        if (null != custonLoaderContaineer) {
            custonLoaderContaineer.setVisibility(View.INVISIBLE);
        }
    }

    public void toggleLoginButtonVisibility (boolean show) {
        if (loginButton != null) {
            loginButton.setVisibility(show ? VISIBLE : GONE);
        }
    }

    public boolean isLoginButtonVisible(){
        return ( (loginButton.getVisibility() == View.VISIBLE)
                && (customMessageContaineer.getVisibility() == View.VISIBLE));
    }

    public void performLoginButtonClick(){
        if(null != loginButton){
            loginButton.performClick();
        }
    }
    public void showRestrictMessage(String message) {
        if (null != customMessageContaineer && null != customMessageView) {
            hideProgressBar();
            customMessageView.setText(message);
            customMessageContaineer.setVisibility(View.VISIBLE);
            // loginButton.requestFocus();
        }
    }

    protected void hideRestrictedMessage() {
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

            adsLoader.requestAds(request);
            isAdsDisplaying = true;

            apod += 1;
            if (appCMSPresenter != null) {
                appCMSPresenter.sendBeaconMessage(contentDatum.getGist().getId(),
                        contentDatum.getGist().getPermalink(),
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
        Log.e(TAG, "OnAdError: " + adErrorEvent.getError().getMessage());
        playVideos(0,contentDatum);
        startFreePlayTimer();
        // startPlayer();
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        Log.d(TAG, "onAdEvent: " + adEvent.getType());

        switch (adEvent.getType()) {
            case LOADED:
                if(null != adsManager) {
                    adsManager.start();
                    isAdsDisplaying = true;
                }
                break;
            case CONTENT_PAUSE_REQUESTED:
                isAdDisplayed = true;
                if (beaconMessageThread != null) {
                    beaconMessageThread.sendBeaconPing = false;
                }

                if (appCMSPresenter != null) {
                    appCMSPresenter.sendBeaconMessage(contentDatum.getGist().getId(),
                            contentDatum.getGist().getPermalink(),
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
                // this.startPlayer();
                if (beaconMessageThread != null) {
                    beaconMessageThread.sendBeaconPing = true;
                }

                if (appCMSPresenter != null) {
                    mStopBufferMilliSec = new Date().getTime();
                    ttfirstframe = mStartBufferMilliSec == 0l ? 0d : ((mStopBufferMilliSec - mStartBufferMilliSec) / 1000d);
                    appCMSPresenter.sendBeaconMessage(contentDatum.getGist().getId(),
                            contentDatum.getGist().getPermalink(),
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
                playVideos(0,contentDatum);
                startFreePlayTimer();
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
            appCMSPresenter.sendBeaconMessage(contentDatum.getGist().getId(),
                    contentDatum.getGist().getPermalink(),
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
                contentDatum.getGist().getId(),
                contentDatum.getGist().getPermalink(),
                false,
                parentScreenName,
                this,
                mStreamId,
                isLiveStream());

        beaconBufferingThread = new BeaconBufferingThread(
                beaconBufferingTimeoutMsec,
                appCMSPresenter,
                contentDatum.getGist().getId(),
                contentDatum.getGist().getPermalink(),
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

    /**
     * Method is used to hide the progress bar, timer, rewind and forward button when a live stream
     * playing
     */
    public void hideControlsForLiveStream() {
        try {
            getPlayerView().findViewById(R.id.exo_position).setVisibility(isLiveStream ? GONE : VISIBLE);
            getPlayerView().findViewById(R.id.exo_progress).setVisibility(isLiveStream ? GONE : VISIBLE);
            getPlayerView().findViewById(R.id.exo_duration).setVisibility(isLiveStream ? GONE : VISIBLE);

            if (isLiveStream) {
                View rewind = getPlayerView().findViewById(R.id.exo_rew);
                rewind.setTag(rewind.getVisibility());
                    rewind.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                    if (rewind.getVisibility() == VISIBLE) {
                        rewind.setVisibility(GONE);
                    }
                });

                View forward = getPlayerView().findViewById(R.id.exo_ffwd);
                forward.setTag(rewind.getVisibility());
                forward.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                    if (forward.getVisibility() == VISIBLE) {
                        forward.setVisibility(GONE);
                    }
                });
            }
        } catch (Exception e) {
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
        boolean isLiveStream;


        public BeaconPingThread(long beaconMsgTimeoutMsec,
                                AppCMSPresenter appCMSPresenter,
                                String filmId,
                                String permaLink,
                                boolean isTrailer,
                                String parentScreenName,
                                VideoPlayerView videoPlayerView,
                                String mStreamId,
                                boolean isLiveStream) {
            this.beaconMsgTimeoutMsec = beaconMsgTimeoutMsec;
            this.appCMSPresenter = appCMSPresenter;
            this.filmId = filmId;
            this.permaLink = permaLink;
            this.parentScreenName = parentScreenName;
            this.videoPlayerView = videoPlayerView;
            this.isTrailer = isTrailer;
            this.mStreamId = mStreamId;
            this.isLiveStream = isLiveStream;
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

                            if (!isTrailer && videoPlayerView != null && !isLiveStream) {
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
    LinearLayout headerTitleContaineer;
    TextView titleView;
    private void createTitleView(){
        headerTitleContaineer = new LinearLayout(getContext());
        FrameLayout.LayoutParams containeerParam = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , Utils.getViewYAxisAsPerScreen(mContext , 100));
        containeerParam.gravity = Gravity.TOP;

        headerTitleContaineer.setLayoutParams(containeerParam);
        headerTitleContaineer.setGravity(Gravity.CENTER_VERTICAL);
        headerTitleContaineer.setBackgroundColor(getResources().getColor(R.color.appcms_shadow_color));

        titleView = new TextView(getContext());
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewParams.leftMargin = 45;
        titleView.setLayoutParams(textViewParams);
        titleView.setSingleLine(true);
        titleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        titleView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        titleView.setTextColor(Color.parseColor(Utils.getTextColor(getContext() , appCMSPresenter)));
        titleView.setTextSize(24);
        headerTitleContaineer.addView(titleView);
        addView(headerTitleContaineer);
        headerTitleContaineer.setVisibility(View.INVISIBLE);
    }

    private void setTitle(){
        if(null != titleView){
            titleView.setText(contentDatum.getGist().getTitle());
        }

    }

    private void exitFullScreenPlayer(){
        getPlayerView().hideController();
        getPlayerView().setUseController(false);
        headerTitleContaineer.setVisibility(INVISIBLE);
        new android.os.Handler().postDelayed(() -> {
            appCMSPresenter.exitFullScreenTVPlayer();
        },100);
    }


    @Override
    public void onPlayerError(ExoPlaybackException e) {
        super.onPlayerError(e);
        String errorString = null;
        if (e instanceof ExoPlaybackException) {
            errorString = e.getCause().toString();
            setUri(Uri.parse(lastUrl), null);
        }
    }
}
