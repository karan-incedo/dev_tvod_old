package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class AppCMSEntitlementResponse {

    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("siteName")
    @Expose
    private String siteName;

    @SerializedName("siteId")
    @Expose
    private String siteId;

    @SerializedName("video")
    @Expose
    private ContentDatum videoContentDatum;

    @SerializedName("playable")
    @Expose
    private boolean playable;

    @SerializedName("dfp")
    @Expose
    private Object dfp;

    @SerializedName("errorMessage")
    @Expose
    private  String errorMessage;

    private int code;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }



    public boolean isPlayable() {
        return playable;
    }

    public void setPlayable(boolean playable) {
        this.playable = playable;
    }

    public Object getDfp() {
        return dfp;
    }

    public void setDfp(Object dfp) {
        this.dfp = dfp;
    }

    public ContentDatum getVideoContentDatum() {
        return videoContentDatum;
    }

    public void setVideoContentDatum(ContentDatum videoContentDatum) {
        this.videoContentDatum = videoContentDatum;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }


    public ContentDatum convertToContentDatum(){
        return this.getVideoContentDatum();
    }

    public AppCMSPageAPI convertToAppCMSPageAPI(String Id, String moduleType) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();
        data.add(this.getVideoContentDatum());

        module.setContentData(data);
        module.setModuleType(moduleType);
        appCMSPageAPI.setId(Id);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }
}
