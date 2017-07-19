package com.viewlift.models.network.components;

import com.viewlift.models.data.appcms.providers.AppCMSSearchableContentProvider;
import com.viewlift.views.activity.AppCMSSearchActivity;
import com.viewlift.models.network.modules.AppCMSSearchUrlModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by viewlift on 6/12/17.
 */

@Singleton
@Component(modules={AppCMSSearchUrlModule.class})
public interface AppCMSSearchUrlComponent {
    void inject(AppCMSSearchActivity appCMSSearchActivity);
    void inject(AppCMSSearchableContentProvider appCMSSearchableContentProvider);
}
