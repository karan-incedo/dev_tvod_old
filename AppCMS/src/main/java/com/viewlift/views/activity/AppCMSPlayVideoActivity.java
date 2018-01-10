package com.viewlift.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.casting.CastHelper;
import com.viewlift.casting.CastingUtils;
import com.viewlift.models.data.appcms.api.AppCMSSignedURLResult;
import com.viewlift.models.data.appcms.api.ClosedCaptions;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.binders.AppCMSVideoPageBinder;
import com.viewlift.views.fragments.AppCMSPlayVideoFragment;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by viewlift on 6/14/17.
 * Owned by ViewLift, NYC
 */

public class AppCMSPlayVideoActivity extends AppCompatActivity implements
        AppCMSPlayVideoFragment.OnClosePlayerEvent,
        AppCMSPlayVideoFragment.OnUpdateContentDatumEvent {
    private static final String TAG = "VideoPlayerActivity";

    private BroadcastReceiver handoffReceiver;
    private ConnectivityManager connectivityManager;
    private BroadcastReceiver networkConnectedReceiver;
    private AppCMSPresenter appCMSPresenter;
    private int currentlyPlayingIndex = 0;
    private AppCMSVideoPageBinder binder;
    private List<String> relateVideoIds;
    private String title;
    private String hlsUrl;
    private String videoImageUrl;
    private String filmId;
    private String primaryCategory;
    private String contentRating;
    private long videoRunTime;
    private FrameLayout appCMSPlayVideoPageContainer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setFullScreenFocus();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_page);

        appCMSPresenter = ((AppCMSApplication) getApplication()).
                getAppCMSPresenterComponent().appCMSPresenter();


        getBundleData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            Fragment fragmentPlayer = getSupportFragmentManager().findFragmentById(R.id.app_cms_play_video_page_container);
            if (fragmentPlayer != null) {
                getSupportFragmentManager()
                        .beginTransaction().
                        remove(getSupportFragmentManager().findFragmentById(R.id.app_cms_play_video_page_container)).commitAllowingStateLoss();
            }
        } catch (Exception e) {

        }
        getBundleData();
        super.onNewIntent(intent);
    }

    private void getBundleData() {
        appCMSPlayVideoPageContainer =
                (FrameLayout) findViewById(R.id.app_cms_play_video_page_container);

        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(getString(R.string.app_cms_video_player_bundle_binder_key));
        String[] extra = intent.getStringArrayExtra(getString(R.string.video_player_hls_url_key));

        boolean useHls = getResources().getBoolean(R.bool.use_hls);
        String defaultVideoResolution = getString(R.string.default_video_resolution);

        try {
            binder = (AppCMSVideoPageBinder)
                    bundleExtra.getBinder(getString(R.string.app_cms_video_player_binder_key));
            String fontColor = "0xffffffff";
            if (binder != null) {
                fontColor = binder.getFontColor();
            }
            if (binder != null
                    && binder.getContentData() != null
                    && binder.getContentData().getGist() != null) {

                Gist gist = binder.getContentData().getGist();

                if (binder.isOffline()) {
                    Handler handler = new Handler();
                    String finalFontColor = fontColor;
                    handler.postDelayed(() -> {
                        try {
                            launchVideoPlayer(gist, extra, useHls, finalFontColor, defaultVideoResolution, intent, appCMSPlayVideoPageContainer, null);
                        } catch (Exception e) {

                        }
                    }, 500);
                } else {
                    String finalFontColor1 = fontColor;
                    String id = binder.getContentData().getGist().getId();
                    if (binder.isTrailer()) {
                        id = null;
                        if (binder.getContentData() != null &&
                                binder.getContentData().getContentDetails() != null &&
                                binder.getContentData().getContentDetails().getTrailers() != null &&
                                !binder.getContentData().getContentDetails().getTrailers().isEmpty() &&
                                binder.getContentData().getContentDetails().getTrailers().get(0) != null)
                            id = binder.getContentData().getContentDetails().getTrailers().get(0).getId();
                    }
                    if (id != null) {
                        appCMSPresenter.refreshVideoData(id,
                                updatedContentDatum -> {
                                    try {
                                        binder.setContentData(updatedContentDatum);
                                    } catch (Exception e) {

                                    }
                                    launchVideoPlayer(updatedContentDatum.getGist(), extra, useHls, finalFontColor1, defaultVideoResolution, intent, appCMSPlayVideoPageContainer, null);
                                });
                    }
                }
            }
        } catch (ClassCastException e) {
            //Log.e(TAG, e.getMessage());
        }

        handoffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sendingPage = intent.getStringExtra(getString(R.string.app_cms_closing_page_name));
                if (intent.getBooleanExtra(getString(R.string.close_self_key), true) &&
                        (sendingPage == null || getString(R.string.app_cms_video_page_tag).equals(sendingPage))) {
                    //Log.d(TAG, "Closing activity");
//                    finish();
                }
            }
        };

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkConnectedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                try {
                    if (((binder != null &&
                            binder.getContentData() != null &&
                            binder.getContentData().getGist() != null &&
                            ((binder.getContentData().getGist().getDownloadStatus() != null &&
                                    binder.getContentData().getGist().getDownloadStatus() != DownloadStatus.STATUS_COMPLETED &&
                                    binder.getContentData().getGist().getDownloadStatus() != DownloadStatus.STATUS_SUCCESSFUL) ||
                                    binder.getContentData().getGist().getDownloadStatus() == null))) &&
                            (activeNetwork == null ||
                                    !activeNetwork.isConnectedOrConnecting())) {
                        appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                                appCMSPresenter.getNetworkConnectedVideoPlayerErrorMsg(),
                                false, () -> closePlayer(),
                                null);
                    }
                } catch (Exception e) {
                    if ((binder != null &&
                            binder.getContentData() != null &&
                            binder.getContentData().getGist() != null &&
                            ((binder.getContentData().getGist().getDownloadStatus() != null &&
                                    binder.getContentData().getGist().getDownloadStatus() != DownloadStatus.STATUS_COMPLETED &&
                                    binder.getContentData().getGist().getDownloadStatus() != DownloadStatus.STATUS_SUCCESSFUL) ||
                                    binder.getContentData().getGist().getDownloadStatus() == null))) {
                        appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                                appCMSPresenter.getNetworkConnectedVideoPlayerErrorMsg(),
                                false, () -> closePlayer(),
                                null);
                    }
                }
            }
        };

        registerReceiver(handoffReceiver, new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void launchVideoPlayer(Gist gist,
                                  String[] extra,
                                  boolean useHls,
                                  String fontColor,
                                  String defaultVideoResolution,
                                  Intent intent,
                                  FrameLayout appCMSPlayVideoPageContainer,
                                  AppCMSSignedURLResult appCMSSignedURLResult) {
        String videoUrl = "";
        String closedCaptionUrl = null;
        title = gist.getTitle();
        if (gist != null && gist.getKisweEventId() != null &&
                gist.getKisweEventId().trim().length() > 0) {
            appCMSPresenter.launchKiswePlayer(gist.getKisweEventId());
            finish();
        } else if (binder.isOffline()
                && extra != null
                && extra.length >= 2
                && extra[1] != null
                && gist.getDownloadStatus().equals(DownloadStatus.STATUS_SUCCESSFUL)) {
            videoUrl = !TextUtils.isEmpty(extra[1]) ? extra[1] : "";
        }
                /*If the video is already downloaded, play if from there, even if Internet is
                * available*/
        else if (gist.getId() != null
                && appCMSPresenter.getRealmController() != null
                && appCMSPresenter.getRealmController().getDownloadById(gist.getId()) != null
                && appCMSPresenter.getRealmController().getDownloadById(gist.getId()).getDownloadStatus() != null
                && appCMSPresenter.getRealmController().getDownloadById(gist.getId()).getDownloadStatus().equals(DownloadStatus.STATUS_SUCCESSFUL)) {
            videoUrl = appCMSPresenter.getRealmController().getDownloadById(gist.getId()).getLocalURI();
        } else if (binder.getContentData() != null &&
                binder.getContentData().getStreamingInfo() != null &&
                binder.getContentData().getStreamingInfo().getVideoAssets() != null) {
            VideoAssets videoAssets = binder.getContentData().getStreamingInfo().getVideoAssets();


            if (useHls) {
                videoUrl = videoAssets.getHls();
            }
            if (TextUtils.isEmpty(videoUrl)) {
                if (videoAssets.getMpeg() != null && !videoAssets.getMpeg().isEmpty()) {
                    if (videoAssets.getMpeg().get(0) != null) {
                        videoUrl = videoAssets.getMpeg().get(0).getUrl();
                    }
                    for (int i = 0; i < videoAssets.getMpeg().size() && TextUtils.isEmpty(videoUrl); i++) {
                        if (videoAssets.getMpeg().get(i) != null &&
                                videoAssets.getMpeg().get(i).getRenditionValue() != null &&
                                videoAssets.getMpeg().get(i).getRenditionValue().contains(defaultVideoResolution)) {
                            videoUrl = videoAssets.getMpeg().get(i).getUrl();
                        }
                    }
                }
            }

            if (useHls && videoAssets.getMpeg() != null && videoAssets.getMpeg().size() > 0) {
                if (videoAssets.getMpeg().get(0).getUrl() != null &&
                        videoAssets.getMpeg().get(0).getUrl().indexOf("?") > 0) {
                    videoUrl = videoUrl + videoAssets.getMpeg().get(0).getUrl().substring(videoAssets.getMpeg().get(0).getUrl().indexOf("?"));
                }
            }
        }

        // TODO: 7/27/2017 Implement CC for multiple languages.
        if (binder.getContentData() != null
                && binder.getContentData().getContentDetails() != null
                && binder.getContentData().getContentDetails().getClosedCaptions() != null
                && !binder.getContentData().getContentDetails().getClosedCaptions().isEmpty()) {
            for (ClosedCaptions cc : binder.getContentData().getContentDetails().getClosedCaptions()) {
                if (cc.getUrl() != null &&
                        !cc.getUrl().equalsIgnoreCase(getString(R.string.download_file_prefix)) &&
                        cc.getFormat() != null &&
                        cc.getFormat().equalsIgnoreCase("SRT")) {
                    closedCaptionUrl = cc.getUrl();
                }
            }
        }

        String permaLink = gist.getPermalink();
        hlsUrl = videoUrl;
        videoImageUrl = gist.getVideoImageUrl();
        if (binder.getContentData() != null && binder.getContentData().getGist() != null) {
            filmId = binder.getContentData().getGist().getId();
            videoRunTime = binder.getContentData().getGist().getRuntime();
        }
        String adsUrl = binder.getAdsUrl();
        String bgColor = binder.getBgColor();
        int playIndex = binder.getCurrentPlayingVideoIndex();
        long watchedTime = intent.getLongExtra(getString(R.string.watched_time_key), 0L);
        long duration = binder.getContentData().getGist().getRuntime();
        if (duration <= watchedTime) {
            watchedTime = 0L;
        }
        if (gist.getPrimaryCategory() != null && gist.getPrimaryCategory().getTitle() != null) {
            primaryCategory = gist.getPrimaryCategory().getTitle();
        }
        boolean playAds = binder.isPlayAds();
        relateVideoIds = binder.getRelateVideoIds();
        currentlyPlayingIndex = binder.getCurrentPlayingVideoIndex();
        if (binder.getContentData() != null && binder.getContentData().getParentalRating() != null) {
            contentRating = binder.getContentData().getParentalRating() == null ? getString(R.string.age_rating_converted_default) : binder.getContentData().getParentalRating();
        }

        if (!TextUtils.isEmpty(bgColor)) {
            appCMSPlayVideoPageContainer.setBackgroundColor(Color.parseColor(bgColor));
        }

        boolean freeContent = false;
        if (binder.getContentData() != null && binder.getContentData().getGist() != null &&
                binder.getContentData().getGist().getFree()) {
            freeContent = binder.getContentData().getGist().getFree();
        }

        String finalClosedCaptionUrl = closedCaptionUrl;
        boolean finalFreeContent = freeContent;
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            final AppCMSPlayVideoFragment appCMSPlayVideoFragment =
                    AppCMSPlayVideoFragment.newInstance(this,
                            primaryCategory,
                            fontColor,
                            title,
                            permaLink,
                            binder.isTrailer(),
                            hlsUrl,
                            filmId,
                            adsUrl,
                            playAds,
                            playIndex,
                            watchedTime,
                            videoImageUrl,
                            finalClosedCaptionUrl,
                            contentRating, videoRunTime,
                            finalFreeContent,
                            appCMSSignedURLResult);
            fragmentTransaction.add(R.id.app_cms_play_video_page_container,
                    appCMSPlayVideoFragment,
                    getString(R.string.video_fragment_tag_key));
            fragmentTransaction.addToBackStack(getString(R.string.video_fragment_tag_key));
            fragmentTransaction.commit();
        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // This is to enable offline video playback even when Internet is not available.
        if (binder != null && !binder.isOffline() && !appCMSPresenter.isNetworkConnected()) {
            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                    appCMSPresenter.getNetworkConnectedVideoPlayerErrorMsg(),
                    false,
                    null,
                    null);
            finish();
        }

        registerReceiver(networkConnectedReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        appCMSPresenter.restrictLandscapeOnly();

        appCMSPresenter.setCancelAllLoads(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        appCMSPresenter.setEntitlementPendingVideoData(null);
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(handoffReceiver);
            unregisterReceiver(networkConnectedReceiver);
        } catch (Exception e) {
            //Log.e(TAG, "Failed to unregister Handoff Receiver: " + e.getMessage());
        }

        super.onDestroy();
    }


    @Override
    public void closePlayer() {
        finish();
    }

    @Override
    public void onMovieFinished() {
        if (appCMSPresenter.getAutoplayEnabledUserPref(getApplication())) {
            if (!binder.isOffline()) {
                if (!binder.isTrailer()
                        && relateVideoIds != null
                        && currentlyPlayingIndex != relateVideoIds.size() - 1) {
                    binder.setCurrentPlayingVideoIndex(currentlyPlayingIndex);
                    appCMSPresenter.openAutoPlayScreen(binder, new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                        }
                    });
                } else {
                    closePlayer();
                }
            } else {
                if (binder.getRelateVideoIds() != null
                        && currentlyPlayingIndex != relateVideoIds.size() - 1) {
                    appCMSPresenter.openAutoPlayScreen(binder, new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                        }
                    });
                } else {
                    closePlayer();
                }
            }
        } else {
            closePlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(networkConnectedReceiver);
        } catch (Exception e) {

        }
    }

    @Override
    public void onRemotePlayback(long currentPosition,
                                 int castingModeChromecast,
                                 boolean sendBeaconPlay,
                                 Action1<CastHelper.OnApplicationEnded> onApplicationEndedAction) {
//        getSupportFragmentManager()
//                .beginTransaction().
//                remove(getSupportFragmentManager().findFragmentById(R.id.app_cms_play_video_page_container)).commit();

        if (castingModeChromecast == CastingUtils.CASTING_MODE_CHROMECAST && !binder.isTrailer()) {
            CastHelper.getInstance(getApplicationContext()).launchRemoteMedia(appCMSPresenter,
                    relateVideoIds,
                    filmId,
                    currentPosition,
                    binder,
                    sendBeaconPlay,
                    onApplicationEndedAction);
        } else if (castingModeChromecast == CastingUtils.CASTING_MODE_CHROMECAST && binder.isTrailer()) {
            CastHelper.getInstance(getApplicationContext()).launchTrailer(appCMSPresenter, filmId, binder, currentPosition);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        setFullScreenFocus();
        super.onWindowFocusChanged(hasFocus);
    }

    private void setFullScreenFocus() {
        getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void updateContentDatum(ContentDatum contentDatum) {
        if (binder != null) {
            binder.setContentData(contentDatum);
        }
    }

    @Override
    public ContentDatum getCurrentContentDatum() {
        if (binder != null && binder.getContentData() != null) {
            return binder.getContentData();
        }
        return null;
    }

    @Override
    public List<String> getCurrentRelatedVideoIds() {
        if (binder != null && binder.getRelateVideoIds() != null) {
            return binder.getRelateVideoIds();
        }
        return null;
    }



}
