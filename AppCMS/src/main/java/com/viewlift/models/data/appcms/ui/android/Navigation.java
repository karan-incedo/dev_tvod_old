package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class Navigation {

    @SerializedName("primary")
    @Expose
    List<NavigationPrimary> navigationPrimary = null;

    @SerializedName("user")
    @Expose
    List<NavigationUser> navigationUser = null;

    @SerializedName("footer")
    @Expose
    List<NavigationFooter> navigationFooter = null;

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
}
