package air.com.snagfilms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import air.com.snagfilms.presenters.AppCMSPresenter;
import air.com.snagfilms.views.components.AppCMSPresenterComponent;
import snagfilms.com.air.appcms.R;

public class AppCMSLaunchActivity extends AppCompatActivity {
    private static final String TAG = "AppCMSLaunchActivity";

    private AppCMSPresenterComponent appCMSPresenterComponent;

    private BroadcastReceiver handoffReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        handoffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra(getString(R.string.close_self_key), true)) {
                    Log.d(TAG, "Closing activity");
                    finish();
                }
            }
        };

        appCMSPresenterComponent = ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent();
        boolean isUserLoggedIn = appCMSPresenterComponent.appCMSPresenter().isUserLoggedIn(this);
        Bundle args = new Bundle();
        args.putString(getString(R.string.page_id), getString(R.string.app_cms_app_name));
        args.putBoolean(getString(R.string.is_logged_in_key), isUserLoggedIn);
        appCMSPresenterComponent.appCMSPresenter().setCurrentActivity(this);
        boolean launchResult = appCMSPresenterComponent
                .appCMSPresenter()
                .launchAction(getString(R.string.app_cms_action_initialize_key), args);
        if (!launchResult) {
            Log.e(TAG, "Error launching initialization action");
            appCMSPresenterComponent.appCMSPresenter().launchErrorActivity(this);
        }

        registerReceiver(handoffReceiver, new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(handoffReceiver);
    }
}
