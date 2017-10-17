package com.viewlift.analytics;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.appsflyer.AppsFlyerLib;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amit on 27/07/17.
 */

public class AppsFlyerUtils {

    private static final String REGISTRATION_APP_EVENT_NAME = "Registration";
    private static final String APP_OPEN_EVENT_NAME = "App open";
    private static final String LOGIN_EVENT_NAME = "Login";
    private static final String LOGOUT_EVENT_NAME = "Logout";
    private static final String SUBSCRIPTION_EVENT_NAME = "Subscription";
    //    public static final String CANCEL_SUBSCRIPTION_EVENT_NAME = "Cancel Subscription";
    private static final String FILM_VIEWING_EVENT_NAME = "Film Viewing";

    private static final String USER_ID_EVENT_VALUE = "UUID";
    private static final String DEVICE_ID_EVENT_VALUE = "Device ID";
    private static final String USER_ENTITLEMENT_STATE_EVENT_VALUE = "Entitled";
    private static final String USER_REGISTER_STATE_EVENT_VALUE = "Registered";
    private static final String PRODUCT_NAME_EVENT_VALUE = "Product Name";
    private static final String PRICE_EVENT_VALUE = "Price";
    private static final String CURRENCY_EVENT_VALUE = "Currency";

    private static final String FILM_CATEGORY_EVENT_VALUE = "Category";
    private static final String FILM_ID_EVENT_VALUE = "Film ID";

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

        AppsFlyerLib.getInstance().setCustomerUserId(userID);
        AppsFlyerLib.getInstance().trackEvent(context, REGISTRATION_APP_EVENT_NAME, eventValue);
    }

    public static void appOpenEvent(Context context) {
        Map<String, Object> eventValue = new HashMap<>();

        AppsFlyerLib.getInstance().trackEvent(context, APP_OPEN_EVENT_NAME, eventValue);
    }

    public static void loginEvent(Context context, String userID) {
        Map<String, Object> eventValue = new HashMap<>();
        eventValue.put(AppsFlyerUtils.USER_ENTITLEMENT_STATE_EVENT_VALUE, true);
        eventValue.put(AppsFlyerUtils.USER_ID_EVENT_VALUE, userID);
        eventValue.put(AppsFlyerUtils.USER_REGISTER_STATE_EVENT_VALUE, true);

        AppsFlyerLib.getInstance().setCustomerUserId(userID);
        AppsFlyerLib.getInstance().trackEvent(context, LOGIN_EVENT_NAME, eventValue);
    }

    public static void logoutEvent(Context context, String userID) {
        Map<String, Object> eventValue = new HashMap<>();
        eventValue.put(AppsFlyerUtils.USER_ENTITLEMENT_STATE_EVENT_VALUE, true);
        eventValue.put(AppsFlyerUtils.USER_ID_EVENT_VALUE, userID);
        eventValue.put(AppsFlyerUtils.USER_REGISTER_STATE_EVENT_VALUE, true);

        AppsFlyerLib.getInstance().setCustomerUserId(userID);
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

            /*
             * As per QA's request - Cancel Subscription isn't needed.
             * For now, It will be done on the Server side.
             */

//        } else {
//            AppsFlyerLib.getInstance().trackEvent(context, CANCEL_SUBSCRIPTION_EVENT_NAME, eventValue);
//            //Log.d("AppsFlyer__", "Cancel Sub Event");
        }
    }

    public static void filmViewingEvent(Context context,
                                        String category,
                                        String filmId,
                                        AppCMSPresenter appCMSPresenter) {

        Map<String, Object> eventValue = new HashMap<>();

        if (!TextUtils.isEmpty(category)) {
            eventValue.put(AppsFlyerUtils.FILM_CATEGORY_EVENT_VALUE, category);
        }
        eventValue.put(USER_ID_EVENT_VALUE, appCMSPresenter.getLoggedInUser());
        eventValue.put(FILM_ID_EVENT_VALUE, filmId);
        eventValue.put("true", true);
        eventValue.put(AppsFlyerUtils.USER_ENTITLEMENT_STATE_EVENT_VALUE,
                !TextUtils.isEmpty(appCMSPresenter.getActiveSubscriptionId()));

        AppsFlyerLib.getInstance().setCustomerUserId(appCMSPresenter.getLoggedInUser());
        AppsFlyerLib.getInstance().trackEvent(context, FILM_VIEWING_EVENT_NAME, eventValue);
    }
}
