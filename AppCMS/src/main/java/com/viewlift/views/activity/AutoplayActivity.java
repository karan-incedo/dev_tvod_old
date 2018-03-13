package com.viewlift.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.binders.AppCMSVideoPageBinder;
import com.viewlift.views.fragments.AutoplayFragment;

public class AutoplayActivity
        extends AppCompatActivity
        implements AutoplayFragment.FragmentInteractionListener,
        AutoplayFragment.OnPageCreation {

    private static final String TAG = "AutoplayActivity";
    AppCMSPresenter appCMSPresenter;
    private AppCMSVideoPageBinder binder;
    private BroadcastReceiver handoffReceiver;
    private AutoplayFragment autoplayFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handoffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sendingPage
                        = intent.getStringExtra(getString(R.string.app_cms_closing_page_name));
                if (intent.getBooleanExtra(getString(R.string.close_self_key), true) &&
                        (sendingPage == null || getString(R.string.app_cms_video_page_tag).equals(sendingPage))) {
                    //Log.d(TAG, "Closing activity");
                    finish();
                }
            }
        };

        registerReceiver(handoffReceiver, new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));
        appCMSPresenter = ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter();

        try {
            Intent intent = getIntent();
            Bundle bundleExtra = intent.getBundleExtra(getString(R.string.app_cms_video_player_bundle_binder_key));
            binder = (AppCMSVideoPageBinder)
                    bundleExtra.getBinder(getString(R.string.app_cms_video_player_binder_key));
            setContentView(R.layout.activity_autoplay);

            //Restore the fragment's instance
            autoplayFragment = (AutoplayFragment) getSupportFragmentManager()
                    .findFragmentByTag(binder.getContentData().getGist().getId());
            if (autoplayFragment == null) {
                createFragment(binder);
            }
        } catch (Exception e) {

        }
    }

    private void createFragment(AppCMSVideoPageBinder appCMSBinder) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            autoplayFragment = AutoplayFragment.newInstance(this, appCMSBinder);
            fragmentTransaction.replace(R.id.app_cms_fragment, autoplayFragment, appCMSBinder.getContentData().getGist().getId());
            fragmentTransaction.addToBackStack(appCMSBinder.getContentData().getGist().getId());
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
            //Log.e(TAG, "Failed to add Fragment to back stack");
        }
    }

    @Override
    public void onCountdownFinished() {

        appCMSPresenter.playNextVideo(binder,
                binder.getCurrentPlayingVideoIndex() + 1,
                binder.getContentData().getGist().getWatchedTime());
        binder.setCurrentPlayingVideoIndex(binder.getCurrentPlayingVideoIndex() + 1);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(handoffReceiver);
        autoplayFragment = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onSuccess(AppCMSVideoPageBinder binder) {

    }

    @Override
    public void onError(AppCMSVideoPageBinder binder) {

    }
}
