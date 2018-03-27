package com.viewlift.models.data.appcms.beacon;

import com.google.android.exoplayer2.ExoPlayer;
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
    }

    @Override
    public void run() {
        runBeaconPing = true;
        while (runBeaconPing) {
            try {
                Thread.sleep(beaconMsgTimeoutMsec);
                if (sendBeaconPing) {

                    long currentTime = videoPlayerView.getCurrentPosition() / 1000;
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
                                "Video",
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
                }
            } catch (InterruptedException e) {
                //Log.e(TAG, "BeaconPingThread sleep interrupted");
            }
        }
    }
}