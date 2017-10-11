package com.viewlift.tv.views.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.views.fragment.AppCMSPlayVideoFragment;
import com.viewlift.views.binders.AppCMSVideoPageBinder;

import java.util.List;


/**
 * Created by viewlift on 6/14/17.
 */

public class AppCMSTVPlayVideoActivity extends Activity implements
        AppCMSPlayVideoFragment.OnClosePlayerEvent {
    private static final String TAG = "VideoPlayerActivity";

    private BroadcastReceiver handoffReceiver;
    private AppCMSPresenter appCMSPresenter;
    FrameLayout appCMSPlayVideoPageContainer;

    private AppCMSVideoPageBinder binder;

    private AppCMSPlayVideoFragment appCMSPlayVideoFragment;
    private String title;
    private String hlsUrl;
    private String videoImageUrl;
    private String filmId;
    private String primaryCategory;
    private List<String> relateVideoIds;
    private int currentlyPlayingIndex = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_video_player_page);
        appCMSPlayVideoPageContainer =
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

                String closedCaptionUrl = null;
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

                    // TODO: 7/27/2017 Implement CC for multiple languages.
                    if (binder.getContentData() != null
                            && binder.getContentData().getContentDetails() != null
                            && binder.getContentData().getContentDetails().getClosedCaptions() != null
                            && binder.getContentData().getContentDetails().getClosedCaptions().size() > 0
                            && binder.getContentData().getContentDetails().getClosedCaptions().get(0).getUrl() != null
                            && !binder.getContentData().getContentDetails().getClosedCaptions()
                            .get(0).getUrl().equalsIgnoreCase(
                                    getString(R.string.download_file_prefix))) {
                        closedCaptionUrl = binder.getContentData().getContentDetails().getClosedCaptions().get(0).getUrl();
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
                String permaLink = gist.getPermalink();
                hlsUrl = videoUrl;
                videoImageUrl = gist.getVideoImageUrl();
                filmId = binder.getContentData().getGist().getId();
                String adsUrl = binder.getAdsUrl();
                String bgColor = binder.getBgColor();
                int playIndex = binder.getCurrentPlayingVideoIndex();
                long watchedTime = binder.getContentData().getGist().getWatchedTime();
                if (gist.getPrimaryCategory() != null && gist.getPrimaryCategory().getTitle() != null)
                    primaryCategory = gist.getPrimaryCategory().getTitle();
                boolean playAds = binder.isPlayAds();
                relateVideoIds = binder.getRelateVideoIds();
                currentlyPlayingIndex = binder.getCurrentPlayingVideoIndex();

        if (!TextUtils.isEmpty(bgColor)) {
            appCMSPlayVideoPageContainer.setBackgroundColor(Color.parseColor(bgColor));
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        appCMSPlayVideoFragment =
                AppCMSPlayVideoFragment.newInstance(this,
                        null,
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
                        binder.getContentData().getGist().getRuntime(),
                        null,
                        closedCaptionUrl,
                        null);
        fragmentTransaction.add(R.id.app_cms_play_video_page_container,
                appCMSPlayVideoFragment,
                getString(R.string.video_fragment_tag_key));
        fragmentTransaction.addToBackStack(getString(R.string.video_fragment_tag_key));
        fragmentTransaction.commit();}
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
           // appCMSPresenter.showErrorDialog(AppCMSPresenter.Error.NETWORK, null); //TODO : need to show error dialog.
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean result = false;
        if(event.getAction() == KeyEvent.ACTION_DOWN ){
            result =  appCMSPlayVideoFragment.showController(event);
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if(null != appCMSPlayVideoPageContainer){
                        appCMSPlayVideoPageContainer.findViewById(R.id.exo_pause).requestFocus();
                        appCMSPlayVideoPageContainer.findViewById(R.id.exo_play).requestFocus();
                        return false;
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_REWIND:
                    if(null != appCMSPlayVideoPageContainer){
                        appCMSPlayVideoPageContainer.findViewById(R.id.exo_rew).requestFocus();
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                    if(null != appCMSPlayVideoPageContainer){
                        appCMSPlayVideoPageContainer.findViewById(R.id.exo_ffwd).requestFocus();
                        return true;
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event) || result;
    }
    @Override
    public void onMovieFinished() {
        if (!binder.isTrailer()
                && relateVideoIds != null
                && currentlyPlayingIndex != relateVideoIds.size() - 1) {
            binder.setCurrentPlayingVideoIndex(currentlyPlayingIndex);
            appCMSPresenter.openAutoPlayScreen(binder);
        } else {
            closePlayer();
        }
    }
}
