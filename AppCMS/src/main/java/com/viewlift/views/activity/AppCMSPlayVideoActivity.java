package com.viewlift.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.views.fragments.AppCMSPlayVideoFragment;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 6/14/17.
 */

public class AppCMSPlayVideoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_page);

        Intent intent = getIntent();
        String hlsUrl = intent.getStringExtra(getString(R.string.video_fragment_tag_key));
        int bgColor = intent.getIntExtra(getString(R.string.app_cms_bg_color_key),
                getResources().getColor(R.color.colorPrimaryDark));
        FrameLayout appCMSPlayVideoPageContainer =
                (FrameLayout) findViewById(R.id.app_cms_play_video_page_container);
        appCMSPlayVideoPageContainer.setBackgroundColor(bgColor);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AppCMSPlayVideoFragment appCMSPlayVideoFragment =
                AppCMSPlayVideoFragment.newInstance(this, hlsUrl);
        fragmentTransaction.add(R.id.app_cms_play_video_page_container,
                appCMSPlayVideoFragment,
                getString(R.string.video_fragment_tag_key));
        fragmentTransaction.addToBackStack(getString(R.string.video_fragment_tag_key));
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
