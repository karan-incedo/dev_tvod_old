package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Footer implements Serializable {

    @SerializedName("textColor")
    @Expose
    String textColor;

    @SerializedName("backgroundColor")
    @Expose
    String backgroundColor;

    @SerializedName("link")
    @Expose
    Link link;

    @SerializedName("link--active")
    @Expose
    LinkActive linkActive;

    @SerializedName("link--hover")
    @Expose
    LinkHover linkHover;

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public LinkActive getLinkActive() {
        return linkActive;
    }

    public void setLinkActive(LinkActive linkActive) {
        this.linkActive = linkActive;
    }

    public LinkHover getLinkHover() {
        return linkHover;
    }

    public void setLinkHover(LinkHover linkHover) {
        this.linkHover = linkHover;
    }
}
