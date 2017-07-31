package com.viewlift.views.components;

import android.support.annotation.Nullable;

import javax.inject.Singleton;

import com.viewlift.views.customviews.PageView;
import com.viewlift.views.customviews.ViewCreator;
import com.viewlift.views.modules.AppCMSPageViewModule;

import java.util.List;

import dagger.Component;

/**
 * Created by viewlift on 5/5/17.
 */

@Singleton
@Component(modules={AppCMSPageViewModule.class})
public interface AppCMSViewComponent {
    ViewCreator viewCreator();
    List<String> modulesToIgnore();
    @Nullable PageView appCMSPageView();
}
