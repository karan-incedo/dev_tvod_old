
package com.viewlift.models.data.appcms.search;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppCMSSearchResult {

    @SerializedName("tmsId")
    @Expose
    private String tmsId;
    @SerializedName("logLine")
    @Expose
    private String logLine;
    @SerializedName("updateDate")
    @Expose
    private String updateDate;
    @SerializedName("addedDate")
    @Expose
    private String addedDate;
    @SerializedName("year")
    @Expose
    private String year;
    @SerializedName("imdbId")
    @Expose
    private String imdbId;
    @SerializedName("publishDate")
    @Expose
    private String publishDate;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("episode")
    @Expose
    private Integer episode;
    @SerializedName("posterImage")
    @Expose
    private PosterImage posterImage;
    @SerializedName("seoTitle")
    @Expose
    private String seoTitle;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("averageViewerGrade")
    @Expose
    private String averageViewerGrade;
    @SerializedName("tmdbRatingAvg")
    @Expose
    private Integer tmdbRatingAvg;
    @SerializedName("parentalRating")
    @Expose
    private String parentalRating;
    @SerializedName("tmdbId")
    @Expose
    private Integer tmdbId;
    @SerializedName("primaryCategory")
    @Expose
    private PrimaryCategory primaryCategory;
    @SerializedName("relatedVideoIds")
    @Expose
    private List<String> relatedVideoIds = null;
    @SerializedName("deviceControls")
    @Expose
    private String deviceControls;
    @SerializedName("objectKey")
    @Expose
    private String objectKey;
    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("free")
    @Expose
    private Boolean free;
    @SerializedName("thumbnail")
    @Expose
    private Thumbnail thumbnail;
    @SerializedName("geoRestriction")
    @Expose
    private String geoRestriction;
    @SerializedName("runtime")
    @Expose
    private Integer runtime;
    @SerializedName("tmdbRatingCount")
    @Expose
    private Integer tmdbRatingCount;
    @SerializedName("autoGenerateRelated")
    @Expose
    private Boolean autoGenerateRelated;
    @SerializedName("ipadAssets")
    @Expose
    private List<Object> ipadAssets = null;
    @SerializedName("liveStream")
    @Expose
    private Boolean liveStream;
    @SerializedName("creditBlocks")
    @Expose
    private List<CreditBlock> creditBlocks = null;
    @SerializedName("tags")
    @Expose
    private List<Tag> tags = null;
    @SerializedName("trailers")
    @Expose
    private List<Object> trailers = null;
    @SerializedName("cuePoints")
    @Expose
    private String cuePoints;
    @SerializedName("site")
    @Expose
    private String site;
    @SerializedName("channels")
    @Expose
    private List<Channel> channels = null;
    @SerializedName("widgetImage")
    @Expose
    private WidgetImage widgetImage;
    @SerializedName("closedCaptions")
    @Expose
    private List<Object> closedCaptions = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("videoImage")
    @Expose
    private VideoImage videoImage;
    @SerializedName("videoAssets")
    @Expose
    private List<VideoAsset> videoAssets = null;
    @SerializedName("permalink")
    @Expose
    private String permalink;
    @SerializedName("status")
    @Expose
    private String status;

    public String getTmsId() {
        return tmsId;
    }

    public void setTmsId(String tmsId) {
        this.tmsId = tmsId;
    }

    public String getLogLine() {
        return logLine;
    }

    public void setLogLine(String logLine) {
        this.logLine = logLine;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEpisode() {
        return episode;
    }

    public void setEpisode(Integer episode) {
        this.episode = episode;
    }

    public PosterImage getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(PosterImage posterImage) {
        this.posterImage = posterImage;
    }

    public String getSeoTitle() {
        return seoTitle;
    }

    public void setSeoTitle(String seoTitle) {
        this.seoTitle = seoTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAverageViewerGrade() {
        return averageViewerGrade;
    }

    public void setAverageViewerGrade(String averageViewerGrade) {
        this.averageViewerGrade = averageViewerGrade;
    }

    public Integer getTmdbRatingAvg() {
        return tmdbRatingAvg;
    }

    public void setTmdbRatingAvg(Integer tmdbRatingAvg) {
        this.tmdbRatingAvg = tmdbRatingAvg;
    }

    public String getParentalRating() {
        return parentalRating;
    }

    public void setParentalRating(String parentalRating) {
        this.parentalRating = parentalRating;
    }

    public Integer getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Integer tmdbId) {
        this.tmdbId = tmdbId;
    }

    public PrimaryCategory getPrimaryCategory() {
        return primaryCategory;
    }

    public void setPrimaryCategory(PrimaryCategory primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public List<String> getRelatedVideoIds() {
        return relatedVideoIds;
    }

    public void setRelatedVideoIds(List<String> relatedVideoIds) {
        this.relatedVideoIds = relatedVideoIds;
    }

    public String getDeviceControls() {
        return deviceControls;
    }

    public void setDeviceControls(String deviceControls) {
        this.deviceControls = deviceControls;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getFree() {
        return free;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getGeoRestriction() {
        return geoRestriction;
    }

    public void setGeoRestriction(String geoRestriction) {
        this.geoRestriction = geoRestriction;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public Integer getTmdbRatingCount() {
        return tmdbRatingCount;
    }

    public void setTmdbRatingCount(Integer tmdbRatingCount) {
        this.tmdbRatingCount = tmdbRatingCount;
    }

    public Boolean getAutoGenerateRelated() {
        return autoGenerateRelated;
    }

    public void setAutoGenerateRelated(Boolean autoGenerateRelated) {
        this.autoGenerateRelated = autoGenerateRelated;
    }

    public List<Object> getIpadAssets() {
        return ipadAssets;
    }

    public void setIpadAssets(List<Object> ipadAssets) {
        this.ipadAssets = ipadAssets;
    }

    public Boolean getLiveStream() {
        return liveStream;
    }

    public void setLiveStream(Boolean liveStream) {
        this.liveStream = liveStream;
    }

    public List<CreditBlock> getCreditBlocks() {
        return creditBlocks;
    }

    public void setCreditBlocks(List<CreditBlock> creditBlocks) {
        this.creditBlocks = creditBlocks;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Object> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Object> trailers) {
        this.trailers = trailers;
    }

    public String getCuePoints() {
        return cuePoints;
    }

    public void setCuePoints(String cuePoints) {
        this.cuePoints = cuePoints;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public WidgetImage getWidgetImage() {
        return widgetImage;
    }

    public void setWidgetImage(WidgetImage widgetImage) {
        this.widgetImage = widgetImage;
    }

    public List<Object> getClosedCaptions() {
        return closedCaptions;
    }

    public void setClosedCaptions(List<Object> closedCaptions) {
        this.closedCaptions = closedCaptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VideoImage getVideoImage() {
        return videoImage;
    }

    public void setVideoImage(VideoImage videoImage) {
        this.videoImage = videoImage;
    }

    public List<VideoAsset> getVideoAssets() {
        return videoAssets;
    }

    public void setVideoAssets(List<VideoAsset> videoAssets) {
        this.videoAssets = videoAssets;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
