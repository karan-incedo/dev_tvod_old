
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Navigation {

    @SerializedName("dropdown")
    @Expose
    private Dropdown dropdown;
    @SerializedName("dropdown--active")
    @Expose
    private DropdownActive dropdownActive;
    @SerializedName("dropdown--hover")
    @Expose
    private DropdownHover dropdownHover;
    @SerializedName("link")
    @Expose
    private Link__ link;
    @SerializedName("link--active")
    @Expose
    private LinkActive_ linkActive;
    @SerializedName("link--hover")
    @Expose
    private LinkHover__ linkHover;

    public Dropdown getDropdown() {
        return dropdown;
    }

    public void setDropdown(Dropdown dropdown) {
        this.dropdown = dropdown;
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

    public Link__ getLink() {
        return link;
    }

    public void setLink(Link__ link) {
        this.link = link;
    }

    public LinkActive_ getLinkActive() {
        return linkActive;
    }

    public void setLinkActive(LinkActive_ linkActive) {
        this.linkActive = linkActive;
    }

    public LinkHover__ getLinkHover() {
        return linkHover;
    }

    public void setLinkHover(LinkHover__ linkHover) {
        this.linkHover = linkHover;
    }

}
