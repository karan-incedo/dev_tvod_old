package com.viewlift.views.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.viewlift.views.fragments.AppCMSErrorFragment;
import com.viewlift.R;

/**
 * Created by viewlift on 5/5/17.
 */

public class AppCMSErrorActivity extends AppCompatActivity {
    private static final String ERROR_TAG = "error_fragment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment errorFragment = AppCMSErrorFragment.newInstance();
        fragmentTransaction.add(R.id.error_fragment, errorFragment, ERROR_TAG);
        fragmentTransaction.commit();
    }
}
