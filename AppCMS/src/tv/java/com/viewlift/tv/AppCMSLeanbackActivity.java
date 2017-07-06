package com.viewlift.tv;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.presenters.AppCMSPresenter;
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
       /* TextView textView = new TextView(this);
        textView.setText("Nitin Mandola");
        setContentView(textView);*/

        Log.d(TAG, "Launching application from main.json");
        AppCMSPresenterComponent appCMSPresenterComponent =
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent();
        appCMSPresenterComponent.appCMSPresenter().getAppCMSMain(this,
                getString(R.string.app_cms_app_name),
                Uri.parse(""),
                AppCMSPresenter.PlatformType.TV);
        /*Intent intent = new Intent(AppCMSLeanbackActivity.this , AppCmsHomeActivity.class);
        startActivity(intent);
*/
        Log.d(TAG, "onCreate()");

    }
}
