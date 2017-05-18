
package air.com.snagfilms.models.data.appcms.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MetaPage {

    @SerializedName("Page-Name")
    @Expose
    private String pageName;
    @SerializedName("Page-Type")
    @Expose
    private String pageType;
    @SerializedName("Page-ID")
    @Expose
    private String pageID;
    @SerializedName("Page-UI")
    @Expose
    private String pageUI;
    @SerializedName("Page-API")
    @Expose
    private String pageAPI;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getPageID() {
        return pageID;
    }

    public void setPageID(String pageID) {
        this.pageID = pageID;
    }

    public String getPageUI() {
        return pageUI;
    }

    public void setPageUI(String pageUI) {
        this.pageUI = pageUI;
    }

    public String getPageAPI() {
        return pageAPI;
    }

    public void setPageAPI(String pageAPI) {
        this.pageAPI = pageAPI;
    }

}
