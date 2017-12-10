package com.viewlift.views.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.kiswe.kmsdkcorekit.KMSDKCoreKit;
import com.kiswe.kmsdkcorekit.KisweMediaPlayer;
import com.kiswe.kmsdkcorekit.fragment.KisweMediaPlayerFragment;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;

/**
 * Created by Sandeep.Singh on 12/5/17.
 */

public class  AppCMSKisweMediaPlayerActivity extends AppCompatActivity implements KisweMediaPlayerFragment.KisweMediaPlayerFragmentListener {

    String eventId;

    private AppCMSPresenter appCMSPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiswe_player);
        KMSDKCoreKit.initialize(this.getApplicationContext());

        appCMSPresenter = ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter();
        eventId=getIntent().getStringExtra("kisweEventId");

        KMSDKCoreKit mKit = KMSDKCoreKit.getInstance();
        /*if (appCMSPresenter.isUserLoggedIn()) {
            mKit.login(getLoggedInUserEmail(), currentContext.getResources().getString(R.string.KISWE_PLAYER_API_KEY));
        }
        else {
            mKit.configUser("guest", currentContext.getResources().getString(R.string.KISWE_PLAYER_API_KEY));
        }
*/

        KisweMediaPlayer mKiswePlayer = KMSDKCoreKit.getInstance().getMediaPlayerInstance(eventId);


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_video_container, mKiswePlayer.getKisweMediaPlayerFragment());
        ft.commit();
    }

    @Override
    public void onVideoUnavailable() {

    }

    @Override
    public void onPlayingFromCellularNetwork() {

    }

    @Override
    public void onNoNetworkConnection() {

    }

    @Override
    public void onVideoOutOfRegion() {

    }

    @Override
    public void onPlayerUpdateState(int i) {

    }
}
