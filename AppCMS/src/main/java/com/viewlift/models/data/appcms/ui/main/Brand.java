package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class Brand {

    @SerializedName("footer")
    @Expose
    Footer footer;

    @SerializedName("navigation")
    @Expose
    Navigation navigation;

    @SerializedName("link")
    @Expose
    Link__ link;

    @SerializedName("cta")
    @Expose
    Cta cta;

    @SerializedName("link--hover")
    @Expose
    LinkHover__ linkHover;

    @SerializedName("general")
    @Expose
    General general;

    public Footer getFooter() {
        return footer;
    }

    public void setFooter(Footer footer) {
        this.footer = footer;
    }

    public Navigation getNavigation() {
        return navigation;
    }

    public void setNavigation(Navigation navigation) {
        this.navigation = navigation;
    }

    public Link__ getLink() {
        return link;
    }

    public void setLink(Link__ link) {
        this.link = link;
    }

    public Cta getCta() {
        return cta;
    }

    public void setCta(Cta cta) {
        this.cta = cta;
    }

    public LinkHover__ getLinkHover() {
        return linkHover;
    }

    public void setLinkHover(LinkHover__ linkHover) {
        this.linkHover = linkHover;
    }

    public General getGeneral() {
        return general;
    }

    public void setGeneral(General general) {
        this.general = general;
    }
}
