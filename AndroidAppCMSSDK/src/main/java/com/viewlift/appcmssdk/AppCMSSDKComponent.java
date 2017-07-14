package com.viewlift.appcmssdk;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by viewlift on 7/13/17.
 */

@Singleton
@Component(modules={AppCMSSDKModule.class})
public interface AppCMSSDKComponent {
    AppCMSSDK appCMSSDK();
}
