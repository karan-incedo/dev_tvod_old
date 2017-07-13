package viewlift.com.myappcmsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.viewlift.appcmssdk.AppCMSSDK;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCMSSDK appCMSSDK = AppCMSSDK.initialize(this,
                getString(R.string.app_cms_baseurl),
                getString(R.string.app_cms_site_id));
        appCMSSDK.launchVideo(getString(R.string.my_film_id));
    }
}
