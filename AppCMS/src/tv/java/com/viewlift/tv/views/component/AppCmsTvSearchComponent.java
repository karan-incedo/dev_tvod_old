package com.viewlift.tv.views.component;

import com.viewlift.models.network.modules.AppCMSSearchUrlModule;
import com.viewlift.tv.views.fragment.AppCmsSearchFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by nitin.tyagi on 7/24/2017.
 */

@Singleton
@Component(modules={AppCMSSearchUrlModule.class})
public interface AppCmsTvSearchComponent {
    void inject(AppCmsSearchFragment appCmsSearchFragment);
}
