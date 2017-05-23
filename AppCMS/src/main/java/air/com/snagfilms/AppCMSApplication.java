package air.com.snagfilms;

import android.app.Application;

import air.com.snagfilms.models.network.components.AppCMSUIComponent;
import air.com.snagfilms.models.network.components.DaggerAppCMSAPIComponent;
import air.com.snagfilms.models.network.modules.AppCMSUIModule;
import air.com.snagfilms.views.components.AppCMSPresenterComponent;
import air.com.snagfilms.views.components.DaggerAppCMSPresenterComponent;
import air.com.snagfilms.views.modules.AppCMSPresenterModule;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSApplication extends Application {
    private AppCMSPresenterComponent appCMSPresenterComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appCMSPresenterComponent = DaggerAppCMSPresenterComponent
                .builder()
                .appCMSUIModule(new AppCMSUIModule(this))
                .appCMSPresenterModule(new AppCMSPresenterModule())
                .build();
    }

    public AppCMSPresenterComponent getAppCMSPresenterComponent() {
        return appCMSPresenterComponent;
    }
}
