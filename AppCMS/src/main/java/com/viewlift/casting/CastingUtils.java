package com.viewlift.casting;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.common.images.WebImage;
import com.viewlift.models.data.appcms.api.ContentDatum;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class CastingUtils {

    public static String MEDIA_KEY = "media_key";
    public static boolean isRemoteMediaControllerOpen = false;
    public static boolean isMediaQueueLoaded = true;
    public static String castingMediaId = "";


    public static int routerDevices = 0;
    private static final int PRELOAD_TIME_S = 20;

    public static final boolean IS_CHROMECAST_ENABLE = true;
    public static final String ROKU_APP_NAME = "TheGreatCoursesPlus";

    public static MediaQueueItem[] BuildCastingQueueItems(List<ContentDatum> detailsRelatedVideoData, String appName, List<String> listCompareRelatedVideosId) {

        MediaQueueItem[] queueItemsArray;

        if (detailsRelatedVideoData != null && detailsRelatedVideoData.size() > 0) {

            queueItemsArray = new MediaQueueItem[detailsRelatedVideoData.size()];

            for (int i = 0; i < queueItemsArray.length; i++) {
                JSONObject seasonObj = new JSONObject();
                try {
                    seasonObj.put(MEDIA_KEY, detailsRelatedVideoData.get(i).getGist().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(getPlayingUrl(detailsRelatedVideoData.get(i))!=null && !TextUtils.isEmpty(getPlayingUrl(detailsRelatedVideoData.get(i)))){
                   int currentPlayingIndex=listCompareRelatedVideosId.indexOf(detailsRelatedVideoData.get(i).getGist().getId());

                    queueItemsArray[currentPlayingIndex] = new MediaQueueItem.Builder(buildMediaInfoFromList(detailsRelatedVideoData.get(i),appName))
                            .setAutoplay(true)
                            .setPreloadTime(PRELOAD_TIME_S)
                            .setCustomData(seasonObj)
                            .build();
                }

            }

            return queueItemsArray;

        }
        return null;
    }

    public static MediaInfo buildMediaInfoFromList(ContentDatum contentData,String appName) {
        String titleMediaInfo = "";
        String subTitleMediaInfo = "";
        String imageMediaInfo = "";
        String urlMediaInfo = "";

        JSONObject medoaInfoCustomData = new JSONObject();
        try {
            medoaInfoCustomData.put(MEDIA_KEY, contentData.getGist().getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (contentData != null) {
            titleMediaInfo = contentData.getGist().getTitle();
            subTitleMediaInfo = appName;
            if (contentData.getContentDetails().getVideoImage() != null && contentData.getContentDetails().getVideoImage().getSecureUrl() != null) {
                imageMediaInfo = contentData.getContentDetails().getVideoImage().getSecureUrl();
            }
        }
        urlMediaInfo = getPlayingUrl(contentData);
        return buildMediaInfo(titleMediaInfo, subTitleMediaInfo, imageMediaInfo, urlMediaInfo, medoaInfoCustomData);
    }

    public static String getPlayingUrl(ContentDatum contentData) {
        String playUrl = "";
        if (contentData.getStreamingInfo() != null && contentData.getStreamingInfo().getVideoAssets() != null) {

            if(contentData.getStreamingInfo().getVideoAssets().getHls()!=null && !TextUtils.isEmpty(contentData.getStreamingInfo().getVideoAssets().getHls())){
                playUrl=contentData.getStreamingInfo().getVideoAssets().getHls();
            }else if(contentData.getStreamingInfo().getVideoAssets().getMpeg()!=null && contentData.getStreamingInfo().getVideoAssets().getMpeg().size()>0){

                if(contentData.getStreamingInfo().getVideoAssets().getMpeg().get(contentData.getStreamingInfo().getVideoAssets().getMpeg().size()-1).getUrl()!=null)
                playUrl=contentData.getStreamingInfo().getVideoAssets().getMpeg().get(contentData.getStreamingInfo().getVideoAssets().getMpeg().size()-1).getUrl();
            }
        }

        return playUrl;
    }


    public static MediaInfo buildMediaInfo(String Title, String subtitle, String Image, String url, JSONObject medoaInfoCustomData) {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, subtitle);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, Title);
        movieMetadata.addImage(new WebImage(Uri.parse(Image)));

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
            e.printStackTrace();
        }

        try {
            getRemoteObject = CastContext.getSharedInstance(mContext).getSessionManager().getCurrentCastSession().getRemoteMediaClient().getMediaInfo().getCustomData();
            remoteMediaId = getRemoteObject.getString(CastingUtils.MEDIA_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return remoteMediaId;
    }
}
