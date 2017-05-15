package air.com.snagfilms;

import android.app.Application;

import air.com.snagfilms.models.network.components.AppCMSAPIComponent;
import air.com.snagfilms.models.network.components.DaggerAppCMSAPIComponent;
import air.com.snagfilms.models.network.modules.AppCMSAPIModule;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSApplication extends Application {
    private AppCMSAPIComponent appCMSAPIComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appCMSAPIComponent = DaggerAppCMSAPIComponent
                .builder()
                .appCMSAPIModule(new AppCMSAPIModule(getString(R.string.app_cms_api_baseurl), getFilesDir()))
                .build();
    }

    public AppCMSAPIComponent getAppCMSAPIComponent() {
        return appCMSAPIComponent;
    }
}
