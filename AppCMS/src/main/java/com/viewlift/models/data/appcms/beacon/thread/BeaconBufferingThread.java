package com.viewlift.models.data.appcms.beacon.thread;

import com.google.android.exoplayer2.ExoPlayer;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.VideoPlayerView;

/**
 * Created by Sandeep.Singh on 12/1/17.
 */

public class BeaconBufferingThread extends Thread {
    final long beaconBufferTimeoutMsec;
    final AppCMSPresenter appCMSPresenter;
    final String filmId;
    final String permaLink;
    final String parentScreenName;
    final String mStreamId;
    VideoPlayerView videoPlayerView;
    boolean runBeaconBuffering;
    boolean isVideoDownloaded;
    public boolean sendBeaconBuffering;
    int bufferCount = 0;


    public BeaconBufferingThread(long beaconBufferTimeoutMsec,
                                 AppCMSPresenter appCMSPresenter,
                                 String filmId,
                                 String permaLink,
                                 String parentScreenName,
                                 VideoPlayerView videoPlayerView,
                                 String mStreamId) {
        this.beaconBufferTimeoutMsec = beaconBufferTimeoutMsec;
        this.appCMSPresenter = appCMSPresenter;
        this.filmId = filmId;
        this.permaLink = permaLink;
        this.parentScreenName = parentScreenName;
        this.videoPlayerView = videoPlayerView;
        this.mStreamId = mStreamId;
    }

    public void run() {
        runBeaconBuffering = true;
        while (runBeaconBuffering) {
            try {
                Thread.sleep(beaconBufferTimeoutMsec);
                if (sendBeaconBuffering) {

                    if (appCMSPresenter != null && videoPlayerView != null &&
                            videoPlayerView.getPlayer().getPlayWhenReady() &&
                            videoPlayerView.getPlayer().getPlaybackState() == ExoPlayer.STATE_BUFFERING) { // For not to sent PIN in PAUSE mode
                        bufferCount++;
                        if (bufferCount >= 5) {

                            appCMSPresenter.sendBeaconMessage(filmId,
                                    permaLink,
                                    parentScreenName,
                                    videoPlayerView.getCurrentPosition(),
                                    false,
                                    AppCMSPresenter.BeaconEvent.BUFFERING,
                                    "Video",
                                    videoPlayerView.getBitrate() != 0 ? String.valueOf(videoPlayerView.getBitrate()) : null,
                                    String.valueOf(videoPlayerView.getVideoHeight()),
                                    String.valueOf(videoPlayerView.getVideoWidth()),
                                    mStreamId,
                                    0d,
                                    0,
                                    isVideoDownloaded);
                            bufferCount = 0;

                        }

                    }
                }
            } catch (InterruptedException e) {
                //Log.e(TAG, "beaconBufferingThread sleep interrupted");
            }
        }
    }
}