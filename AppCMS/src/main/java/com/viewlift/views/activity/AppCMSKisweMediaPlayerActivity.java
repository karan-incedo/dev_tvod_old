package com.viewlift.views.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kiswe.kmsdkcorekit.KMSDKCoreKit;

import com.kiswe.kmsdkcorekit.mediaplayer.KisweMediaPlayer;
import com.kiswe.kmsdkcorekit.reports.Report;
import com.kiswe.kmsdkcorekit.reports.ReportSubscriber;
import com.kiswe.kmsdkcorekit.reports.Reports;
import com.kiswe.kmsdkcorekit.sdkinternal.fragment.KisweMediaPlayerFragment;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;

/**
 * Created by Sandeep.Singh on 12/5/17.
 */

public class AppCMSKisweMediaPlayerActivity extends AppCompatActivity implements KisweMediaPlayerFragment.KisweMediaPlayerFragmentListener {

    String eventId;

    private AppCMSPresenter appCMSPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiswe_player);
        KMSDKCoreKit.initialize(this.getApplicationContext());

        appCMSPresenter = ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter();
        appCMSPresenter.restrictLandscapeOnly();
        eventId = getIntent().getStringExtra("kisweEventId");

        KMSDKCoreKit mKit = KMSDKCoreKit.getInstance()
                            .addReportSubscriber(Reports.TYPE_STATUS, reportSubscriber)
                            .setLogLevel(KMSDKCoreKit.DEBUG);

        mKit.configUser(appCMSPresenter.isUserLoggedIn()?appCMSPresenter.getLoggedInUserEmail():"guest",
                getResources().getString(R.string.KISWE_PLAYER_API_KEY));

        KisweMediaPlayer mKiswePlayer = mKit.getMediaPlayerInstance(eventId);


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_video_container, mKiswePlayer.getKisweMediaPlayerFragment());
        ft.commit();

    }

    private ReportSubscriber reportSubscriber = new ReportSubscriber() {
        String TAG="ReportSubscriber";
        @Override
        public void handleReport(Report report) {

            if (!Reports.STATUS_SOURCE_PLAYER.equals(report.getString(Reports.FIELD_STATUS_SOURCE))) {
                return;
            }

            String eventId = report.getString(Reports.FIELD_STATUS_EVENT_ID, "unknown");
            String msg = report.getString(Reports.FIELD_STATUS_MESSAGE, "unknown status");
            int code = report.getInt(Reports.FIELD_STATUS_CODE, -1);

            Log.i(TAG, "(handleReport) Status (" + code + "): " + msg + " [" + eventId + "]");
        }
    };


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

    @Override
    public void onPlayheadChanged(String s) {

    }

    @Override
    public void onStreamChanged(String s) {

    }

    @Override
    public void onUiVisibilityChanged(int i) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
