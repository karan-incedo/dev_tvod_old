package com.viewlift;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.apptentive.android.sdk.Apptentive;
import com.squareup.leakcanary.LeakCanary;
import com.viewlift.models.network.modules.AppCMSSiteModule;
import com.viewlift.views.components.AppCMSPresenterComponent;
import com.viewlift.views.modules.AppCMSPresenterModule;
import com.viewlift.models.network.modules.AppCMSUIModule;

import com.viewlift.views.components.DaggerAppCMSPresenterComponent;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSApplication extends Application {
    private static String TAG = "AppCMSApp";

    private AppCMSPresenterComponent appCMSPresenterComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        Apptentive.register(this, getString(R.string.app_cms_apptentive_api_key));

        appCMSPresenterComponent = DaggerAppCMSPresenterComponent
                .builder()
                .appCMSUIModule(new AppCMSUIModule(this))
                .appCMSSiteModule(new AppCMSSiteModule())
                .appCMSPresenterModule(new AppCMSPresenterModule())
                .build();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                appCMSPresenterComponent.appCMSPresenter().setCurrentActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d(TAG, "Activity being started: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityResumed(Activity activity) {
                appCMSPresenterComponent.appCMSPresenter().setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.d(TAG, "Activity being paused: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.d(TAG, "Activity being stopped: " + activity.getLocalClassName());
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d(TAG, "Activity being destroyed: " + activity.getLocalClassName());
            }
        });

        Fabric.with(this, new Crashlytics());
    }

    public AppCMSPresenterComponent getAppCMSPresenterComponent() {
        return appCMSPresenterComponent;
    }
}
