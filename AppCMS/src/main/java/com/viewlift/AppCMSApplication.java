package com.viewlift;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.amazon.alexa.vsk.clientlib.AlexaClientManager;
import com.amazon.device.messaging.ADM;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.viewlift.models.data.appcms.downloads.DownloadMediaMigration;
import com.viewlift.models.network.modules.AppCMSSiteModule;
import com.viewlift.models.network.modules.AppCMSUIModule;
import com.viewlift.views.components.AppCMSPresenterComponent;
import com.viewlift.views.components.DaggerAppCMSPresenterComponent;
import com.viewlift.views.modules.AppCMSPresenterModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.functions.Action0;

import static com.viewlift.analytics.AppsFlyerUtils.trackInstallationEvent;

/*
 * Created by viewlift on 5/4/17.
 */

public class AppCMSApplication extends MultiDexApplication {
    private static String TAG = "AppCMSApp";

    private AppCMSPresenterComponent appCMSPresenterComponent;

    private AppsFlyerConversionListener conversionDataListener;

    private int openActivities;
    private int visibleActivities;

    private Action0 onActivityResumedAction;

    private void initRealmonfig() {

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .schemaVersion(3)
                .migration(new DownloadMediaMigration())
//                .deleteRealmIfMigrationNeeded()  // for Development purpose
                .build();
        Realm.setDefaultConfiguration(config);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initRealmonfig();
        openActivities = 0;


        new Thread(() -> {
            conversionDataListener = new AppsFlyerConversionListener() {

                @Override
                public void onInstallConversionDataLoaded(Map<String, String> map) {
                    //
                }

                @Override
                public void onInstallConversionFailure(String s) {
                    //
                }

                @Override
                public void onAppOpenAttribution(Map<String, String> map) {
                    //
                }

                @Override
                public void onAttributionFailure(String s) {
                    //
                }
            };

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
                    Log.d(TAG, "Activity being started: " + activity.getLocalClassName());
                    openActivities++;
                    visibleActivities++;
                    if(appCMSPresenterComponent.appCMSPresenter()!=null){
                        appCMSPresenterComponent.appCMSPresenter().setResumedActivities(visibleActivities);
                    }
                }

                @Override
                public void onActivityResumed(Activity activity) {
                    appCMSPresenterComponent.appCMSPresenter().setCurrentActivity(activity);
                    if (onActivityResumedAction != null) {
                        onActivityResumedAction.call();
                        onActivityResumedAction = null;
                    }
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    Log.d(TAG, "Activity being paused: " + activity.getLocalClassName());
                    appCMSPresenterComponent.appCMSPresenter().closeSoftKeyboard();
                    visibleActivities--;
                    if(appCMSPresenterComponent.appCMSPresenter()!=null){
                        appCMSPresenterComponent.appCMSPresenter().setResumedActivities(visibleActivities);
                    }
                }

                @Override
                public void onActivityStopped(Activity activity) {
                    Log.d(TAG, "Activity being stopped: " + activity.getLocalClassName());
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
                    Log.d(TAG, "Activity being destroyed: " + activity.getLocalClassName());
                    appCMSPresenterComponent.appCMSPresenter().unsetCurrentActivity(activity);
                    appCMSPresenterComponent.appCMSPresenter().closeSoftKeyboard();
                }
            });

        }).run();


        Log.d(TAG, "checkIsTelevision(): " + checkIsTelevision());

        if (Utils.isFireTVDevice(getApplicationContext()) && checkIsTelevision()) {
            try {
                // Initialize the Alexa Video Skills Client Library first.
                initializeAlexaClientLibrary();

                // Initialize ADM.
                initializeAdm();

                AlexaClientManager.getSharedInstance().setAlexaEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private void initializeAdm() {
        try {
            final ADM adm = new ADM(this);
            if (adm.isSupported()) {
                if (adm.getRegistrationId() == null) {
                    // ADM is not ready now. You have to start ADM registration by calling
                    // startRegister() API. ADM will calls onRegister() API on your ADM
                    // handler service when ADM registration was completed with registered ADM id.
                    adm.startRegister();
                } else {
                    // [IMPORTANT]
                    // ADM down-channel is already available. This is a common case that your
                    // application restarted. ADM manager on your Fire TV cache the previous
                    // ADM registration info and provide it immediately when your application
                    // is identified as restarted.
                    //
                    // You have to provide the retrieved ADM registration Id to the VSK Client library.
                    final String admRegistrationId = adm.getRegistrationId();
                    Log.i(TAG, "ADM registration Id:" + admRegistrationId);

                    // Provide the acquired ADM registration ID.
                    final AlexaClientManager alexaClientManager = AlexaClientManager.getSharedInstance();
                    alexaClientManager.setDownChannelReady(true, admRegistrationId);
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "ADM initialization has failed with exception", ex);
        }
    }



    public void initializeAlexaClientLibrary() {
        // Retrieve the shared instance of the AlexaClientManager
        AlexaClientManager clientManager = AlexaClientManager.getSharedInstance();

        // Gather your Skill ID and list of capabilities
        final String alexaSkillId = "amzn1.ask.skill.3cc5691b-cd12-4429-b399-d00e8cb52fae";

        // Create a list of supported capabilities in your skill.
        List<String> capabilities = new ArrayList<>();
        capabilities.add(AlexaClientManager.CAPABILITY_REMOTE_VIDEO_PLAYER);
        capabilities.add(AlexaClientManager.CAPABILITY_PLAY_BACK_CONTROLLER);
        capabilities.add(AlexaClientManager.CAPABILITY_SEEK_CONTROLLER);

        clientManager.initialize(this.getApplicationContext(),
                alexaSkillId,
                AlexaClientManager.SKILL_STAGE_LIVE,
                capabilities);
    }


    public AppCMSPresenterComponent getAppCMSPresenterComponent() {
        return appCMSPresenterComponent;
    }

    public void initAppsFlyer(String appsFlyerKey) {
        AppsFlyerLib.getInstance().init(appsFlyerKey, conversionDataListener);
        AppsFlyerLib.getInstance().setCollectIMEI(false);
        AppsFlyerLib.getInstance().setCollectAndroidID(false);
        sendAnalytics();
    }

    private void sendAnalytics() {
        trackInstallationEvent(this);
    }

    public Action0 getOnActivityResumedAction() {
        return onActivityResumedAction;
    }

    public void setOnActivityResumedAction(Action0 onActivityResumedAction) {
        this.onActivityResumedAction = onActivityResumedAction;
    }
    private boolean checkIsTelevision() {
        int uiMode = getResources().getConfiguration().uiMode;
        return (uiMode & Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION;
    }

}
