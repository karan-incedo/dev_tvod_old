package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.List;

@UseStag
public class Navigation implements Serializable {

    @SerializedName("primary")
    @Expose
    List<NavigationPrimary> navigationPrimary = null;

    @SerializedName("user")
    @Expose
    List<NavigationUser> navigationUser = null;

    @SerializedName("footer")
    @Expose
    List<NavigationFooter> navigationFooter = null;

    public List<NavigationPrimary> getNavigationLeft() {
        return navigationLeft;
    }

    public void setNavigationLeft(List<NavigationPrimary> navigationLeft) {
        this.navigationLeft = navigationLeft;
    }

    public List<NavigationPrimary> getNavigationRight() {
        return navigationRight;
    }

    public void setNavigationRight(List<NavigationPrimary> navigationRight) {
        this.navigationRight = navigationRight;
    }

    @SerializedName("left")
    @Expose
    List<NavigationPrimary> navigationLeft = null;

    @SerializedName("right")
    @Expose
    List<NavigationPrimary> navigationRight = null;


    @SerializedName("tabBar")
    @Expose
    List<NavigationTabBar> navigationTabbar = null;

    public List<NavigationPrimary> getNavigationPrimary() {
        return navigationPrimary;
    }

    public void setNavigationPrimary(List<NavigationPrimary> navigationPrimary) {
        this.navigationPrimary = navigationPrimary;
    }

    public List<NavigationUser> getNavigationUser() {
        return navigationUser;
    }

    public void setNavigationUser(List<NavigationUser> navigationUser) {
        this.navigationUser = navigationUser;
    }

    public List<NavigationFooter> getNavigationFooter() {
        return navigationFooter;
    }

    public void setNavigationFooter(List<NavigationFooter> navigationFooter) {
        this.navigationFooter = navigationFooter;
    }


    public List<NavigationTabBar> getNavigationTabbar() {
        return navigationTabbar;
    }

    public void setNavigationTabbar(List<NavigationTabBar> navigationTabbar) {
        this.navigationTabbar = navigationTabbar;
    }
}
