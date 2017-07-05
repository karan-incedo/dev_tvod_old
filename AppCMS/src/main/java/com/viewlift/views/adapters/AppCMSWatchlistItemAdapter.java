package com.viewlift.views.adapters;

/*
 * Created by Viewlift on 6/28/2017.
 */

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewlift.models.data.appcms.watchlist.AppCMSWatchlistResult;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.BaseView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import snagfilms.com.air.appcms.R;

public class AppCMSWatchlistItemAdapter extends RecyclerView.Adapter<AppCMSWatchlistItemAdapter.ViewHolder> {
    private static final float STANDARD_MOBILE_WIDTH_PX = 375f;
    private static final float STANDARD_MOBILE_HEIGHT_PX = 667f;

    private static final float STANDARD_TABLET_WIDTH_PX = 768f;
    private static final float STANDARD_TABLET_HEIGHT_PX = 1024f;
    private static final String TAG = "WatchlistAdapterTAG_";

    private static float DEVICE_WIDTH;
    private static int DEVICE_HEIGHT;

    private static final float IMAGE_WIDTH_MOBILE = 111f;
    private static final float IMAGE_HEIGHT_MOBILE = 164f;
    private static final float IMAGE_WIDTH_TABLET_LANDSCAPE = 154f;
    private static final float IMAGE_HEIGHT_TABLET_LANDSCAPE = 240f;
    private static final float IMAGE_WIDTH_TABLET_PORTRAIT = 154f;
    private static final float IMAGE_HEIGHT_TABLET_PORTRAIT = 240f;
    private static final float TEXTSIZE_MOBILE = 11f;
    private static final float TEXTSIZE_TABLET_LANDSCAPE = 14f;
    private static final float TEXTSIZE_TABLET_PORTRAIT = 14f;
    private static final float TEXT_WIDTH_MOBILE = IMAGE_WIDTH_MOBILE;
    private static final float TEXT_WIDTH_TABLET_LANDSCAPE = 154f;
    private static final float TEXT_WIDTH_TABLET_PORTRAIT = 154f;
    private static final float TEXT_TOPMARGIN_MOBILE = 170f;
    private static final float TEXT_TOPMARGIN_TABLET_LANDSCAPE = 242f;
    private static final float TEXT_TOPMARGIN_TABLET_PORTRAIT = 242f;

    private int imageWidth = 0;
    private int imageHeight = 0;
    private int textSize = 0;
    private int textWidth = 0;
    private int textTopMargin = 0;

    private AppCMSPresenter appCMSPresenter;
    private List<AppCMSWatchlistResult> appCMSWatchlistResults;

    public AppCMSWatchlistItemAdapter(Context context, AppCMSPresenter appCMSPresenter,
                                      List<AppCMSWatchlistResult> appCMSWatchlistResults) {
        this.appCMSPresenter = appCMSPresenter;
        this.appCMSWatchlistResults = appCMSWatchlistResults;
        DEVICE_WIDTH = context.getResources().getDisplayMetrics().widthPixels;
        DEVICE_HEIGHT = context.getResources().getDisplayMetrics().heightPixels;
        this.imageWidth = (int) getImageWidth(context);
        this.imageHeight = (int) getImageHeight(context);
        this.textSize = (int) getTextSize(context);
        this.textWidth = (int) getTextWidth(context);
        this.textTopMargin = (int) getTextTopMargin(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.watchlist_result_item, viewGroup, false);

        return new ViewHolder(view, imageWidth, imageHeight, textSize, textWidth, textTopMargin);
    }

    @Override
    public void onBindViewHolder(AppCMSWatchlistItemAdapter.ViewHolder viewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: " + viewHolder);
    }

    @Override
    public int getItemCount() {
        return appCMSWatchlistResults == null ? 0 : appCMSWatchlistResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.watchlist_result_item_view)
        FrameLayout frameLayout;

        View view;
        FrameLayout parentLayout;
        ImageView filmThumbNail;
        TextView filmTitle;

        public ViewHolder(View itemView,
                          int imageWidth,
                          int imageHeight,
                          int textSize,
                          int textWidth,
                          int textTopMargin) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            this.view = itemView;
            this.parentLayout = frameLayout;
            this.filmThumbNail = new ImageView(itemView.getContext());

            FrameLayout.LayoutParams filmThumbnailLayoutParams = new FrameLayout.LayoutParams(imageWidth,
                    imageHeight);
            this.filmThumbNail.setLayoutParams(filmThumbnailLayoutParams);
            this.parentLayout.addView(this.filmThumbNail);
            this.filmTitle = new TextView(itemView.getContext());

            FrameLayout.LayoutParams filmTitleLayoutParams =
                    new FrameLayout.LayoutParams(textWidth,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            filmTitleLayoutParams.setMargins(0, textTopMargin, 0, 0);

            this.filmTitle.setLayoutParams(filmTitleLayoutParams);
            this.filmTitle.setTextSize(textSize);
            this.filmTitle.setMaxLines(1);
            this.filmTitle.setTextColor(ContextCompat.getColor(view.getContext(),
                    android.R.color.white));
            this.filmTitle.setEllipsize(TextUtils.TruncateAt.END);
            this.parentLayout.addView(this.filmTitle);
        }
    }

    public void setData(List<AppCMSWatchlistResult> results) {
        appCMSWatchlistResults = results;
        notifyDataSetChanged();
    }

    private float getImageWidth(Context context) {

        if (BaseView.isTablet(context)) {
            if (BaseView.isLandscape(context)) {
                return DEVICE_WIDTH * (IMAGE_WIDTH_TABLET_LANDSCAPE / STANDARD_TABLET_HEIGHT_PX);
            } else {
                return DEVICE_WIDTH * (IMAGE_WIDTH_TABLET_PORTRAIT / STANDARD_TABLET_WIDTH_PX);
            }
        }

        return DEVICE_WIDTH * (IMAGE_WIDTH_MOBILE / STANDARD_MOBILE_WIDTH_PX);
    }

    private float getTextWidth(Context context) {

        if (BaseView.isTablet(context)) {
            if (BaseView.isLandscape(context)) {
                return DEVICE_WIDTH * (TEXT_WIDTH_TABLET_LANDSCAPE / STANDARD_TABLET_HEIGHT_PX);
            } else {
                return DEVICE_WIDTH * (TEXT_WIDTH_TABLET_PORTRAIT / STANDARD_TABLET_WIDTH_PX);
            }
        }

        return DEVICE_WIDTH * (TEXT_WIDTH_MOBILE / STANDARD_MOBILE_WIDTH_PX);
    }

    private float getImageHeight(Context context) {

        if (BaseView.isTablet(context)) {
            if (BaseView.isLandscape(context)) {
                return DEVICE_HEIGHT * (IMAGE_HEIGHT_TABLET_LANDSCAPE / STANDARD_TABLET_WIDTH_PX);
            } else {
                return DEVICE_HEIGHT * (IMAGE_HEIGHT_TABLET_PORTRAIT / STANDARD_TABLET_HEIGHT_PX);
            }
        }

        return DEVICE_HEIGHT * (IMAGE_HEIGHT_MOBILE / STANDARD_MOBILE_HEIGHT_PX);
    }

    private float getTextTopMargin(Context context) {

        if (BaseView.isTablet(context)) {
            if (BaseView.isLandscape(context)) {
                return DEVICE_HEIGHT * (TEXT_TOPMARGIN_TABLET_LANDSCAPE / STANDARD_TABLET_WIDTH_PX);
            } else {
                return DEVICE_HEIGHT * (TEXT_TOPMARGIN_TABLET_PORTRAIT / STANDARD_TABLET_HEIGHT_PX);
            }
        }

        return DEVICE_HEIGHT * (TEXT_TOPMARGIN_MOBILE / STANDARD_MOBILE_HEIGHT_PX);
    }

    private float getTextSize(Context context) {

        if (BaseView.isTablet(context)) {
            if (BaseView.isLandscape(context)) {
                return TEXTSIZE_TABLET_LANDSCAPE;
            } else {
                return TEXTSIZE_TABLET_PORTRAIT;
            }
        }

        return TEXTSIZE_MOBILE;
    }
}
