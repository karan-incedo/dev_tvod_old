package com.viewlift.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.viewlift.appcmssdk.R;
import com.viewlift.views.fragments.AppCMSPlayVideoFragment;

/**
 * Created by viewlift on 6/14/17.
 */

public class AppCMSPlayVideoActivity extends AppCompatActivity implements
        AppCMSPlayVideoFragment.OnClosePlayerEvent {
    private static final String TAG = "VideoPlayerActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_page);

        Intent intent = getIntent();
        String title = intent.getStringExtra(getString(R.string.video_player_title_key));
        String hlsUrl = intent.getStringExtra(getString(R.string.video_player_hls_url_key));
        String adsUrl = intent.getStringExtra(getString(R.string.video_player_ads_url_key));

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AppCMSPlayVideoFragment appCMSPlayVideoFragment =
                AppCMSPlayVideoFragment.newInstance(this,
                        title,
                        hlsUrl,
                        adsUrl);
        fragmentTransaction.add(R.id.app_cms_play_video_page_container,
                appCMSPlayVideoFragment,
                getString(R.string.video_fragment_tag_key));
        fragmentTransaction.addToBackStack(getString(R.string.video_fragment_tag_key));
        fragmentTransaction.commit();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void closePlayer() {
        finish();
    }
}
