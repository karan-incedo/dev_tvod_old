package com.viewlift.analytics;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.appsflyer.AppsFlyerLib;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.HashMap;
import java.util.Map;

import okhttp3.internal.Util;

/**
 * Created by amit on 27/07/17.
 */

public class AppsFlyerUtils {

    public static final String REGISTRATION_APP_EVENT_NAME = "Registration";
    public static final String APP_OPEN_EVENT_NAME = "App open";
    public static final String LOGIN_EVENT_NAME = "Login";
    public static final String LOGOUT_EVENT_NAME = "Logout";
    public static final String SUBSCRIPTION_EVENT_NAME = "Subscription";
    public static final String CANCEL_SUBSCRIPTION_EVENT_NAME = "Cancel Subscription";
    public static final String FILM_VIEWING_EVENT_NAME = "Film Viewing";
    public static final String UNINSTALL_EVENT_NAME = "Uninstall";

    public static final String USER_ID_EVENT_VALUE = "UUID";
    public static final String DEVICE_ID_EVENT_VALUE = "Device ID";
    public static final String USER_ENTITLEMENT_STATE_EVENT_VALUE = "Entitled";
    public static final String USER_REGISTER_STATE_EVENT_VALUE = "Registered";
    public static final String PRODUCT_ID_EVENT_VALUE = "Product ID";
    public static final String PRODUCT_NAME_EVENT_VALUE = "Product Name";
    public static final String PLAN_EVENT_VALUE = "Plan";
    public static final String PRICE_EVENT_VALUE = "Price";
    public static final String CURRENCY_EVENT_VALUE = "Currency";

    public static final String FILM_CATEGORY_EVENT_VALUE = "Category";
    public static final String FILM_ID_EVENT_VALUE = "Film ID";

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

    public static void registrationEvent(Context context, String userID, String key) {
        Map<String, Object> eventValue = new HashMap<>();
        eventValue.put(USER_ENTITLEMENT_STATE_EVENT_VALUE, "true");
        eventValue.put(USER_ID_EVENT_VALUE, userID);
        eventValue.put(AppsFlyerUtils.DEVICE_ID_EVENT_VALUE, key);
        eventValue.put(USER_REGISTER_STATE_EVENT_VALUE, true);

        AppsFlyerLib.getInstance().trackEvent(context, REGISTRATION_APP_EVENT_NAME, eventValue);
    }

    public static void appOpenEvent(Context context) {
        AppsFlyerLib.getInstance().trackEvent(context, APP_OPEN_EVENT_NAME, null);
    }

    public static void loginEvent(Context context, String userID) {
        Map<String, Object> eventValue = new HashMap<>();
        eventValue.put(AppsFlyerUtils.USER_ENTITLEMENT_STATE_EVENT_VALUE, true);
        eventValue.put(AppsFlyerUtils.USER_ID_EVENT_VALUE, userID);
        eventValue.put(AppsFlyerUtils.USER_REGISTER_STATE_EVENT_VALUE, true);

        AppsFlyerLib.getInstance().trackEvent(context, LOGIN_EVENT_NAME, eventValue);
    }

    public static void logoutEvent(Context context, String userID) {
        Map<String, Object> eventValue = new HashMap<>();
        eventValue.put(AppsFlyerUtils.USER_ENTITLEMENT_STATE_EVENT_VALUE, true);
        eventValue.put(AppsFlyerUtils.USER_ID_EVENT_VALUE, userID);
        eventValue.put(AppsFlyerUtils.USER_REGISTER_STATE_EVENT_VALUE, true);

        AppsFlyerLib.getInstance().trackEvent(context, LOGOUT_EVENT_NAME, eventValue);
    }

    public static void subscriptionEvent(Context context,
                                         boolean isSubscribing,
                                         String deviceID,
                                         String price,
                                         String plan,
                                         String currency) {
        Map<String, Object> eventValue = new HashMap<>();

        eventValue.put(AppsFlyerUtils.PRODUCT_NAME_EVENT_VALUE, plan);
        eventValue.put(AppsFlyerUtils.PRICE_EVENT_VALUE, price);
        eventValue.put(AppsFlyerUtils.USER_ENTITLEMENT_STATE_EVENT_VALUE, true);
        eventValue.put(AppsFlyerUtils.DEVICE_ID_EVENT_VALUE, deviceID);
        eventValue.put(AppsFlyerUtils.CURRENCY_EVENT_VALUE, currency);

        if (isSubscribing) {
            AppsFlyerLib.getInstance().trackEvent(context, SUBSCRIPTION_EVENT_NAME, eventValue);
        } else {
            AppsFlyerLib.getInstance().trackEvent(context, CANCEL_SUBSCRIPTION_EVENT_NAME, eventValue);
        }
    }

    public static void filmViewingEvent(Context context,
                                        String category,
                                        String filmId,
                                        AppCMSPresenter appCMSPresenter) {

        Map<String, Object> eventValue = new HashMap<>();

        eventValue.put(AppsFlyerUtils.FILM_CATEGORY_EVENT_VALUE, category);
        eventValue.put(USER_ID_EVENT_VALUE, appCMSPresenter.getLoggedInUser(context));
        eventValue.put(FILM_ID_EVENT_VALUE, filmId);
        eventValue.put("true", true);
//        eventValue.put(AppsFlyerUtils.USER_REGISTER_STATE_EVENT_VALUE, true);
        eventValue.put(AppsFlyerUtils.USER_ENTITLEMENT_STATE_EVENT_VALUE,
                !TextUtils.isEmpty(appCMSPresenter.getActiveSubscriptionId(context)));

        AppsFlyerLib.getInstance().trackEvent(context, FILM_VIEWING_EVENT_NAME, eventValue);
    }

    public static void uninstallApp(Application application, String GCM_PROJECT_KEY) {
        AppsFlyerLib.getInstance().setGCMProjectNumber(application, GCM_PROJECT_KEY);
    }
}
