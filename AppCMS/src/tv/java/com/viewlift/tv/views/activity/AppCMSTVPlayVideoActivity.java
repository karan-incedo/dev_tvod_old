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
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.views.fragment.AppCMSPlayVideoFragment;


/**
 * Created by viewlift on 6/14/17.
 */

public class AppCMSTVPlayVideoActivity extends Activity implements
        AppCMSPlayVideoFragment.OnClosePlayerEvent {
    private static final String TAG = "VideoPlayerActivity";

    private BroadcastReceiver handoffReceiver;
    private AppCMSPresenter appCMSPresenter;
    FrameLayout appCMSPlayVideoPageContainer;

    private AppCMSPlayVideoFragment appCMSPlayVideoFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_page);
        appCMSPlayVideoPageContainer =
                (FrameLayout) findViewById(R.id.app_cms_play_video_page_container);

        Intent intent = getIntent();
        String title = intent.getStringExtra(getString(R.string.video_player_title_key));
        String fontColor = intent.getStringExtra(getString(R.string.video_player_font_color_key));
        String[] extraData = intent.getStringArrayExtra(getString(R.string.video_player_hls_url_key));
        String permaLink = extraData[0];
        String hlsUrl = extraData[1];
        String filmId = extraData[2];
        String adsUrl = intent.getStringExtra(getString(R.string.video_player_ads_url_key));
        String bgColor = intent.getStringExtra(getString(R.string.app_cms_bg_color_key));
        String closedCaptionUrl = intent.getStringExtra(getString(R.string.video_player_closed_caption_key));
        long watchedTime = intent.getLongExtra(getString(R.string.video_player_watched_time_key), 0);
        long runtime = intent.getLongExtra(getString(R.string.video_player_run_time_key), 0);
        boolean playAds = intent.getBooleanExtra(getString(R.string.play_ads_key), true);


        System.out.println("Ads Url = "+adsUrl);

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
                        false,
                        hlsUrl,
                        filmId,
                        adsUrl,
                        playAds,
                        0,
                        watchedTime,
                        runtime,
                        null,
                        closedCaptionUrl,
                        null);
        fragmentTransaction.add(R.id.app_cms_play_video_page_container,
                appCMSPlayVideoFragment,
                getString(R.string.video_fragment_tag_key));
        fragmentTransaction.addToBackStack(getString(R.string.video_fragment_tag_key));
        fragmentTransaction.commit();

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
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_REWIND:
                    if(null != appCMSPlayVideoPageContainer){
                        appCMSPlayVideoPageContainer.findViewById(R.id.exo_rew).requestFocus();
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                    if(null != appCMSPlayVideoPageContainer){
                        appCMSPlayVideoPageContainer.findViewById(R.id.exo_ffwd).requestFocus();
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event) || result;
    }

}
