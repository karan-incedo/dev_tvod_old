package com.viewlift.casting;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.common.images.WebImage;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.views.customviews.BaseView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class CastingUtils {

    private static final String TAG = "CastingUtils";

    public static String MEDIA_KEY = "media_key";
    public static String PARAM_KEY = "param_key";
    public static String VIDEO_TITLE = "video_title";
    public static final String ITEM_TYPE = "item_type";
    public static final String ITEM_TYPE_AUDIO = "item_type_audio";
    public static final String ITEM_TYPE_VIDEO = "item_type_video";

    public static boolean isRemoteMediaControllerOpen = false;
    public static boolean isMediaQueueLoaded = true;
    public static String castingMediaId = "";

    public static int CASTING_MODE_CHROMECAST = 1;
    public static int CASTING_MODE_ROKU = 2;

    public static int routerDevices = 0;
    private static final int PRELOAD_TIME_S = 20;

    public static final boolean IS_CHROMECAST_ENABLE = true;


    public static MediaQueueItem[] BuildCastingQueueItems(List<ContentDatum> detailsRelatedVideoData,
                                                          String appName,
                                                          List<String> listCompareRelatedVideosId,
                                                          Context context) {

        MediaQueueItem[] queueItemsArray;
        String appPackageName=context.getPackageName();

        if (detailsRelatedVideoData != null && detailsRelatedVideoData.size() > 0) {

            queueItemsArray = new MediaQueueItem[detailsRelatedVideoData.size()];

            for (int i = 0; i < queueItemsArray.length; i++) {
                JSONObject seasonObj = new JSONObject();
                try {
                    seasonObj.put(MEDIA_KEY, detailsRelatedVideoData.get(i).getGist().getId());
                    seasonObj.put(PARAM_KEY, detailsRelatedVideoData.get(i).getGist().getPermalink());
                    seasonObj.put(ITEM_TYPE, appPackageName+""+CastingUtils.ITEM_TYPE_VIDEO);

                } catch (Exception e) {
                    //Log.e(TAG, "Error create session JSON object: " + e.getMessage());
                }

                int watchTime = (int) detailsRelatedVideoData.get(i).getGist().getWatchedTime();
                //Log.e(TAG, "Added watched Time: " + watchTime);

                if (getPlayingUrl(detailsRelatedVideoData.get(i)) != null && !TextUtils.isEmpty(getPlayingUrl(detailsRelatedVideoData.get(i)))) {
                    int currentPlayingIndex = listCompareRelatedVideosId.indexOf(detailsRelatedVideoData.get(i).getGist().getId());

                    if (0 <= currentPlayingIndex && currentPlayingIndex < queueItemsArray.length) {
                        queueItemsArray[currentPlayingIndex] = new MediaQueueItem.Builder(buildMediaInfoFromList(detailsRelatedVideoData.get(i), appName, context))
                                .setAutoplay(true)
                                .setPreloadTime(PRELOAD_TIME_S)
                                .setCustomData(seasonObj).setStartTime(watchTime)
                                .build();
                    }
                }

            }

            return queueItemsArray;

        }
        return null;
    }

    public static MediaInfo buildMediaInfoFromList(ContentDatum contentData,
                                                   String appName,
                                                   Context context) {
        String titleMediaInfo = "";
        String subTitleMediaInfo = "";
        String imageMediaInfo = "";
        String urlMediaInfo = "";
        String appPackageName=context.getPackageName();

        JSONObject medoaInfoCustomData = new JSONObject();
        try {
            medoaInfoCustomData.put(MEDIA_KEY, contentData.getGist().getId());
            medoaInfoCustomData.put(PARAM_KEY, contentData.getGist().getPermalink());
            medoaInfoCustomData.put(ITEM_TYPE, appPackageName+""+CastingUtils.ITEM_TYPE_VIDEO);

        } catch (JSONException e) {
            //Log.e(TAG, "Error retrieving media information: " + e.getMessage());
        }
        if (contentData != null) {
            titleMediaInfo = contentData.getGist().getTitle();
            subTitleMediaInfo = appName;
            if (contentData.getContentDetails().getVideoImage() != null && contentData.getContentDetails().getVideoImage().getSecureUrl() != null) {
                imageMediaInfo = contentData.getContentDetails().getVideoImage().getSecureUrl();
            }
        }
        urlMediaInfo = getPlayingUrl(contentData);
        return buildMediaInfo(titleMediaInfo,
                subTitleMediaInfo,
                imageMediaInfo,
                urlMediaInfo,
                medoaInfoCustomData,
                context);
    }

    public static String getPlayingUrl(ContentDatum contentData) {
        String playUrl = "";
        if (contentData != null && contentData.getStreamingInfo() != null && contentData.getStreamingInfo().getVideoAssets() != null) {


            if (contentData.getStreamingInfo().getVideoAssets().getMpeg() != null && contentData.getStreamingInfo().getVideoAssets().getMpeg().size() > 0) {

                if (contentData.getStreamingInfo().getVideoAssets().getMpeg().get(0).getUrl() != null)
                    playUrl = contentData.getStreamingInfo().getVideoAssets().getMpeg().get(0).getUrl();
            } else if (contentData.getStreamingInfo().getVideoAssets().getHls() != null && !TextUtils.isEmpty(contentData.getStreamingInfo().getVideoAssets().getHls())) {
                playUrl = contentData.getStreamingInfo().getVideoAssets().getHls();
            }
        }
//
//        if (playUrl != null && playUrl.contains("Policy=")
//                && playUrl.contains("Key-Pair-Id=")
//                && playUrl.contains("Signature=")
//                && playUrl.contains("?")) {
//            playUrl = playUrl.substring(0, playUrl.indexOf("?"));
//        }
        return playUrl;
    }

    public static String getTitle(ContentDatum contentData, boolean isTrailer) {
        String videoTitle = "";
        if (!isTrailer && contentData != null && contentData.getGist() != null && contentData.getGist().getTitle() != null) {
            videoTitle = contentData.getGist().getTitle();
        } else if (isTrailer && contentData != null && contentData.getContentDetails() != null && contentData.getContentDetails().getTrailers() != null && contentData.getContentDetails().getTrailers().get(0) != null) {
            videoTitle = contentData.getContentDetails().getTrailers().get(0).getTitle();
        }

        return videoTitle;
    }

    public static MediaInfo buildMediaInfo(String Title,
                                           String subtitle,
                                           String image,
                                           String url,
                                           JSONObject medoaInfoCustomData,
                                           Context context) {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, subtitle);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, Title);
        int imageWidth = BaseView.getDeviceWidth();
        int imageHeight = BaseView.getDeviceHeight();
        String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                image,
                imageWidth,
                imageHeight);
        movieMetadata.addImage(new WebImage(Uri.parse(imageUrl),
                imageWidth,
                imageHeight));

        return new MediaInfo.Builder(url)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("videos/mp4")
                .setMetadata(movieMetadata)
                .setCustomData(medoaInfoCustomData)
                .build();
    }

    public static String getRemoteMediaId(Context mContext) {
        JSONObject getRemoteObject = null;
        String remoteMediaId = "";
        try {
            getRemoteObject = CastContext.getSharedInstance(mContext).getSessionManager().getCurrentCastSession().getRemoteMediaClient().getCurrentItem().getCustomData();
            remoteMediaId = getRemoteObject.getString(CastingUtils.MEDIA_KEY);
        } catch (Exception e) {
            //Log.e(TAG, "Error retrieving media information: " + e.getMessage());
        }

        try {
            getRemoteObject = CastContext.getSharedInstance(mContext).getSessionManager().getCurrentCastSession().getRemoteMediaClient().getMediaInfo().getCustomData();
            remoteMediaId = getRemoteObject.getString(CastingUtils.MEDIA_KEY);
        } catch (Exception e) {
            //Log.e(TAG, "Error retrieving remote media object: " + e.getMessage());
        }
        return remoteMediaId;
    }

    public static String getCurrentPlayingVideoName(Context mContext) {
        JSONObject getRemoteObject = null;
        String remoteParamKey = "";
        try {
            getRemoteObject = CastContext.getSharedInstance(mContext).getSessionManager().getCurrentCastSession().getRemoteMediaClient().getMediaInfo().getCustomData();
            remoteParamKey = getRemoteObject.getString(CastingUtils.VIDEO_TITLE);
        } catch (Exception e) {
            remoteParamKey = mContext.getResources().getString(R.string.app_cms_touch_to_cast_msg);
            //Log.e(TAG, "Error retrieving remote media object: " + e.getMessage());
        }
        return remoteParamKey;
    }


    public static String getRemoteParamKey(Context mContext) {
        JSONObject getRemoteObject = null;
        String remoteParamKey = "";
        try {
            getRemoteObject = CastContext.getSharedInstance(mContext).getSessionManager().getCurrentCastSession().getRemoteMediaClient().getMediaInfo().getCustomData();
            remoteParamKey = getRemoteObject.getString(CastingUtils.PARAM_KEY);
        } catch (Exception e) {
            //Log.e(TAG, "Error retrieving remote media object: " + e.getMessage());
        }
        return remoteParamKey;
    }

}
