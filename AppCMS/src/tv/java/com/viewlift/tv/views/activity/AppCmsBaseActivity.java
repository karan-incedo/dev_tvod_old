package com.viewlift.tv.views.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.viewlift.R;
import com.viewlift.tv.utility.CustomProgressBar;
import com.viewlift.tv.views.fragment.AppCmsNavigationFragment;

/**
 * Created by nitin.tyagi on 6/27/2017.
 */

public abstract class AppCmsBaseActivity extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public void setNavigationFragment(AppCmsNavigationFragment navigationFragment){
        getFragmentManager().beginTransaction().add(getNavigationContaineer() ,navigationFragment , "nav" ).commit();
    }

    public void pageLoading(final boolean shouldShowProgress){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(shouldShowProgress){
                    CustomProgressBar.getInstance(AppCmsBaseActivity.this).showProgressDialog(AppCmsBaseActivity.this,"Loading...");
                }else{
                    CustomProgressBar.getInstance(AppCmsBaseActivity.this).dismissProgressDialog();
                }
            }
        });
    }


    public abstract int getNavigationContaineer();
}
