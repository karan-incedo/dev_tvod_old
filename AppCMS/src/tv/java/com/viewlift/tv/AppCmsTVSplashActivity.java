package com.viewlift.tv;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.Utils;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.CustomProgressBar;
import com.viewlift.tv.views.fragment.AppCmsTvErrorFragment;
import com.viewlift.views.components.AppCMSPresenterComponent;

/**
 * Created by viewlift on 6/22/17.
 */

public class AppCmsTVSplashActivity extends Activity implements AppCmsTvErrorFragment.ErrorFragmentListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        setContentView(R.layout.activity_launch_tv);
        ImageView imageView = (ImageView) findViewById(R.id.splash_logo);
        imageView.setBackgroundResource(R.drawable.tv_logo);
        getAppCmsMain();
    }

    private void getAppCmsMain(){
        AppCMSPresenterComponent appCMSPresenterComponent =
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent();

        if(appCMSPresenterComponent.appCMSPresenter().isNetworkConnected()){
        appCMSPresenterComponent.appCMSPresenter().getAppCMSMain(this,
                Utils.getProperty("SiteId", getApplicationContext()),
                Uri.parse(""),
                AppCMSPresenter.PlatformType.TV,
                false);
        }else{
            showErrorFragment(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(AppCMSPresenter.ERROR_DIALOG_ACTION));
        registerReceiver(broadcastReceiver,new IntentFilter(AppCMSPresenter.ACTION_LOGO_ANIMATION));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AppCMSPresenter.ERROR_DIALOG_ACTION)){
                Bundle bundle = intent.getBundleExtra(getString(R.string.retryCallBundleKey));
                boolean shouldRetry = bundle.getBoolean(getString(R.string.retry_key));
                showErrorFragment(shouldRetry);
            }else if(intent.getAction().equals(AppCMSPresenter.ACTION_LOGO_ANIMATION)){
                startLogoAnimation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CustomProgressBar.getInstance(AppCmsTVSplashActivity.this).showProgressDialog(AppCmsTVSplashActivity.this,"");
                    }
                },2000);
            }
        }
    };


    private void startLogoAnimation() {
        final ImageView logo = (ImageView) findViewById(R.id.splash_logo);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator translateX = ObjectAnimator.ofFloat(logo, "translationX",
                        (( Resources.getSystem().getDisplayMetrics().widthPixels / 2
                                - getResources().getDimension(R.dimen.footer_logo_width)/2 )
                                - getResources().getDimension(R.dimen.footer_logo_margin_right)));
                translateX.setDuration(1500);
                translateX.start();

                ObjectAnimator translateY = ObjectAnimator.ofFloat(logo, "translationY",
                        (( Resources.getSystem().getDisplayMetrics().heightPixels / 2 -
                                getResources().getDimension(R.dimen.footer_logo_height)/2)
                                - getResources().getDimension(R.dimen.footer_logo_margin_bottom)));
                translateY.setDuration(1500);
                translateY.start();

                ObjectAnimator anim = ObjectAnimator.ofFloat(logo,"scaleX",0.50f);
                anim.setDuration(1500); // duration 3 seconds
                anim.start();

                ObjectAnimator anim2 = ObjectAnimator.ofFloat(logo,"scaleY",0.50f);
                anim2.setDuration(1500); // duration 3 seconds
                anim2.start();
            }
        });
    }

    public void showErrorFragment(boolean shouldRegisterInternetReciever){
        CustomProgressBar.getInstance(this).dismissProgressDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean(getString(R.string.retry_key) , true);
        bundle.putBoolean(getString(R.string.register_internet_receiver_key) , shouldRegisterInternetReciever);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        AppCmsTvErrorFragment errorActivityFragment = AppCmsTvErrorFragment.newInstance(
                bundle);
        errorActivityFragment.setErrorListener(this);
        errorActivityFragment.show(ft, getString(R.string.error_dialog_fragment_tag));
    }


    @Override
    public void onErrorScreenClose() {
        finish();
    }

    @Override
    public void onRetry(Bundle bundle) {
        getAppCmsMain();
    }


    @Override
    protected void onStop() {
        CustomProgressBar.getInstance(this).dismissProgressDialog();
        super.onStop();
        finish();
    }
}
