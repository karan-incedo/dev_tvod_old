package com.viewlift.models.network.components;

import com.viewlift.models.network.modules.AppCMSUIModule;
import com.viewlift.models.network.rest.AppCMSAndroidUICall;
import com.viewlift.models.network.rest.AppCMSMainUICall;
import com.viewlift.models.network.rest.AppCMSPageUICall;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by viewlift on 5/4/17.
 */

@Singleton
@Component(modules={AppCMSUIModule.class})
public interface AppCMSUIComponent {
    AppCMSMainUICall appCMSMainCall();
    AppCMSAndroidUICall appCMSAndroidCall();
    AppCMSPageUICall appCMSPageCall();
}
