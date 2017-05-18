
package air.com.snagfilms.models.data.appcms.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Layout_ {

    @SerializedName("tabletLandscape")
    @Expose
    private TabletLandscape tabletLandscape;
    @SerializedName("tabletPortrait")
    @Expose
    private TabletPortrait tabletPortrait;
    @SerializedName("mobile")
    @Expose
    private Mobile mobile;

    public TabletLandscape getTabletLandscape() {
        return tabletLandscape;
    }

    public void setTabletLandscape(TabletLandscape tabletLandscape) {
        this.tabletLandscape = tabletLandscape;
    }

    public TabletPortrait getTabletPortrait() {
        return tabletPortrait;
    }

    public void setTabletPortrait(TabletPortrait tabletPortrait) {
        this.tabletPortrait = tabletPortrait;
    }

    public Mobile getMobile() {
        return mobile;
    }

    public void setMobile(Mobile mobile) {
        this.mobile = mobile;
    }

}
