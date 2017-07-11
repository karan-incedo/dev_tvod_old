package com.viewlift.tv.utility;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v17.leanback.widget.TitleView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import java.io.InputStream;

import snagfilms.com.air.appcms.R;

/**
 * Created by nitin.tyagi on 7/3/2017.
 */

public class Utils {

    public static void setBrowseFragmentViewParameters(View browseFragmentView, int marginLeft,
                                                       int marginTop) {
        View browseContainerDoc = browseFragmentView.findViewById(R.id.browse_container_dock);
        if (null != browseContainerDoc) {
            browseContainerDoc.setBackgroundColor(Color.TRANSPARENT);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) browseContainerDoc
                    .getLayoutParams();
            params.leftMargin = marginLeft;// -80;
            params.topMargin = marginTop;// -225;
            browseContainerDoc.setLayoutParams(params);

        }

        View browseHeaders = browseFragmentView.findViewById(R.id.browse_headers);
        if (null != browseHeaders) {
            browseHeaders.setBackgroundColor(Color.TRANSPARENT);
        }

        View containerList = browseFragmentView.findViewById(R.id.container_list);
        if (null != containerList) {
            containerList.setBackgroundColor(Color.TRANSPARENT);
        }
    }


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

    public static int convertPixelsToDp(int px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = px / 2/*(metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)*/;
        return dp;
    }

}
