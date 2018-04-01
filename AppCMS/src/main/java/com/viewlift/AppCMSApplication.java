package com.viewlift;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.apptentive.android.sdk.Apptentive;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.viewlift.models.network.modules.AppCMSSiteModule;
import com.viewlift.models.network.modules.AppCMSUIModule;
import com.viewlift.views.components.AppCMSPresenterComponent;
import com.viewlift.views.components.DaggerAppCMSPresenterComponent;
import com.viewlift.views.modules.AppCMSPresenterModule;

import java.util.Map;

import io.fabric.sdk.android.Fabric;

import static com.viewlift.analytics.AppsFlyerUtils.trackInstallationEvent;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSApplication extends MultiDexApplication {
    private static String TAG = "AppCMSApp";

    private AppCMSPresenterComponent appCMSPresenterComponent;

    private AppsFlyerConversionListener conversionDataListener;

    private int openActivities;

    private static AppCMSApplication instance;

    public static AppCMSApplication getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        openActivities = 0;

        conversionDataListener = new AppsFlyerConversionListener() {

            @Override
            public void onInstallConversionDataLoaded(Map<String, String> map) {

            }

            @Override
            public void onInstallConversionFailure(String s) {

            }

            @Override
            public void onAppOpenAttribution(Map<String, String> map) {

            }

            @Override
            public void onAttributionFailure(String s) {

            }
        };

        new Thread(() -> {
            // NOTE: Replaced with Utils.getProperty()
            //AppsFlyerLib.getInstance().init(getString(R.string.app_cms_appsflyer_dev_key), conversionDataListener);
            AppsFlyerLib.getInstance().init(Utils.getProperty("AppsFlyerDevKey", getApplicationContext()), conversionDataListener);

            Fabric.with(AppCMSApplication.this, new Crashlytics());

            // NOTE: Replaced with Utils.getProperty()
            //Apptentive.register(this, getString(R.string.app_cms_apptentive_api_key));
            Apptentive.register(this, Utils.getProperty("ApptentiveApiKey", getApplicationContext()), Utils.getProperty("ApptentiveSignatureKey", getApplicationContext()));

            FacebookSdk.setApplicationId(Utils.getProperty("FacebookAppId", getApplicationContext()));
            FacebookSdk.sdkInitialize(getApplicationContext());
        }).run();

        appCMSPresenterComponent = DaggerAppCMSPresenterComponent
                .builder()
                .appCMSUIModule(new AppCMSUIModule(this))
                .appCMSSiteModule(new AppCMSSiteModule())
                .appCMSPresenterModule(new AppCMSPresenterModule())
                .build();

        appCMSPresenterComponent.appCMSPresenter().setCurrentContext(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                appCMSPresenterComponent.appCMSPresenter().setCurrentActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                //Log.d(TAG, "Activity being started: " + activity.getLocalClassName());
                openActivities++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                appCMSPresenterComponent.appCMSPresenter().setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                //Log.d(TAG, "Activity being paused: " + activity.getLocalClassName());
                appCMSPresenterComponent.appCMSPresenter().closeSoftKeyboard();
            }

            @Override
            public void onActivityStopped(Activity activity) {
                //Log.d(TAG, "Activity being stopped: " + activity.getLocalClassName());
                if (openActivities == 1) {
                    appCMSPresenterComponent.appCMSPresenter().setCancelAllLoads(true);
                }
                openActivities--;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                //Log.d(TAG, "Activity being destroyed: " + activity.getLocalClassName());
                appCMSPresenterComponent.appCMSPresenter().unsetCurrentActivity(activity);
                appCMSPresenterComponent.appCMSPresenter().closeSoftKeyboard();
            }
        });
    }

    public AppCMSPresenterComponent getAppCMSPresenterComponent() {
        return appCMSPresenterComponent;
    }

    public void initAppsFlyer(String appsFlyerKey) {
        AppsFlyerLib.getInstance().init(appsFlyerKey, conversionDataListener);
        sendAnalytics();
    }

    private void sendAnalytics() {
        trackInstallationEvent(this);
    }
}
