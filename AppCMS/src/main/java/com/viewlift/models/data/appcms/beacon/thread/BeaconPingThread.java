package com.viewlift.models.data.appcms.beacon.thread;

import com.google.android.exoplayer2.ExoPlayer;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.VideoPlayerView;

/**
 * Created by Sandeep.Singh on 12/1/17.
 */

public class BeaconPingThread extends Thread {
    final long beaconMsgTimeoutMsec;
    final AppCMSPresenter appCMSPresenter;
    final String filmId;
    final String permaLink;
    final String parentScreenName;
    final String mStreamId;
    VideoPlayerView videoPlayerView;
    boolean runBeaconPing;
    public boolean sendBeaconPing;
    boolean isVideoDownloaded;
    boolean isTrailer;
    public int playbackState;


    public BeaconPingThread(long beaconMsgTimeoutMsec,
                            AppCMSPresenter appCMSPresenter,
                            String filmId,
                            String permaLink,
                            boolean isTrailer,
                            String parentScreenName,
                            VideoPlayerView videoPlayerView,
                            String mStreamId) {
        this.beaconMsgTimeoutMsec = beaconMsgTimeoutMsec;
        this.appCMSPresenter = appCMSPresenter;
        this.filmId = filmId;
        this.permaLink = permaLink;
        this.parentScreenName = parentScreenName;
        this.videoPlayerView = videoPlayerView;
        this.isTrailer = isTrailer;
        this.mStreamId = mStreamId;
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
                            && playbackState == ExoPlayer.STATE_READY && currentTime % 30 == 0) { // For not to sent PIN in PAUSE mode

                        //Log.d(TAG, "Beacon Message Request position: " + currentTime);

                        appCMSPresenter.sendBeaconMessage(filmId,
                                permaLink,
                                parentScreenName,
                                videoPlayerView.getCurrentPosition(),
                                false,
                                AppCMSPresenter.BeaconEvent.PING,
                                "Video",
                                videoPlayerView.getBitrate() != 0 ? String.valueOf(videoPlayerView.getBitrate()) : null,
                                String.valueOf(videoPlayerView.getVideoHeight()),
                                String.valueOf(videoPlayerView.getVideoWidth()),
                                mStreamId,
                                0d,
                                0,
                                isVideoDownloaded);

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