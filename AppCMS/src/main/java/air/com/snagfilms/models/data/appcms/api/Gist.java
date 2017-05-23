
package air.com.snagfilms.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gist {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("permalink")
    @Expose
    private String permalink;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("year")
    @Expose
    private String year;
    @SerializedName("publishDate")
    @Expose
    private Long publishDate;
    @SerializedName("runtime")
    @Expose
    private Integer runtime;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("updateDate")
    @Expose
    private Long updateDate;
    @SerializedName("watchedTime")
    @Expose
    private Integer watchedTime;
    @SerializedName("contentType")
    @Expose
    private String contentType;

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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Long publishDate) {
        this.publishDate = publishDate;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getWatchedTime() {
        return watchedTime;
    }

    public void setWatchedTime(Integer watchedTime) {
        this.watchedTime = watchedTime;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
