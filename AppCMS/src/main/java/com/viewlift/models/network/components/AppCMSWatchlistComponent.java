package com.viewlift.models.network.components;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.viewlift.models.network.modules.AppCMSWatchlistModule;
import com.viewlift.models.network.rest.AppCMSWatchlistCall;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppCMSWatchlistModule.class})
public interface AppCMSWatchlistComponent {
    AppCMSWatchlistCall appCMSWatchlistCall();
}
