package com.viewlift.models.data.appcms.beacon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by sandeep.singh on 8/22/2017.
 */

@UseStag
public class BeaconRequest {


    @SerializedName("aid")
    @Expose
    private String aid;

    @SerializedName("cid")
    @Expose
    private String cid;

    @SerializedName("pfm")
    @Expose
    private String pfm;

    @SerializedName("vid")
    @Expose
    private String vid;

    @SerializedName("uid")
    @Expose
    private String uid;

    @SerializedName("profid")
    @Expose
    private String profid;

    @SerializedName("pa")
    @Expose
    private String pa;

    @SerializedName("player")
    @Expose
    private String player;

    @SerializedName("environment")
    @Expose
    private String environment;

    @SerializedName("media_type")
    @Expose
    private String media_type;

    @SerializedName("tstampoverride")
    @Expose
    private String tstampoverride;

    @SerializedName("stream_id")
    @Expose
    private String stream_id;

    @SerializedName("dp1")
    @Expose
    private String dp1;

    @SerializedName("dp2")
    @Expose
    private String dp2;

    @SerializedName("dp3")
    @Expose
    private String dp3;

    @SerializedName("dp4")
    @Expose
    private String dp4;

    @SerializedName("dp5")
    @Expose
    private String dp5;

    @SerializedName("ref")
    @Expose
    private String ref;

    @SerializedName("apos")
    @Expose
    private int apos;

    @SerializedName("apod")
    @Expose
    private int apod;

    @SerializedName("vpos")
    @Expose
    private int vpos;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("embedurl")
    @Expose
    private String embedurl;

    @SerializedName("ttfirstframe")
    @Expose
    private String ttfirstframe;

    @SerializedName("bitrate")
    @Expose
    private String bitrate;

    @SerializedName("connectionspeed")
    @Expose
    private String connectionspeed;

    @SerializedName("resolutionheight")
    @Expose
    private String resolutionheight;

    @SerializedName("resolutionwidth")
    @Expose
    private String resolutionwidth;

    @SerializedName("bufferhealth")
    @Expose
    private String bufferhealth;


    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPfm() {
        return pfm;
    }

    public void setPfm(String pfm) {
        this.pfm = pfm;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfid() {
        return profid;
    }

    public void setProfid(String profid) {
        this.profid = profid;
    }

    public String getPa() {
        return pa;
    }

    public void setPa(String pa) {
        this.pa = pa;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getTstampoverride() {
        return tstampoverride;
    }

    public void setTstampoverride(String tstampoverride) {
        this.tstampoverride = tstampoverride;
    }

    public String getStream_id() {
        return stream_id;
    }

    public void setStream_id(String stream_id) {
        this.stream_id = stream_id;
    }

    public String getDp1() {
        return dp1;
    }

    public void setDp1(String dp1) {
        this.dp1 = dp1;
    }

    public String getDp2() {
        return dp2;
    }

    public void setDp2(String dp2) {
        this.dp2 = dp2;
    }

    public String getDp3() {
        return dp3;
    }

    public void setDp3(String dp3) {
        this.dp3 = dp3;
    }

    public String getDp4() {
        return dp4;
    }

    public void setDp4(String dp4) {
        this.dp4 = dp4;
    }

    public String getDp5() {
        return dp5;
    }

    public void setDp5(String dp5) {
        this.dp5 = dp5;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public int getApos() {
        return apos;
    }

    public void setApos(int apos) {
        this.apos = apos;
    }

    public int getApod() {
        return apod;
    }

    public void setApod(int apod) {
        this.apod = apod;
    }

    public int getVpos() {
        return vpos;
    }

    public void setVpos(int vpos) {
        this.vpos = vpos;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmbedurl() {
        return embedurl;
    }

    public void setEmbedurl(String embedurl) {
        this.embedurl = embedurl;
    }

    public String getTtfirstframe() {
        return ttfirstframe;
    }

    public void setTtfirstframe(String ttfirstframe) {
        this.ttfirstframe = ttfirstframe;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public String getConnectionspeed() {
        return connectionspeed;
    }

    public void setConnectionspeed(String connectionspeed) {
        this.connectionspeed = connectionspeed;
    }

    public String getResolutionheight() {
        return resolutionheight;
    }

    public void setResolutionheight(String resolutionheight) {
        this.resolutionheight = resolutionheight;
    }

    public String getResolutionwidth() {
        return resolutionwidth;
    }

    public void setResolutionwidth(String resolutionwidth) {
        this.resolutionwidth = resolutionwidth;
    }

    public String getBufferhealth() {
        return bufferhealth;
    }

    public void setBufferhealth(String bufferhealth) {
        this.bufferhealth = bufferhealth;
    }

    public OfflineBeaconData convertToOfflineBeaconData(){
        OfflineBeaconData offlineBeaconData =new OfflineBeaconData();

        offlineBeaconData.setAid(getAid());
        offlineBeaconData.setApod(getApod());
        offlineBeaconData.setApos(getApos());
        offlineBeaconData.setBitrate(getBitrate());
        offlineBeaconData.setBufferhealth(getBufferhealth());
        offlineBeaconData.setCid(getCid());
        offlineBeaconData.setConnectionspeed(getConnectionspeed());
        offlineBeaconData.setDp1(getAid());
        offlineBeaconData.setDp2(getDp2());
        offlineBeaconData.setDp3(getDp3());
        offlineBeaconData.setDp4(getDp4());
        offlineBeaconData.setDp5(getDp5());
        offlineBeaconData.setEmbedurl(getEmbedurl());
        offlineBeaconData.setEnvironment(getEnvironment());
        offlineBeaconData.setMedia_type(getMedia_type());
        offlineBeaconData.setPa(getPa());
        offlineBeaconData.setPfm(getPfm());
        offlineBeaconData.setPlayer(getPlayer());
        offlineBeaconData.setProfid(getProfid());
        offlineBeaconData.setRef(getRef());
        offlineBeaconData.setResolutionheight(getResolutionheight());
        offlineBeaconData.setResolutionwidth(getResolutionwidth());
        offlineBeaconData.setStream_id(getStream_id());
        offlineBeaconData.setTstampoverride(getTstampoverride());
        offlineBeaconData.setTtfirstframe(getTtfirstframe());
        offlineBeaconData.setUid(getUid());
        offlineBeaconData.setUrl(getUrl());
        offlineBeaconData.setVid(getVid());
        offlineBeaconData.setVpos(getVpos());


        return offlineBeaconData;
    }
}
