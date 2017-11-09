package com.viewlift.models.data.appcms.sites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.ui.android.Analytics;
import com.viewlift.models.data.appcms.ui.main.CustomerService;
import com.viewlift.models.data.appcms.ui.main.SocialMedia;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 11/9/17.
 */

@UseStag
public class Settings {
    @SerializedName("appStore")
    @Expose
    AppStore appStore;

    @SerializedName("customerService")
    @Expose
    CustomerService customerService;

    @SerializedName("analytics")
    @Expose
    Analytics analytics;

    @SerializedName("social")
    @Expose
    SocialMedia social;

    @SerializedName("notifications")
    @Expose
    Notifications notifications;

    public AppStore getAppStore() {
        return appStore;
    }

    public void setAppStore(AppStore appStore) {
        this.appStore = appStore;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public Analytics getAnalytics() {
        return analytics;
    }

    public void setAnalytics(Analytics analytics) {
        this.analytics = analytics;
    }

    public SocialMedia getSocial() {
        return social;
    }

    public void setSocial(SocialMedia social) {
        this.social = social;
    }

    public Notifications getNotifications() {
        return notifications;
    }

    public void setNotifications(Notifications notifications) {
        this.notifications = notifications;
    }
}
