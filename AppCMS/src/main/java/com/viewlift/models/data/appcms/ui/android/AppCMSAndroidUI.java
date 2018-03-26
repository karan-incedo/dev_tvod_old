package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.List;

@UseStag
public class AppCMSAndroidUI implements Serializable {

    @SerializedName("advertising")
    @Expose
    Advertising advertising;

    @SerializedName("navigation")
    @Expose
    Navigation navigation;

    @SerializedName("images")
    @Expose
    Images images;

    @SerializedName("pages")
    @Expose
    List<MetaPage> metaPages = null;

    @SerializedName("analytics")
    @Expose
    Analytics analytics;

    @SerializedName("version")
    @Expose
    String version;

    @SerializedName("appName")
    @Expose
    String appName;

    @SerializedName("shortAppName")
    @Expose
    String shortAppName;

    @SerializedName("blocks")
    @Expose
    List<Blocks> blocks;

    @SerializedName("blocksVersion")
    @Expose
    int blocksVersion;

    @SerializedName("pagesUpdated")
    @Expose
    long pagesUpdated;

    @SerializedName("blocksBundleUrl")
    @Expose
    String blocksBundleUrl;

    @SerializedName("blocksBaseUrl")
    @Expose
    String blocksBaseUrl;

    @SerializedName("subscription_flow_content")
    @Expose
    SubscriptionFlowContent subscriptionFlowContent;

    public SubscriptionAudioFlowContent getSubscriptionAudioFlowContent() {
        return subscriptionAudioFlowContent;
    }

    public void setSubscriptionAudioFlowContent(SubscriptionAudioFlowContent subscriptionAudioFlowContent) {
        this.subscriptionAudioFlowContent = subscriptionAudioFlowContent;
    }

    @SerializedName("subscription_flow_audio_content")
    @Expose
    SubscriptionAudioFlowContent subscriptionAudioFlowContent;


    public Advertising getAdvertising() {
        return advertising;
    }

    public void setAdvertising(Advertising advertising) {
        this.advertising = advertising;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getShortAppName() {
        return shortAppName;
    }

    public void setShortAppName(String shortAppName) {
        this.shortAppName = shortAppName;
    }

    public Navigation getNavigation() {
        return navigation;
    }

    public void setNavigation(Navigation navigation) {
        this.navigation = navigation;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public List<MetaPage> getMetaPages() {
        return metaPages;
    }

    public void setMetaPages(List<MetaPage> metaPages) {
        this.metaPages = metaPages;
    }

    public Analytics getAnalytics() {
        return analytics;
    }

    public void setAnalytics(Analytics analytics) {
        this.analytics = analytics;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Blocks> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Blocks> blocks) {
        this.blocks = blocks;
    }

    public int getBlocksVersion() {
        return blocksVersion;
    }

    public void setBlocksVersion(int blocksVersion) {
        this.blocksVersion = blocksVersion;
    }

    public long getPagesUpdated() {
        return pagesUpdated;
    }

    public void setPagesUpdated(long pagesUpdated) {
        this.pagesUpdated = pagesUpdated;
    }

    public String getBlocksBundleUrl() {
        return blocksBundleUrl;
    }

    public void setBlocksBundleUrl(String blocksBundleUrl) {
        this.blocksBundleUrl = blocksBundleUrl;
    }

    public String getBlocksBaseUrl() {
        return blocksBaseUrl;
    }

    public void setBlocksBaseUrl(String blocksBaseUrl) {
        this.blocksBaseUrl = blocksBaseUrl;
    }

    public SubscriptionFlowContent getSubscriptionFlowContent() {
        return subscriptionFlowContent;
    }

    public void setSubscriptionFlowContent(SubscriptionFlowContent subscriptionFlowContent) {
        this.subscriptionFlowContent = subscriptionFlowContent;
    }
}
