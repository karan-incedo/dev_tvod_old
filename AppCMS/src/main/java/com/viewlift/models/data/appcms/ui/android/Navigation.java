package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.ui.page.Settings;
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

    @SerializedName("left")
    @Expose
    List<NavigationPrimary> left = null;

    @SerializedName("tabBar")
    @Expose
    List<NavigationPrimary> tabBar = null;


	@SerializedName("right")
    @Expose
    List<NavigationPrimary> right = null;

    @SerializedName("settings")
    @Expose
    Settings settings;

    public List<NavigationPrimary> getLeft() {
        return left;
    }

    public void setLeft(List<NavigationPrimary> navigationLeft) {
        this.left = navigationLeft;
    }

    public List<NavigationPrimary> getRight() {
        return right;
    }

    public void setRight(List<NavigationPrimary> navigationRight) {
        this.right = navigationRight;
    }

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


    public List<NavigationPrimary> getTabBar() {
        return tabBar;
    }

    public void setTabBar(List<NavigationPrimary> tabBar) {
        this.tabBar = tabBar;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
