
package com.viewlift.models.data.appcms.ui.android;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Navigation {

    @SerializedName("primary")
    @Expose
    private List<NavigationPrimary> navigationPrimary = null;
    @SerializedName("user")
    @Expose
    private List<NavigationUser> navigationUser = null;
    @SerializedName("footer")
    @Expose
    private List<NavigationFooter> navigationFooter = null;

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
