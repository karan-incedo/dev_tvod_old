package air.com.snagfilms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
    protected void onDestroy() {
        super.onDestroy();
        if (this != AppCMSPresenter.getAppCMSPresenter().popActivityFromStack()) {

        }
    }
}
