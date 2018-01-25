package com.viewlift.models.data.appcms.audio;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.BadgeImages;
import com.viewlift.models.data.appcms.api.ImageGist;
import com.viewlift.models.data.appcms.api.PrimaryCategory;
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class AudioGist implements Serializable {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("permalink")
    @Expose
    String permalink;

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("description")
    @Expose
    String description;

    @SerializedName("runtime")
    @Expose
    long runtime;

    @SerializedName("posterImageUrl")
    @Expose
    String posterImageUrl;

    @SerializedName("imageGist")
    @Expose
    ImageGist imageGist;

    @SerializedName("primaryCategory")
    @Expose
    PrimaryCategory primaryCategory;

    @SerializedName("contentType")
    @Expose
    String contentType;

    String downloadStatus;
    /**
     * This is to store the url of the downloaded file
     */
    String localFileUrl;

    public String getMediaType() {
        return mediaType;
    }

    @SerializedName("mediaType")
    @Expose
    String mediaType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public String getPosterImageUrl() {
        return posterImageUrl;
    }

    public void setPosterImageUrl(String posterImageUrl) {
        this.posterImageUrl = posterImageUrl;
    }


    public PrimaryCategory getPrimaryCategory() {
        return primaryCategory;
    }

    public void setPrimaryCategory(PrimaryCategory primaryCategory) {
        this.primaryCategory = primaryCategory;
    }


    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    public DownloadStatus getDownloadStatus() {
        if (downloadStatus != null) {
            return DownloadStatus.valueOf(downloadStatus);
        }
        return DownloadStatus.STATUS_PENDING;
    }

    public void setDownloadStatus(DownloadStatus downloadStatus) {
        this.downloadStatus = downloadStatus.toString();
    }

    public String getLocalFileUrl() {
        return localFileUrl;
    }

    public void setLocalFileUrl(String localFileUrl) {
        this.localFileUrl = localFileUrl;
    }

    public ImageGist getImageGist() {
        return imageGist;
    }

    public void setImageGist(ImageGist imageGist) {
        this.imageGist = imageGist;
    }

    long currentPlayingPosition;
    Boolean isCastingConnected;

    public long getCurrentPlayingPosition() {
        return currentPlayingPosition;
    }

    public void setCurrentPlayingPosition(long currentPlayingPosition) {
        this.currentPlayingPosition = currentPlayingPosition;
    }

    public Boolean getCastingConnected() {
        return isCastingConnected;
    }

    public void setCastingConnected(Boolean castingConnected) {
        isCastingConnected = castingConnected;
    }
}
