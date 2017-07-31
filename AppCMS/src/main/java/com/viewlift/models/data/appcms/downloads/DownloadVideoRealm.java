package com.viewlift.models.data.appcms.downloads;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sandeep.singh on 7/18/2017.
 */


public class DownloadVideoRealm extends RealmObject {
    @PrimaryKey
    private String videoId;
    private long videoId_DM;
    private long videoThumbId_DM;
    private String videoTitle;
    private String videoDescription;
    private String downloadStatus;
    private String videoWebURL;
    private String videoFileURL;
    private String localURI;
    private long videoSize;
    private long video_Downloaded_so_far;
    private long downloadDate;
    private long lastWatchDate;
    private long videoPlayedDuration;
    private long videoDuration;
    private int bitRate;
    private String showId;
    private String showTitle;
    private String showDescription;
    private String videoNumber;
    private String permalink;
    private String posterImageUrl;
    private String userId;


    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public long getVideoId_DM() {
        return videoId_DM;
    }

    public void setVideoId_DM(long videoId_DM) {
        this.videoId_DM = videoId_DM;
    }

    public long getVideoThumbId_DM() {
        return videoThumbId_DM;
    }

    public void setVideoThumbId_DM(long videoThumbId_DM) {
        this.videoThumbId_DM = videoThumbId_DM;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public DownloadStatus getDownloadStatus() {
        return DownloadStatus.valueOf(downloadStatus);
    }

    public void setDownloadStatus(DownloadStatus downloadStatus) {
        this.downloadStatus = downloadStatus.toString();
    }

    public String getVideoWebURL() {
        return videoWebURL;
    }

    public void setVideoWebURL(String videoWebURL) {
        this.videoWebURL = videoWebURL;
    }

    public String getVideoFileURL() {
        return videoFileURL;
    }

    public void setVideoFileURL(String videoFileURL) {
        this.videoFileURL = videoFileURL;
    }

    public String getLocalURI() {
        return localURI;
    }

    public void setLocalURI(String localURI) {
        this.localURI = localURI;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }

    public long getVideo_Downloaded_so_far() {
        return video_Downloaded_so_far;
    }

    public void setVideo_Downloaded_so_far(long video_Downloaded_so_far) {
        this.video_Downloaded_so_far = video_Downloaded_so_far;
    }

    public long getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(long downloadDate) {
        this.downloadDate = downloadDate;
    }

    public long getLastWatchDate() {
        return lastWatchDate;
    }

    public void setLastWatchDate(long lastWatchDate) {
        this.lastWatchDate = lastWatchDate;
    }

    public long getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(long videoDuration) {
        this.videoDuration = videoDuration;
    }

    public long getVideoPlayedDuration() {
        return videoPlayedDuration;
    }

    public void setVideoPlayedDuration(long videoPlayedDuration) {
        this.videoPlayedDuration = videoPlayedDuration;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getShowTitle() {
        return showTitle;
    }

    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }

    public String getShowDescription() {
        return showDescription;
    }

    public void setShowDescription(String showDescription) {
        this.showDescription = showDescription;
    }

    public String getVideoNumber() {
        return videoNumber;
    }

    public void setVideoNumber(String videoNumber) {
        this.videoNumber = videoNumber;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getPosterImageUrl() {
        return posterImageUrl;
    }

    public void setPosterImageUrl(String posterImageUrl) {
        this.posterImageUrl = posterImageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
