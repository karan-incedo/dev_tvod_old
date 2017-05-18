package air.com.snagfilms.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import air.com.snagfilms.models.data.binders.AppCMSBinder;
import air.com.snagfilms.presenters.AppCMSPresenter;
import air.com.snagfilms.views.fragments.AppCMSPageFragment;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/5/17.
 */

public class AppCMSPageActivity extends FragmentActivity implements AppCMSPageFragment.OnPageCreationError {
    private AppCMSBinder appCMSBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCMSPresenter.getAppCMSPresenter().pushActivityToStack(this);
        setContentView(R.layout.activity_appcms_page);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
        appCMSBinder = (AppCMSBinder)
                args.getBinder(getString(R.string.app_cms_binder_key));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment appCMSPageFragment = AppCMSPageFragment.newInstance(this, appCMSBinder);
        fragmentTransaction.add(R.id.app_cms_fragment, appCMSPageFragment, appCMSBinder.getPageName());
        fragmentTransaction.commit();
    }

    @Override
    public void onError() {
        setFinishResult(RESULT_CANCELED);
        finish();
    }

    private void setFinishResult(int resultCode) {
        Intent resultIntent = new Intent();
        Bundle args = new Bundle();
        args.putBinder(getString(R.string.app_cms_binder_key), appCMSBinder);
        resultIntent.putExtra(getString(R.string.app_cms_bundle_key), args);
        setResult(resultCode, resultIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this != AppCMSPresenter.getAppCMSPresenter().popActivityFromStack()) {

        }
    }
}
