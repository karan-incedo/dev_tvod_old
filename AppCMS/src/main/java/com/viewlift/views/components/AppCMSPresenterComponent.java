package com.viewlift.views.components;

import com.viewlift.presenters.AppCMSPresenter;

import javax.inject.Singleton;

import com.viewlift.views.modules.AppCMSPresenterModule;

import dagger.Component;
import dagger.Provides;

/**
 * Created by viewlift on 5/22/17.
 */

@Singleton
@Component(modules = {AppCMSPresenterModule.class})
public interface AppCMSPresenterComponent {
    AppCMSPresenter appCMSPresenter();
}
