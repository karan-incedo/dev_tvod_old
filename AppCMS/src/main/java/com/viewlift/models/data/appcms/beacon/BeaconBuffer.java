package com.viewlift.models.data.appcms.beacon;

import com.google.android.exoplayer2.ExoPlayer;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.VideoPlayerView;

public class BeaconBuffer extends Thread {
    public AppCMSPresenter appCMSPresenter;
    public String filmId;
    public String permaLink;
    public VideoPlayerView videoPlayerView;
    public boolean runBeaconBuffering;
    public boolean sendBeaconBuffering;
    private long beaconBufferTimeoutMsec;
    private String parentScreenName;
    private String streamId;
    private int bufferCount = 0;
    ContentDatum contentDatum;

    public BeaconBuffer(long beaconBufferTimeoutMsec,
                        AppCMSPresenter appCMSPresenter,
                        String filmId,
                        String permaLink,
                        String parentScreenName,
                        VideoPlayerView videoPlayerView,
                        String streamId,
                        ContentDatum contentDatum) {
        this.beaconBufferTimeoutMsec = beaconBufferTimeoutMsec;
        this.appCMSPresenter = appCMSPresenter;
        this.filmId = filmId;
        this.permaLink = permaLink;
        this.parentScreenName = parentScreenName;
        this.videoPlayerView = videoPlayerView;
        this.streamId = streamId;
        this.contentDatum = contentDatum;
    }

    @Override
    public void run() {
        runBeaconBuffering = true;
        while (runBeaconBuffering) {
            try {
                Thread.sleep(beaconBufferTimeoutMsec);
                if (sendBeaconBuffering) {

                    if (appCMSPresenter != null && videoPlayerView != null &&
                            videoPlayerView.getPlayer().getPlayWhenReady() &&
                            videoPlayerView.getPlayer().getPlaybackState() == ExoPlayer.STATE_BUFFERING) {

                        bufferCount++;

                        if (bufferCount >= 5) {
                            appCMSPresenter.sendBeaconMessage(filmId,
                                    permaLink,
                                    parentScreenName,
                                    videoPlayerView.getCurrentPosition(),
                                    false,
                                    AppCMSPresenter.BeaconEvent.BUFFERING,
                                    "Video",
                                    videoPlayerView.getBitrate() != 0 ?
                                            String.valueOf(videoPlayerView.getBitrate()) : null,
                                    String.valueOf(videoPlayerView.getVideoHeight()),
                                    String.valueOf(videoPlayerView.getVideoWidth()),
                                    streamId,
                                    0d,
                                    0,
                                    appCMSPresenter.isVideoDownloaded(filmId));
                            bufferCount = 0;
                        }
                    }

                    if (appCMSPresenter!=null && appCMSPresenter.getCurrentActivity()!=null && contentDatum != null &&
                            contentDatum.getGist() != null &&contentDatum.getGist().getMediaType()!=null&&
                            contentDatum.getGist().getMediaType().toLowerCase().contains(appCMSPresenter.getCurrentActivity().getString(R.string.media_type_audio).toLowerCase()) &&
                            contentDatum.getGist().getContentType() != null &&
                            contentDatum.getGist().getContentType().toLowerCase().contains(appCMSPresenter.getCurrentActivity().getString(R.string.content_type_audio).toLowerCase())) {
                        bufferCount++;

                        if (bufferCount >= 5) {
                            appCMSPresenter.sendBeaconMessage(contentDatum.getGist().getId(),
                                    contentDatum.getGist().getPermalink(),
                                    null,
                                    contentDatum.getGist().getCurrentPlayingPosition(),
                                    contentDatum.getGist().getCastingConnected(),
                                    AppCMSPresenter.BeaconEvent.BUFFERING,
                                    contentDatum.getGist().getMediaType(),
                                    null,
                                    null,
                                    null,
                                    streamId,
                                    0d,
                                    0,
                                    appCMSPresenter.isVideoDownloaded(contentDatum.getGist().getId()));
                            bufferCount = 0;
                        }
                    }
                }
            } catch (InterruptedException e) {
                //Log.e(TAG, "beaconBufferingThread sleep interrupted");
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