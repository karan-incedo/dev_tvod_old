package com.viewlift.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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
import com.viewlift.views.customviews.BaseView;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_page);

        appCMSPresenter = ((AppCMSApplication) getApplication()).
                getAppCMSPresenterComponent().appCMSPresenter();

        FrameLayout appCMSPlayVideoPageContainer =
                (FrameLayout) findViewById(R.id.app_cms_play_video_page_container);

        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(getString(R.string.app_cms_video_player_bundle_binder_key));
        String[] extra = intent.getStringArrayExtra(getString(R.string.video_player_hls_url_key));

        boolean useHls = getResources().getBoolean(R.bool.use_hls);
        String defaultVideoResolution = getString(R.string.default_video_resolution);

        try {
            binder = (AppCMSVideoPageBinder)
                    bundleExtra.getBinder(getString(R.string.app_cms_video_player_binder_key));
            if (binder != null
                    && binder.getContentData() != null
                    && binder.getContentData().getGist() != null) {

                appCMSPresenter.getAppCMSSignedURL(filmId, appCMSSignedURLResult -> {
                    Gist gist = binder.getContentData().getGist();
                    String videoUrl = "";
                    String fontColor = binder.getFontColor();
                    title = "";

                    if (appCMSSignedURLResult == null ||
                            TextUtils.isEmpty(appCMSSignedURLResult.getSigned()) &&
                                    (TextUtils.isEmpty(appCMSSignedURLResult.getPolicy()) ||
                                            TextUtils.isEmpty(appCMSSignedURLResult.getSignature()) ||
                                            TextUtils.isEmpty(appCMSSignedURLResult.getKeyPairId()))) {
                        appCMSSignedURLResult = new AppCMSSignedURLResult();
                        appCMSSignedURLResult.setSigned(hlsUrl);
                    }

                    String closedCaptionUrl = null;
                    if (!binder.isTrailer()) {
                        title = gist.getTitle();
                        if (binder.isOffline()
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
                        } else if (binder.getContentData().getStreamingInfo() != null &&
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
                    } else {

                        if (binder.isOffline()
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
                        } else if (binder.getContentData().getContentDetails() != null
                                && binder.getContentData().getContentDetails().getTrailers() != null
                                && !binder.getContentData().getContentDetails().getTrailers().isEmpty()
                                && binder.getContentData().getContentDetails().getTrailers().get(0) != null
                                && binder.getContentData().getContentDetails().getTrailers().get(0).getVideoAssets() != null) {
                            title = binder.getContentData().getContentDetails().getTrailers().get(0).getTitle();
                            VideoAssets videoAssets = binder.getContentData().getContentDetails().getTrailers().get(0).getVideoAssets();
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
                        } else {
                            if (gist != null) {
                                title = gist.getTitle();
                            }
                            if (binder.getContentData() != null &&
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
                        }
                    }
                    String permaLink = gist.getPermalink();
                    hlsUrl = videoUrl;
                    videoImageUrl = gist.getVideoImageUrl();
                    if (binder.getContentData() != null && binder.getContentData().getGist() != null) {
                        filmId = binder.getContentData().getGist().getId();
                    }
                    if (binder.getContentData() != null &&
                            binder.getContentData().getGist() != null) {
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
                });
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
                    finish();
                }
            }
        };

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkConnectedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                try {
                    if (((binder.getContentData().getGist().getDownloadStatus() != null &&
                            binder.getContentData().getGist().getDownloadStatus() != DownloadStatus.STATUS_COMPLETED &&
                            binder.getContentData().getGist().getDownloadStatus() != DownloadStatus.STATUS_SUCCESSFUL) ||
                            binder.getContentData().getGist().getDownloadStatus() == null) &&
                            (activeNetwork == null ||
                            !activeNetwork.isConnectedOrConnecting())) {
                        appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                                appCMSPresenter.getNetworkConnectedVideoPlayerErrorMsg(),
                                false, () -> closePlayer(),
                                null);
                    }
                } catch (Exception e) {
                    if ((binder.getContentData().getGist().getDownloadStatus() != null &&
                            binder.getContentData().getGist().getDownloadStatus() != DownloadStatus.STATUS_COMPLETED &&
                            binder.getContentData().getGist().getDownloadStatus() != DownloadStatus.STATUS_SUCCESSFUL) ||
                            binder.getContentData().getGist().getDownloadStatus() == null) {
                        appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                                appCMSPresenter.getNetworkConnectedVideoPlayerErrorMsg(),
                                false, () -> closePlayer(),
                                null);
                    }
                }
            }
        };

        registerReceiver(handoffReceiver, new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));
        registerReceiver(networkConnectedReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        appCMSPresenter.setCancelAllLoads(false);
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

        appCMSPresenter.restrictLandscapeOnly();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
                    appCMSPresenter.openAutoPlayScreen(binder);
                } else {
                    closePlayer();
                }
            } else {
                if (binder.getRelateVideoIds() != null
                        && currentlyPlayingIndex != relateVideoIds.size() - 1) {
                    appCMSPresenter.openAutoPlayScreen(binder);
                } else {
                    closePlayer();
                }
            }
        } else {
            closePlayer();
        }
    }

    @Override
    public void onRemotePlayback(long currentPosition,
                                 int castingModeChromecast,
                                 boolean sendBeaconPlay,
                                 Action1<CastHelper.OnApplicationEnded> onApplicationEndedAction) {
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
}
