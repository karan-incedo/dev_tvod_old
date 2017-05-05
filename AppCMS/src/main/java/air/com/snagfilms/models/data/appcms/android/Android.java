
package air.com.snagfilms.models.data.appcms.android;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Android {

    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("advertising")
    @Expose
    private Advertising advertising;
    @SerializedName("analytics")
    @Expose
    private Analytics analytics;
    @SerializedName("appName")
    @Expose
    private String appName;
    @SerializedName("customerService")
    @Expose
    private CustomerService customerService;
    @SerializedName("images")
    @Expose
    private Images images;
    @SerializedName("notifications")
    @Expose
    private Notifications notifications;
    @SerializedName("shortAppName")
    @Expose
    private String shortAppName;
    @SerializedName("Pages")
    @Expose
    private List<Page> pages = null;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Advertising getAdvertising() {
        return advertising;
    }

    public void setAdvertising(Advertising advertising) {
        this.advertising = advertising;
    }

    public Analytics getAnalytics() {
        return analytics;
    }

    public void setAnalytics(Analytics analytics) {
        this.analytics = analytics;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public Notifications getNotifications() {
        return notifications;
    }

    public void setNotifications(Notifications notifications) {
        this.notifications = notifications;
    }

    public String getShortAppName() {
        return shortAppName;
    }

    public void setShortAppName(String shortAppName) {
        this.shortAppName = shortAppName;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

}
