package com.viewlift.tv.views.activity;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.sites.AppCMSSite;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.watchlist.AppCMSWatchlistResult;
import com.viewlift.models.network.modules.AppCMSSearchUrlModule;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.AppCmsTVSplashActivity;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.component.AppCmsTvSearchComponent;
import com.viewlift.tv.views.component.DaggerAppCmsTvSearchComponent;
import com.viewlift.tv.views.fragment.AppCmsBrowseFragment;
import com.viewlift.tv.views.fragment.AppCmsGenericDialogFragment;
import com.viewlift.tv.views.fragment.AppCmsLinkYourAccountFragment;
import com.viewlift.tv.views.fragment.AppCmsLoginDialogFragment;
import com.viewlift.tv.views.fragment.AppCmsNavigationFragment;
import com.viewlift.tv.views.fragment.AppCmsResetPasswordFragment;
import com.viewlift.tv.views.fragment.AppCmsSearchFragment;
import com.viewlift.tv.views.fragment.AppCmsSignUpDialogFragment;
import com.viewlift.tv.views.fragment.AppCmsSubNavigationFragment;
import com.viewlift.tv.views.fragment.AppCmsTVPageFragment;
import com.viewlift.tv.views.fragment.AppCmsTvErrorFragment;
import com.viewlift.tv.views.fragment.BaseFragment;
import com.viewlift.tv.views.fragment.TextOverlayDialogFragment;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.binders.AppCMSSwitchSeasonBinder;
import com.viewlift.views.binders.RetryCallBinder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import io.fabric.sdk.android.Fabric;
import rx.functions.Action1;

/**
 * Created by nitin.tyagi on 6/27/2017.
 */

public class AppCmsHomeActivity extends AppCmsBaseActivity implements
        AppCmsNavigationFragment.OnNavigationVisibilityListener,
        AppCmsTvErrorFragment.ErrorFragmentListener {

    public static final String DIALOG_FRAGMENT_TAG = "text_overlay";
    public static final int OPEN_SEARCH_RESULT_CODE = 9182389;
    private final String TAG = AppCmsHomeActivity.class.getName();
    public boolean isActive;
    RelativeLayout navParentContainer;
    AppCmsNavigationFragment navigationFragment;
    AppCMSBinder updatedAppCMSBinder;
    AppCMSPresenter appCMSPresenter;
    AppCmsLoginDialogFragment loginDialog;
    AppCmsSignUpDialogFragment signUpDialog;
    boolean shouldShowLeftNav = false;
    boolean shouldShowSubLeftNav = false;
    private FrameLayout navHolder;
    private FrameLayout homeHolder;
    private FrameLayout shadowView;
    private BroadcastReceiver presenterActionReceiver;
    private BroadcastReceiver updateHistoryDataReciever;
    private Stack<String> appCMSBinderStack;
    private Map<String, AppCMSBinder> appCMSBinderMap;
    private AppCmsTvSearchComponent appCMSSearchUrlComponent;
    private AppCmsResetPasswordFragment appCmsResetPasswordFragment;
    private AppCmsSubNavigationFragment appCmsSubNavigationFragment;
    // private FrameLayout subNavHolder;
    private BroadcastReceiver updateWatchListDataReceiver;
    private AppCmsLinkYourAccountFragment appCmsLinkYourAccountFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null && getIntent().getAction() != null && getIntent().getData() != null) {
            if (getIntent().getAction().equalsIgnoreCase(getString(R.string.LAUNCHER_DEEPLINK_ACTION))) {
                ContentDatum contentDatum = new ContentDatum();
                Gist gist = new Gist();
                gist.setId(getIntent().getData().toString());
                contentDatum.setGist(gist);
                appCMSPresenter.launchTVVideoPlayer(contentDatum, 0, null, 0, null);
            }
        }
        isActive = true;
        Bundle args = getIntent().getBundleExtra(getString(R.string.app_cms_bundle_key));
        AppCMSBinder appCMSBinder = null;
        if (null != args && args.getBinder(getString(R.string.app_cms_binder_key)) instanceof AppCMSBinder) {
            appCMSBinder = (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
        } else {
            if (null != savedInstanceState) {
                startSplashActivity();
                finish();
                return;
            }
        }


        updatedAppCMSBinder = appCMSBinder;

        appCMSBinderStack = new Stack<>();
        appCMSBinderMap = new HashMap<>();

        appCMSPresenter = ((AppCMSApplication) getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        new Thread(() -> Fabric.with(getApplication(), new Crashlytics())).run();
        //Settings The Firebase Analytics for TV
        FirebaseAnalytics mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (mFireBaseAnalytics != null && appCMSPresenter != null) {
            appCMSPresenter.setmFireBaseAnalytics(mFireBaseAnalytics);
        }

        if (null != appCMSPresenter.getAppCMSAndroid() && appCMSPresenter.getAppCMSAndroid().getAnalytics() != null) {
            appCMSPresenter.initializeGA(appCMSPresenter.getAppCMSAndroid().getAnalytics().getGoogleAnalyticsId());
        }


        String tag = getTag(updatedAppCMSBinder);
        appCMSBinderStack.push(tag);
        appCMSBinderMap.put(tag, updatedAppCMSBinder);

        AppCMSMain appCMSMain = appCMSBinder.getAppCMSMain();

        AppCMSSite appCMSSite = appCMSPresenter.getAppCMSSite();

        int textColor = Color.parseColor(appCMSMain.getBrand().getCta().getPrimary().getTextColor());/*Color.parseColor("#F6546A");*/
        int bgColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getBackgroundColor());//Color.parseColor("#660066");

        String[] color = appCMSPresenter.getAppBackgroundColor().split("#");
        String firstColor = "#ff" + color[1];
        String secondColor = "#00" + color[1];


        navigationFragment = AppCmsNavigationFragment.newInstance(
                this,
                this,
                appCMSBinder,
                textColor,
                bgColor);

        //  appCmsSubNavigationFragment = AppCmsSubNavigationFragment.newInstance(this, this);

        setContentView(R.layout.activity_app_cms_tv_home);

        RelativeLayout subscribeNowStripContaineer = (RelativeLayout) findViewById(R.id.subscribe_now_strip_containeer);
        navParentContainer = (RelativeLayout) findViewById(R.id.navigation_layouts_container);
        navHolder = (FrameLayout) findViewById(R.id.navigation_placholder);
        // subNavHolder = (FrameLayout) findViewById(R.id.sub_navigation_placeholder);

        if (appCMSPresenter.isLeftNavigationEnabled()) {
//            navHolder.findViewById(R.id.left_menu_app_logo).setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = navHolder.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.width = 700;

            RelativeLayout.LayoutParams parentLayoutparam = (RelativeLayout.LayoutParams) navParentContainer.getLayoutParams();
            parentLayoutparam.height = ViewGroup.LayoutParams.MATCH_PARENT;
            parentLayoutparam.addRule(RelativeLayout.CENTER_HORIZONTAL, R.id.navRecylerView);
            navParentContainer.bringToFront();
            navParentContainer.setBackground(getDrawable(R.drawable.left_nav_gradient));
            navParentContainer.getBackground().setTint(Color.parseColor(appCMSPresenter.getAppBackgroundColor()));
            navParentContainer.setVisibility(View.INVISIBLE);
        } else if (appCMSPresenter.getTemplateType().equals(AppCMSPresenter.TemplateType.SPORTS)) {
            ViewGroup.LayoutParams layoutParams = navHolder.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) subscribeNowStripContaineer.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.navigation_layouts_container);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) subscribeNowStripContaineer.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.navigation_layouts_container);
        }
        homeHolder = (FrameLayout) findViewById(R.id.home_placeholder);
        homeHolder.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppBackgroundColor()));
        shadowView = (FrameLayout) findViewById(R.id.shadow_view);
        setNavigationFragment(navigationFragment);
        if (appCMSPresenter.isLeftNavigationEnabled()) {
            showNavigation(true);
        }
        setPageFragment(appCMSBinder);
        appCMSPresenter.sendGaScreen(appCMSBinder.getScreenName());
        showInfoIcon(appCMSBinder.getPageId());

        if (null == appCMSSearchUrlComponent) {
            appCMSSearchUrlComponent = DaggerAppCmsTvSearchComponent.builder()
                    .appCMSSearchUrlModule(new AppCMSSearchUrlModule(appCMSMain.getApiBaseUrl(),
                            appCMSSite.getGist().getSiteInternalName(),
                            appCMSPresenter.getApiKey(),
                            appCMSBinder.getAppCMSSearchCall()))
                    .build();
        }

        updateHistoryDataReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppCMSPresenter.PRESENTER_UPDATE_HISTORY_ACTION)) {
                    updateData();
                }
            }
        };

        updateWatchListDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppCMSPresenter.PRESENTER_UPDATE_WATCHLIST_ACTION)) {
                    updateWatchListData();
                }
            }
        };

        registerReceiver(updateWatchListDataReceiver, new IntentFilter(AppCMSPresenter.PRESENTER_UPDATE_WATCHLIST_ACTION));
        registerReceiver(updateHistoryDataReciever, new IntentFilter(AppCMSPresenter.PRESENTER_UPDATE_HISTORY_ACTION));

        presenterActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION)) {
                    Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
                    try {
                        if (isActive) {
                            if (/*!appCMSPresenter.isPagePrimary(((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key))).getPageId()) &&*/
                                    (appCMSPresenter.isPageUser(((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key))).getPageId())
                                            || appCMSPresenter.isPageFooter(((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key))).getPageId())
                                            || appCMSPresenter.getTosPage().getPageId().equalsIgnoreCase(((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key))).getPageId())
                                            || appCMSPresenter.getPrivacyPolicyPage().getPageId().equalsIgnoreCase(((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key))).getPageId()))) {
                                //check first its a request for Terms of service or Privacy Policy dialog.
                                if ((((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key))).getExtraScreenType() ==
                                        AppCMSPresenter.ExtraScreenType.TERM_OF_SERVICE)) {
                                    openGenericDialog(intent, false);
                                } else if ((((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key))).getExtraScreenType() ==
                                        AppCMSPresenter.ExtraScreenType.EDIT_PROFILE)) {
                                    AppCMSBinder binder = (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
                                    if (binder.getPageName().equalsIgnoreCase(getString(R.string.app_cms_sign_up_pager_title))) {
                                        openSignUpDialog(intent, true);
                                    } else {
                                        openLoginDialog(intent, true);
                                    }
                                } else {
                                    openMyProfile();
                                    handleProfileFragmentAction((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key)));
                                    showNavigation(false);
                                    /*if(appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.SPORTS){
                                        showSubNavigation(false, false); //close subnavigation if any.
                                    }*/
                            }
                            } else {
                                updatedAppCMSBinder = (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
                                handleLaunchPageAction(updatedAppCMSBinder);
                                showNavigation(false); //close navigation if any.
                            }
                        }
                    } catch (ClassCastException e) {
                        //Log.e(TAG, "Could not read AppCMSBinder: " + e.toString());
                    }
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION)) {
                    Utils.pageLoading(true, AppCmsHomeActivity.this);
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION)) {
                    Utils.pageLoading(false, AppCmsHomeActivity.this);
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_RESET_NAVIGATION_ITEM_ACTION)) {
                    //Log.d(TAG, "Nav item - Received broadcast to select navigation item with page Id: " +
//                            intent.getStringExtra(getString(R.string.navigation_item_key)));
                    //  selectNavItem(intent.getStringExtra(getString(R.string.navigation_item_key)));
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_DEEPLINK_ACTION)) {
                    if (intent.getData() != null) {
                        //   processDeepLink(intent.getData());
                    }
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_DIALOG_ACTION)) {
                    Bundle bundle = intent.getBundleExtra(getString(R.string.dialog_item_key));
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    TextOverlayDialogFragment newFragment = TextOverlayDialogFragment.newInstance(
                            context,
                            bundle);
                    newFragment.show(ft, DIALOG_FRAGMENT_TAG);
                } else if (intent.getAction().equals(AppCMSPresenter.SEARCH_ACTION)) {
                    openSearchFragment(intent);
                    //  showSubNavigation(false, false); //close subnavigation if any.
                    showNavigation(false); //close navigation if any.
                } else if (intent.getAction().equals(AppCMSPresenter.CLOSE_DIALOG_ACTION)) {
                    Utils.pageLoading(false, AppCmsHomeActivity.this);
                    closeSignInDialog();
                    closeSignUpDialog();
                } else if (intent.getAction().equals(AppCMSPresenter.ERROR_DIALOG_ACTION)) {
                    openErrorDialog(intent);
                } else if (intent.getAction().equals(AppCMSPresenter.ACTION_RESET_PASSWORD)) {
                    openResetPasswordScreen(intent);
                } else if (intent.getAction().equals(AppCMSPresenter.ACTION_LINK_YOUR_ACCOUNT)) {
                    openLinkYourAccountScreen(intent);
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_CLEAR_DIALOG_ACTION)) {

                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_UPDATE_HISTORY_ACTION)) {
                    updateData();
                } else if (intent.getAction().equals(AppCMSPresenter.UPDATE_SUBSCRIPTION)) {
                    updateSubscriptionStrip();
                } else if (intent.getAction().equals(AppCMSPresenter.SWITCH_SEASON_ACTION)) {
                    AppCMSSwitchSeasonBinder appCMSSwitchSeasonBinder = (AppCMSSwitchSeasonBinder) intent.getExtras().getBundle("app_cms_season_selector_key").getBinder("app_cms_episode_data");
                    AppCmsTVPageFragment parentFragment = (AppCmsTVPageFragment) getSupportFragmentManager().findFragmentById(R.id.home_placeholder);
                    parentFragment.updateSeasonData(appCMSSwitchSeasonBinder);
                } else if (intent.getAction().equals(getString(R.string.intent_msg_action))) {
//                    Toast.makeText(AppCmsHomeActivity.this, intent.getStringExtra(getString(R.string.json_data_msg_key)), Toast.LENGTH_SHORT).show();
                    if (intent.getStringExtra(getString(R.string.json_data_msg_key)).equalsIgnoreCase(getString(R.string.adm_directive_search_and_play))) {
                        String contentId = intent.getStringExtra(getString(R.string.json_content_id_key));
                        if (contentId != null) {
                            ContentDatum contentDatum = new ContentDatum();
                            Gist gist = new Gist();
                            gist.setId(contentId);
                            contentDatum.setGist(gist);
                            appCMSPresenter.launchTVVideoPlayer(
                                    contentDatum,
                                    0,
                                    null,
                                    0,
                                    null);
                        } else {
                            Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show();
                        }
                    } else if (intent.getStringExtra(getString(R.string.json_data_msg_key)).equalsIgnoreCase(getString(R.string.adm_directive_search_and_display_results))) {
                        String searchString = intent.getStringExtra(getString(R.string.json_seek_search_string_key));
                        appCMSPresenter.openSearch("Search", "Search", searchString);
                    } else if (intent.getStringExtra(getString(R.string.json_data_msg_key)).equalsIgnoreCase(getString(R.string.adm_directive_play))) {
                    } else if (intent.getStringExtra(getString(R.string.json_data_msg_key)).equalsIgnoreCase(getString(R.string.adm_directive_pause))) {
                    } else if (intent.getStringExtra(getString(R.string.json_data_msg_key)).equalsIgnoreCase(getString(R.string.adm_directive_fast_forward))) {
                    } else if (intent.getStringExtra(getString(R.string.json_data_msg_key)).equalsIgnoreCase(getString(R.string.adm_directive_rewind))) {
                    }
                }
            }
        };


        updateSubscriptionStrip();
        //Show "Push menu button for menu" icon.
        if (appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.SPORTS) {
            findViewById(R.id.press_up_button).setVisibility(View.VISIBLE);
            findViewById(R.id.top_logo).setVisibility(View.VISIBLE);

            findViewById(R.id.footer_logo).setVisibility(View.INVISIBLE);
            showMenuIcon(View.INVISIBLE);
            findViewById(R.id.black_shadow).setVisibility(View.INVISIBLE);

        } else {
            findViewById(R.id.press_up_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.press_down_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.top_logo).setVisibility(View.INVISIBLE);

            findViewById(R.id.footer_logo).setVisibility(View.VISIBLE);
            showMenuIcon(View.VISIBLE);
            findViewById(R.id.black_shadow).setVisibility(View.VISIBLE);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    private void showMenuIcon(int visibility) {
        if (null != findViewById(R.id.info_icon))
            findViewById(R.id.info_icon).setVisibility(
                    appCMSPresenter.isLeftNavigationEnabled() ? View.INVISIBLE : visibility
            );
    }

    private void startSplashActivity() {
        Intent intent = new Intent(AppCmsHomeActivity.this, AppCmsTVSplashActivity.class);
        startActivity(intent);
    }

    private void updateSubscriptionStrip() {
        /*Check Subscription in case of SPORTS TEMPLATE*/
        if (appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.SPORTS) {
            if (appCMSPresenter.isAppSVOD()) {
                if (!appCMSPresenter.isUserLoggedIn()) {
                    setSubscriptionText(false);
                } else {

                    if (appCMSPresenter.getCurrentActivity() != null) {
                        appCMSPresenter.getSubscriptionData(appCMSUserSubscriptionPlanResult -> {
                            try {
                                if (appCMSUserSubscriptionPlanResult != null) {
                                    String subscriptionStatus = appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus();
                                    if (subscriptionStatus.equalsIgnoreCase("COMPLETED") ||
                                            subscriptionStatus.equalsIgnoreCase("DEFERRED_CANCELLATION")) {
                                        setSubscriptionText(true);
                                    } else {
                                        setSubscriptionText(false);
                                    }
                                } else {
                                    setSubscriptionText(false);
                                }
                            } catch (Exception e) {
                                setSubscriptionText(false);
                            }
                        });
                    }
                }
            } else {
                setSubscriptionText(true);
            }
        } else {
            findViewById(R.id.subscribe_now_strip).setVisibility(View.GONE);
        }
    }

    private void setSubscriptionText(boolean isSubscribe) {
        String message = getResources().getString(R.string.blank_string);
        if (!isSubscribe) {
            if (null != appCMSPresenter && null != appCMSPresenter.getNavigation()
                    && null != appCMSPresenter.getNavigation().getSettings()
                    && null != appCMSPresenter.getNavigation().getSettings().getPrimaryCta()
                    ) {
                message = appCMSPresenter.getNavigation().getSettings().getPrimaryCta().getBannerText() +
                        appCMSPresenter.getNavigation().getSettings().getPrimaryCta().getCtaText();
            } else {
                message = getResources().getString(R.string.watch_live_text);
            }
        }

        TextView textView = (TextView) findViewById(R.id.subscribe_now_strip);
        textView.setText(message);
        textView.setBackgroundColor(Color.parseColor(Utils.getFocusColor(this, appCMSPresenter)));
        textView.setTextColor(Color.parseColor(Utils.getTextColor(this, appCMSPresenter)));
        textView.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        if (message.length() == 0) {
            layoutParams.height = 10;
        } else {
            layoutParams.height = 40;
        }
        textView.setLayoutParams(layoutParams);
    }

    private void handleProfileFragmentAction(AppCMSBinder updatedAppCMSBinder) {
        String tag = getTag(updatedAppCMSBinder);
        appCMSBinderMap.put(tag, updatedAppCMSBinder);
        Fragment previousFragment = getSupportFragmentManager().findFragmentById(R.id.home_placeholder);
        if (null != previousFragment && previousFragment instanceof AppCmsMyProfileFragment) {
            getSupportFragmentManager().popBackStack();
            appCMSBinderStack.pop();
        }

        appCMSPresenter.sendGaScreen(updatedAppCMSBinder.getScreenName());
        AppCmsMyProfileFragment appCmsMyProfileFragment = AppCmsMyProfileFragment.newInstance(this, updatedAppCMSBinder);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.home_placeholder, appCmsMyProfileFragment, tag).addToBackStack(tag).commitAllowingStateLoss();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppCMSPresenter.PLAYER_REQUEST_CODE) {
            updateSubscriptionStrip();
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.home_placeholder);
            if (null != fragment && fragment instanceof AppCmsTVPageFragment) {
                ((AppCmsTVPageFragment) fragment).refreshBrowseFragment();
                ((AppCmsTVPageFragment) fragment).refreshPage();
                AppCmsTVPageFragment appCmsTVPageFragment = ((AppCmsTVPageFragment) fragment);
                AppCMSBinder appCmsBinder = appCMSBinderMap.get(appCmsTVPageFragment.getTag());
                if (appCmsBinder.getPageName()
                        .equalsIgnoreCase(getString(R.string.app_cms_history_navigation_title))) {
                    ((AppCmsTVPageFragment) fragment).updateAdapterData(appCmsBinder);
                }
            } else if (null != fragment && fragment instanceof AppCmsMyProfileFragment) {
                AppCmsMyProfileFragment profileFragment = ((AppCmsMyProfileFragment) fragment);
                AppCMSBinder appCmsBinder = appCMSBinderMap.get(profileFragment.getTag());
                ((AppCmsMyProfileFragment) fragment).updateAdapterData(appCmsBinder);
            }

            if (resultCode == OPEN_SEARCH_RESULT_CODE) {
                new Handler().postDelayed(() -> {
                    appCMSPresenter.openSearch("Search","Search",
                            data.getStringExtra(getString(R.string.json_seek_search_string_key)));
                }, 0);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceivers();
        isActive = true;
        appCMSPresenter.tryLaunchingPlayerFromDeeplink(null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getAction() != null && intent.getData() != null) {
            if (intent.getAction().equalsIgnoreCase(getString(R.string.LAUNCHER_DEEPLINK_ACTION))) {
                appCMSPresenter.tryLaunchingPlayerFromDeeplink(null);
            }
        }
    }

    private void registerReceivers() {
        registerReceiver(presenterActionReceiver, new IntentFilter(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION));
        registerReceiver(presenterActionReceiver, new IntentFilter(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
        registerReceiver(presenterActionReceiver, new IntentFilter(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION));
        registerReceiver(presenterActionReceiver, new IntentFilter(AppCMSPresenter.PRESENTER_RESET_NAVIGATION_ITEM_ACTION));
        registerReceiver(presenterActionReceiver, new IntentFilter(AppCMSPresenter.PRESENTER_DIALOG_ACTION));
        registerReceiver(presenterActionReceiver, new IntentFilter(AppCMSPresenter.PRESENTER_CLEAR_DIALOG_ACTION));
        registerReceiver(presenterActionReceiver, new IntentFilter(AppCMSPresenter.SEARCH_ACTION));
        registerReceiver(presenterActionReceiver, new IntentFilter(AppCMSPresenter.CLOSE_DIALOG_ACTION));
        registerReceiver(presenterActionReceiver, new IntentFilter(AppCMSPresenter.ERROR_DIALOG_ACTION));
        registerReceiver(presenterActionReceiver, new IntentFilter(AppCMSPresenter.ACTION_RESET_PASSWORD));
        registerReceiver(presenterActionReceiver, new IntentFilter(AppCMSPresenter.UPDATE_SUBSCRIPTION));
        registerReceiver(presenterActionReceiver, new IntentFilter(AppCMSPresenter.SWITCH_SEASON_ACTION));
        registerReceiver(presenterActionReceiver, new IntentFilter(AppCMSPresenter.ACTION_LINK_YOUR_ACCOUNT));
        registerReceiver(presenterActionReceiver, new IntentFilter(getString(R.string.intent_msg_action)));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(presenterActionReceiver);

        //when activity pause then close the full screen player.
        if (appCMSPresenter.isFullScreenVisible) {
            appCMSPresenter.tvVideoPlayerView.getPlayerView().hideController();
            appCMSPresenter.tvVideoPlayerView.getPlayerView().setUseController(false);
            appCMSPresenter.exitFullScreenTVPlayer();
        }

        super.onPause();
        isActive = false;
    }

    @Override
    protected void onStop() {
        if (isNavigationVisible()) {
            handleNavigationVisibility();
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    private void openErrorDialog(Intent intent) {
        Utils.pageLoading(false, this);
        Bundle bundle = intent.getBundleExtra(getString(R.string.retryCallBundleKey));
        bundle.putBoolean(getString(R.string.retry_key), bundle.getBoolean(getString(R.string.retry_key)));
        bundle.putBoolean(getString(R.string.register_internet_receiver_key), bundle.getBoolean(getString(R.string.register_internet_receiver_key)));
        bundle.putString(getString(R.string.tv_dialog_msg_key), bundle.getString(getString(R.string.tv_dialog_msg_key)));
        bundle.putString(getString(R.string.tv_dialog_header_key), bundle.getString(getString(R.string.tv_dialog_header_key)));

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AppCmsTvErrorFragment newFragment = AppCmsTvErrorFragment.newInstance(
                bundle);
        newFragment.setErrorListener(this);
        newFragment.show(ft, DIALOG_FRAGMENT_TAG);
    }

    private void openResetPasswordScreen(Intent intent) {
        if (null != intent) {
            Bundle bundle = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
            if (null != bundle) {
                AppCMSBinder appCMSBinder = (AppCMSBinder) bundle.get(getString(R.string.app_cms_binder_key));
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                appCmsResetPasswordFragment = AppCmsResetPasswordFragment.newInstance(
                        appCMSBinder);
                appCmsResetPasswordFragment.show(ft, DIALOG_FRAGMENT_TAG);
                Utils.pageLoading(false, this);
            }
        }
    }

    private void openLoginDialog(Intent intent, boolean isLoginPage) {
        if (null != intent) {
            Bundle bundle = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
            if (null != bundle) {
                AppCMSBinder appCMSBinder = (AppCMSBinder) bundle.get(getString(R.string.app_cms_binder_key));
                bundle.putBoolean("isLoginPage", isLoginPage);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                loginDialog = AppCmsLoginDialogFragment.newInstance(
                        appCMSBinder);
                loginDialog.show(ft, DIALOG_FRAGMENT_TAG);
                Utils.pageLoading(false, this);
            }
        }
    }

    private void openSignUpDialog(Intent intent, boolean isLoginPage) {
        if (null != intent) {
            Bundle bundle = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
            if (null != bundle) {
                AppCMSBinder appCMSBinder = (AppCMSBinder) bundle.get(getString(R.string.app_cms_binder_key));
                bundle.putBoolean("isLoginPage", isLoginPage);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                signUpDialog = AppCmsSignUpDialogFragment.newInstance(
                        appCMSBinder);
                signUpDialog.show(ft, DIALOG_FRAGMENT_TAG);
                Utils.pageLoading(false, this);
            }
        }
    }

    private void openGenericDialog(Intent intent, boolean isLoginPage) {
        if (null != intent) {
            Bundle bundle = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
            if (null != bundle) {
                AppCMSBinder appCMSBinder = (AppCMSBinder) bundle.get(getString(R.string.app_cms_binder_key));
                bundle.putBoolean("isLoginPage", isLoginPage);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                AppCmsGenericDialogFragment newFragment = AppCmsGenericDialogFragment.newInstance(
                        appCMSBinder);
                newFragment.show(ft, DIALOG_FRAGMENT_TAG);
                Utils.pageLoading(false, this);
            }
        }


    }

    @Override
    public void onErrorScreenClose() {
        if (appCmsResetPasswordFragment != null
                && appCmsResetPasswordFragment.isAdded()
                && appCmsResetPasswordFragment.isVisible()) {
            appCmsResetPasswordFragment.dismiss();
        }
    }

    @Override
    public void onRetry(Bundle bundle) {
        RetryCallBinder retryCallBinder = (RetryCallBinder) bundle.getBinder(getString(R.string.retryCallBinderKey));
        AppCMSPresenter.RETRY_TYPE retryType = retryCallBinder != null ? retryCallBinder.getRetry_type() : null;
        boolean isTosPage = bundle.getBoolean(getString(R.string.is_tos_dialog_page_key));
        boolean isLoginPage = bundle.getBoolean(getString(R.string.is_login_dialog_page_key));
        if (retryType != null) {
            switch (retryType) {
                case BUTTON_ACTION:
                    appCMSPresenter.launchTVButtonSelectedAction(
                            retryCallBinder.getPagePath(),
                            retryCallBinder.getAction(),
                            retryCallBinder.getFilmTitle(),
                            retryCallBinder.getExtraData(),
                            retryCallBinder.getContentDatum(),
                            retryCallBinder.isCloselauncher(),
                            -1,
                            null,
                            null);
                    break;
                case VIDEO_ACTION:
                    appCMSPresenter.launchTVVideoPlayer(
                            retryCallBinder.getContentDatum(),
                            0,
                            retryCallBinder.getContentDatum().getContentDetails() != null
                                    ? retryCallBinder.getContentDatum().getContentDetails().getRelatedVideoIds()
                                    : null,
                            retryCallBinder.getContentDatum().getGist().getWatchedTime(),
                            null);

                    break;
                case PAGE_ACTION:
                    appCMSPresenter.navigateToTVPage(
                            retryCallBinder.getFilmId(),
                            retryCallBinder.getFilmTitle(),
                            retryCallBinder.getPagePath(),
                            retryCallBinder.isCloselauncher(),
                            Uri.EMPTY,
                            false,
                            isTosPage,
                            isLoginPage);
                    break;

                case SEARCH_RETRY_ACTION:
                    String tag = getString(R.string.app_cms_search_label);
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                    if (fragment instanceof AppCmsSearchFragment) {
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
                case SUB_NAV_RETRY_ACTION:
                    appCMSPresenter.showLoadingDialog(true);
                    appCMSPresenter.navigateToSubNavigationPage(
                            retryCallBinder.getPageId(),
                            retryCallBinder.getFilmTitle(),
                            retryCallBinder.getPagePath(),
                            retryCallBinder.getPrimary(),
                            retryCallBinder.getItems(),
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
                case RESET_PASSWORD_RETRY:
                    appCMSPresenter.showLoadingDialog(true);
                    appCMSPresenter.resetPassword(retryCallBinder.getFilmTitle()); //filmtitle here means emailid.
                    break;

                case LOGOUT_ACTION:
                    appCMSPresenter.logoutTV();
                    break;

                case EDIT_WATCHLIST:
                    if (appCMSPresenter.isNetworkConnected()) {
                        appCMSPresenter.editWatchlist(retryCallBinder.getContentDatum(),
                                retryCallBinder.getCallback(),
                                !bundle.getBoolean("queued"), true);
                    } else {
                        appCMSPresenter.openErrorDialog(retryCallBinder.getContentDatum(),
                                bundle.getBoolean("queued"),
                                retryCallBinder.getCallback());
                    }
                    break;
            }
        }
    }

    private String getTag(AppCMSBinder appCMSBinder) {
        String key = null;
        if (!appCMSPresenter.isPagePrimary(appCMSBinder.getPageId())) {
            key = appCMSBinder.getPageId() + appCMSBinder.getScreenName();
        } else {
            key = appCMSBinder.getPageId();
        }
        return key;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void selectNavItem(String pageId) {
        //Log.d(TAG , "Nav Pageid = "+pageId);
        navigationFragment.setSelectedPageId(pageId);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(updateHistoryDataReciever);
        unregisterReceiver(updateWatchListDataReceiver);
        appCMSPresenter.getPlayerLruCache().evictAll();
        super.onDestroy();
    }

    private void handleLaunchPageAction(AppCMSBinder appCMSBinder) {

        int distanceFromStackTop = -1;
        String tag = getTag(appCMSBinder);

        distanceFromStackTop = appCMSBinderStack.search(tag);

        //Log.d(TAG, "Page distance from top: " + distanceFromStackTop);
//        if (0 < distanceFromStackTop) {
//            for (int i = 0; i < distanceFromStackTop; i++) {
//                //Log.d(TAG, "Popping stack to get to page item");
//                try {
//                    appCMSBinderStack.pop();
//                    //getFragmentManager().popBackStack();
//                } catch (IllegalStateException e) {
//                    //Log.e(TAG, "Error popping back stack: " + e.getMessage());
//                }
//            }
//        }

        appCMSBinderStack.push(tag);
        appCMSBinderMap.put(tag, appCMSBinder);

        showInfoIcon(appCMSBinder.getPageId());
        //Log.d(TAG, "Launching new page: " + appCMSBinder.getPageName());
        appCMSPresenter.sendGaScreen(appCMSBinder.getScreenName());
        boolean isPoped = getSupportFragmentManager().popBackStackImmediate(appCMSBinder.getPageId(), 1);

        if (isPoped) {
            if (appCMSBinderStack.contains(getTag(appCMSBinder)))
                appCMSBinderStack.remove(getTag(appCMSBinder));
        }
        //if(!isPoped)
        setPageFragment(updatedAppCMSBinder);
        //else
        //selectNavItem(updatedAppCMSBinder.getPageId());
    }

    private Fragment getTopFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        Fragment currentFragment = fragmentManager.findFragmentByTag(fragmentTag);
        return currentFragment;
    }

    @Override
    public void onBackPressed() {

        if (AppCMSPresenter.isFullScreenVisible) {
            appCMSPresenter.tvVideoPlayerView.getPlayerView().hideController();
            appCMSPresenter.tvVideoPlayerView.getPlayerView().setUseController(false);
            appCMSPresenter.exitFullScreenTVPlayer();
            return;
        }

        //if navigation is visible then first hide the navigation.
        if (isNavigationVisible()) {
            handleNavigationVisibility();
            return;
        }
       /* if (isSubNavigationVisible()) {
            if (appCmsSubNavigationFragment.isTeamsShowing()) {
                handleNavigationVisibility();
                showSubNavigation(false, false);
            } else {
                handleNavigationVisibility();
                showSubNavigation(false, true);
            }
            return;
        }*/

        /*if (appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.SPORTS) {
            if (null != getTopFragment() && getTopFragment() instanceof AppCmsMyProfileFragment) {
                showSubNavigation(true, false);
            }
        }*/

        if (appCMSBinderStack.size() > 0) {
            appCMSBinderStack.pop();
        }

        if (appCMSBinderStack.size() > 0) {
            if (appCMSBinderStack.peek().equalsIgnoreCase(getString(R.string.app_cms_search_label))
                    || appCMSBinderStack.peek().equalsIgnoreCase(getString(R.string.app_cms_my_profile_label,
                    getString(R.string.profile_label)))) {
                selectNavItem(appCMSBinderStack.peek());
                showInfoIcon(appCMSBinderStack.peek());
            } else {
                updatedAppCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
                String pageId = null;
                if (null != updatedAppCMSBinder) {
                    pageId = updatedAppCMSBinder.getPageId();
                }
                if (null == pageId) {
                    pageId = appCMSBinderStack.peek();
                }
                selectNavItem(pageId);
                showInfoIcon(pageId);
            }
        }

        super.onBackPressed();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
    }

   /* @Override
    public void showSubNavigation(boolean shouldShow, boolean showTeams) {
        new Handler().post(() -> {
            *//*subNavHolder.setVisibility(shouldShow ? View.VISIBLE : View.GONE);*//*
            shadowView.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
            appCmsSubNavigationFragment.setFocusable(shouldShow);
            if (shouldShow) {
                // navigationFragment.setSelectorColor();
                appCmsSubNavigationFragment.notifyDataSetInvalidate(showTeams);
            }
        });
    }*/

    private void setPageFragment(AppCMSBinder appCMSBinder) {
        Fragment attached = getSupportFragmentManager().findFragmentById(R.id.home_placeholder);
        if (attached == null || (attached != null && !attached.getTag().equalsIgnoreCase(getTag(appCMSBinder)))) {
            AppCmsTVPageFragment appCMSPageFragment = AppCmsTVPageFragment.newInstance(this, appCMSBinder);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            String tag = getTag(appCMSBinder);
            fragmentTransaction.replace(R.id.home_placeholder, appCMSPageFragment, tag).addToBackStack(tag).commitAllowingStateLoss();
        } else {
            if (appCMSBinderStack.contains(getTag(appCMSBinder)))
                appCMSBinderStack.remove(getTag(appCMSBinder));
            if (null != appCMSPresenter)
                appCMSPresenter.sendStopLoadingPageAction(false, null);
        }
        selectNavItem(appCMSBinder.getPageId());
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();
        if (AppCMSPresenter.isFullScreenVisible) {
            appCMSPresenter.tvVideoPlayerView.getPlayerView().showController();
            switch (action) {
                case KeyEvent.ACTION_DOWN:
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            appCMSPresenter.tvVideoPlayerView.findViewById(R.id.exo_pause).requestFocus();
                            appCMSPresenter.tvVideoPlayerView.findViewById(R.id.exo_play).requestFocus();
                            if (appCMSPresenter.tvVideoPlayerView.getPlayerView() != null) {
                                appCMSPresenter.tvVideoPlayerView.setHardPause(appCMSPresenter.tvVideoPlayerView.getPlayer().getPlayWhenReady());
                                if (appCMSPresenter.tvVideoPlayerView.getPlayer().getPlayWhenReady()) {
                                    appCMSPresenter.tvVideoPlayerView.getPlayerView().getPlayer().seekTo(appCMSPresenter.tvVideoPlayerView.getPlayer().getContentPosition() + 1000);
                                }
                                return super.dispatchKeyEvent(event)
                                        || appCMSPresenter.tvVideoPlayerView.getPlayerView()
                                        .dispatchKeyEvent(event);
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                            if (appCMSPresenter.tvVideoPlayerView.getPlayerView() != null
                                    && (findViewById(R.id.exo_play).hasFocus()
                                    || findViewById(R.id.exo_pause).hasFocus())) {
                                appCMSPresenter.tvVideoPlayerView.setHardPause(appCMSPresenter.tvVideoPlayerView.getPlayer().getPlayWhenReady());
                                if (appCMSPresenter.tvVideoPlayerView.getPlayer().getPlayWhenReady()) {
                                    appCMSPresenter.tvVideoPlayerView.getPlayerView().getPlayer().seekTo(appCMSPresenter.tvVideoPlayerView.getPlayer().getContentPosition() + 1000);
                                }
                            }
                            return super.dispatchKeyEvent(event);
                        case KeyEvent.KEYCODE_MEDIA_REWIND:
                            if (null != appCMSPresenter.tvVideoPlayerView) {
                                if (appCMSPresenter.tvVideoPlayerView.isLiveStream()) return true;
                                appCMSPresenter.tvVideoPlayerView.findViewById(R.id.exo_rew).requestFocus();
                                return super.dispatchKeyEvent(event);
                            }
                            break;
                        case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                            if (null != appCMSPresenter.tvVideoPlayerView) {
                                if (appCMSPresenter.tvVideoPlayerView.isLiveStream()) return true;
                                appCMSPresenter.tvVideoPlayerView.findViewById(R.id.exo_ffwd).requestFocus();
                                return super.dispatchKeyEvent(event);
                            }
                        case KeyEvent.KEYCODE_DPAD_UP:
                            if (findViewById(R.id.exo_pause).hasFocus() ||
                                    findViewById(R.id.exo_play).hasFocus() ||
                                    findViewById(R.id.exo_ffwd).hasFocus() ||
                                    findViewById(R.id.exo_rew).hasFocus()) {
                                return true;
                            } else {
                                return super.dispatchKeyEvent(event);
                            }
                        default:
                            return super.dispatchKeyEvent(event);
                    }
                default:
                    return super.dispatchKeyEvent(event);
            }
        }

        switch (action) {
            case KeyEvent.ACTION_DOWN:
                switch (keyCode) {
                    case KeyEvent.KEYCODE_MENU:
                        if (!appCMSPresenter.isLeftNavigationEnabled()) {
                            handleNavigationVisibility();
                            hideFooterControl();
                        }
                        return true;
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        handlePlayRemoteKey();
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        //if navigation fragment is open then hold down key event otherwise pass it.
                        if (!appCMSPresenter.isLeftNavigationEnabled()
                                && isNavigationVisible()
                                && (navigationFragment.getNavMenuSubscriptionModule() != null
                                && !navigationFragment.getNavMenuSubscriptionModule().isFocused())) {
                            handleNavigationVisibility();
                            return true;
                        }
                        break;

                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        if (appCMSPresenter.isLeftNavigationEnabled()) {
                            if (getTopFragment() instanceof BaseFragment) {
                                BaseFragment baseFragment = (BaseFragment) getTopFragment();
                                if (baseFragment.isSubNavExist()) {
                                    if (baseFragment.isSubNavigationVisible()) {
                                        baseFragment.showSubNavigation(false);
                                    } else if (shouldShowSubLeftNav) {
                                        baseFragment.showSubNavigation(true);
                                    }
                                } else if (shouldShowLeftNav) {
                                    handleNavigationVisibility();
                                    hideFooterControl();
                                }
                            }
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        if (appCMSPresenter.isLeftNavigationEnabled()) {
                            if (getTopFragment() instanceof BaseFragment) {
                                BaseFragment baseFragment = (BaseFragment) getTopFragment();
                                if (baseFragment.isSubNavExist()) {
                                    if (baseFragment.isSubNavigationVisible()) {
                                        baseFragment.showSubNavigation(false);
                                    }
                                } else if (isNavigationVisible()) {
                                    handleNavigationVisibility();
                                    hideFooterControl();
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
            case KeyEvent.ACTION_UP:
                switch (keyCode) {
                    case KeyEvent.KEYCODE_MENU:
                        return true;
                }
        }
        return super.dispatchKeyEvent(event);
    }

    private void handleNavigationVisibility() {
        if (!appCMSBinderStack.isEmpty() && appCMSPresenter.isPagePrimary(appCMSBinderStack.peek())) {

            Fragment parentFragment = getSupportFragmentManager().findFragmentById(R.id.home_placeholder);
            AppCmsBrowseFragment browseFragment = null;
            if (null != parentFragment) {
                if (parentFragment instanceof AppCmsTVPageFragment) {
                    browseFragment = (AppCmsBrowseFragment) parentFragment.getChildFragmentManager().
                            findFragmentById(R.id.appcms_browsefragment);
                }
            }

            if (isNavigationVisible()) {
                showNavigation(false);
                if (null != browseFragment && null != browseFragment.getCustomVideoVideoPlayerView() /*&& !isSubNavigationVisible()*/) {
                    browseFragment.getCustomVideoVideoPlayerView().resumePlayer();
                }
            } else {
                if (null != browseFragment && null != browseFragment.getCustomVideoVideoPlayerView()) {
                    browseFragment.getCustomVideoVideoPlayerView().pausePlayer();
                }
                showNavigation(true);
            }
        }
    }

    public void keyPressed(View v) {
        String tag = appCMSBinderStack.peek();//getString(R.string.app_cms_search_label);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment instanceof AppCmsSearchFragment) {
            ((AppCmsSearchFragment) fragment).keyPressed(v);
        }
    }

    private void handlePlayRemoteKey() {
        Fragment parentFragment = getSupportFragmentManager().findFragmentById(R.id.home_placeholder);
        if (null != parentFragment) {
            AppCmsBrowseFragment browseFragment = null;
            if (parentFragment instanceof AppCmsTVPageFragment) {
                browseFragment = (AppCmsBrowseFragment) parentFragment.getChildFragmentManager().
                        findFragmentById(R.id.appcms_browsefragment);
            } else if (parentFragment instanceof AppCmsSearchFragment) {
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
        new Handler().post(() -> {
            navHolder.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
            shadowView.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
            if(appCMSPresenter.isLeftNavigationEnabled())
            navParentContainer.setVisibility(shouldShow ? View.VISIBLE : View.INVISIBLE);
            navigationFragment.setFocusable(shouldShow);
            if (shouldShow) {
                // navigationFragment.setSelectorColor();
                navigationFragment.notifyDataSetInvalidate();
            }
        });
    }

    public boolean isNavigationVisible() {
        return (navHolder.getVisibility() == View.VISIBLE);
    }

    public void openSearchFragment(Intent intent) {
        Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
        AppCMSBinder appCmsBinder = (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
        int distanceFromStackTop = -1;
        String tag = getTag(appCmsBinder);/*getString(R.string.app_cms_search_label);*/

        distanceFromStackTop = appCMSBinderStack.search(tag);

        //Log.d(TAG, "Page distance from top: " + distanceFromStackTop);
        if (0 < distanceFromStackTop) {
            for (int i = 0; i < distanceFromStackTop; i++) {
                //Log.d(TAG, "Popping stack to get to page item");
                try {
                    appCMSBinderStack.pop();
                } catch (IllegalStateException e) {
                    //Log.e(TAG, "Error popping back stack: " + e.getMessage());
                }
            }
        }

        showInfoIcon(tag);
        appCMSBinderStack.push(tag);
        appCMSPresenter.sendGaScreen(appCmsBinder.getScreenName());

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.home_placeholder);
        if (null != fragment && fragment instanceof AppCmsSearchFragment) {
            getSupportFragmentManager().popBackStack();
        }
        AppCmsSearchFragment searchFragment = AppCmsSearchFragment.newInstance(appCmsBinder.getSearchQuery().toString());
        getSupportFragmentManager().beginTransaction().replace(R.id.home_placeholder, searchFragment,
                tag).addToBackStack(tag).commit();
        selectNavItem(tag);
    }

    private void openMyProfile() {
        int distanceFromStackTop = -1;
        String tag = getString(R.string.app_cms_my_profile_label,
                getString(R.string.profile_label));

        distanceFromStackTop = appCMSBinderStack.search(tag);

        //Log.d(TAG, "Page distance from top: " + distanceFromStackTop);
        /*if (0 < distanceFromStackTop) {
            for (int i = 0; i < distanceFromStackTop; i++) {
                //Log.d(TAG, "Popping stack to get to page item");
                try {
                    appCMSBinderStack.pop();
                } catch (IllegalStateException e) {
                    //Log.e(TAG, "Error popping back stack: " + e.getMessage());
                }
            }
        }*/
        showInfoIcon(tag);
        appCMSBinderStack.push(tag);
        selectNavItem(tag);
    }

    private void showInfoIcon(String pageId) {
        if (appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.ENTERTAINMENT) {
                showMenuIcon(appCMSPresenter.isPagePrimary(pageId) ? View.VISIBLE : View.INVISIBLE);
        }
    }

   /* @Override
    public int getSubNavigationContainer() {
        return R.id.sub_navigation_placeholder;
    }*/

    public AppCmsTvSearchComponent getAppCMSSearchComponent() {
        return appCMSSearchUrlComponent;
    }

    @Override
    public int getNavigationContainer() {
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

                    if (!TextUtils.isEmpty(endPoint)
                            && !appCMSBinder.getPageName().equalsIgnoreCase
                            (getString(R.string.app_cms_watchlist_navigation_title))
                            && !appCMSBinder.getPageName().equalsIgnoreCase
                            (getString(R.string.app_cms_history_navigation_title))) {

                        String apiUrl = appCMSPresenter.getApiUrl(usePageIdQueryParam,
                                false,
                                false,
                                appCMSMain.getApiBaseUrl(),
                                endPoint,
                                appCMSSite.getGist().getSiteInternalName(),
                                appCMSBinder.getPagePath(),
                                appCMSBinder.getAppCMSPageUI().getCaching() != null &&
                                        !appCMSBinder.getAppCMSPageUI().getCaching().shouldOverrideCaching() &&
                                        appCMSBinder.getAppCMSPageUI().getCaching().isEnabled());

                        //appCMSPresenter.getPageAPILruCache().remove(appCMSBinder.getPagePath());
                        appCMSPresenter.getPageIdContent(apiUrl,
                                appCMSBinder.getPagePath(),
                                null,
                                appCMSBinder.getAppCMSPageUI().getCaching() != null &&
                                        !appCMSBinder.getAppCMSPageUI().getCaching().shouldOverrideCaching() &&
                                        appCMSBinder.getAppCMSPageUI().getCaching().isEnabled(),
                                appCMSPageAPI -> {
                                    if (appCMSPageAPI != null) {
                                        boolean updatedHistory = false;
                                        if (appCMSPresenter.isUserLoggedIn()) {
                                            if (appCMSPageAPI.getModules() != null) {
                                                List<Module> modules = appCMSPageAPI.getModules();
                                                for (int i = 0; i < modules.size(); i++) {
                                                    Module module = modules.get(i);
                                                    AppCMSUIKeyType moduleType = appCMSPresenter.getJsonValueKeyMap().get(module.getModuleType());
                                                    if (moduleType == AppCMSUIKeyType.PAGE_API_HISTORY_MODULE_KEY) {
                                                        if (module.getContentData() != null &&
                                                                !module.getContentData().isEmpty()) {
                                                            int finalI = i;
                                                            appCMSPresenter.getHistoryData(appCMSHistoryResult -> {
                                                                if (appCMSHistoryResult != null) {
                                                                    AppCMSPageAPI historyAPI = appCMSHistoryResult.convertToAppCMSPageAPI(appCMSPageAPI.getId());
                                                                    historyAPI.getModules().get(0).setId(module.getId());
                                                                    historyAPI.getModules().get(0).setTitle(module.getTitle());
                                                                    modules.set(finalI, historyAPI.getModules().get(0));
                                                                    appCMSBinder.updateAppCMSPageAPI(appCMSPageAPI);
                                                                }
                                                            });
                                                            updatedHistory = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        appCMSBinderMap.put(getTag(appCMSBinder), appCMSBinder);
                                        int totalNoOfFragment = getSupportFragmentManager().getBackStackEntryCount();
                                        for (int i = 0; i < totalNoOfFragment; i++) {
                                            FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(i);
                                            String tag = backStackEntry.getName();
                                            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                                            AppCMSBinder appCmsBinder = appCMSBinderMap.get(tag);
                                            if (fragment instanceof AppCmsTVPageFragment) {
                                                ((AppCmsTVPageFragment) fragment).updateBinder(appCmsBinder);
                                            }
                                        }
                                    }
                                });
                    } else if (appCMSBinder.getPageName().equalsIgnoreCase
                            (getString(R.string.app_cms_history_navigation_title))) {
                        AppCMSPageAPI appCMSPageAPI = appCMSBinder.getAppCMSPageAPI();
                        appCMSPresenter.getHistoryData(appCMSHistoryResult -> {
                            if (appCMSHistoryResult != null) {
                                AppCMSPageAPI historyAPI =
                                        appCMSHistoryResult.convertToAppCMSPageAPI(appCMSPageAPI.getId());
                                appCMSBinder.updateAppCMSPageAPI(historyAPI);
                            }
                        });
                    }
                }
            }
        }
    }

    private void updateWatchListData() {
        if (appCMSPresenter != null) {
            for (Map.Entry<String, AppCMSBinder> appCMSBinderEntry : appCMSBinderMap.entrySet()) {
                final AppCMSBinder appCMSBinder = appCMSBinderEntry.getValue();
                if (appCMSBinder != null) {
                    if (appCMSBinder.getPageName().equalsIgnoreCase(getString(R.string.app_cms_watchlist_navigation_title))) {
                        AppCMSPageAPI appCMSPageAPI = appCMSBinder.getAppCMSPageAPI();
                        appCMSPresenter.getWatchlistData(new Action1<AppCMSWatchlistResult>() {
                            @Override
                            public void call(AppCMSWatchlistResult appCMSWatchlistResult) {
                                if (null != appCMSWatchlistResult) {
                                    AppCMSPageAPI pageAPI;
                                    if (appCMSWatchlistResult != null) {
                                        pageAPI = appCMSWatchlistResult.convertToAppCMSPageAPI(appCMSPageAPI.getId());
                                        appCMSBinder.updateAppCMSPageAPI(pageAPI);
                                    }
                                    int totalNoOfFragment = getSupportFragmentManager().getBackStackEntryCount();
                                    for (int i = 0; i < totalNoOfFragment; i++) {
                                        FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(i);
                                        String tag = backStackEntry.getName();
                                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                                        AppCMSBinder appCmsBinder = appCMSBinderMap.get(tag);
                                        if (appCMSBinder.getPageName().equalsIgnoreCase(getString(R.string.app_cms_watchlist_navigation_title))) {
                                            if (fragment instanceof AppCmsTVPageFragment) {
                                                ((AppCmsTVPageFragment) fragment).updateBinder(appCmsBinder);
                                            } else if (fragment instanceof AppCmsMyProfileFragment) {
                                                ((AppCmsMyProfileFragment) fragment).updateBinder(appCmsBinder);
                                            }
                                        }


                                    }
                                }
                            }
                        });

                    }

                }
            }
        }
    }

    public void closeSignUpDialog() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (signUpDialog != null) {
                    signUpDialog.dismiss();
                    signUpDialog = null;
                }
            }
        }, 100);

    }

    public void closeSignInDialog() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loginDialog != null) {
                    loginDialog.dismiss();
                    loginDialog = null;
                }
            }
        }, 100);

    }

    private void hideFooterControl() {
        if (appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.SPORTS) {
            findViewById(R.id.press_up_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.press_down_button).setVisibility(View.INVISIBLE);
        }
    }

    private void openLinkYourAccountScreen(Intent intent) {
        if (null != intent) {
            Bundle bundle = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
            if (null != bundle) {
                AppCMSBinder appCMSBinder = (AppCMSBinder) bundle.get(getString(R.string.app_cms_binder_key));
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                appCmsLinkYourAccountFragment = AppCmsLinkYourAccountFragment.newInstance(
                        appCMSBinder);
                appCmsLinkYourAccountFragment.show(ft, DIALOG_FRAGMENT_TAG);
                Utils.pageLoading(false, this);
            }
        }
    }

    public void shouldShowLeftNavigation(boolean shouldShowLeftnav) {
        this.shouldShowLeftNav = shouldShowLeftnav;
    }

    public void shouldShowSubLeftNavigation(boolean shouldShowSubLeftnav) {
        this.shouldShowSubLeftNav = shouldShowSubLeftnav;
    }

}