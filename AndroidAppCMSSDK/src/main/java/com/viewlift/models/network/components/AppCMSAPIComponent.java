package com.viewlift.models.network.components;

import com.viewlift.models.network.modules.AppCMSAPIModule;
import com.viewlift.models.network.rest.AppCMSPageAPICall;
import com.viewlift.models.network.rest.AppCMSStreamingInfoCall;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by viewlift on 5/9/17.
 */

@Singleton
@Component(modules={AppCMSAPIModule.class})
public interface AppCMSAPIComponent {
    AppCMSPageAPICall appCMSPageAPICall();
    AppCMSStreamingInfoCall appCMSStreamingInfoCall();
}
