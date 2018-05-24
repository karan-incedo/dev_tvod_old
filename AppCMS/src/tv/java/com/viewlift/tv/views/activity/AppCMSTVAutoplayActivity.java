package com.viewlift.tv.views.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.views.fragment.AppCMSTVAutoplayFragment;
import com.viewlift.views.binders.AppCMSVideoPageBinder;

public class AppCMSTVAutoplayActivity extends AppCmsBaseActivity
        implements AppCMSTVAutoplayFragment.FragmentInteractionListener,
        AppCMSTVAutoplayFragment.OnPageCreation {

    private static final String TAG = "TVAutoPlayActivity";
    AppCMSPresenter appCMSPresenter;
    private AppCMSVideoPageBinder binder;
    private BroadcastReceiver handoffReceiver;
    private AppCMSTVAutoplayFragment autoplayFragment;

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
                    Log.d(TAG, "Closing activity");
                    finish();
                }
            }
        };

        registerReceiver(handoffReceiver, new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));
        appCMSPresenter = ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter();

        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(getString(R.string.app_cms_video_player_bundle_binder_key));
        binder = (AppCMSVideoPageBinder)
                bundleExtra.getBinder(getString(R.string.app_cms_video_player_binder_key));
        setContentView(R.layout.activity_app_cms_tv_auto_play);

        //Restore the fragment's instance
        autoplayFragment = (AppCMSTVAutoplayFragment) getFragmentManager()
                .findFragmentByTag(binder.getContentData().getGist().getId());
        if (autoplayFragment == null) {
            createFragment(binder);
        }
    }

    private void createFragment(AppCMSVideoPageBinder appCMSBinder) {
        try {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            autoplayFragment = AppCMSTVAutoplayFragment.newInstance(this, appCMSBinder);
            fragmentTransaction.replace(R.id.app_cms_fragment, autoplayFragment, appCMSBinder.getContentData().getGist().getId());
            fragmentTransaction.addToBackStack(appCMSBinder.getContentData().getGist().getId());
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Failed to add Fragment to back stack");
        }
    }

    @Override
    public void onSuccess(AppCMSVideoPageBinder binder) {

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
        closeActivity();
    }

    @Override
    public void onError(AppCMSVideoPageBinder binder) {

    }

    @Override
    public void onCountdownFinished() {
        appCMSPresenter.playNextVideo(binder,
                binder.getCurrentPlayingVideoIndex() + 1,
                0);
        binder.setCurrentPlayingVideoIndex(binder.getCurrentPlayingVideoIndex() + 1);
//        finish();
    }

    @Override
    public void closeActivity() {
        appCMSPresenter.sendCloseOthersAction(null, true,false);
        finish();
    }

    @Override
    public int getNavigationContainer() {
        return 0;
    }

    /*@Override
    public int getSubNavigationContainer() {
        return 0;
    }*/
}
