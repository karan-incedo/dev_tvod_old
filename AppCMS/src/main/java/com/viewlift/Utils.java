package com.viewlift;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Chandan Kumar on 27/07/17.
 */

public class Utils {

    public static String getProperty(String key,Context context){
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        try{

            InputStream inputStream = assetManager.open("version.properties");
            properties.load(inputStream);
            return properties.getProperty(key);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

}
