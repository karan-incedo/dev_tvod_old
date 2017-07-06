package com.viewlift.tv.views.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.views.fragment.AppCmsNavigationFragment;
import com.viewlift.tv.views.fragment.AppCmsTVPageFragment;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.fragments.AppCMSPageFragment;

import snagfilms.com.air.appcms.R;

import static android.R.attr.animationDuration;

/**
 * Created by nitin.tyagi on 6/27/2017.
 */

public class AppCmsHomeActivity extends AppCmsBaseActivity implements AppCmsNavigationFragment.OnNavigationVisibilityListener{

    private final String TAG = AppCmsHomeActivity.class.getName();
    private FrameLayout navHolder;
    private FrameLayout homeHolder;
    private FrameLayout shadowView;
    AppCmsNavigationFragment navigationFragment;
    private BroadcastReceiver presenterActionReceiver;
    AppCMSBinder updatedAppCMSBinder;
    AppCMSPresenter appCMSPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle args = getIntent().getBundleExtra(getString(R.string.app_cms_bundle_key));
        AppCMSBinder appCMSBinder = (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));


        appCMSPresenter = ((AppCMSApplication) getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        int textColor = Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor());/*Color.parseColor("#F6546A");*/
        int bgColor = Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor());//Color.parseColor("#660066");

        navigationFragment = AppCmsNavigationFragment.newInstance(this,this,appCMSBinder,textColor,bgColor);

        setContentView(R.layout.activity_app_cms_tv_home);
        navHolder = (FrameLayout)findViewById(R.id.navigation_placholder);
        homeHolder = (FrameLayout)findViewById(R.id.home_placeholder);
        shadowView = (FrameLayout)findViewById(R.id.shadow_view);
        setNavigationFragment(navigationFragment);
        setPageFragment(appCMSBinder);


        presenterActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION)) {
                    Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
                    try {
                        updatedAppCMSBinder =
                                (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
                        /*if (isActive)*/ {
                            handleLaunchPageAction(updatedAppCMSBinder);
                        }
                    } catch (ClassCastException e) {
                        Log.e(TAG, "Could not read AppCMSBinder: " + e.toString());
                    }
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION)) {
                    //pageLoading(true);
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION)) {
                   // pageLoading(false);
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_RESET_NAVIGATION_ITEM_ACTION)) {
                    Log.d(TAG, "Nav item - Received broadcast to select navigation item with page Id: " +
                            intent.getStringExtra(getString(R.string.navigation_item_key)));
                  //  selectNavItem(intent.getStringExtra(getString(R.string.navigation_item_key)));
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_DEEPLINK_ACTION)) {
                    if (intent.getData() != null) {
                     //   processDeepLink(intent.getData());
                    }
                }
            }
        };

        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION));

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(presenterActionReceiver);
        super.onDestroy();
    }

    private void handleLaunchPageAction(AppCMSBinder appCMSBinder) {

        Log.d(TAG, "Launching new page: " + appCMSBinder.getPageName());
        appCMSPresenter.sendGaScreen(appCMSBinder.getScreenName());
        boolean isPoped = getFragmentManager().popBackStackImmediate(appCMSBinder.getPageId() , 0 );
        /*int distanceFromStackTop = appCMSBinderStack.search(appCMSBinder.getPageId());
        Log.d(TAG, "Page distance from top: " + distanceFromStackTop);
        if (0 < distanceFromStackTop) {
            for (int i = 0; i < distanceFromStackTop; i++) {
                Log.d(TAG, "Popping stack to get to page item");
                try {
                    getSupportFragmentManager().popBackStack();
                } catch (IllegalStateException e) {
                    Log.e(TAG, "Error popping back stack: " + e.getMessage());
                }
                handleBack(true, false, false);
            }
        }*/
        if(!isPoped)
            setPageFragment(updatedAppCMSBinder);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getFragmentManager().getBackStackEntryCount() == 0){
                finish();
            }
        }

    private void playExitAnimation() {
        LinearInterpolator interpolator = new LinearInterpolator();

        ObjectAnimator upperAnim = ObjectAnimator.ofFloat(navHolder, "y", 30, 0);
        upperAnim.setDuration(animationDuration);
        upperAnim.setInterpolator(interpolator);

        ObjectAnimator y = ObjectAnimator.ofFloat(homeHolder, "y", 1080 - 30, 30);
        y.setDuration(animationDuration);
        y.setInterpolator(interpolator);


        ObjectAnimator alpha = ObjectAnimator.ofFloat(homeHolder, "alpha", 1f, 0f);
        alpha.setInterpolator(interpolator);
        alpha.setDuration(animationDuration);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(upperAnim).with(y).with(alpha);
        animatorSet.start();
    }

    private final int animationDuration = 300;
    private void playEnterAnimation() {
        LinearInterpolator interpolator = new LinearInterpolator();

        ObjectAnimator upperAnim = ObjectAnimator.ofFloat(navHolder, "y", 0, 30);
        upperAnim.setDuration(animationDuration);
        upperAnim.setInterpolator(interpolator);

        ObjectAnimator y = ObjectAnimator.ofFloat(homeHolder, "y", 30, (1080-30));
        y.setDuration(animationDuration);
        y.setInterpolator(interpolator);


        ObjectAnimator alpha = ObjectAnimator.ofFloat(homeHolder, "alpha", 0f, 1f);
        alpha.setInterpolator(interpolator);
        alpha.setDuration(animationDuration);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(upperAnim).with(y).with(alpha);
        animatorSet.start();
    }

    private void setNavigationFragment(AppCmsNavigationFragment navigationFragment){
         getFragmentManager().beginTransaction().add( R.id.navigation_placholder ,navigationFragment , "nav" ).commit();
    }

    private void setPageFragment(AppCMSBinder appCMSBinder){

        Fragment attached = getFragmentManager().findFragmentById(R.id.home_placeholder);
        if(attached == null || (attached != null && !attached.getTag().equalsIgnoreCase(appCMSBinder.getPageId()))){
            AppCmsTVPageFragment appCMSPageFragment = AppCmsTVPageFragment.newInstance(this , appCMSBinder);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.home_placeholder ,appCMSPageFragment,appCMSBinder.getPageId());
            fragmentTransaction.addToBackStack(appCMSBinder.getPageId());
            fragmentTransaction.commitAllowingStateLoss();
        }

    }


    boolean isVisible = false;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();

        if ( (keyCode == KeyEvent.KEYCODE_MENU || keyCode == 85) && action == KeyEvent.ACTION_DOWN) {

           /* if(!isVisible){
                playEnterAnimation();
                isVisible = true;
            }else{
                playExitAnimation();
                isVisible = false;
            }*/

            if(navHolder.getVisibility() == View.GONE){
                navHolder.setVisibility(View.VISIBLE);
                shadowView.setVisibility(View.VISIBLE);
                navigationFragment.setFocusable(true);
                navigationFragment.setSelectorColor();

               // playEnterAnimation();
            }else if(navHolder.getVisibility() == View.VISIBLE){
                navHolder.setVisibility(View.GONE);
                shadowView.setVisibility(View.GONE);
                navigationFragment.setFocusable(false);
                navigationFragment.setSelectorColor();
            }
        }
return super.dispatchKeyEvent(event);
    }

    @Override
    public void showNavigation(boolean shouldShow) {
        navHolder.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
    }
}
