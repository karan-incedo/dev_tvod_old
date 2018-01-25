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
    ContentDatum contentDatum;

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
    }

    @Override
    public void run() {
        runBeaconPing = true;
        while (runBeaconPing) {
            try {
                Thread.sleep(beaconMsgTimeoutMsec);
                if (sendBeaconPing) {
                    long currentTime = 0;
                    if (videoPlayerView != null) {
                        currentTime = videoPlayerView.getCurrentPosition() / 1000;
                    }
                    if (appCMSPresenter != null && videoPlayerView != null
                            && 30 <= (videoPlayerView.getCurrentPosition() / 1000)
                            && playbackState == ExoPlayer.STATE_READY && currentTime % 30 == 0) {

                        //Log.d(TAG, "Beacon Message Request position: " + currentTime);
                        appCMSPresenter.sendBeaconMessage(filmId,
                                permaLink,
                                parentScreenName,
                                videoPlayerView.getCurrentPosition(),
                                false,
                                AppCMSPresenter.BeaconEvent.PING,
                                contentDatum.getMediaType(),
                                videoPlayerView.getBitrate() != 0 ?
                                        String.valueOf(videoPlayerView.getBitrate()) : null,
                                String.valueOf(videoPlayerView.getVideoHeight()),
                                String.valueOf(videoPlayerView.getVideoWidth()),
                                streamId,
                                0d,
                                0,
                                appCMSPresenter.isVideoDownloaded(filmId));

                        if (!isTrailer && videoPlayerView != null) {
                            appCMSPresenter.updateWatchedTime(filmId,
                                    videoPlayerView.getCurrentPosition() / 1000);
                        }
                    }
                    if (contentDatum != null &&
                            contentDatum.getAudioGist() != null &&
                            contentDatum.getAudioGist().getMediaType().toLowerCase().contains(appCMSPresenter.getCurrentActivity().getString(R.string.media_type_audio).toLowerCase()) &&
                            contentDatum.getAudioGist().getContentType() != null &&
                            contentDatum.getAudioGist().getContentType().toLowerCase().contains(appCMSPresenter.getCurrentActivity().getString(R.string.content_type_audio).toLowerCase())) {
                        appCMSPresenter.sendBeaconMessage(contentDatum.getAudioGist().getId(),
                                contentDatum.getAudioGist().getPermalink(),
                                null,
                                contentDatum.getAudioGist().getCurrentPlayingPosition(),
                                contentDatum.getAudioGist().getCastingConnected(),
                                AppCMSPresenter.BeaconEvent.PING,
                                contentDatum.getAudioGist().getMediaType(),
                                null,
                                null,
                                null,
                                streamId,
                                0d,
                                0,
                                appCMSPresenter.isVideoDownloaded(contentDatum.getAudioGist().getId()));
                    }
                }
            } catch (InterruptedException e) {
                //Log.e(TAG, "BeaconPingThread sleep interrupted");
            }
        }
    }

    public void setBeaconData(String videoId, String permaLink, String streamId) {
        this.filmId = videoId;
        this.permaLink = permaLink;
        this.streamId = streamId;
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
}