
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Footer {

    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("link")
    @Expose
    private Link link;
    @SerializedName("link--active")
    @Expose
    private LinkActive linkActive;
    @SerializedName("link--hover")
    @Expose
    private LinkHover linkHover;
    @SerializedName("textColor")
    @Expose
    private String textColor;

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

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

}
