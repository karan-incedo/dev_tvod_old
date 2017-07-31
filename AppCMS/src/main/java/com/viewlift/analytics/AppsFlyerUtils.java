package com.viewlift.analytics;

import android.app.Application;
import android.content.Context;

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

    /**
     * Event to capture the data from playing Film
     *
     * @param context
     * @param filmId
     * @param appCMSPresenter
     */
    public static void appsFlyerPlayEvent (Context context, String filmId, AppCMSPresenter appCMSPresenter) {

        Map<String, Object> eventValue  = new HashMap<String, Object>();
        eventValue.put(context.getString(R.string.app_cms_appsflyer_film_id),filmId);

        if (appCMSPresenter.isUserLoggedIn(context)){
            eventValue.put(AFInAppEventParameterName.CUSTOMER_USER_ID,appCMSPresenter.getLoggedInUser(context));
            eventValue.put(context.getString(R.string.app_cms_appsflyer_entitled_state),appCMSPresenter.getActiveSubscriptionId(context));
            eventValue.put(context.getString(R.string.app_cms_appsflyer_registered_state),true);
        }else {
            eventValue.put(context.getString(R.string.app_cms_appsflyer_entitled_state),false);
            eventValue.put(context.getString(R.string.app_cms_appsflyer_registered_state),false);
        }

        AppsFlyerLib.getInstance().trackEvent(context,context.getString(R.string.app_cms_appsflyer_video_view),eventValue);
    }


    /**
     * Method for adding device info called at the time of Application start to track Installation
     *
     * @param application
     */
    public static void addDeviceInfo(Application application) {
        AppsFlyerLib.getInstance().setAndroidIdData(getAndroidId(application));
    }

    public static String getAndroidId(Context context) {
        String androidId="";
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public static void appsFlyerLogoutEvent (Context context, String userID) {
        //AppsFlyer Logout Success event
        Map<String, Object> eventValue  = new HashMap<String, Object>();
        eventValue.put(AFInAppEventParameterName.CUSTOMER_USER_ID,userID);
        eventValue.put(context.getString(R.string.app_cms_appsflyer_login_status),
                context.getString(R.string.app_cms_appsflyer_login_success));
        eventValue.put(context.getString(R.string.app_cms_registered_state),true);

        System.out.print("User ID is = " + userID);
        AppsFlyerLib.getInstance().setCustomerUserId(userID);
        //trackAppsFlyerEvents(currentActivity,
                //currentActivity.getString(R.string.app_cms_appsflyer_event_type_logout),eventValue);
        AppsFlyerLib.getInstance().trackEvent(context,context.getString(R.string.app_cms_appsflyer_video_view),eventValue);
    }

    public static void appsFlyerLoginEvent (Context context, boolean isSignin, String userID) {
        //AppsFlyer Login Success event
        Map<String, Object> eventValue  = new HashMap<String, Object>();
        eventValue.put(AFInAppEventParameterName.CUSTOMER_USER_ID,userID);
        if (isSignin) {
            eventValue.put(context.getString(R.string.app_cms_appsflyer_login_status),
                    context.getString(R.string.app_cms_appsflyer_login_success));
        } else {
            eventValue.put(context.getString(R.string.app_cms_appsflyer_login_status),
                    context.getString(R.string.app_cms_appsflyer_signup_success));
        }
        eventValue.put(context.getString(R.string.app_cms_registered_state),true);
        AppsFlyerLib.getInstance().setCustomerUserId(userID);
        AppsFlyerLib.getInstance().trackEvent(context,context.getString(R.string.app_cms_login),eventValue);
    }

    public static void uninstallApp (Application application,String GCM_PROJECT_KEY) {
        AppsFlyerLib.getInstance().setGCMProjectNumber(application,GCM_PROJECT_KEY);
    }

    public static void appsFlyerSubscriptionEvent () {

    }

    /**
     * Method for updating the payment fail
     * @param context
     * @param userId
     */

    public static void addEventPaymentFail(Context context,String userId){
        Map<String, Object> eventValue  = new HashMap<String, Object>();
        eventValue.put(AFInAppEventParameterName.CUSTOMER_USER_ID,userId);
        eventValue.put(context.getString(R.string.app_cms_appsflyer_order_id),context.getString(R.string.app_cms_user_cancel_payment));
        AppsFlyerLib.getInstance().trackEvent(context,context.getString(R.string.app_cms_appsflyer_cancel_subscription),eventValue);
    }

//    public static final String CUSTOMER_USER_NAME = "af_customer_user_name";
//    public static final String PAYMENT_CANCEL_BY_USER = "af_order_id";
//    public static final String ORDER_ID = "af_order_id";
//    public static final String PRODUCT_ID = "af_product_id";
//    public static final String PRICE = "af_price";
//    public static final String PLAN_ID = "af_plan_type";

    /**
     * Method for updating the payment information at appsflyer
     * @param context
     * @param orderId
     * @param productId
     * @param price
     * @param plan
     */
    public static void addEventPaymentSuccess(Context context,String orderId,String productId,
                                              String price,String plan,String currency, AppCMSPresenter appCMSPresenter) {
        Map<String, Object> eventValue  = new HashMap<String, Object>();
        if (appCMSPresenter.isUserLoggedIn(context))
        {
            eventValue.put(AFInAppEventParameterName.CUSTOMER_USER_ID,appCMSPresenter.getLoggedInUser(context));
            //eventValue.put(context.getString(R.string.app_cms_appsflyer_entitled_state),LoginUtil.getUser(context).getSubscribed());
            eventValue.put(context.getString(R.string.app_cms_appsflyer_registered_state),true);

        }
        else {
            eventValue.put(context.getString(R.string.app_cms_appsflyer_entitled_state),false);
            eventValue.put(context.getString(R.string.app_cms_appsflyer_registered_state),false);
        }

        eventValue.put(context.getString(R.string.app_cms_appsflyer_order_id),orderId);
        eventValue.put(context.getString(R.string.app_cms_appsflyer_product_id),productId);
        //eventValue.put(AppsFlyerUtils.PRICE,price);
        eventValue.put(AFInAppEventParameterName.REVENUE,price);
        eventValue.put(AFInAppEventParameterName.CURRENCY,currency);
        eventValue.put(context.getString(R.string.app_cms_plan_type),plan);

        AppsFlyerLib.getInstance().trackEvent(context,context.getString(R.string.app_cms_appsflyer_subscription),eventValue);
    }


}
