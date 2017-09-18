package com.viewlift.tv.views.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.sites.AppCMSSite;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.network.modules.AppCMSSearchUrlModule;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.views.component.AppCmsTvSearchComponent;
import com.viewlift.tv.views.component.DaggerAppCmsTvSearchComponent;
import com.viewlift.tv.views.fragment.AppCmsBrowseFragment;
import com.viewlift.tv.views.fragment.AppCmsNavigationFragment;
import com.viewlift.tv.views.fragment.AppCmsResetPasswordFragment;
import com.viewlift.tv.views.fragment.AppCmsSearchFragment;
import com.viewlift.tv.views.fragment.AppCmsTVPageFragment;
import com.viewlift.tv.views.fragment.AppCmsTvErrorFragment;
import com.viewlift.tv.views.fragment.TextOverlayDialogFragment;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.binders.RetryCallBinder;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by nitin.tyagi on 6/27/2017.
 */

public class AppCmsHomeActivity extends AppCmsBaseActivity implements
        AppCmsNavigationFragment.OnNavigationVisibilityListener ,
        AppCmsTvErrorFragment.ErrorFragmentListener{

    private final String TAG = AppCmsHomeActivity.class.getName();
    private FrameLayout navHolder;
    private FrameLayout homeHolder;
    private FrameLayout shadowView;
    AppCmsNavigationFragment navigationFragment;
    private BroadcastReceiver presenterActionReceiver;
    AppCMSBinder updatedAppCMSBinder;
    AppCMSPresenter appCMSPresenter;
    private Stack<String> appCMSBinderStack;
    private Map<String, AppCMSBinder> appCMSBinderMap;
    public static final String DIALOG_FRAGMENT_TAG = "text_overlay";
    private AppCmsTvSearchComponent appCMSSearchUrlComponent;
    private boolean isActive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isActive = true;
        Bundle args = getIntent().getBundleExtra(getString(R.string.app_cms_bundle_key));
        AppCMSBinder appCMSBinder = (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
        updatedAppCMSBinder = appCMSBinder;

        appCMSBinderStack = new Stack<>();
        appCMSBinderMap = new HashMap<>();

        appCMSPresenter = ((AppCMSApplication) getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();


        String tag = getTag(updatedAppCMSBinder);
        appCMSBinderStack.push(tag);
        appCMSBinderMap.put(tag, updatedAppCMSBinder);

        AppCMSMain appCMSMain = appCMSBinder.getAppCMSMain();

        AppCMSSite appCMSSite = appCMSPresenter.getAppCMSSite();

        int textColor = Color.parseColor(appCMSMain.getBrand().getCta().getPrimary().getTextColor());/*Color.parseColor("#F6546A");*/
        int bgColor = Color.parseColor(appCMSMain.getBrand().getCta().getPrimary().getBackgroundColor());//Color.parseColor("#660066");

        navigationFragment = AppCmsNavigationFragment.newInstance(this,this,appCMSBinder,textColor,bgColor);

        setContentView(R.layout.activity_app_cms_tv_home);
        navHolder = (FrameLayout)findViewById(R.id.navigation_placholder);
        homeHolder = (FrameLayout)findViewById(R.id.home_placeholder);
        shadowView = (FrameLayout)findViewById(R.id.shadow_view);
        setNavigationFragment(navigationFragment);
        setPageFragment(appCMSBinder);
        appCMSPresenter.sendGaScreen(appCMSBinder.getScreenName());
        showInfoIcon(appCMSBinder.getPageId());

        if(null == appCMSSearchUrlComponent){
            appCMSSearchUrlComponent = DaggerAppCmsTvSearchComponent.builder()
                    .appCMSSearchUrlModule(new AppCMSSearchUrlModule(appCMSMain.getApiBaseUrl(),
                            appCMSSite.getGist().getSiteInternalName(),
                            appCMSBinder.getAppCMSSearchCall()))
                    .build();
        }

        presenterActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION)) {
                    Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
                    try {
                     if (isActive) {
                            if(appCMSPresenter.isPageUser(((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key))).getPageId())
                                    || appCMSPresenter.isPageFooter(((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key))).getPageId())){
                                openMyProfile();
                                handleProfileFragmentAction((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key)));
                            }else {
                                updatedAppCMSBinder = (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
                                handleLaunchPageAction(updatedAppCMSBinder);
                            }
                            //appCMSPresenter.sendStopLoadingPageAction(); //stop the progress bar..
                        }
                    } catch (ClassCastException e) {
                        Log.e(TAG, "Could not read AppCMSBinder: " + e.toString());
                    }
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION)) {
                        pageLoading(true);
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION)) {
                       pageLoading(false);
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_RESET_NAVIGATION_ITEM_ACTION)) {
                    Log.d(TAG, "Nav item - Received broadcast to select navigation item with page Id: " +
                            intent.getStringExtra(getString(R.string.navigation_item_key)));
                  //  selectNavItem(intent.getStringExtra(getString(R.string.navigation_item_key)));
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_DEEPLINK_ACTION)) {
                    if (intent.getData() != null) {
                     //   processDeepLink(intent.getData());
                    }
                }
                else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_DIALOG_ACTION)) {
                    Bundle bundle = intent.getBundleExtra(getString(R.string.dialog_item_key));
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    TextOverlayDialogFragment newFragment = TextOverlayDialogFragment.newInstance(
                            context,
                            bundle);
                    newFragment.show(ft, DIALOG_FRAGMENT_TAG);
                }else if (intent.getAction().equals(AppCMSPresenter.SEARCH_ACTION)) {
                   openSearchFragment();
                }else if(intent.getAction().equals(AppCMSPresenter.MY_PROFILE_ACTION)){
                    openMyProfile();
                }else if(intent.getAction().equals(AppCMSPresenter.ERROR_DIALOG_ACTION)){
                    openErrorDialog(intent);
                }else if(intent.getAction().equals(AppCMSPresenter.ACTION_RESET_PASSWORD)){
                    openResetPasswordScreen(intent);
                }else if(intent.getAction().equals(AppCMSPresenter.PRESENTER_CLEAR_DIALOG_ACTION)){

                }else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_UPDATE_HISTORY_ACTION)) {
                    updateData();
                    int totalNoOfFragment = getFragmentManager().getBackStackEntryCount();
                    for(int i=0;i<totalNoOfFragment;i++){
                        FragmentManager.BackStackEntry backStackEntry = getFragmentManager().getBackStackEntryAt(i);
                        String tag = backStackEntry.getName();
                        Fragment fragment = getFragmentManager().findFragmentByTag(tag);
                        AppCMSBinder appCmsBinder = appCMSBinderMap.get(tag);
                        if(fragment instanceof AppCmsTVPageFragment){
                            ((AppCmsTVPageFragment)fragment).updateBinder(appCmsBinder);
                        }
                    }
                }

            }
        };
        registerReceiver(presenterActionReceiver,new IntentFilter(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION));
        registerReceiver(presenterActionReceiver,new IntentFilter(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
        registerReceiver(presenterActionReceiver,new IntentFilter(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION));
        registerReceiver(presenterActionReceiver , new IntentFilter(AppCMSPresenter.PRESENTER_RESET_NAVIGATION_ITEM_ACTION));
        registerReceiver(presenterActionReceiver , new IntentFilter(AppCMSPresenter.PRESENTER_DIALOG_ACTION));
        registerReceiver(presenterActionReceiver , new IntentFilter(AppCMSPresenter.PRESENTER_CLEAR_DIALOG_ACTION));
        registerReceiver(presenterActionReceiver , new IntentFilter(AppCMSPresenter.SEARCH_ACTION));
        registerReceiver(presenterActionReceiver , new IntentFilter(AppCMSPresenter.MY_PROFILE_ACTION));
        registerReceiver(presenterActionReceiver , new IntentFilter(AppCMSPresenter.ERROR_DIALOG_ACTION));
        registerReceiver(presenterActionReceiver , new IntentFilter(AppCMSPresenter.ACTION_RESET_PASSWORD));
        registerReceiver(presenterActionReceiver , new IntentFilter(AppCMSPresenter.PRESENTER_UPDATE_HISTORY_ACTION));

    }

    private void handleProfileFragmentAction(AppCMSBinder updatedAppCMSBinder) {
        String tag = getTag(updatedAppCMSBinder);
        Fragment fragment = getFragmentManager().findFragmentById(R.id.home_placeholder);
        if(null != fragment && fragment instanceof AppCmsMyProfileFragment){
            getFragmentManager().popBackStack();
        }

        AppCmsMyProfileFragment appCmsMyProfileFragment = AppCmsMyProfileFragment.newInstance(this , updatedAppCMSBinder);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.home_placeholder ,appCmsMyProfileFragment,tag).addToBackStack(tag).commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
    }

    @Override
    protected void onStop() {
        if(isNavigationVisible()){
            handleNavigationVisibility();
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    private void openErrorDialog(Intent intent){
        pageLoading(false);
        Bundle bundle = intent.getBundleExtra(getString(R.string.retryCallBundleKey));
        bundle.putBoolean(getString(R.string.retry_key) , bundle.getBoolean(getString(R.string.retry_key)));
        bundle.putBoolean(getString(R.string.register_internet_receiver_key) , bundle.getBoolean(getString(R.string.register_internet_receiver_key)));
        bundle.putString(getString(R.string.tv_dialog_msg_key) , bundle.getString(getString(R.string.tv_dialog_msg_key)));
        bundle.putString(getString(R.string.tv_dialog_header_key) , bundle.getString(getString(R.string.tv_dialog_header_key)));

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        AppCmsTvErrorFragment newFragment = AppCmsTvErrorFragment.newInstance(
                bundle);
        newFragment.setErrorListener(this);
        newFragment.show(ft, DIALOG_FRAGMENT_TAG);
    }


    private void openResetPasswordScreen(Intent intent){

        if(null != intent){
            Bundle bundle = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
            if(null != bundle){
                AppCMSBinder appCMSBinder = (AppCMSBinder)bundle.get(getString(R.string.app_cms_binder_key));
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                AppCmsResetPasswordFragment newFragment = AppCmsResetPasswordFragment.newInstance(
                        appCMSBinder);
                newFragment.show(ft, DIALOG_FRAGMENT_TAG);
                pageLoading(false);
            }
        }


    }

    @Override
    public void onErrorScreenClose() {

    }


    @Override
    public void onRetry(Bundle bundle) {
        RetryCallBinder retryCallBinder = (RetryCallBinder)bundle.getBinder(getString(R.string.retryCallBinderKey));
        AppCMSPresenter.RETRY_TYPE retryType = retryCallBinder.getRetry_type();
        switch(retryType){
            case BUTTON_ACTION:
                appCMSPresenter.launchTVButtonSelectedAction(
                        retryCallBinder.getPagePath(),
                        retryCallBinder.getAction(),
                        retryCallBinder.getFilmTitle(),
                        retryCallBinder.getExtraData(),
                        retryCallBinder.isCloselauncher(),
                        retryCallBinder.getContentDatum()
                );
                break;
            case VIDEO_ACTION:
                appCMSPresenter.launchTVVideoPlayer(
                        retryCallBinder.getFilmId(),
                        retryCallBinder.getPagePath(),
                        retryCallBinder.getFilmTitle(),
                        retryCallBinder.getContentDatum()
                );

                break;
            case PAGE_ACTION:
                appCMSPresenter.navigateToTVPage(
                        retryCallBinder.getFilmId(),
                        retryCallBinder.getFilmTitle(),
                        retryCallBinder.getPagePath(),
                        retryCallBinder.isCloselauncher(),
                        Uri.EMPTY,
                        false
                );
                break;

            case SEARCH_RETRY_ACTION:
                String tag = getString(R.string.app_cms_search_label);
                Fragment fragment = getFragmentManager().findFragmentByTag(tag);
                if(fragment instanceof AppCmsSearchFragment){
                    ((AppCmsSearchFragment) fragment).searchResult(retryCallBinder.getFilmTitle());
                }
                break;

            case WATCHLIST_RETRY_ACTION:
                appCMSPresenter.showLoadingDialog(true);
                appCMSPresenter.navigateToWatchlistPage(
                        retryCallBinder.getPageId(),
                        retryCallBinder.getFilmTitle(),
                        retryCallBinder.getPagePath(),
                        false);
                break;
            case HISTORY_RETRY_ACTION:
                appCMSPresenter.showLoadingDialog(true);
                appCMSPresenter.navigateToHistoryPage(
                        retryCallBinder.getPageId(),
                        retryCallBinder.getFilmTitle(),
                        retryCallBinder.getPagePath(),
                        false);
                break;
        }
    }

    private String getTag(AppCMSBinder appCMSBinder){
        String key = null;
        if(!appCMSPresenter.isPagePrimary(appCMSBinder.getPageId())){
            key = appCMSBinder.getPageId() + appCMSBinder.getScreenName();
        }else{
            key = appCMSBinder.getPageId();
        }
        return key;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }



    private void selectNavItem(String pageId) {
        Log.d(TAG , "Nav Pageid = "+pageId);
        navigationFragment.setSelectedPageId(pageId);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(presenterActionReceiver);
        super.onDestroy();
    }

    private void handleLaunchPageAction(AppCMSBinder appCMSBinder) {

        int distanceFromStackTop = -1;
        String tag = getTag(appCMSBinder);

        distanceFromStackTop = appCMSBinderStack.search(tag);

        Log.d(TAG, "Page distance from top: " + distanceFromStackTop);
        if (0 < distanceFromStackTop) {
            for (int i = 0; i < distanceFromStackTop; i++) {
                Log.d(TAG, "Popping stack to get to page item");
                try {
                    appCMSBinderStack.pop();
                    //getFragmentManager().popBackStack();
                } catch (IllegalStateException e) {
                    Log.e(TAG, "Error popping back stack: " + e.getMessage());
                }
            }
        }

        appCMSBinderStack.push(tag);
        appCMSBinderMap.put(tag, appCMSBinder);

        showInfoIcon(appCMSBinder.getPageId());
        Log.d(TAG, "Launching new page: " + appCMSBinder.getPageName());
        appCMSPresenter.sendGaScreen(appCMSBinder.getScreenName());
        boolean isPoped = getFragmentManager().popBackStackImmediate(appCMSBinder.getPageId() , 1 );
        //if(!isPoped)
            setPageFragment(updatedAppCMSBinder);
        //else
        //selectNavItem(updatedAppCMSBinder.getPageId());
    }


    @Override
    public void onBackPressed() {

        //if navigation is visible then first hide the navigation.
        if(isNavigationVisible()){
            handleNavigationVisibility();
            return;
        }

        if(appCMSBinderStack.size() > 0){
            appCMSBinderStack.pop();
        }

        if(appCMSBinderStack.size() > 0){
            if(appCMSBinderStack.peek().equalsIgnoreCase(getString(R.string.app_cms_search_label))
                    || appCMSBinderStack.peek().equalsIgnoreCase(getString(R.string.app_cms_my_profile_label ,
                                                                   getString(R.string.profile_label)))){
                selectNavItem(appCMSBinderStack.peek());
                showInfoIcon(appCMSBinderStack.peek());
            }else {
                updatedAppCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
                selectNavItem(updatedAppCMSBinder.getPageId());
                showInfoIcon(updatedAppCMSBinder.getPageId());
            }
        }

        super.onBackPressed();

        if(getFragmentManager().getBackStackEntryCount() == 0){
                finish();
            }
        }


    private void setPageFragment(AppCMSBinder appCMSBinder){
        Fragment attached = getFragmentManager().findFragmentById(R.id.home_placeholder);
        if(attached == null || (attached != null && !attached.getTag().equalsIgnoreCase(appCMSBinder.getPageId()))){
            AppCmsTVPageFragment appCMSPageFragment = AppCmsTVPageFragment.newInstance(this , appCMSBinder);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            String tag = getTag(appCMSBinder);
            fragmentTransaction.replace(R.id.home_placeholder ,appCMSPageFragment,tag).addToBackStack(tag).commitAllowingStateLoss();
        }else{
            if(null != appCMSPresenter)
                appCMSPresenter.sendStopLoadingPageAction();
        }
        selectNavItem(appCMSBinder.getPageId());
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();

        switch (action) {
            case KeyEvent.ACTION_DOWN:
                switch (keyCode) {
                    case KeyEvent.KEYCODE_MENU:
                   // case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                        handleNavigationVisibility();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        handlePlayRemoteKey();
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        //if navigation fragment is open then hold down key event otherwise pass it.
                       if(isNavigationVisible()){
                           handleNavigationVisibility();
                           return true;
                       }

                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    private void handleNavigationVisibility(){
        Log.d(TAG , "handleNavigationVisibility*****");
        if(appCMSPresenter.isPagePrimary(appCMSBinderStack.peek())){
            if(isNavigationVisible()){
                showNavigation(false);
            }else{
                showNavigation(true);
            }
        }
    }

    public void keyPressed(View v){
        String tag = getString(R.string.app_cms_search_label);
        Fragment fragment = getFragmentManager().findFragmentByTag(tag);
        if(fragment instanceof AppCmsSearchFragment){
            ((AppCmsSearchFragment) fragment).keyPressed(v);
        }
    }

    private void handlePlayRemoteKey(){
        Fragment parentFragment = getFragmentManager().findFragmentById(R.id.home_placeholder);
        if(null != parentFragment) {
            AppCmsBrowseFragment browseFragment = null;
            if(parentFragment instanceof AppCmsTVPageFragment){
                browseFragment = (AppCmsBrowseFragment) parentFragment.getChildFragmentManager().
                                                      findFragmentById(R.id.appcms_browsefragment);
            }else if(parentFragment instanceof AppCmsSearchFragment){
                browseFragment = (AppCmsBrowseFragment) parentFragment.getChildFragmentManager().
                                                      findFragmentById(R.id.appcms_search_results_container);
            }
            if (null != browseFragment && browseFragment.hasFocus()) {
                browseFragment.pushedPlayKey();
            }
        }
    }


    @Override
    public void showNavigation(final boolean shouldShow) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                navHolder.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
                shadowView.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
                navigationFragment.setFocusable(shouldShow);
                if(shouldShow) {
                   // navigationFragment.setSelectorColor();
                    navigationFragment.notifiDataSetInvlidate();
                }
            }
        });
    }

    private boolean isNavigationVisible(){
        return ( (navHolder.getVisibility() == View.VISIBLE) ? true : false );
    }

    public void openSearchFragment(){
        int distanceFromStackTop = -1;
        String tag = getString(R.string.app_cms_search_label);

        distanceFromStackTop = appCMSBinderStack.search(tag);

        Log.d(TAG, "Page distance from top: " + distanceFromStackTop);
        if (0 < distanceFromStackTop) {
            for (int i = 0; i < distanceFromStackTop; i++) {
                Log.d(TAG, "Popping stack to get to page item");
                try {
                    appCMSBinderStack.pop();
                } catch (IllegalStateException e) {
                    Log.e(TAG, "Error popping back stack: " + e.getMessage());
                }
            }
        }

        showInfoIcon(tag);
        appCMSBinderStack.push(tag);
        appCMSPresenter.sendGaScreen(tag);

        AppCmsSearchFragment searchFragment = new AppCmsSearchFragment();
        getFragmentManager().beginTransaction().replace(R.id.home_placeholder , searchFragment ,
                    tag).addToBackStack(tag).commit();

        selectNavItem(tag);
    }

    private void openMyProfile() {
        int distanceFromStackTop = -1;
        String tag = getString(R.string.app_cms_my_profile_label ,
                               getString(R.string.profile_label));

        distanceFromStackTop = appCMSBinderStack.search(tag);

        Log.d(TAG, "Page distance from top: " + distanceFromStackTop);
        if (0 < distanceFromStackTop) {
            for (int i = 0; i < distanceFromStackTop; i++) {
                Log.d(TAG, "Popping stack to get to page item");
                try {
                    appCMSBinderStack.pop();
                } catch (IllegalStateException e) {
                    Log.e(TAG, "Error popping back stack: " + e.getMessage());
                }
            }
        }
        showInfoIcon(tag);
        appCMSBinderStack.push(tag);
        selectNavItem(tag);
    }

    private void showInfoIcon(String pageId){
        findViewById(R.id.info_icon).setVisibility(
                appCMSPresenter.isPagePrimary(pageId) ? View.VISIBLE : View.INVISIBLE
        );
    }

    public AppCmsTvSearchComponent getAppCMSSearchComponent(){
        return appCMSSearchUrlComponent;
    }

    @Override
    public int getNavigationContaineer() {
        return R.id.navigation_placholder;
    }


    private void updateData() {
        final AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
        final AppCMSSite appCMSSite = appCMSPresenter.getAppCMSSite();

        if (appCMSPresenter != null) {
            for (Map.Entry<String, AppCMSBinder> appCMSBinderEntry : appCMSBinderMap.entrySet()) {
                final AppCMSBinder appCMSBinder = appCMSBinderEntry.getValue();
                if (appCMSBinder != null) {
                    String endPoint = appCMSPresenter.getPageIdToPageAPIUrl(appCMSBinder.getPageId());
                    boolean usePageIdQueryParam = true;
                    if (appCMSPresenter.isPageAVideoPage(appCMSBinder.getScreenName())) {
                        endPoint = appCMSPresenter.getPageNameToPageAPIUrl(appCMSBinder.getScreenName());
                        usePageIdQueryParam = false;
                    }

                    if (!TextUtils.isEmpty(endPoint)) {
                        appCMSPresenter.getPageIdContent(appCMSMain.getApiBaseUrl(),
                                endPoint,
                                appCMSSite.getGist().getSiteInternalName(),
                                usePageIdQueryParam,
                                appCMSBinder.getPagePath(),
                                appCMSPageAPI -> {
                                    if (appCMSPageAPI != null) {
                                        boolean updatedHistory = false;
                                        if (appCMSPresenter.isUserLoggedIn(this)) {
                                            if (appCMSPageAPI.getModules() != null) {
                                                for (Module module : appCMSPageAPI.getModules()) {
                                                    AppCMSUIKeyType moduleType = appCMSPresenter.getJsonValueKeyMap().get(module.getModuleType());
                                                    if (moduleType == AppCMSUIKeyType.PAGE_API_HISTORY_MODULE_KEY ||
                                                            moduleType == AppCMSUIKeyType.PAGE_VIDEO_DETAILS_KEY) {
                                                        if (module.getContentData() != null &&
                                                                !module.getContentData().isEmpty()) {
                                                            appCMSPresenter.getHistoryData(appCMSHistoryResult -> {
                                                                if (appCMSHistoryResult != null) {
                                                                    AppCMSPageAPI historyAPI =
                                                                            appCMSHistoryResult.convertToAppCMSPageAPI(appCMSPageAPI.getId());
                                                                    historyAPI.getModules().get(0).setId(module.getId());
                                                                    appCMSPresenter.mergeData(historyAPI, appCMSPageAPI);
                                                                    appCMSBinder.updateAppCMSPageAPI(appCMSPageAPI);
                                                                }
                                                            });
                                                            updatedHistory = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (!updatedHistory) {
                                            appCMSBinder.updateAppCMSPageAPI(appCMSPageAPI);
                                        }
                                    }
                                });
                    }
                    appCMSBinderMap.put(getTag(appCMSBinder) ,appCMSBinder );
                }
            }
        }
    }

}
