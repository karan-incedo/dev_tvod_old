package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class Navigation {

    @SerializedName("link")
    @Expose
    Link_ link;

    @SerializedName("link--active")
    @Expose
    LinkActive_ linkActive;

    @SerializedName("dropdown--active")
    @Expose
    DropdownActive dropdownActive;

    @SerializedName("dropdown--hover")
    @Expose
    DropdownHover dropdownHover;

    @SerializedName("link--hover")
    @Expose
    LinkHover_ linkHover;

    @SerializedName("dropdown")
    @Expose
    Dropdown dropdown;

    public Link_ getLink() {
        return link;
    }

    public void setLink(Link_ link) {
        this.link = link;
    }

    public LinkActive_ getLinkActive() {
        return linkActive;
    }

    public void setLinkActive(LinkActive_ linkActive) {
        this.linkActive = linkActive;
    }

    public DropdownActive getDropdownActive() {
        return dropdownActive;
    }

    public void setDropdownActive(DropdownActive dropdownActive) {
        this.dropdownActive = dropdownActive;
    }

    public DropdownHover getDropdownHover() {
        return dropdownHover;
    }

    public void setDropdownHover(DropdownHover dropdownHover) {
        this.dropdownHover = dropdownHover;
    }

    public LinkHover_ getLinkHover() {
        return linkHover;
    }

    public void setLinkHover(LinkHover_ linkHover) {
        this.linkHover = linkHover;
    }

    public Dropdown getDropdown() {
        return dropdown;
    }

    public void setDropdown(Dropdown dropdown) {
        this.dropdown = dropdown;
    }
}
