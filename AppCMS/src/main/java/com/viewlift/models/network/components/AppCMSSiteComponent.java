package com.viewlift.models.network.components;

import com.viewlift.models.network.modules.AppCMSSiteModule;
import com.viewlift.models.network.rest.AppCMSSiteCall;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by viewlift on 6/15/17.
 */

@Singleton
@Component(modules = {AppCMSSiteModule.class})
public interface AppCMSSiteComponent {
    AppCMSSiteCall appCMSSiteCall();
}
