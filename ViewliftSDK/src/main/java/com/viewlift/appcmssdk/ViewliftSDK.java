package com.viewlift.appcmssdk;

import android.content.Context;
import android.content.Intent;

/**
 * Created by viewlift on 7/13/17.
 */

public class ViewliftSDK {
    private static final String TAG = "ViewliftSDK";

    private final Context context;

    /**
     * This will initialize an instance of the ViewliftSDK that can be used to launch a Video.
     * @param context This is the base context to is used by the application.
     */
    public static ViewliftSDK initialize(Context context) {
        return DaggerViewliftSDKComponent.builder()
                .viewliftSDKModule(new ViewliftSDKModule(context, ""))
                .build()
                .appCMSSDK();
    }

    ViewliftSDK(Context context) {
        this.context = context;
    }

    /**
     *
     * @param filmTitle
     * @param hlsUrl
     * @param adUrl
     */
    public void launchVideoPlayer(String filmTitle,
                                  String hlsUrl,
                                  String adUrl) {
        Intent playVideoIntent = new Intent(context, VideoActivity.class);

        playVideoIntent.putExtra(context.getString(R.string.video_player_title_key),
                filmTitle);

        playVideoIntent.putExtra(context.getString(R.string.video_player_hls_url_key),
                hlsUrl);

        playVideoIntent.putExtra(context.getString(R.string.video_player_ads_url_key),
                adUrl);

        context.startActivity(playVideoIntent);
    }
}
