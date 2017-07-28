package com.viewlift.models.data.appcms.downloads;

/**
 * Created by sandeep.singh on 7/18/2017.
 */

public class UserVideoDownloadStatus {

    private String videoId;
    private String downloadStatus;
    private long videoId_DM;

    private String videoUri;
    private String thumbUri;
    private long videoSize;


    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public DownloadStatus getDownloadStatus() {
        return DownloadStatus.valueOf(downloadStatus);
    }

    public void setDownloadStatus(DownloadStatus downloadStatus) {
        this.downloadStatus = downloadStatus.toString();
    }

    public long getVideoId_DM() {
        return videoId_DM;
    }

    public void setVideoId_DM(long videoId_DM) {
        this.videoId_DM = videoId_DM;
    }

    public String getThumbUri() {
        return thumbUri;
    }

    public void setThumbUri(String thumbUri) {
        this.thumbUri = thumbUri;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }
}
