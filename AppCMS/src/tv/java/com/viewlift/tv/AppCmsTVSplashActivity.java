package com.viewlift.tv;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.Utils;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.CustomProgressBar;
import com.viewlift.tv.views.fragment.AppCmsTvErrorFragment;
import com.viewlift.views.components.AppCMSPresenterComponent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by viewlift on 6/22/17.
 */

public class AppCmsTVSplashActivity extends Activity implements AppCmsTvErrorFragment.ErrorFragmentListener {

    private CountDownTimer countDownTimer;

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
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
       progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(android.R.color.white), PorterDuff.Mode.MULTIPLY);
 /*
        progressBar.getProgressDrawable().setColorFilter(
                getResources().getColor(R.color.splash_progress_color), android.graphics.PorterDuff.Mode.SRC_IN);*/

       /* Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(getResources().getColor(android.R.color.holo_red_dark), android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setProgressDrawable(progressDrawable);*/

        imageView.setBackgroundResource(R.drawable.tv_logo);
        getAppCmsMain();

        /*Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                progress = progress+10;
                progressBar.setProgress(progress);
            }
        },1000,10*1000);*/

        countDownTimer = new CountDownTimer(11000 ,1000) {
            @Override
            public void onTick(long l) {
                progress = progress+1;
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                
            }
        }.start();
    }

    int progress = 0;
    private void getAppCmsMain(){
        AppCMSPresenterComponent appCMSPresenterComponent =
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent();

        if(appCMSPresenterComponent.appCMSPresenter().isNetworkConnected()){
        appCMSPresenterComponent.appCMSPresenter().getAppCMSMain(this,
                Utils.getProperty("SiteId", getApplicationContext()),
                Uri.parse(""),
                AppCMSPresenter.PlatformType.TV,
                true);
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
        if(null != countDownTimer)
        countDownTimer.cancel();
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
                /*startLogoAnimation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CustomProgressBar.getInstance(AppCmsTVSplashActivity.this).showProgressDialog(AppCmsTVSplashActivity.this,"");
                    }
                },550);*/
            }
        }
    };


    private void startLogoAnimation() {
        final ImageView logo = (ImageView) findViewById(R.id.splash_logo);
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        BitmapDrawable bd = (BitmapDrawable) this.getResources().getDrawable(R.drawable.app_logo);
        int smallWidth = bd.getBitmap().getWidth();
        int smallHeight = bd.getBitmap().getHeight();

        float xScale = 0;//(float) (((smallWidth * 100) / logoWidth)) / 100;
        float yScale = 0;//(float) (((smallHeight * 100) / logoHeight)) / 100;


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator translateX = ObjectAnimator.ofFloat(logo, "translationX",
                        ((Resources.getSystem().getDisplayMetrics().widthPixels / 2) - smallWidth / 2 - getResources().getDimension(R.dimen.logo_margin)));
                translateX.setDuration(500);
                translateX.start();

                ObjectAnimator translateY = ObjectAnimator.ofFloat(logo, "translationY",
                        ((Resources.getSystem().getDisplayMetrics().heightPixels / 2) - smallHeight / 2 - getResources().getDimension(R.dimen.logo_margin)));
                translateY.setDuration(500);
                translateY.start();

                ObjectAnimator anim = ObjectAnimator.ofFloat(logo, "scaleX", xScale);
                anim.setDuration(500); // duration 3 seconds
                anim.start();

                ObjectAnimator anim2 = ObjectAnimator.ofFloat(logo, "scaleY", yScale);
                anim2.setDuration(500); // duration 3 seconds
                anim2.start();
            }
        });
    }


    private String getDeviceDetail(){
        StringBuffer stringBuffer = new StringBuffer();
        try {
            final String AMAZON_FEATURE_FIRE_TV = "amazon.hardware.fire_tv";
            String AMAZON_MODEL = Build.MODEL;
            if (getPackageManager().hasSystemFeature(AMAZON_FEATURE_FIRE_TV)) {
                stringBuffer.append("FireTV :: ");
            } else {
                stringBuffer.append("NOT A FireTV :: ");
            }
            if (AMAZON_MODEL.matches("AFTN")) {
                stringBuffer.append("Firetv Gen = 3rd");
            } else if (AMAZON_MODEL.matches("AFTS")) {
                stringBuffer.append("Firetv  Gen = 2nd");
            } else if (AMAZON_MODEL.matches("AFTB")) {
                stringBuffer.append("Firetv  Gen = 1st");
            } else if (AMAZON_MODEL.matches("AFTT")) {
                stringBuffer.append("FireStick  Gen = 2nd");
            } else if (AMAZON_MODEL.matches("AFTM")) {
                stringBuffer.append("FireStick  Gen = 1st");
            } else if (AMAZON_MODEL.matches("AFTRS")) {
                stringBuffer.append("FireTV Edition ");
            }
            stringBuffer.append("SDK_INT = " + Build.VERSION.SDK_INT);
        }catch (Exception e){

        }
        return stringBuffer.toString();
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
