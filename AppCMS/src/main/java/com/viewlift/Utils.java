package com.viewlift;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;

import com.viewlift.Audio.MusicService;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Chandan Kumar on 27/07/17.
 */

public class Utils {

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

}
