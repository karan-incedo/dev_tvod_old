package com.viewlift;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.viewlift.models.data.appcms.api.ClosedCaptions;
import com.viewlift.models.data.appcms.api.Season_;
import com.viewlift.models.data.appcms.downloads.DownloadClosedCaptionRealm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Chandan Kumar on 27/07/17.
 */

public class Utils {

    private static String AMAZON_FEATURE_FIRE_TV = "amazon.hardware.fire_tv";

    public static String getProperty(String key, Context context) {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        try {

            InputStream inputStream = assetManager.open("version.properties");
            properties.load(inputStream);
            return properties.getProperty(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static String loadJsonFromAssets(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    /*The startService() method now throws an IllegalStateException if an app targeting Android 8.0 tries to use that method in a situation when it isn't permitted to create background services.
    The new Context.startForegroundService() method starts a foreground service. The system allows apps to call Context.startForegroundService() even while the app is in the background.*/
    public static void startService(Context context,Intent intent){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
          context.startForegroundService(intent);
        else
           context.startService(intent);
    }

    private static boolean hls;
    public static boolean isHLS(){
        return hls;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void setHls(boolean hls) {
        hls = hls;
    }

    public static boolean isFireTVDevice(Context context){
        return context.getPackageManager().hasSystemFeature(AMAZON_FEATURE_FIRE_TV);
    }

    public static List<String> getRelatedVideosInShow2(List<Season_> season, int showNumber, int episodeNumber, String episodeId) {
        List<String> relatedVids = new ArrayList<>();
        boolean foundEpisode = false;
        for (int i = showNumber; i < season.size(); i++) {
            if (i == showNumber) {
                for (int j = episodeNumber + 1; j < season.get(i).getEpisodes().size(); j++) {
                    String episode = season.get(i).getEpisodes().get(j).getId();
                    if (foundEpisode || episode.equalsIgnoreCase(episodeId)) {
                        foundEpisode = true;
                        relatedVids.add(episode);
                    }
                }
            } else {
                for (int j = 0; j < season.get(i).getEpisodes().size(); j++) {
                    String episode = season.get(i).getEpisodes().get(j).getId();
                    if (foundEpisode || episode.equalsIgnoreCase(episodeId)) {
                        foundEpisode = true;
                        relatedVids.add(episode);
                    }
                }
            }
        }
        return relatedVids;
    }

    public static DownloadClosedCaptionRealm convertClosedCaptionToDownloadClosedCaption(ClosedCaptions cc,String gistId){
        DownloadClosedCaptionRealm downloadClosedCaptionRealm = new DownloadClosedCaptionRealm();
        downloadClosedCaptionRealm.setId(cc.getId());
        downloadClosedCaptionRealm.setAddedDate(cc.getAddedDate());
        downloadClosedCaptionRealm.setFormat(cc.getFormat());
        downloadClosedCaptionRealm.setLanguage(cc.getLanguage());
        downloadClosedCaptionRealm.setPermalink(cc.getPermalink());
        downloadClosedCaptionRealm.setPublishDate(cc.getPublishDate());
        downloadClosedCaptionRealm.setRegisteredDate(cc.getRegisteredDate());
        downloadClosedCaptionRealm.setSiteOwner(cc.getSiteOwner());
        downloadClosedCaptionRealm.setSize(cc.getSize());
        downloadClosedCaptionRealm.setUpdateDate(cc.getUpdateDate());
        downloadClosedCaptionRealm.setUrl(cc.getUrl());
        downloadClosedCaptionRealm.setGistId(gistId);
        return downloadClosedCaptionRealm;
    }

    public static ClosedCaptions convertDownloadClosedCaptionToClosedCaptions(DownloadClosedCaptionRealm dc){
        ClosedCaptions closedCaptions = new ClosedCaptions();
        closedCaptions.setAddedDate(dc.getAddedDate());
        closedCaptions.setFormat(dc.getFormat());
        closedCaptions.setId(dc.getId());
        closedCaptions.setLanguage(dc.getLanguage());
        closedCaptions.setPermalink(dc.getPermalink());
        closedCaptions.setPublishDate(dc.getPublishDate());
        closedCaptions.setRegisteredDate(dc.getRegisteredDate());
        closedCaptions.setSiteOwner(dc.getSiteOwner());
        closedCaptions.setSize(dc.getSize());
        closedCaptions.setUpdateDate(dc.getUpdateDate());
        closedCaptions.setUrl(dc.getUrl());
        return closedCaptions;
    }

}
