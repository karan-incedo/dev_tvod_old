
package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.ui.tv.FireTV;

public class Layout {

    @SerializedName("tabletPortrait")
    @Expose
    private TabletPortrait tabletPortrait;
    @SerializedName("desktop")
    @Expose
    private Desktop desktop;
    @SerializedName("mobile")
    @Expose
    private Mobile mobile;
    @SerializedName("tabletLandscape")
    @Expose
    private TabletLandscape tabletLandscape;

    public TabletPortrait getTabletPortrait() {
        return tabletPortrait;
    }

    public void setTabletPortrait(TabletPortrait tabletPortrait) {
        this.tabletPortrait = tabletPortrait;
    }

    public Desktop getDesktop() {
        return desktop;
    }

    public void setDesktop(Desktop desktop) {
        this.desktop = desktop;
    }

    public Mobile getMobile() {
        return mobile;
    }

    public void setMobile(Mobile mobile) {
        this.mobile = mobile;
    }

    public TabletLandscape getTabletLandscape() {
        return tabletLandscape;
    }

    public void setTabletLandscape(TabletLandscape tabletLandscape) {
        this.tabletLandscape = tabletLandscape;
    }

    @SerializedName("ftv")
    @Expose
    private FireTV tv;

    public FireTV getTv() {
        return tv;
    }

    public void setTv(FireTV ftv) {
        this.tv = tv;
    }
}
