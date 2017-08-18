package com.viewlift.models.data.appcms.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.StreamingInfo;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class AppCMSSearchResult {

    @SerializedName("tmsId")
    @Expose
    String tmsId;

    @SerializedName("logLine")
    @Expose
    String logLine;

    @SerializedName("updateDate")
    @Expose
    String updateDate;

    @SerializedName("addedDate")
    @Expose
    String addedDate;

    @SerializedName("year")
    @Expose
    String year;

    @SerializedName("imdbId")
    @Expose
    String imdbId;

    @SerializedName("publishDate")
    @Expose
    String publishDate;

    @SerializedName("description")
    @Expose
    String description;

    @SerializedName("episode")
    @Expose
    int episode;

    @SerializedName("posterImage")
    @Expose
    PosterImage posterImage;

    @SerializedName("seoTitle")
    @Expose
    String seoTitle;

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("averageViewerGrade")
    @Expose
    String averageViewerGrade;

    @SerializedName("tmdbRatingAvg")
    @Expose
    int tmdbRatingAvg;

    @SerializedName("parentalRating")
    @Expose
    String parentalRating;

    @SerializedName("tmdbId")
    @Expose
    int tmdbId;

    @SerializedName("primaryCategory")
    @Expose
    PrimaryCategory primaryCategory;

    @SerializedName("relatedVideoIds")
    @Expose
    List<String> relatedVideoIds = null;

    @SerializedName("deviceControls")
    @Expose
    String deviceControls;

    @SerializedName("objectKey")
    @Expose
    String objectKey;

    @SerializedName("categories")
    @Expose
    List<Category> categories = null;

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("free")
    @Expose
    boolean free;

    @SerializedName("thumbnail")
    @Expose
    Thumbnail thumbnail;

    @SerializedName("geoRestriction")
    @Expose
    String geoRestriction;

    @SerializedName("runtime")
    @Expose
    int runtime;

    @SerializedName("tmdbRatingCount")
    @Expose
    int tmdbRatingCount;

    @SerializedName("autoGenerateRelated")
    @Expose
    boolean autoGenerateRelated;

    @SerializedName("ipadAssets")
    @Expose
    List<Object> ipadAssets = null;

    @SerializedName("liveStream")
    @Expose
    boolean liveStream;

    @SerializedName("creditBlocks")
    @Expose
    List<CreditBlock> creditBlocks = null;

    @SerializedName("tags")
    @Expose
    List<Tag> tags = null;

    @SerializedName("trailers")
    @Expose
    List<Object> trailers = null;

    @SerializedName("cuePoints")
    @Expose
    String cuePoints;

    @SerializedName("site")
    @Expose
    String site;

    @SerializedName("channels")
    @Expose
    List<Channel> channels = null;

    @SerializedName("widgetImage")
    @Expose
    WidgetImage widgetImage;

    @SerializedName("closedCaptions")
    @Expose
    List<Object> closedCaptions = null;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("videoImage")
    @Expose
    VideoImage videoImage;

    @SerializedName("videoAssets")
    @Expose
    List<VideoAsset> videoAssets = null;

    @SerializedName("permalink")
    @Expose
    String permalink;

    @SerializedName("status")
    @Expose
    String status;

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

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
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

    public int getTmdbRatingAvg() {
        return tmdbRatingAvg;
    }

    public void setTmdbRatingAvg(int tmdbRatingAvg) {
        this.tmdbRatingAvg = tmdbRatingAvg;
    }

    public String getParentalRating() {
        return parentalRating;
    }

    public void setParentalRating(String parentalRating) {
        this.parentalRating = parentalRating;
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(int tmdbId) {
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

    public boolean getFree() {
        return free;
    }

    public void setFree(boolean free) {
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

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public int getTmdbRatingCount() {
        return tmdbRatingCount;
    }

    public void setTmdbRatingCount(int tmdbRatingCount) {
        this.tmdbRatingCount = tmdbRatingCount;
    }

    public boolean getAutoGenerateRelated() {
        return autoGenerateRelated;
    }

    public void setAutoGenerateRelated(boolean autoGenerateRelated) {
        this.autoGenerateRelated = autoGenerateRelated;
    }

    public List<Object> getIpadAssets() {
        return ipadAssets;
    }

    public void setIpadAssets(List<Object> ipadAssets) {
        this.ipadAssets = ipadAssets;
    }

    public boolean getLiveStream() {
        return liveStream;
    }

    public void setLiveStream(boolean liveStream) {
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


    public ContentDatum getContent(){
        ContentDatum contentDatum = new ContentDatum();
        Gist gist = new Gist();
        gist.setTitle(getTitle());
        gist.setPosterImageUrl(getPosterImage().getUrl());
        gist.setPermalink(getPermalink());
        gist.setId(getId());

        VideoAssets videoAssets = new VideoAssets();
        videoAssets.setHls(getVideoAssets() != null ? getVideoAssets().get(0).getHls() : null);
        StreamingInfo streamingInfo = new StreamingInfo();
        streamingInfo.setVideoAssets(videoAssets);


        contentDatum.setStreamingInfo(streamingInfo);
        contentDatum.setGist(gist);
        return contentDatum;
    }
}
