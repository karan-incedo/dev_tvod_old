package com.viewlift.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.viewlift.AppCMSApplication;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.fragments.AppCMSErrorFragment;
import com.viewlift.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by viewlift on 5/5/17.
 */

public class AppCMSErrorActivity extends AppCompatActivity {
    private static final String ERROR_TAG = "error_fragment";

    private static final String TAG = "ErrorActivity";

    private BroadcastReceiver presenterCloseActionReceiver;

    private ConnectivityManager connectivityManager;
    private BroadcastReceiver networkConnectedReceiver;
    private boolean timerScheduled;
    private String errorMessage = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BaseView.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_error);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (getIntent()!= null &&
                getIntent().getStringExtra("error_message")!= null &&
                !TextUtils.isEmpty(getIntent().getStringExtra("error_message")) ){
            errorMessage = getIntent().getStringExtra("error_message");
        }
        Fragment errorFragment = AppCMSErrorFragment.newInstance(errorMessage);
        fragmentTransaction.add(R.id.error_fragment, errorFragment, ERROR_TAG);
        fragmentTransaction.commit();

        presenterCloseActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null &&
                        intent.getStringExtra(getString(R.string.app_cms_package_name_key)) != null &&
                        !intent.getStringExtra(getString(R.string.app_cms_package_name_key)).equals(getPackageName())) {
                    return;
                }
                if (intent.getAction().equals(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION) &&
                        !"Error Screen".equals(intent.getStringExtra(getString(R.string.app_cms_closing_page_name)))) {
                    finish();
                }
            }
        };

        ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter().sendCloseOthersAction("Error Screen", false, false);

        registerReceiver(presenterCloseActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkConnectedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null &&
                        intent.getStringExtra(getString(R.string.app_cms_package_name_key)) != null &&
                        !intent.getStringExtra(getString(R.string.app_cms_package_name_key)).equals(getPackageName())) {
                    return;
                }
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected && !timerScheduled) {
                    timerScheduled = true;
                    new Timer().schedule(new TimerTask() {
                                             @Override
                                             public void run() {
                                                 if (timerScheduled) {
                                                     /*Intent relaunchApp = getPackageManager().getLaunchIntentForPackage(getPackageName());
                                                     relaunchApp.putExtra(getString(R.string.force_reload_from_network_key), true);
                                                     startActivity(relaunchApp);*/
                                                     AppCMSErrorActivity.this.finish();
                                                     try {
                                                         unregisterReceiver(networkConnectedReceiver);
                                                     } catch (Exception e) {
                                                         //Log.e(TAG, "Failed to unregister network receiver: " + e.getMessage());
                                                     }
                                                     finish();
                                                     timerScheduled = false;
                                                 }
                                             }
                                         }, 500);
                }
            }
        };

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(presenterCloseActionReceiver);
        } catch (Exception e) {
            //Log.e(TAG, "Failed to unregister Close Action Receiver");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null ||
                !activeNetwork.isConnectedOrConnecting()) {
            registerReceiver(networkConnectedReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(networkConnectedReceiver);
        } catch (Exception e) {
            //Log.e(TAG, "Failed to unregister Network Connectivity Receiver");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timerScheduled = false;
        try {
            ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter().sendCloseOthersAction("Error Screen", false, false);
        } catch (Exception e) {
            //Log.e(TAG, "Caught exception attempting to send close others action: " + e.getMessage());
        }
        finish();
    }
}
