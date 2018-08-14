package com.viewlift.models.data.appcms.beacon;

import com.google.android.exoplayer2.Player;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.VideoPlayerView;

public class BeaconPing extends Thread {
    public AppCMSPresenter appCMSPresenter;
    public String filmId;
    public String permaLink;
    public VideoPlayerView videoPlayerView;
    public boolean runBeaconPing;
    public boolean sendBeaconPing;
    public boolean isTrailer;
    public int playbackState;
    private long beaconMsgTimeoutMsec;
    private String parentScreenName;
    private String streamId;
    private long liveSeekCounter;
    private static final long MILLISECONDS_PER_SECOND = 1L;
    ContentDatum contentDatum;
    int bufferCount;
    int bufferTime;
    private String lastPlayType = "";
    boolean sent2MinEvent, sent25pctEvent, sent50pctEvent, sent75pctEvent, sent80pctEvent, sent30secMusicEvent;
    long lastEventStreamTime;
    long lastEventStreamTimeMusic;

    public BeaconPing(long beaconMsgTimeoutMsec,
                      AppCMSPresenter appCMSPresenter,
                      String filmId,
                      String permaLink,
                      boolean isTrailer,
                      String parentScreenName,
                      VideoPlayerView videoPlayerView,
                      String streamId,
                      ContentDatum contentDatum) {
        this.beaconMsgTimeoutMsec = beaconMsgTimeoutMsec;
        this.appCMSPresenter = appCMSPresenter;
        this.filmId = filmId;
        this.permaLink = permaLink;
        this.parentScreenName = parentScreenName;
        this.videoPlayerView = videoPlayerView;
        this.isTrailer = isTrailer;
        this.streamId = streamId;
        this.contentDatum = contentDatum;
        this.liveSeekCounter = MILLISECONDS_PER_SECOND;
        sent2MinEvent = false;
        sent25pctEvent = false;
        sent50pctEvent = false;
        sent75pctEvent = false;
        sent80pctEvent = false;
        lastEventStreamTime = 0;
    }

    @Override
    public void run() {
        runBeaconPing = true;
        while (runBeaconPing) {
            try {
                Thread.sleep(beaconMsgTimeoutMsec);
                if (sendBeaconPing) {
                    long currentTime = 0;
                    if (videoPlayerView != null)
                        currentTime = videoPlayerView.getCurrentPosition() / 1000;
                    if (videoPlayerView != null && contentDatum != null &&
                            contentDatum.getStreamingInfo() != null &&
                            !contentDatum.getStreamingInfo().getIsLiveStream()) {
                        currentTime = videoPlayerView.getCurrentPosition() / 1000;
                    } else if (contentDatum != null &&
                            contentDatum.getStreamingInfo() != null &&
                            contentDatum.getStreamingInfo().getIsLiveStream()) {
                        liveSeekCounter += MILLISECONDS_PER_SECOND;
                        currentTime = liveSeekCounter;

                    }
                    if (appCMSPresenter != null && videoPlayerView != null
                            && videoPlayerView.getPlayer().getPlaybackState() == ExoPlayer.STATE_READY) {

                        if (30 <= currentTime &&
                                currentTime % 30 == 0) {
                            if (contentDatum != null && contentDatum.getMediaType() == null) {
                                contentDatum.setMediaType("video");
                            }
                            //Log.d(TAG, "Beacon Message Request position: " + currentTime);

                            appCMSPresenter.sendBeaconMessage(filmId,
                                    permaLink,
                                    parentScreenName,
                                    currentTime,
                                    false,
                                    AppCMSPresenter.BeaconEvent.PING,
                                    contentDatum != null ? contentDatum.getMediaType() : "Video",
                                    videoPlayerView.getBitrate() != 0 ?
                                            String.valueOf(videoPlayerView.getBitrate()) : null,
                                    String.valueOf(videoPlayerView.getVideoHeight()),
                                    String.valueOf(videoPlayerView.getVideoWidth()),
                                    streamId,
                                    0d,
                                    0,
                                    appCMSPresenter.isVideoDownloaded(filmId));
                        }
                        if (currentTime % 30 == 0) {
                            if (!isTrailer && videoPlayerView != null) {
                                appCMSPresenter.updateWatchedTime(filmId,
                                        videoPlayerView.getCurrentPosition() / 1000);
                            }
                        }
                        if (currentTime == 120) {
                            lastEventStreamTime = currentTime;
                            appCMSPresenter.sendWatchedEvent(contentDatum, lastEventStreamTime, "2mins", bufferCount, bufferTime);
                            sent2MinEvent = true;
                        }
                        if (currentTime == (int) ((videoPlayerView.getDuration() / 1000) * 0.25)) {
                            lastEventStreamTime = currentTime - lastEventStreamTime;
                            appCMSPresenter.sendWatchedEvent(contentDatum, lastEventStreamTime, "25", bufferCount, bufferTime);
                            bufferCount = 0;
                            bufferTime = 0;
                            sent2MinEvent = true;
                            sent25pctEvent = true;
                        }
                        if (currentTime == (int) ((videoPlayerView.getDuration() / 1000) * 0.5)) {
                            lastEventStreamTime = currentTime - lastEventStreamTime;
                            appCMSPresenter.sendWatchedEvent(contentDatum, lastEventStreamTime, "50", bufferCount, bufferTime);
                            bufferCount = 0;
                            bufferTime = 0;
                            sent25pctEvent = true;
                            sent50pctEvent = true;
                        }
                        if (currentTime == (int) ((videoPlayerView.getDuration() / 1000) * 0.75)) {
                            lastEventStreamTime = currentTime - lastEventStreamTime;
                            appCMSPresenter.sendWatchedEvent(contentDatum, lastEventStreamTime, "75", bufferCount, bufferTime);
                            bufferCount = 0;
                            bufferTime = 0;
                            sent50pctEvent = true;
                            sent75pctEvent = true;
                        }
                        if (currentTime == (int) ((videoPlayerView.getDuration() / 1000) * 0.8)) {
                            lastEventStreamTime = currentTime - lastEventStreamTime;
                            appCMSPresenter.sendWatchedEvent(contentDatum, lastEventStreamTime, "80", bufferCount, bufferTime);
                            sent75pctEvent = true;
                            sent80pctEvent = true;
                        }

                        if (!sent2MinEvent && currentTime > 120 && currentTime < (int) ((videoPlayerView.getDuration() / 1000) * 0.25)) {
                            lastEventStreamTime = currentTime - lastEventStreamTime;
                            appCMSPresenter.sendWatchedEvent(contentDatum, lastEventStreamTime, "2mins", bufferCount, bufferTime);
                            sent2MinEvent = true;
                        }
                        if (!sent25pctEvent && currentTime > (int) ((videoPlayerView.getDuration() / 1000) * 0.25) && currentTime < (int) ((videoPlayerView.getDuration() / 1000) * 0.5)) {
                            lastEventStreamTime = currentTime - lastEventStreamTime;
                            appCMSPresenter.sendWatchedEvent(contentDatum, lastEventStreamTime, "25", bufferCount, bufferTime);
                            sent2MinEvent = true;
                            sent25pctEvent = true;
                        }
                        if (!sent50pctEvent && currentTime > (int) ((videoPlayerView.getDuration() / 1000) * 0.5) && currentTime < (int) ((videoPlayerView.getDuration() / 1000) * 0.5)) {
                            lastEventStreamTime = currentTime - lastEventStreamTime;
                            appCMSPresenter.sendWatchedEvent(contentDatum, lastEventStreamTime, "50", bufferCount, bufferTime);
                            sent25pctEvent = true;
                            sent50pctEvent = true;
                        }
                        if (!sent75pctEvent && currentTime > (int) ((videoPlayerView.getDuration() / 1000) * 0.75) && currentTime < (int) ((videoPlayerView.getDuration() / 1000) * 0.8)) {
                            lastEventStreamTime = currentTime - lastEventStreamTime;
                            appCMSPresenter.sendWatchedEvent(contentDatum, lastEventStreamTime, "75", bufferCount, bufferTime);
                            sent50pctEvent = true;
                            sent75pctEvent = true;
                        }
                        if (!sent80pctEvent && currentTime > (int) ((videoPlayerView.getDuration() / 1000) * 0.8)) {
                            lastEventStreamTime = currentTime - lastEventStreamTime;
                            appCMSPresenter.sendWatchedEvent(contentDatum, lastEventStreamTime, "80", bufferCount, bufferTime);
                            sent75pctEvent = true;
                            sent80pctEvent = true;
                        }

                    }
                    if (appCMSPresenter != null && appCMSPresenter.getCurrentActivity() != null && contentDatum != null &&
                            contentDatum.getGist() != null && contentDatum.getGist().getMediaType() != null &&
                            contentDatum.getGist().getMediaType().toLowerCase().contains(appCMSPresenter.getCurrentActivity().getString(R.string.media_type_audio).toLowerCase()) &&
                            contentDatum.getGist().getContentType() != null &&
                            contentDatum.getGist().getContentType().toLowerCase().contains(appCMSPresenter.getCurrentActivity().getString(R.string.content_type_audio).toLowerCase())) {
                        currentTime = contentDatum.getGist().getCurrentPlayingPosition() / 1000;
                        if (30 <= currentTime
                                && currentTime % 30 == 0) {
                            appCMSPresenter.sendBeaconMessage(contentDatum.getGist().getId(),
                                    contentDatum.getGist().getPermalink(),
                                    null,
                                    currentTime,
                                    contentDatum.getGist().getCastingConnected(),
                                    AppCMSPresenter.BeaconEvent.PING,
                                    contentDatum.getGist().getMediaType(),
                                    null,
                                    null,
                                    null,
                                    getStreamId(),
                                    0d,
                                    0,
                                    appCMSPresenter.isVideoDownloaded(contentDatum.getGist().getId()));
                        }

                        if (currentTime == 30) {
                            appCMSPresenter.sendWatchedEvent(contentDatum, currentTime, "30", bufferCount, bufferTime);
                            sent30secMusicEvent = true;
                        }

                        if (!sent30secMusicEvent && currentTime > 30) {
                            appCMSPresenter.sendWatchedEvent(contentDatum, currentTime, "30", bufferCount, bufferTime);
                            sent30secMusicEvent = true;
                        }

                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                //Log.e(TAG, "BeaconPingThread sleep interrupted");
            } catch (InterruptedException e) {
                e.printStackTrace();
                //Log.e(TAG, "BeaconPingThread sleep interrupted");
            }
        }
    }

    public void setBeaconData(String videoId, String permaLink, String streamId) {
        this.filmId = videoId;
        this.permaLink = permaLink;
        this.streamId = streamId;
        this.liveSeekCounter = MILLISECONDS_PER_SECOND;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }

    public void setPermaLink(String permaLink) {
        this.permaLink = permaLink;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }
    public String getStreamId() {
        return streamId;
    }

    public void setContentDatum(ContentDatum contentDatum) {
        this.contentDatum = contentDatum;
    }

    public void setBufferCount(int bufferCount) {
        this.bufferCount = bufferCount;
    }

    public void setBufferTime(int bufferTime) {
        this.bufferTime = bufferTime;
    }
}