package air.com.snagfilms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import air.com.snagfilms.models.data.binders.AppCMSBinder;
import air.com.snagfilms.models.network.components.AppCMSAPIComponent;
import air.com.snagfilms.presenters.AppCMSPresenter;
import snagfilms.com.air.appcms.R;

public class LaunchActivity extends AppCompatActivity {
    private AppCMSAPIComponent appCMSAPIComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        getSupportActionBar().setHomeButtonEnabled(true);

        appCMSAPIComponent = ((AppCMSApplication) getApplication()).getAppCMSAPIComponent();
        String appCMSMainUrl = getString(R.string.app_cms_main_url,
                getString(R.string.app_cms_api_baseurl),
                getString(R.string.app_cms_app_name));
        AppCMSPresenter.getAppCMSPresenter().loadMain(this, appCMSAPIComponent, appCMSMainUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppCMSPresenter.REQUEST_SPLASH_SCREEN) {
            if (resultCode == RESULT_OK) {
                Bundle args = data.getBundleExtra(getString(R.string.app_cms_bundle_key));
                AppCMSBinder appCMSBinder = (AppCMSBinder)
                        args.getBinder(getString(R.string.app_cms_binder_key));
                AppCMSPresenter
                        .getAppCMSPresenter()
                        .processMetaPagesQueue(appCMSAPIComponent,
                                this,
                                appCMSBinder.getLoadedFromFile());
            } else {
                AppCMSPresenter.getAppCMSPresenter().launchErrorActivity(this);
            }
        } else {
            AppCMSPresenter.getAppCMSPresenter().launchErrorActivity(this);
        }
    }
}
