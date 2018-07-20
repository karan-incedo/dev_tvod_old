package com.viewlift.models.data.appcms.downloads;

import android.text.TextUtils;

import com.viewlift.models.data.appcms.api.ClosedCaptions;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.ContentDetails;
import com.viewlift.models.data.appcms.api.Gist;

import java.util.ArrayList;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sandeep.singh on 7/18/2017.
 */


public class DownloadVideoRealm extends RealmObject {

    @PrimaryKey
    private String videoIdDB;
    private String videoId;
    private long videoId_DM;
    private long videoThumbId_DM;
    private long posterThumbId_DM;
    private long subtitlesId_DM;
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
    private String videoImageUrl;
    private String posterFileURL;
    private String subtitlesFileURL;
    private String userId;
    private long watchedTime;
    private boolean isSyncedWithServer;
    public String contentType;
    public String mediaType;

    public String artistName;
    public String directorName;
    public String songYear;
    private String playListName;

    String episodeNum;
    String showName;
    String seasonNum;

    public String getEpisodeNum() {
        return episodeNum;
    }

    public void setEpisodeNum(String episodeNum) {
        this.episodeNum = episodeNum;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getSeasonNum() {
        return seasonNum;
    }

    public void setSeasonNum(String seasonNum) {
        this.seasonNum = seasonNum;
    }

    public String getPlayListName() {
        return playListName;
    }

    public void setPlayListName(String playListName) {
        this.playListName = playListName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public String getSongYear() {
        return songYear;
    }

    public void setSongYear(String songYear) {
        this.songYear = songYear;
    }



    public String getVideoIdDB() {
        return videoIdDB;
    }
    public void setVideoIdDB(String videoIdDB) {
        this.videoIdDB = videoIdDB;
    }

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

    public long getPosterThumbId_DM() {
        return posterThumbId_DM;
    }

    public void setPosterThumbId_DM(long posterThumbId_DM) {
        this.posterThumbId_DM = posterThumbId_DM;
    }

    public long getSubtitlesId_DM() {
        return subtitlesId_DM;
    }

    public void setSubtitlesId_DM(long subtitlesId_DM) {
        this.subtitlesId_DM = subtitlesId_DM;
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

    public String getPosterFileURL() {
        return posterFileURL;
    }

    public void setPosterFileURL(String posterFileURL) {
        this.posterFileURL = posterFileURL;
    }

    public String getSubtitlesFileURL() {
        return subtitlesFileURL;
    }

    public void setSubtitlesFileURL(String subtitlesFileURL) {
        this.subtitlesFileURL = subtitlesFileURL;
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

    public String getVideoImageUrl() {
        return videoImageUrl;
    }

    public void setVideoImageUrl(String videoImageUrl) {
        this.videoImageUrl = videoImageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getWatchedTime() {
        return watchedTime;
    }

    public void setWatchedTime(long watchedTime) {
        this.watchedTime = watchedTime;
    }

    public boolean isSyncedWithServer() {
        return isSyncedWithServer;
    }

    public void setSyncedWithServer(boolean syncedWithServer) {
        isSyncedWithServer = syncedWithServer;
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

    public ContentDatum convertToContentDatum(String userId) {
        ContentDatum data = new ContentDatum();
        Gist gist = new Gist();
        gist.setId(getVideoId());
        gist.setTitle(getVideoTitle());
        gist.setDescription(getVideoDescription());
        gist.setPosterImageUrl(getPosterFileURL());
        gist.setVideoImageUrl(getVideoFileURL());
        gist.setYear(getSongYear());
        gist.setArtistName(getArtistName());
        gist.setDirectorName(getDirectorName());
        gist.setLocalFileUrl(getLocalURI());

        if (!TextUtils.isEmpty(getSubtitlesFileURL())) {
            ClosedCaptions closedCaption = new ClosedCaptions();
            closedCaption.setUrl(getSubtitlesFileURL());
            ArrayList<ClosedCaptions> closedCaptions = new ArrayList<>();
            closedCaptions.add(closedCaption);
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setClosedCaptions(closedCaptions);
            data.setContentDetails(contentDetails);
        }
        gist.setPermalink(getPermalink());
        gist.setDownloadStatus(getDownloadStatus());
        gist.setRuntime(getVideoDuration());

        gist.setWatchedTime(getWatchedTime());

        data.setGist(gist);
        data.setShowQueue(true);
        data.setUserId(userId);
        data.setAddedDate(getDownloadDate());
        gist.setContentType(getContentType());
        gist.setMediaType(getMediaType());
        gist.setEpisodeNum(getEpisodeNum());
        gist.setShowName(getShowName());
        gist.setSeasonNum(getSeasonNum());
        return data;
    }
    public DownloadVideoRealm createCopy(){
        DownloadVideoRealm downloadVideoRealm = new DownloadVideoRealm();
        downloadVideoRealm.setEpisodeNum(getEpisodeNum());
        downloadVideoRealm.setShowName(getShowName());
        downloadVideoRealm.setSeasonNum(getSeasonNum());
        downloadVideoRealm.setVideoId(getVideoId());
        downloadVideoRealm.setDownloadStatus(getDownloadStatus());
        downloadVideoRealm.setSyncedWithServer(isSyncedWithServer);
        downloadVideoRealm.setVideoId_DM(getVideoId_DM());
        downloadVideoRealm.setVideoDuration(getVideoDuration());
        downloadVideoRealm.setVideo_Downloaded_so_far(getVideo_Downloaded_so_far());
        downloadVideoRealm.setVideoFileURL(getVideoFileURL());
        downloadVideoRealm.setVideoSize(getVideoSize());
        downloadVideoRealm.setVideoImageUrl(getVideoImageUrl());
        downloadVideoRealm.setVideoWebURL(getVideoWebURL());
        downloadVideoRealm.setVideoDescription(getVideoDescription());
        downloadVideoRealm.setVideoTitle(getVideoTitle());
        downloadVideoRealm.setVideoNumber(getVideoNumber());
        downloadVideoRealm.setVideoPlayedDuration(getVideoPlayedDuration());
        downloadVideoRealm.setVideoThumbId_DM(getVideoThumbId_DM());
        downloadVideoRealm.setShowId(getShowId());
        downloadVideoRealm.setShowDescription(getShowDescription());
        downloadVideoRealm.setShowTitle(getShowTitle());
        downloadVideoRealm.setSubtitlesFileURL(getSubtitlesFileURL());
        downloadVideoRealm.setSubtitlesId_DM(getSubtitlesId_DM());
        downloadVideoRealm.setWatchedTime(getWatchedTime());
        downloadVideoRealm.setBitRate(getBitRate());
        downloadVideoRealm.setLastWatchDate(getLastWatchDate());
        downloadVideoRealm.setDownloadDate(getDownloadDate());
        downloadVideoRealm.setLocalURI(getLocalURI());
        downloadVideoRealm.setUserId(getUserId());
        downloadVideoRealm.setPermalink(getPermalink());
        downloadVideoRealm.setPosterFileURL(getPosterFileURL());
        downloadVideoRealm.setPosterThumbId_DM(getPosterThumbId_DM());
        downloadVideoRealm.setContentType(getContentType());
        downloadVideoRealm.setMediaType(getMediaType());
        return downloadVideoRealm;
    }
}