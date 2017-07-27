package com.viewlift.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import com.viewlift.casting.CastHelper;
import com.viewlift.casting.CastServiceProvider;
import com.viewlift.casting.CastingUtils;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.binders.AppCMSVideoPageBinder;
import com.viewlift.views.fragments.AppCMSPlayVideoFragment;

import java.util.List;

import com.viewlift.R;

/**
 * Created by viewlift on 6/14/17.
 * Owned by ViewLift, NYC
 */

public class AppCMSPlayVideoActivity extends AppCompatActivity implements
        AppCMSPlayVideoFragment.OnClosePlayerEvent {
    private static final String TAG = "VideoPlayerActivity";

    private BroadcastReceiver handoffReceiver;
    private AppCMSPresenter appCMSPresenter;
    private int currentlyPlayingIndex = 0;
    private AppCMSVideoPageBinder binder;
    private List<String> relateVideoIds;
    private String title;
    private String hlsUrl;
    private String videoImageUrl;
    private String filmId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_page);
        FrameLayout appCMSPlayVideoPageContainer =
                (FrameLayout) findViewById(R.id.app_cms_play_video_page_container);

        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(getString(R.string.app_cms_video_player_bundle_binder_key));

        try {
            binder = (AppCMSVideoPageBinder)
                    bundleExtra.getBinder(getString(R.string.app_cms_video_player_binder_key));
            if (binder != null
                    && binder.getContentData() != null
                    && binder.getContentData().getGist() != null) {
                Gist gist = binder.getContentData().getGist();
                String videoUrl = "";
                String fontColor = binder.getFontColor();
                title = "";
                if (!binder.isTrailer()) {
                    title = gist.getTitle();
                    if (binder.getContentData().getStreamingInfo() != null &&
                            binder.getContentData().getStreamingInfo().getVideoAssets() != null) {
                        VideoAssets videoAssets = binder.getContentData().getStreamingInfo().getVideoAssets();
                        videoUrl = videoAssets.getHls();
                        if (TextUtils.isEmpty(videoUrl)) {
                            for (int i = 0; i < videoAssets.getMpeg().size() && TextUtils.isEmpty(videoUrl); i++) {
                                videoUrl = videoAssets.getMpeg().get(i).getUrl();
                            }
                        }
                    }
                } else {
                    if (binder.getContentData().getContentDetails() != null
                            && binder.getContentData().getContentDetails().getTrailers() != null
                            && binder.getContentData().getContentDetails().getTrailers().get(0) != null
                            && binder.getContentData().getContentDetails().getTrailers().get(0).getVideoAssets() != null) {
                        title = binder.getContentData().getContentDetails().getTrailers().get(0).getTitle();
                        VideoAssets videoAssets = binder.getContentData().getContentDetails().getTrailers().get(0).getVideoAssets();
                        videoUrl = videoAssets.getHls();
                        if (TextUtils.isEmpty(videoUrl)) {
                            for (int i = 0; i < videoAssets.getMpeg().size() && TextUtils.isEmpty(videoUrl); i++) {
                                videoUrl = videoAssets.getMpeg().get(i).getUrl();
                            }
                        }
                    }
                }
                String closedCaptionUrl = null;
                // TODO: 7/27/2017 Implement CC for multiple languages.
                if (binder.getContentData() != null
                        && binder.getContentData().getContentDetails() != null
                        && binder.getContentData().getContentDetails().getClosedCaptions() != null
                        && binder.getContentData().getContentDetails().getClosedCaptions().get(0).getUrl() != null){
                    closedCaptionUrl = binder.getContentData().getContentDetails().getClosedCaptions().get(0).getUrl();
                }
                String permaLink = gist.getPermalink();
                hlsUrl = videoUrl;
                videoImageUrl = gist.getVideoImageUrl();
                filmId = binder.getContentData().getGist().getId();
                String adsUrl = binder.getAdsUrl();
                String bgColor = binder.getBgColor();
                int watchedTime = gist.getWatchedTime();
                boolean playAds = binder.isPlayAds();
                relateVideoIds = binder.getRelateVideoIds();
                currentlyPlayingIndex = binder.getCurrentPlayingVideoIndex();

                if (!TextUtils.isEmpty(bgColor)) {
                    appCMSPlayVideoPageContainer.setBackgroundColor(Color.parseColor(bgColor));
                }

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                final AppCMSPlayVideoFragment appCMSPlayVideoFragment =
                        AppCMSPlayVideoFragment.newInstance(this,
                                fontColor,
                                title,
                                permaLink,
                                hlsUrl,
                                filmId,
                                adsUrl,
                                playAds,
                                watchedTime,
                                videoImageUrl,
                                closedCaptionUrl);
                fragmentTransaction.add(R.id.app_cms_play_video_page_container,
                        appCMSPlayVideoFragment,
                        getString(R.string.video_fragment_tag_key));
                fragmentTransaction.addToBackStack(getString(R.string.video_fragment_tag_key));
                fragmentTransaction.commit();
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        handoffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sendingPage = intent.getStringExtra(getString(R.string.app_cms_closing_page_name));
                if (intent.getBooleanExtra(getString(R.string.close_self_key), true) &&
                        (sendingPage == null || getString(R.string.app_cms_video_page_tag).equals(sendingPage))) {
                    Log.d(TAG, "Closing activity");
                    finish();
                }
            }
        };

        registerReceiver(handoffReceiver, new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));

        appCMSPresenter =
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!appCMSPresenter.isNetworkConnected()) {
            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                    null,
                    null);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(handoffReceiver);
        super.onDestroy();
    }

    @Override
    public void closePlayer() {
        finish();
    }

    @Override
    public void onMovieFinished() {
        // TODO: 7/12/2017 Add a check for autoplay from settings
        if (!binder.isTrailer()
                && relateVideoIds != null
                && currentlyPlayingIndex != relateVideoIds.size() - 1) {
            binder.setCurrentPlayingVideoIndex(currentlyPlayingIndex);
            appCMSPresenter.openAutoPlayScreen(binder);
        }
        closePlayer();
    }

    @Override
    public void onRemotePlayback(long currentPosition, int castingModeChromecast) {
        // TODO: Add a check for autoplay from settings
        if(castingModeChromecast== CastingUtils.CASTING_MODE_CHROMECAST){
            CastHelper.getInstance(getApplicationContext()).launchRemoteMedia(appCMSPresenter, relateVideoIds,filmId, currentPosition, binder);
        }else if(castingModeChromecast== CastingUtils.CASTING_MODE_ROKU){
            CastServiceProvider.getInstance(getApplicationContext()).launchRokuCasting(filmId,videoImageUrl,title);
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
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
