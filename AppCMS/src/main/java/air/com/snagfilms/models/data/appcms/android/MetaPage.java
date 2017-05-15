
package air.com.snagfilms.models.data.appcms.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MetaPage {

    @SerializedName("Page-Name")
    @Expose
    private String pageName;
    @SerializedName("Page-API")
    @Expose
    private String pageAPI;
    @SerializedName("Page-UI")
    @Expose
    private String pageUI;
    @SerializedName("Page-type")
    @Expose
    private String pageType;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageAPI() {
        return pageAPI;
    }

    public void setPageAPI(String pageAPI) {
        this.pageAPI = pageAPI;
    }

    public String getPageUI() {
        return pageUI;
    }

    public void setPageUI(String pageUI) {
        this.pageUI = pageUI;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

}
