package com.viewlift.views.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewlift.views.customviews.VideoPlayerView;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 6/14/17.
 */

public class AppCMSPlayVideoFragment extends Fragment {
    private static final String TAG = "PlayVideoFragment";

    private String hlsUrl;
    private VideoPlayerView videoPlayerView;

    public static AppCMSPlayVideoFragment newInstance(Context context, String hlsUrl) {
        AppCMSPlayVideoFragment appCMSPlayVideoFragment = new AppCMSPlayVideoFragment();
        Bundle args = new Bundle();
        args.putString(context.getString(R.string.video_player_hls_url_key), hlsUrl);
        appCMSPlayVideoFragment.setArguments(args);
        return appCMSPlayVideoFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            hlsUrl = args.getString(getContext().getString(R.string.video_player_hls_url_key));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_player, container, false);
        videoPlayerView = (VideoPlayerView) rootView.findViewById(R.id.app_cms_video_player_container);
        if (!TextUtils.isEmpty(hlsUrl)) {
            videoPlayerView.setUri(Uri.parse(hlsUrl));
        }
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (videoPlayerView != null) {
            videoPlayerView.stopPlayer();
        }
    }
}
