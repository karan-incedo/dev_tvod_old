
package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class AppCMSShowDetail {

    @SerializedName("updateDate")
    @Expose
    private String updateDate;

    @SerializedName("lastUpdated")
    @Expose
    private String lastUpdated;

    @SerializedName("permalink")
    @Expose
    private String permalink;

    @SerializedName("site")
    @Expose
    private String site;

    @SerializedName("isActive")
    @Expose
    private Boolean isActive;

    @SerializedName("categories")
    @Expose
     List<Category> categories;

    @SerializedName("creditBlocks")
    @Expose
     List<CreditBlock> creditBlocks;

    @SerializedName("images")
    @Expose
    private Images images;

    @SerializedName("addedDate")
    @Expose
    private String addedDate;

    @SerializedName("gist")
    @Expose
    private Gist gist;

    @SerializedName("publishDate")
    @Expose
    private String publishDate;

    @SerializedName("seasons")
    @Expose
    List<Season_> seasons;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("seo")
    @Expose
    private Seo seo;

    @SerializedName("tags")
    @Expose
     List<Tag> tags;

    @SerializedName("author")
    @Expose
    private Author author;

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<CreditBlock> getCreditBlocks() {
        return creditBlocks;
    }

    public void setCreditBlocks(List<CreditBlock> creditBlocks) {
        this.creditBlocks = creditBlocks;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public Gist getGist() {
        return gist;
    }

    public void setGist(Gist gist) {
        this.gist = gist;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public List<Season_> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season_> seasons) {
        this.seasons = seasons;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Seo getSeo() {
        return seo;
    }

    public void setSeo(Seo seo) {
        this.seo = seo;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }


    public ContentDatum convertToContentDatum() {
        ContentDatum contentDatum = new ContentDatum();
        ContentDetails contentDetails = new ContentDetails();
        contentDatum.setGist(this.getGist());
        contentDatum.setContentDetails(contentDetails);
        contentDatum.setSeason(this.getSeasons());
        contentDatum.setPermalink(this.getPermalink());
        contentDatum.setId(this.getId());

        return contentDatum;
    }
}
