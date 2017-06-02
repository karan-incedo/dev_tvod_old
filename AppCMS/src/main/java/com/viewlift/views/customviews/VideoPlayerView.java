package com.viewlift.views.customviews;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;

import rx.functions.Action1;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/31/17.
 */

public class VideoPlayerView extends BaseView {
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private final Component component;
    private final Uri uri;
    private SimpleExoPlayer player;
    protected DataSource.Factory mediaDataSourceFactory;
    protected String userAgent;

    public VideoPlayerView(Context context, Component component, Uri uri) {
        super(context);
        this.component = component;
        this.uri = uri;
        init();
    }

    @Override
    protected void init() {
        int width = getViewWidth(getContext(), component.getLayout(), LayoutParams.MATCH_PARENT);
        int height = getViewHeight(getContext(), component.getLayout(), LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(width, height);
        setLayoutParams(layoutParams);
        initializePlayer();
        onLifecycleChangeHandler = new Action1<LifecycleStatus>() {
            @Override
            public void call(LifecycleStatus lifecycleStatus) {
                switch (lifecycleStatus) {
                    case CREATE:
                        break;
                    case START:
                        break;
                    case RESUME:
                        break;
                    case PAUSE:
                        break;
                    case STOP:
                        break;
                    case DESTROY:
                        break;
                    default:
                }
            }
        };
    }

    @Override
    protected Component getChildComponent(int index) {
        return null;
    }

    @Override
    protected Layout getLayout() {
        return component.getLayout();
    }

    @Override
    protected ViewGroup createChildrenContainer() {
        childrenContainer = new SimpleExoPlayerView(getContext());
        int viewWidth = getViewWidth(getContext(), getLayout(), LayoutParams.MATCH_PARENT);
        int viewHeight = getViewHeight(getContext(), getLayout(), LayoutParams.MATCH_PARENT);
        FrameLayout.LayoutParams childContainerLayoutParams =
                new FrameLayout.LayoutParams(viewWidth, viewHeight);
        childrenContainer.setLayoutParams(childContainerLayoutParams);
        addView(childrenContainer);
        return childrenContainer;
    }

    private void initializePlayer() {
        createChildrenContainer();
        userAgent = Util.getUserAgent(getContext(),
                getContext().getString(R.string.app_cms_user_agent));
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        ((SimpleExoPlayerView) childrenContainer).setPlayer(player);
        player.prepare(buildMediaSource(uri,
                getContext().getString(R.string.app_cms_default_video_ext)));
    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri) :
                Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri,
                        buildDataSourceFactory(false),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                        null,
                        null);
            case C.TYPE_DASH:
                return new DashMediaSource(uri,
                        buildDataSourceFactory(false),
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                        null,
                        null);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri,
                        mediaDataSourceFactory,
                        null,
                        null);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri,
                        mediaDataSourceFactory,
                        new DefaultExtractorsFactory(),
                        null,
                        null);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(getContext(),
                bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }
}
