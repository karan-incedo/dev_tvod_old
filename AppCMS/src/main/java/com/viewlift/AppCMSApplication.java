package com.viewlift;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

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

            }

            @Override
            public void onActivityResumed(Activity activity) {
                appCMSPresenterComponent.appCMSPresenter().setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

        Fabric.with(this, new Crashlytics());
    }

    public AppCMSPresenterComponent getAppCMSPresenterComponent() {
        return appCMSPresenterComponent;
    }
}
