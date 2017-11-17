package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.ContentDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.VideoPlayerView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_READY;

/**
 * Created by viewlift on 5/31/17.
 */

public class CustomVideoVideoPlayerView extends VideoPlayerView{

    private Context mContext;
    private AppCMSPresenter appCMSPresenter;
    public CustomVideoVideoPlayerView(Context context ) {
        super(context);
        mContext = context;
        appCMSPresenter = ((AppCMSApplication)mContext.getApplicationContext()).getAppCMSPresenterComponent().appCMSPresenter();
        createLoader();
    }


    int currentPlayingIndex = 0;
    List<String> relatedVideoId;
    public void setVideoUri(String videoId){
        showProgressBar("Loading...");
        appCMSPresenter.refreshVideoData(videoId, new Action1<ContentDatum>() {
            @Override
            public void call(ContentDatum contentDatum) {
                String url = null;
                if(null != contentDatum && null != contentDatum.getStreamingInfo()&& null != contentDatum.getStreamingInfo().getVideoAssets()){
                    if(null != contentDatum.getStreamingInfo().getVideoAssets().getHls()){
                        url = contentDatum.getStreamingInfo().getVideoAssets().getHls();
                    }else if(null != contentDatum.getStreamingInfo().getVideoAssets().getMpeg()
                            && contentDatum.getStreamingInfo().getVideoAssets().getMpeg().size() > 0){
                        url = contentDatum.getStreamingInfo().getVideoAssets().getMpeg().get(0).getUrl();
                    }
                }
                if(null != url) {
                    setUri(Uri.parse(url), null);
                    getPlayerView().getPlayer().setPlayWhenReady(true);
                    relatedVideoId = contentDatum.getContentDetails().getRelatedVideoIds();
                    currentPlayingIndex = 0;
                    hideProgressBar();
                }
            }
        });
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch(playbackState){
            case STATE_ENDED:
                getPlayerView().getPlayer().setPlayWhenReady(false);
                if(null != relatedVideoId && currentPlayingIndex <= relatedVideoId.size()-1){
                    showProgressBar("Loading Next Video...");
                    appCMSPresenter.refreshVideoData(relatedVideoId.get(currentPlayingIndex), new Action1<ContentDatum>() {
                        @Override
                        public void call(ContentDatum contentDatum) {
                            setUri(Uri.parse(contentDatum.getStreamingInfo().getVideoAssets().getHls()),null);
                            getPlayerView().getPlayer().setPlayWhenReady(true);
                            hideProgressBar();
                        }
                    });
                }
                break;
            case STATE_BUFFERING:
                showProgressBar("buffering...");
                break;
            case STATE_READY:
                hideProgressBar();
                break;
            default:
                hideProgressBar();
        }
    }

    public void pausePlayer(){
     super.pausePlayer();
     super.releasePlayer();
    }


    public void resumePlayer(){
        if(null != getPlayer() && !getPlayer().getPlayWhenReady()){
            getPlayer().setPlayWhenReady(true);
        }
    }


    LinearLayout linearLayout;
    TextView textView;
    private void createLoader(){
        linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        ProgressBar progressBar = new ProgressBar(mContext);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().
                setColorFilter(Color.parseColor(Utils.getFocusColor(mContext, appCMSPresenter)),
                        PorterDuff.Mode.MULTIPLY
                );
        LinearLayout.LayoutParams progressbarParam = new LinearLayout.LayoutParams(50,50);
        progressBar.setLayoutParams(progressbarParam);
        linearLayout.addView(progressBar);
        textView = new TextView(mContext);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(textViewParams);
        linearLayout.addView(textView);
        this.addView(linearLayout);
    }


    private void showProgressBar(String text){
        if(null != linearLayout && null != textView) {
            textView.setText(text);
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar(){
        if(null != linearLayout){
            linearLayout.setVisibility(View.INVISIBLE);
        }
    }

}
