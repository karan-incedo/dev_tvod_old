package com.viewlift.utility;

import android.content.Context;

import java.io.InputStream;

/**
 * Created by viewlift on 7/24/17.
 */

public class Utils {
    public static String loadJsonFromAssets(Context context , String fileName){
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
}
