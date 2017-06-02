package com.viewlift;

import android.app.Application;

import com.viewlift.views.components.AppCMSPresenterComponent;
import com.viewlift.views.modules.AppCMSPresenterModule;
import com.viewlift.models.network.modules.AppCMSAPIModule;
import com.viewlift.models.network.modules.AppCMSUIModule;

import com.viewlift.views.components.DaggerAppCMSPresenterComponent;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSApplication extends Application {
    private AppCMSPresenterComponent appCMSPresenterComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appCMSPresenterComponent = DaggerAppCMSPresenterComponent
                .builder()
                .appCMSUIModule(new AppCMSUIModule(this))
                .appCMSAPIModule(new AppCMSAPIModule(getString(R.string.app_cms_api_key)))
                .appCMSPresenterModule(new AppCMSPresenterModule())
                .build();
    }

    public AppCMSPresenterComponent getAppCMSPresenterComponent() {
        return appCMSPresenterComponent;
    }
}
