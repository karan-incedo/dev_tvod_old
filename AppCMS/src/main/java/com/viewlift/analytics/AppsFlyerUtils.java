package com.viewlift.analytics;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amit on 27/07/17.
 */

public class AppsFlyerUtils {

    public static final String REGISTER_APP = "Register App";
    public static final String APP_OPEN = "App open";
    public static final String LOGIN = "Login";
    public static final String LOGOUT = "Logout";
    public static final String SUBSCRIPTION = "Subscription";
    public static final String CANCLE_SUBSCRIPTION = "Cancel Subscription";
    public static final String FILM_VIEWING = "Film Viewing";
    public static final String UNINSTALL = "Uninstall App";

    public static final String USER_ENTITLEMENT_STATE = "Entitled state";
    public static final String USER_REGISTER_STATE = "Registered state";
    public static final String PRODUCT_ID = "Product ID";
    public static final String PLAN = "Plan";
    public static final String PRICE = "Price";
    public static final String CURRENCY = "Currency";

    /**
     * Method for adding device info called at the time of Application start to track Installation
     *
     * @param application
     */
    public static void trackInstallationEvent(Application application) {
        AppsFlyerLib.getInstance().setAndroidIdData(getAndroidId(application));
    }

    @SuppressLint("HardwareIds")
    private static String getAndroidId(Context context) {
        String androidId;
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public static void registerAppEvent(Application application, String key) {
        AppsFlyerLib.getInstance().startTracking(application, key);
    }

    public static void appsFlyerAppOpenEvent(Context context) {
        Map<String, Object> eventValue = new HashMap<>();
        eventValue.put(context.getString(R.string.app_cms_appsflyer_app_open_key),
                context.getString(R.string.app_cms_appsflyer_event_app_open_value));

        AppsFlyerLib.getInstance().trackEvent(context, APP_OPEN, eventValue);
    }

    public static void appsFlyerLoginEvent(Context context, boolean isLogin, String userID) {
        Map<String, Object> eventValue = new HashMap<>();
        eventValue.put(AFInAppEventParameterName.CUSTOMER_USER_ID, userID);

        if (isLogin) {
            eventValue.put(context.getString(R.string.app_cms_appsflyer_login_status_key),
                    context.getString(R.string.app_cms_appsflyer_login_success));
        } else {
            eventValue.put(context.getString(R.string.app_cms_appsflyer_login_status_key),
                    context.getString(R.string.app_cms_appsflyer_signup_success));
        }

        eventValue.put(AppsFlyerUtils.USER_REGISTER_STATE, true);
        AppsFlyerLib.getInstance().setCustomerUserId(userID);
        AppsFlyerLib.getInstance().trackEvent(context, LOGIN, eventValue);
    }

    public static void appsFlyerLogoutEvent(Context context, String userID) {
        Map<String, Object> eventValue = new HashMap<>();
        eventValue.put(AFInAppEventParameterName.CUSTOMER_USER_ID, userID);
        eventValue.put(context.getString(R.string.app_cms_appsflyer_login_status), true);
        eventValue.put(AppsFlyerUtils.USER_REGISTER_STATE, true);

        System.out.print("User ID is = " + userID);
        AppsFlyerLib.getInstance().setCustomerUserId(userID);
        AppsFlyerLib.getInstance().trackEvent(context, LOGOUT, eventValue);
    }

    public static void appsFlyerSubscriptionEvent(Context context,
                                                  AppCMSPresenter appCMSPresenter,
                                                  boolean isSubscribing,
                                                  String userID,
                                                  String productId,
                                                  String price,
                                                  String plan,
                                                  String currency) {
        Map<String, Object> eventValue = new HashMap<>();

        if (isSubscribing) {
            eventValue.put(AFInAppEventParameterName.CUSTOMER_USER_ID, userID);
            eventValue.put(AppsFlyerUtils.USER_ENTITLEMENT_STATE, true);
            eventValue.put(AppsFlyerUtils.USER_REGISTER_STATE, true);

            //

        } else {
            eventValue.put(AppsFlyerUtils.USER_ENTITLEMENT_STATE, false);
            eventValue.put(AppsFlyerUtils.USER_REGISTER_STATE, false);
        }

        eventValue.put(AppsFlyerUtils.PRODUCT_ID, productId);
        eventValue.put(AppsFlyerUtils.PLAN, plan);
        eventValue.put(AppsFlyerUtils.PRICE, price);
        eventValue.put(AppsFlyerUtils.CURRENCY, currency);

        AppsFlyerLib.getInstance().trackEvent(context, SUBSCRIPTION, eventValue);
    }

    public static void appsFlyerFilmViewingEvent(Context context, String filmId,
                                                 AppCMSPresenter appCMSPresenter) {

        Map<String, Object> eventValue = new HashMap<>();
        eventValue.put(context.getString(R.string.app_cms_appsflyer_film_id), filmId);

        if (appCMSPresenter.isUserLoggedIn(context)) {
            eventValue.put(AFInAppEventParameterName.CUSTOMER_USER_ID,
                    appCMSPresenter.getLoggedInUser(context));
            eventValue.put(AppsFlyerUtils.USER_ENTITLEMENT_STATE,
                    !TextUtils.isEmpty(appCMSPresenter.getActiveSubscriptionId(context)));
            eventValue.put(AppsFlyerUtils.USER_REGISTER_STATE, true);
        } else {
            eventValue.put(AppsFlyerUtils.USER_ENTITLEMENT_STATE, false);
            eventValue.put(AppsFlyerUtils.USER_REGISTER_STATE, false);
        }

        AppsFlyerLib.getInstance().trackEvent(context, FILM_VIEWING, eventValue);
    }

    public static void uninstallAppEvent(Application application, String GCM_PROJECT_KEY) {
        AppsFlyerLib.getInstance().setGCMProjectNumber(application, GCM_PROJECT_KEY);
    }
}
