package com.viewlift.models.data.appcms.beacon;

import com.google.android.exoplayer2.ExoPlayer;
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
    }

    @Override
    public void run() {
        runBeaconPing = true;
        while (runBeaconPing) {
            try {
                Thread.sleep(beaconMsgTimeoutMsec);
                if (sendBeaconPing) {
                    long currentTime = 0;
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
                        if (currentTime == (int) ((videoPlayerView.getDuration() / 1000) * 0.25)) {
                            appCMSPresenter.sendWatchedEvent(contentDatum, currentTime, "25", bufferCount, bufferTime);
                            bufferCount = 0;
                            bufferTime = 0;
                        }
                        if (currentTime == (int) ((videoPlayerView.getDuration() / 1000) * 0.5)) {
                            appCMSPresenter.sendWatchedEvent(contentDatum, currentTime, "50", bufferCount, bufferTime);
                            bufferCount = 0;
                            bufferTime = 0;
                        }
                        if (currentTime == (int) ((videoPlayerView.getDuration() / 1000) * 0.75)) {
                            appCMSPresenter.sendWatchedEvent(contentDatum, currentTime, "75", bufferCount, bufferTime);
                            bufferCount = 0;
                            bufferTime = 0;
                        }
                        if (currentTime == (int) ((videoPlayerView.getDuration() / 1000) * 0.8))
                            appCMSPresenter.sendWatchedEvent(contentDatum, currentTime, "80", bufferCount, bufferTime);
                        if (currentTime == 120)
                            appCMSPresenter.sendWatchedEvent(contentDatum, currentTime, "2mins", bufferCount, bufferTime);
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
                                    streamId,
                                    0d,
                                    0,
                                    appCMSPresenter.isVideoDownloaded(contentDatum.getGist().getId()));
                        }

                        if (currentTime == (int) (contentDatum.getGist().getRuntime() * 0.25)) {
                            appCMSPresenter.sendWatchedEvent(contentDatum, currentTime, "25", bufferCount, bufferTime);
                            bufferCount = 0;
                            bufferTime = 0;
                        }
                        if (currentTime == (int) (contentDatum.getGist().getRuntime() * 0.5)) {
                            appCMSPresenter.sendWatchedEvent(contentDatum, currentTime, "50", bufferCount, bufferTime);
                            bufferCount = 0;
                            bufferTime = 0;
                        }
                        if (currentTime == (int) (contentDatum.getGist().getRuntime() * 0.75)) {
                            appCMSPresenter.sendWatchedEvent(contentDatum, currentTime, "75", bufferCount, bufferTime);
                            bufferCount = 0;
                            bufferTime = 0;
                        }
                        if (currentTime == (int) (contentDatum.getGist().getRuntime() * 0.8))
                            appCMSPresenter.sendWatchedEvent(contentDatum, currentTime, "80", bufferCount, bufferTime);
                        if (currentTime == 120)
                            appCMSPresenter.sendWatchedEvent(contentDatum, currentTime, "2mins", bufferCount, bufferTime);

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