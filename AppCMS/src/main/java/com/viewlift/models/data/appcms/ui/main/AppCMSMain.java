package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class AppCMSMain implements Serializable {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("accessKey")
    @Expose
    String accessKey;

    @SerializedName("apiBaseUrl")
    @Expose
    String apiBaseUrl;

    @SerializedName("pageEndpoint")
    @Expose
    String pageEndpoint;

    public String getInternalName() {
        return internalName;
    }

    @SerializedName("internalName")
    @Expose
    String internalName;

    @SerializedName("faqUrl")
    @Expose
    String faqUrl;

    @SerializedName("beacon")
    @Expose
    Beacon beacon;

    @SerializedName("site")
    @Expose
    String site;

    @SerializedName("serviceType")
    @Expose
    String serviceType;

    @SerializedName("apiBaseUrlCached")
    @Expose
    String apiBaseUrlCached;

    @SerializedName("domainName")
    @Expose
    String domainName;

    @SerializedName("brand")
    @Expose
    Brand brand;

    @SerializedName("content")
    @Expose
    Content content;

    @SerializedName("images")
    @Expose
    Images images;

    @SerializedName("version")
    @Expose
    String version;

    @SerializedName("old_version")
    @Expose
    String oldVersion;

    @SerializedName("Web")
    @Expose
    String web;

    @SerializedName("iOS")
    @Expose
    String iOS;

    @SerializedName("Android")
    @Expose
    String android;

    @SerializedName("features")
    @Expose
    Features features;

    @SerializedName("appVersions")
    @Expose
    AppVersions appVersions;

    @SerializedName("companyName")
    @Expose
    String companyName;

    boolean loadFromFile;

    public String getFireTv() {
        return fireTv;
    }

    public void setFireTv(String fireTv) {
        this.fireTv = fireTv;
    }

    @SerializedName("fireTv")
    @Expose
    private String fireTv;
    @SerializedName("timestamp")
    @Expose
    long timestamp;

    @SerializedName("socialMedia")
    @Expose
    SocialMedia socialMedia;

    @SerializedName("forceLogin")
    @Expose
    boolean forceLogin;

    @SerializedName("isDownloadable")
    @Expose
    boolean isDownloadable;

    @SerializedName("paymentProviders")
    @Expose
    PaymentProviders paymentProviders;

    @SerializedName("taxProviders")
    @Expose
    TaxProviders taxProviders;

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @SerializedName("customerService")
    @Expose
    CustomerService customerService;

    @SerializedName("templateName")
    @Expose
    String templateName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getPageEndpoint() {
        return pageEndpoint;
    }

    public void setPageEndpoint(String pageEndpoint) {
        this.pageEndpoint = pageEndpoint;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public String getFaqUrl() {
        return faqUrl;
    }

    public void setFaqUrl(String faqUrl) {
        this.faqUrl = faqUrl;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getIOS() {
        return iOS;
    }

    public void setIOS(String iOS) {
        this.iOS = iOS;
    }

    public String getAndroid() {
        return android;
    }

    public void setAndroid(String android) {
        this.android = android;
    }

    public String getOldVersion() {
        return oldVersion;
    }

    public void setOldVersion(String oldVersion) {
        this.oldVersion = oldVersion;
    }

    public String getiOS() {
        return iOS;
    }

    public void setiOS(String iOS) {
        this.iOS = iOS;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isForceLogin() {
        return forceLogin;
    }

    public void setForceLogin(boolean forceLogin) {
        this.forceLogin = forceLogin;
    }

    public SocialMedia getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(SocialMedia socialMedia) {
        this.socialMedia = socialMedia;
    }

    public boolean shouldLoadFromFile() {
        return loadFromFile;
    }

    public void setLoadFromFile(boolean loadFromFile) {
        this.loadFromFile = loadFromFile;
    }

    public Features getFeatures() {
        return features;
    }

    public void setFeatures(Features features) {
        this.features = features;
    }

    public PaymentProviders getPaymentProviders() {
        return paymentProviders;
    }

    public void setPaymentProviders(PaymentProviders paymentProviders) {
        this.paymentProviders = paymentProviders;
    }

    public boolean isDownloadable() {
        return isDownloadable;
    }

    public void setDownloadable(boolean downloadable) {
        isDownloadable = downloadable;
    }

    public AppVersions getAppVersions() {
        return appVersions;
    }

    public void setAppVersions(AppVersions appVersions) {
        this.appVersions = appVersions;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getApiBaseUrlCached() {
        return apiBaseUrlCached;
    }

    public void setApiBaseUrlCached(String apiBaseUrlCached) {
        this.apiBaseUrlCached = apiBaseUrlCached;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
