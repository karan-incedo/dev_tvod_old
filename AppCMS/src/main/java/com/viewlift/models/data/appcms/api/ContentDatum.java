package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.audio.AudioAssets;
import com.viewlift.models.data.appcms.playlist.AudioList;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@UseStag
public class ContentDatum implements Serializable {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("renewable")
    @Expose
    boolean renewable;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("identifier")
    @Expose
    String identifier;

    @SerializedName("description")
    @Expose
    String description;

    @SerializedName("renewalCyclePeriodMultiplier")
    @Expose
    int renewalCyclePeriodMultiplier;

    @SerializedName("renewalCycleType")
    @Expose
    String renewalCycleType;

    @SerializedName("planDetails")
    @Expose
    List<PlanDetail> planDetails;

    @SerializedName("gist")
    @Expose
    Gist gist;

    @SerializedName("grade")
    @Expose
    String grade;

    @SerializedName("userId")
    @Expose
    String userId;
    public Fights Fights;

    @SerializedName("showQueue")
    @Expose
    boolean showQueue;

    @SerializedName("addedDate")
    @Expose
    long addedDate;

    @SerializedName("updateDate")
    @Expose
    long updateDate;

    @SerializedName("contentDetails")
    @Expose
    ContentDetails contentDetails;

    @SerializedName("streamingInfo")
    @Expose
    StreamingInfo streamingInfo;

    @SerializedName("categories")
    @Expose
    List<Category> categories = null;

    @SerializedName("tags")
    @Expose
    List<Tag> tags = null;

    @SerializedName("seasons")
    @Expose
    List<Season_> season = null;

    @SerializedName("external")
    @Expose
    External external;

    @SerializedName("statistics")
    @Expose
    Statistics statistics;

    @SerializedName("channels")
    @Expose
    List<Object> channels = null;

    @SerializedName("creditBlocks")
    @Expose
    List<CreditBlock> creditBlocks = null;
    @SerializedName("showDetails")
    @Expose
    ShowDetails showDetails;

    @SerializedName("parentalRating")
    @Expose
    String parentalRating;
    @SerializedName("permalink")
    @Expose
    String permalink;
    @SerializedName("title")
    @Expose
    String title;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    @SerializedName("dataId")
    @Expose
    String dataId;

    @SerializedName("contentType")
    @Expose
    String contentType;
    @SerializedName("mediaType")
    @Expose
    String mediaType;

    @SerializedName("seo")
    @Expose
    List<Seo> seo = null;

    HashMap<String,List<ContentDatum>> monthlySchedule;

    @SerializedName("drmEnabled")
    @Expose
    boolean isDRMEnabled = false;

    Players players;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    boolean isSelected = false;

    public void setPlayersData(Players players) {
        this.players = players;
    }
    public Players getPlayersData() {
        return players;
    }



    List<Players> playersList;


    public List<Players> getPlayers() {
        return playersList;
    }

    public void setPlayers(List<Players> players) {
        this.playersList = players;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    Team team;


    private String playListName;
    private String seriesName;

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getPlayListName() {
        return playListName;
    }

    public void setPlayListName(String playListName) {
        this.playListName = playListName;
    }

    public boolean isRenewable() {
        return renewable;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }


    public Gist getGist() {
        return gist;
    }

    public void setGist(Gist gist) {
        this.gist = gist;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isShowQueue() {
        return showQueue;
    }

    public void setShowQueue(boolean showQueue) {
        this.showQueue = showQueue;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Long addedDate) {
        this.addedDate = addedDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public ContentDetails getContentDetails() {
        return contentDetails;
    }

    public void setContentDetails(ContentDetails contentDetails) {
        this.contentDetails = contentDetails;
    }

    public Fights getFights() {
        return Fights;
    }

    public void setFights(Fights fights) {
        Fights = fights;
    }

    public StreamingInfo getStreamingInfo() {
        return streamingInfo;
    }

    public void setStreamingInfo(StreamingInfo streamingInfo) {
        this.streamingInfo = streamingInfo;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Season_> getSeason() {
        return season;
    }

    public void setSeason(List<Season_> season) {
        this.season = season;
    }

    public External getExternal() {
        return external;
    }

    public void setExternal(External external) {
        this.external = external;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public List<Object> getChannels() {
        return channels;
    }

    public void setChannels(List<Object> channels) {
        this.channels = channels;
    }

    public List<CreditBlock> getCreditBlocks() {
        return creditBlocks;
    }

    public void setCreditBlocks(List<CreditBlock> creditBlocks) {
        this.creditBlocks = creditBlocks;
    }

    public String getParentalRating() {
        return parentalRating;
    }

    public void setParentalRating(String parentalRating) {
        this.parentalRating = parentalRating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getRenewable() {
        return renewable;
    }

    public void setRenewable(boolean renewable) {
        this.renewable = renewable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRenewalCyclePeriodMultiplier() {
        return renewalCyclePeriodMultiplier;
    }

    public void setRenewalCyclePeriodMultiplier(int renewalCyclePeriodMultiplier) {
        this.renewalCyclePeriodMultiplier = renewalCyclePeriodMultiplier;
    }

    public String getRenewalCycleType() {
        return renewalCycleType;
    }

    public void setRenewalCycleType(String renewalCycleType) {
        this.renewalCycleType = renewalCycleType;
    }

    public List<PlanDetail> getPlanDetails() {
        return planDetails;
    }

    public void setPlanDetails(List<PlanDetail> planDetails) {
        this.planDetails = planDetails;
    }

    public ShowDetails getShowDetails() {
        return showDetails;
    }

    public void setShowDetails(ShowDetails showDetails) {
        this.showDetails = showDetails;
    }

    public AppCMSPageAPI convertToAppCMSPageAPI(String moduleType) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        module.setModuleType(moduleType);
        List<ContentDatum> data = new ArrayList<>();
        data.add(this);
        module.setContentData(data);
        appCMSPageAPI.setId(id);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);
        return appCMSPageAPI;
    }





    List<AudioList> audioList = null;

    public List<Team> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<Team> teamList) {
        this.teamList = teamList;
    }

    List<Team> teamList = null;

    public List<AudioList> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<AudioList> audioList) {
        this.audioList = audioList;
    }
    AudioAssets audioAssets = null;

    public AudioAssets getAudioAssets() {
        return audioAssets;
    }

    public void setAudioAssets(AudioAssets audioAssets) {
        this.audioAssets = audioAssets;
    }

    public List<Seo> getSeo() {
        return seo;
    }

    public void setSeo(List<Seo> seo) {
        this.seo = seo;
    }

    public boolean isDRMEnabled(){
        return false;
    }

    public void setDRMEnabled(boolean DRMEnabled) {
        isDRMEnabled = DRMEnabled;
    }
    public HashMap<String, List<ContentDatum>> getMonthlySchedule() {
        return monthlySchedule;
    }

    public void setMonthlySchedule(HashMap<String, List<ContentDatum>> monthlySchedule) {
        this.monthlySchedule = monthlySchedule;
    }

    List<ContentDatum> contentData = null;

    public List<com.viewlift.models.data.appcms.api.LiveEvents> getLiveEvents() {
        return LiveEvents;
    }

    public void setLiveEvents(List<com.viewlift.models.data.appcms.api.LiveEvents> liveEvents) {
        LiveEvents = liveEvents;
    }

    List<LiveEvents> LiveEvents = null;

    public void setContentData(List<ContentDatum> contentData) {
        this.contentData = contentData;
    }

    public List<ContentDatum> getContentData() {
        return contentData;
    }
}
