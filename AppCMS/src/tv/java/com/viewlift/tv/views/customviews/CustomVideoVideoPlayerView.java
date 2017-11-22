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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.views.customviews.VideoPlayerView;

import java.util.List;

import rx.functions.Action1;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_READY;

/**
 * Created by viewlift on 5/31/17.
 */

public class CustomVideoVideoPlayerView
        extends VideoPlayerView
        implements AdErrorEvent.AdErrorListener,
        AdEvent.AdEventListener {

    private static final String TAG = CustomVideoVideoPlayerView.class.getSimpleName();
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

    public CustomVideoVideoPlayerView(Context context) {
        super(context);
        mContext = context;
        appCMSPresenter = ((AppCMSApplication) mContext.getApplicationContext()).getAppCMSPresenterComponent().appCMSPresenter();
        createLoader();
        createCustomMessageView();
    }


    int currentPlayingIndex = 0;
    List<String> relatedVideoId;


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
            adsManager.addAdErrorListener(CustomVideoVideoPlayerView.this);
            adsManager.addAdEventListener(CustomVideoVideoPlayerView.this);
            adsManager.init();
        });
    }

    public void setVideoUri(String videoId) {
        showProgressBar("Loading...");
        appCMSPresenter.refreshVideoData(videoId, new Action1<ContentDatum>() {
            @Override
            public void call(ContentDatum contentDatum) {
                /*if (!checkVideoSubscriptionStatus(contentDatum)) {
                    showRestrictMessage("This video is only available to Monumental Sports Network subscribers");
                    return;
                }*/
                if (!contentDatum.getGist().getFree()) {
                    //check login and subscription first.
                    if (!appCMSPresenter.isUserLoggedIn()) {
                        showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                    } else {
                        //check subscription data
                        appCMSPresenter.getSubscriptionData(appCMSUserSubscriptionPlanResult -> {
                            try {
                                if (appCMSUserSubscriptionPlanResult != null) {
                                    String subscriptionStatus = appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus();
                                    if (subscriptionStatus.equalsIgnoreCase("COMPLETED") ||
                                            subscriptionStatus.equalsIgnoreCase("DEFERRED_CANCELLATION")) {
                                        if (shouldRequestAds) requestAds(adsUrl);
                                        playVideos(0,contentDatum);
                                    } else {
                                        showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                                    }
                                } else {
                                    showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                                }
                            } catch (Exception e) {
                                showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                            }
                        });
                    }
                } else {
                    if (shouldRequestAds) requestAds(adsUrl);
                    playVideos(0,contentDatum);
                }
            }
        });
    }


    private void playVideos(int currentIndex , ContentDatum contentDatum){
        hideRestrictedMessage();
        String url = null;
        if (null != contentDatum && null != contentDatum.getStreamingInfo() && null != contentDatum.getStreamingInfo().getVideoAssets()) {
            if (null != contentDatum.getStreamingInfo().getVideoAssets().getHls()) {
                url = contentDatum.getStreamingInfo().getVideoAssets().getHls();
            } else if (null != contentDatum.getStreamingInfo().getVideoAssets().getMpeg()
                    && contentDatum.getStreamingInfo().getVideoAssets().getMpeg().size() > 0) {
                url = contentDatum.getStreamingInfo().getVideoAssets().getMpeg().get(0).getUrl();
            }
        }
        if (null != url) {
            setUri(Uri.parse(url), null);
            getPlayerView().getPlayer().setPlayWhenReady(true);
            if(currentIndex == 0) {
                relatedVideoId = contentDatum.getContentDetails().getRelatedVideoIds();
            }
            currentPlayingIndex = currentIndex;
            hideProgressBar();
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch (playbackState) {
            case STATE_ENDED:
                getPlayerView().getPlayer().setPlayWhenReady(false);
                currentPlayingIndex ++;
                if (null != relatedVideoId && currentPlayingIndex <= relatedVideoId.size() - 1) {
                    showProgressBar("Loading Next Video...");
                    appCMSPresenter.refreshVideoData(relatedVideoId.get(currentPlayingIndex), new Action1<ContentDatum>() {
                        @Override
                        public void call(ContentDatum contentDatum) {
                            if (!contentDatum.getGist().getFree()) {
                                //check login and subscription first.
                                if (!appCMSPresenter.isUserLoggedIn()) {
                                    showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                                } else {
                                    //check subscription data
                                    appCMSPresenter.getSubscriptionData(appCMSUserSubscriptionPlanResult -> {
                                        try {
                                            if (appCMSUserSubscriptionPlanResult != null) {
                                                String subscriptionStatus = appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus();
                                                if (subscriptionStatus.equalsIgnoreCase("COMPLETED") ||
                                                        subscriptionStatus.equalsIgnoreCase("DEFERRED_CANCELLATION")) {
                                                    if (shouldRequestAds) requestAds(adsUrl);
                                                    playVideos(currentPlayingIndex,contentDatum);
                                                } else {
                                                    showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                                                }
                                            } else {
                                                showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                                            }
                                        } catch (Exception e) {
                                            showRestrictMessage(getResources().getString(R.string.unsubscribe_text));
                                        }
                                    });
                                }
                            } else {
                                if (shouldRequestAds) requestAds(adsUrl);
                                playVideos(currentPlayingIndex,contentDatum);
                            }
                        }
                    });
                }
                break;
            case STATE_BUFFERING:
                showProgressBar("buffering...");
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
        super.releasePlayer();
    }


    public void resumePlayer() {
        if (null != getPlayer() && !getPlayer().getPlayWhenReady()) {
            getPlayer().setPlayWhenReady(true);
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


    private void createCustomMessageView() {
        customMessageContaineer = new LinearLayout(mContext);
        customMessageContaineer.setOrientation(LinearLayout.VERTICAL);
        customMessageContaineer.setGravity(Gravity.CENTER);
        customMessageContaineer.setBackgroundColor(Color.parseColor("#d4000000"));
        customMessageView = new TextView(mContext);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        customMessageView.setLayoutParams(textViewParams);
        customMessageView.setPadding(20, 20, 20, 20);
        if (customMessageView.getParent() != null) {
            ((ViewGroup) customMessageView.getParent()).removeView(customMessageView);
        }
        customMessageContaineer.addView(customMessageView);
        customMessageContaineer.setVisibility(View.INVISIBLE);
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

            /*apod += 1;
            if (appCMSPresenter != null) {
                appCMSPresenter.sendBeaconMessage(filmId,
                        permaLink,
                        parentScreenName,
                        videoPlayerView.getCurrentPosition(),
                        false,
                        AppCMSPresenter.BeaconEvent.AD_REQUEST,
                        "Video",
                        videoPlayerView.getBitrate() != 0 ? String.valueOf(videoPlayerView.getBitrate()) : null,
                        String.valueOf(videoPlayerView.getVideoHeight()),
                        String.valueOf(videoPlayerView.getVideoWidth()),
                        mStreamId,
                        0d,
                        apod,
                        false);
            }*/
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
                /*if (beaconMessageThread != null) {
                    beaconMessageThread.sendBeaconPing = false;
                }

                if (appCMSPresenter != null) {
                    appCMSPresenter.sendBeaconMessage(filmId,
                            permaLink,
                            parentScreenName,
                            videoPlayerView.getCurrentPosition(),
                            false,
                            AppCMSPresenter.BeaconEvent.AD_IMPRESSION,
                            "Video",
                            videoPlayerView.getBitrate() != 0 ? String.valueOf(videoPlayerView.getBitrate()) : null,
                            String.valueOf(videoPlayerView.getVideoHeight()),
                            String.valueOf(videoPlayerView.getVideoWidth()),
                            mStreamId,
                            0d,
                            apod,
                            false);
                }*/
                getPlayer().setPlayWhenReady(false);
                break;
            case CONTENT_RESUME_REQUESTED:
                isAdDisplayed = false;
                this.startPlayer();
                /*if (beaconMessageThread != null) {
                    beaconMessageThread.sendBeaconPing = true;
                }

                if (appCMSPresenter != null) {
                    mStopBufferMilliSec = new Date().getTime();
                    ttfirstframe = mStartBufferMilliSec == 0l ? 0d : ((mStopBufferMilliSec - mStartBufferMilliSec) / 1000d);
                    appCMSPresenter.sendBeaconMessage(filmId,
                            permaLink,
                            parentScreenName,
                            videoPlayerView.getCurrentPosition(),
                            false,
                            AppCMSPresenter.BeaconEvent.FIRST_FRAME,
                            "Video",
                            videoPlayerView.getBitrate() != 0 ? String.valueOf(videoPlayerView.getBitrate()) : null,
                            String.valueOf(videoPlayerView.getVideoHeight()),
                            String.valueOf(videoPlayerView.getVideoWidth()),
                            mStreamId,
                            ttfirstframe,
                            0,
                            false);
                }
                if (beaconMessageThread != null && !beaconMessageThread.isAlive()) {
                    beaconMessageThread.start();

                }*/


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
}
