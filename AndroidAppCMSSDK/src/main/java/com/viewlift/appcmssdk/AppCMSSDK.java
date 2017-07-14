package com.viewlift.appcmssdk;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.viewlift.models.data.appcms.films.StreamingInfo;
import com.viewlift.models.data.appcms.films.FilmRecordResult;
import com.viewlift.models.data.appcms.films.Gist;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.network.modules.AppCMSUIModule;
import com.viewlift.models.network.rest.AppCMSFilmRecordsCall;
import com.viewlift.models.network.rest.AppCMSMainUICall;
import com.viewlift.views.activities.AppCMSPlayVideoActivity;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;

import rx.functions.Action1;

/**
 * Created by viewlift on 7/13/17.
 */

public class AppCMSSDK {
    private static final String TAG = "AppCMSSDK";

    private final Context context;
    private final String appCMSBaseURL;
    private final String appCMSSiteId;
    private final AppCMSMainUICall appCMSMainUICall;
    private final AppCMSFilmRecordsCall appCMSFilmRecordsCall;

    /**
     * This will initialize an instance of the AppCMSSDK that can be used to launch a Video.
     * @param context This is the base context to is used by the application.
     * @param appCMSBaseURL This is the base URL of the AppCMS site.
     * @param appCMSSiteId This is the Site ID of the AppCMS site.
     * @return Returns an instance of the AppCMSSDK.
     */
    public static AppCMSSDK initialize(Context context,
                                       String appCMSBaseURL,
                                       String appCMSSiteId) {
        return DaggerAppCMSSDKComponent.builder()
                .appCMSUIModule(new AppCMSUIModule(context,
                        appCMSBaseURL))
                .appCMSSDKModule(new AppCMSSDKModule(context,
                        appCMSBaseURL,
                        appCMSSiteId))
                .build()
                .appCMSSDK();
    }

    /**
     * This launches a Video Player that will play a video associated with the Film ID.
     * @param filmId This is the Film ID for the video to be played.
     */
    public void launchVideo(final String filmId) {
        Log.d(TAG, "Retrieving AppCMSMain to get data for film with filmId: " + filmId);
        getAppCMSMain(new Action1<AppCMSMain>() {
            @Override
            public void call(final AppCMSMain appCMSMain) {
                if (appCMSMain != null) {
                    Log.d(TAG, "Retrieving film data for film with filmId: " + filmId);
                    getVideoData(appCMSMain.getApiBaseUrl(),
                            filmId,
                            appCMSMain.getSite(),
                            new Action1<FilmRecordResult>() {
                                @Override
                                public void call(FilmRecordResult filmRecordResult) {
                                    if (filmRecordResult != null) {
                                        Log.d(TAG, "Extracting video details for film with film Id: " + filmId);
                                        Gist gist = null;
                                        if (filmRecordResult.getRecords() != null &&
                                                filmRecordResult.getRecords().size() > 0 &&
                                                filmRecordResult.getRecords().get(0) != null &&
                                                filmRecordResult.getRecords().get(0).getGist() != null) {
                                            gist = filmRecordResult.getRecords().get(0).getGist();
                                        }

                                        StreamingInfo streamingInfo = null;
                                        if (filmRecordResult.getRecords() != null &&
                                                filmRecordResult.getRecords().size() > 0 &&
                                                filmRecordResult.getRecords().get(0) != null &&
                                                filmRecordResult.getRecords().get(0).getStreamingInfo() != null) {
                                            streamingInfo = filmRecordResult.getRecords().get(0).getStreamingInfo();
                                        }

                                        if (gist != null && streamingInfo != null) {
                                            String videoUrl = null;
                                            if (streamingInfo.getVideoAssets() != null &&
                                                    !TextUtils.isEmpty(streamingInfo.getVideoAssets().getHls())) {
                                                videoUrl = streamingInfo.getVideoAssets().getHls();
                                            } else if (streamingInfo.getVideoAssets() != null &&
                                                    streamingInfo.getVideoAssets().getMpeg() != null &&
                                                    streamingInfo.getVideoAssets().getMpeg().size() > 0 &&
                                                    streamingInfo.getVideoAssets().getMpeg().get(0) != null &&
                                                    !TextUtils.isEmpty(streamingInfo.getVideoAssets().getMpeg().get(0).getUrl())) {
                                                videoUrl = streamingInfo.getVideoAssets().getMpeg().get(0).getUrl();
                                            }
                                            if (!TextUtils.isEmpty(videoUrl)) {
                                                Log.d(TAG, "Launching video with film URL: " + videoUrl);
                                                launchVideoPlayer(gist.getTitle(),
                                                        videoUrl,
                                                        gist.getPermalink(),
                                                        appCMSMain);
                                            } else {
                                                Log.e(TAG, "Video URL for film is null");
                                            }
                                        } else {
                                            if (gist == null) {
                                                Log.e(TAG, "Result for Gist for film is null");
                                            }
                                            if (streamingInfo == null) {
                                                Log.e(TAG, "Result for StreamingInfo for film is null");
                                            }
                                        }
                                    } else {
                                        Log.e(TAG, "Result for film is null");
                                    }
                                }
                            });
                } else {
                    Log.e(TAG, "Result for retrieving AppCMSMain is null");
                }
            }
        });
    }

    AppCMSSDK(Context context,
              String appCMSBaseURL,
              String appCMSSiteId,
              AppCMSMainUICall appCMSMainUICall,
              AppCMSFilmRecordsCall appCMSFilmRecordsCall) {
        this.context = context;
        this.appCMSBaseURL = appCMSBaseURL;
        this.appCMSSiteId = appCMSSiteId;
        this.appCMSMainUICall = appCMSMainUICall;
        this.appCMSFilmRecordsCall = appCMSFilmRecordsCall;
    }

    private void getAppCMSMain(Action1<AppCMSMain> resultReady) {
        try {
            appCMSMainUICall.call(context,
                    appCMSBaseURL,
                    appCMSSiteId,
                    resultReady);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Failed to retrieve AppCMSMain: " + e.getMessage());
        }
    }

    private void getVideoData(String apiBaseUrl,
                              String filmId,
                              String siteName,
                              Action1<FilmRecordResult> resultReady) {
        String url = context.getString(R.string.app_cms_films_api_url,
                apiBaseUrl,
                filmId,
                siteName);
        appCMSFilmRecordsCall.getFilmsRecords(url, resultReady);
    }

    private void launchVideoPlayer(String filmTitle,
                                   String hlsUrl,
                                   String permaLink,
                                   AppCMSMain appCMSMain) {
        Intent playVideoIntent = new Intent(context, AppCMSPlayVideoActivity.class);
        playVideoIntent.putExtra(context.getString(R.string.video_player_title_key),
                filmTitle);
        playVideoIntent.putExtra(context.getString(R.string.video_player_hls_url_key),
                hlsUrl);

        Date now = new Date();
        playVideoIntent.putExtra(context.getString(R.string.video_player_ads_url_key),
                context.getString(R.string.app_cms_ads_api_url,
                        getPermalinkCompletePath(permaLink, appCMSMain),
                        now.getTime(),
                        appCMSMain.getSite()));
        context.startActivity(playVideoIntent);
    }

    private String getPermalinkCompletePath(String pagePath, AppCMSMain appCMSMain) {
        StringBuffer permalinkCompletePath = new StringBuffer();
        permalinkCompletePath.append(context.getString(R.string.https_scheme));
        permalinkCompletePath.append(appCMSMain.getDomainName());
        permalinkCompletePath.append(File.separatorChar);
        permalinkCompletePath.append(pagePath);
        return permalinkCompletePath.toString();
    }
}
