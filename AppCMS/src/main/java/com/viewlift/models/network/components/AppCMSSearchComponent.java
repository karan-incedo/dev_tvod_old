package com.viewlift.models.network.components;

import com.viewlift.models.network.modules.AppCMSSearchModule;
import com.viewlift.models.network.rest.AppCMSSearchCall;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by viewlift on 6/12/17.
 */

@Singleton
@Component(modules = {AppCMSSearchModule.class})
public interface AppCMSSearchComponent {
    AppCMSSearchCall appCMSSearchCall();
}
