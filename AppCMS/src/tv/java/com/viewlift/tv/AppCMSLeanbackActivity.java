package com.viewlift.tv;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.CustomProgressBar;
import com.viewlift.tv.views.activity.AppCmsHomeActivity;
import com.viewlift.views.components.AppCMSPresenterComponent;

import snagfilms.com.air.appcms.R;

import static android.content.ContentValues.TAG;

/**
 * Created by viewlift on 6/22/17.
 */

public class AppCMSLeanbackActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Log.d(TAG, "Launching application from main.json");
        AppCMSPresenterComponent appCMSPresenterComponent =
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent();
        appCMSPresenterComponent.appCMSPresenter().getAppCMSMain(this,
                getString(R.string.app_cms_app_name),
                Uri.parse(""),
                AppCMSPresenter.PlatformType.TV);

        Log.d(TAG, "onCreate()");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CustomProgressBar.getInstance(AppCMSLeanbackActivity.this).showProgressDialog(AppCMSLeanbackActivity.this,"");
            }
        } , 2500);

        final ImageView logo = (ImageView) findViewById(R.id.splash_logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator translateX = ObjectAnimator.ofFloat(logo, "translationX",
                        Resources.getSystem().getDisplayMetrics().widthPixels / 2 - logo.getWidth()/2/*- getResources().getDimension(R.dimen.splash_logo_animation_right_margin)*/);
                translateX.setDuration(2000);
                translateX.start();

                ObjectAnimator translateY = ObjectAnimator.ofFloat(logo, "translationY",
                        Resources.getSystem().getDisplayMetrics().heightPixels / 2 - logo.getHeight()/2/*-getResources().getDimension(R.dimen.splash_logo_animation_right_margin)*/);
                translateY.setDuration(2000);
                translateY.start();

                ObjectAnimator anim = ObjectAnimator.ofFloat(logo,"scaleX",0.5f);
                anim.setDuration(2000); // duration 3 seconds
                anim.start();

                ObjectAnimator anim2 = ObjectAnimator.ofFloat(logo,"scaleY",0.5f);
                anim2.setDuration(2000); // duration 3 seconds
                anim2.start();
            }
        } , 500);

    }


    @Override
    protected void onStop() {
        CustomProgressBar.getInstance(this).dismissProgressDialog();
        super.onStop();
        finish();
    }
}
