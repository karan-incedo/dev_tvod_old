package com.viewlift.models.data.appcms.beacon;



import io.realm.RealmObject;

/**
 * Created by sandeep.singh on 8/21/2017.
 */

public class OfflineBeaconData extends RealmObject{


    private String aid;


    private String cid;


    private String pfm;


    private String vid;


    private String uid;


    private String profid;


    private String pa;


    private String player;


    private String environment;


    private String media_type;

    private String tstampoverride;


    private String stream_id;


    private String dp1;

    private String dp2;

    private String dp3;

    private String dp4;

    private String dp5;

    private String ref;

    private String apos;

    private String apod;

    private String vpos;

    private String url;

    private String embedurl;

    private String ttfirstframe;

    private String bitrate;

    private String connectionspeed;

    private String resolutionheight;

    private String resolutionwidth;

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

    public String getApos() {
        return apos;
    }

    public void setApos(String apos) {
        this.apos = apos;
    }

    public String getApod() {
        return apod;
    }

    public void setApod(String apod) {
        this.apod = apod;
    }

    public String getVpos() {
        return vpos;
    }

    public void setVpos(String vpos) {
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

    public BeaconRequest convertToBeaconRequest(){
        BeaconRequest beaconRequest =new BeaconRequest();

        beaconRequest.setAid(getAid());
        beaconRequest.setApod(getApod());
        beaconRequest.setApos(getApos());
        beaconRequest.setBitrate(getBitrate());
        beaconRequest.setBufferhealth(getBufferhealth());
        beaconRequest.setCid(getCid());
        beaconRequest.setConnectionspeed(getConnectionspeed());
        beaconRequest.setDp1(getDp1());
        beaconRequest.setDp2(getDp2());
        beaconRequest.setDp3(getDp3());
        beaconRequest.setDp4(getDp4());
        beaconRequest.setDp5(getDp5());
        beaconRequest.setEmbedurl(getEmbedurl());
        beaconRequest.setEnvironment(getEnvironment());
        beaconRequest.setMedia_type(getMedia_type());
        beaconRequest.setPa(getPa());
        beaconRequest.setPfm(getPfm());
        beaconRequest.setPlayer(getPlayer());
        beaconRequest.setProfid(getProfid());
        beaconRequest.setRef(getRef());
        beaconRequest.setResolutionheight(getResolutionheight());
        beaconRequest.setResolutionwidth(getResolutionwidth());
        beaconRequest.setStream_id(getStream_id());
        beaconRequest.setTstampoverride(getTstampoverride());
        beaconRequest.setTtfirstframe(getTtfirstframe());
        beaconRequest.setUid(getUid());
        beaconRequest.setUrl(getUrl());
        beaconRequest.setVid(getVid());
        beaconRequest.setVpos(getVpos());


        return beaconRequest;
    }
}
