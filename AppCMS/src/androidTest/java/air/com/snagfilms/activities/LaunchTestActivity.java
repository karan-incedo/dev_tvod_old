package air.com.snagfilms.activities;

import android.content.Intent;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.os.Bundle;

import air.com.snagfilms.LaunchActivity;

public class LaunchTestActivity extends LaunchActivity {
    private CountingIdlingResource launchActivityIdlingResource = new CountingIdlingResource("launchActivityIdlingResource");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launchActivityIdlingResource.increment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        launchActivityIdlingResource.decrement();
    }
}
