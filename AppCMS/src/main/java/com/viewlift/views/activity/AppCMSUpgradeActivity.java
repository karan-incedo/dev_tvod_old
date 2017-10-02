package com.viewlift.views.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.views.fragments.AppCMSUpgradeFragment;

/**
 * Created by viewlift on 10/2/17.
 */

public class AppCMSUpgradeActivity extends AppCompatActivity {
    private static final String UPGRADE_TAG = "upgrade_app_tag";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appcms_upgrade_page);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AppCMSUpgradeFragment appCMSUpgradeActivity = AppCMSUpgradeFragment.newInstance();
        fragmentTransaction.add(R.id.error_fragment, appCMSUpgradeActivity, UPGRADE_TAG);
        fragmentTransaction.commit();

        if (getApplication() instanceof AppCMSApplication) {
            ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter().sendCloseOthersAction(null, false);
        }
    }
}
