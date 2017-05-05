
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Brand {

    @SerializedName("background")
    @Expose
    private Background background;
    @SerializedName("cta")
    @Expose
    private Cta cta;
    @SerializedName("font")
    @Expose
    private Font font;
    @SerializedName("footer")
    @Expose
    private Footer footer;
    @SerializedName("link")
    @Expose
    private Link_ link;
    @SerializedName("link--hover")
    @Expose
    private LinkHover_ linkHover;
    @SerializedName("navigation")
    @Expose
    private Navigation navigation;

    public Background getBackground() {
        return background;
    }

    public void setBackground(Background background) {
        this.background = background;
    }

    public Cta getCta() {
        return cta;
    }

    public void setCta(Cta cta) {
        this.cta = cta;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Footer getFooter() {
        return footer;
    }

    public void setFooter(Footer footer) {
        this.footer = footer;
    }

    public Link_ getLink() {
        return link;
    }

    public void setLink(Link_ link) {
        this.link = link;
    }

    public LinkHover_ getLinkHover() {
        return linkHover;
    }

    public void setLinkHover(LinkHover_ linkHover) {
        this.linkHover = linkHover;
    }

    public Navigation getNavigation() {
        return navigation;
    }

    public void setNavigation(Navigation navigation) {
        this.navigation = navigation;
    }

}
