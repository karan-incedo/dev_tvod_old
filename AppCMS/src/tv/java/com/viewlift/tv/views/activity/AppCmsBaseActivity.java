package com.viewlift.tv.views.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.viewlift.tv.views.fragment.AppCmsNavigationFragment;

/**
 * Created by nitin.tyagi on 6/27/2017.
 */

public abstract class AppCmsBaseActivity extends AppCompatActivity {

    private boolean isProfileFirstTime;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public void setNavigationFragment(AppCmsNavigationFragment navigationFragment) {
        getFragmentManager().beginTransaction().add(getNavigationContainer(), navigationFragment, "nav").commit();
    }

    /*public void setSubNavigationFragment(AppCmsSubNavigationFragment navigationFragment, AppCMSBinder appCMSBinder) {
        Bundle bundle = new Bundle();
        bundle.putBinder("app_cms_binder", appCMSBinder);
        navigationFragment.setArguments(bundle);
        navigationFragment.setSelectedPageId(appCMSBinder.getPageId());
        getFragmentManager().beginTransaction().add(getSubNavigationContainer(), navigationFragment, "subnav").commit();
    }*/

    public abstract int getNavigationContainer();

    public boolean isProfileFirstTime() {
        return isProfileFirstTime;
    }

    public void setProfileFirstTime(boolean profileFirstTime) {
        isProfileFirstTime = profileFirstTime;
    }
    // public abstract int getSubNavigationContainer();
}
