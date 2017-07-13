package com.viewlift.appcmssdk;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by viewlift on 7/13/17.
 */

@Singleton
@Component(modules={ViewliftSDKModule.class})
public interface ViewliftSDKComponent {
    ViewliftSDK appCMSSDK();
}
