package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.ui.tv.FireTV;
import com.vimeo.stag.UseStag;

@UseStag
public class Layout {

    @SerializedName("tabletPortrait")
    @Expose
    TabletPortrait tabletPortrait;

    @SerializedName("desktop")
    @Expose
    Desktop desktop;

    @SerializedName("mobile")
    @Expose
    Mobile mobile;

    @SerializedName("tabletLandscape")
    @Expose
    TabletLandscape tabletLandscape;

    @SerializedName("ftv")
    @Expose
    FireTV tv;

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

    public FireTV getTv() {
        return tv;
    }

    public void setTv(FireTV ftv) {
        this.tv = tv;
    }
}
