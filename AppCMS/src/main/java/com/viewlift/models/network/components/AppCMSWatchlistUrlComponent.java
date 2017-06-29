package com.viewlift.models.network.components;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.viewlift.models.data.appcms.providers.AppCMSWatchlistContentProvider;
import com.viewlift.models.network.modules.AppCMSWatchlistUrlModule;
import com.viewlift.views.activity.AppCMSWatchlistActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppCMSWatchlistUrlModule.class})
public interface AppCMSWatchlistUrlComponent {
    void inject(AppCMSWatchlistActivity appCMSWatchlistActivity);

    void inject(AppCMSWatchlistContentProvider appCMSWatchableContentProvider);
}
