package air.com.snagfilms.views.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import air.com.snagfilms.views.fragments.ErrorFragment;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/5/17.
 */

public class ErrorActivity extends FragmentActivity {
    private static final String ERROR_TAG = "error_fragment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment errorFragment = ErrorFragment.newInstance();
        fragmentTransaction.add(R.id.error_fragment, errorFragment, ERROR_TAG);
        fragmentTransaction.commit();
    }
}
