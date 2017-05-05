package air.com.snagfilms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import air.com.snagfilms.models.data.appcms.main.Main;
import air.com.snagfilms.models.network.background.tasks.GetAppCMSAsyncTask;
import air.com.snagfilms.models.network.components.AppCMSAPIComponent;
import air.com.snagfilms.presenters.AppCMSPresenter;
import rx.functions.Action1;
import snagfilms.com.air.appcms.R;

public class LaunchActivity extends AppCompatActivity {
    private FrameLayout fragmentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        fragmentLayout = (FrameLayout) findViewById(R.id.app_cms_fragment);

        getSupportActionBar().setHomeButtonEnabled(true);

        AppCMSAPIComponent appCMSAPIComponent =
                ((AppCMSApplication) getApplication()).getAppCMSAPIComponent();
        getAppCMSMain(appCMSAPIComponent);
    }

    private void getAppCMSMain(final AppCMSAPIComponent appCMSAPIComponent) {
        String appCMSMainUrl = getString(R.string.app_cms_main_url);
        new GetAppCMSAsyncTask<Main>(appCMSAPIComponent.appCMSMainCall(), new Action1<Main>() {
            @Override
            public void call(Main main) {
                if (main == null ||
                        main.getMain() == null ||
                        main.getMain().getAndroid() == null ||
                        main.getMain().getAndroid().isEmpty()) {
                    setErrorPage();
                } else {
                    getAppCMSAndroid(appCMSAPIComponent, main.getMain().getAndroid());
                }
            }
        }).execute(appCMSMainUrl);
    }

    private void getAppCMSAndroid(final AppCMSAPIComponent appCMSAPIComponent, String url) {
    }

    private void getAppCMSPage(final AppCMSAPIComponent appCMSAPIComponent) {

    }

    private void setErrorPage() {
        AppCMSPresenter.getAppCMSPresenter().launchErrorFragment(getSupportFragmentManager());
    }
}
