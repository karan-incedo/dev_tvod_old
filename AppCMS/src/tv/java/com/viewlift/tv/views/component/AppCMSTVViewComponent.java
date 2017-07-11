package com.viewlift.tv.views.component;

import android.support.annotation.Nullable;

import com.viewlift.tv.views.customviews.TVPageView;
import com.viewlift.tv.views.customviews.TVViewCreator;
import com.viewlift.tv.views.module.AppCMSTVPageViewModule;
import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by viewlift on 5/5/17.
 */

@Singleton
@Component(modules={AppCMSTVPageViewModule.class})
public interface AppCMSTVViewComponent {
    TVViewCreator tvviewCreator();
    @Nullable
    TVPageView appCMSTVPageView();
}
