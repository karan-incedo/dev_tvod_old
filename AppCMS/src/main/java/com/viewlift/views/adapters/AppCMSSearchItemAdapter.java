package com.viewlift.views.adapters;

import android.app.ActionBar;
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

import com.squareup.picasso.Picasso;
import com.viewlift.models.data.appcms.search.AppCMSSearchResult;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.BaseView;

import java.util.List;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 6/12/17.
 */

public class AppCMSSearchItemAdapter extends RecyclerView.Adapter<AppCMSSearchItemAdapter.ViewHolder> {
    private static final String TAG = "AppCMSSearchAdapter";

    private static final float STANDARD_MOBILE_WIDTH_PX = 375f;
    private static final float STANDARD_MOBILE_HEIGHT_PX = 667f;

    private static final float STANDARD_TABLET_WIDTH_PX = 768f;
    private static final float STANDARD_TABLET_HEIGHT_PX = 1024f;

    private static float DEVICE_WIDTH;
    private static int DEVICE_HEIGHT;

    private static final float IMAGE_WIDTH_MOBILE = 111f;
    private static final float IMAGE_HEIGHT_MOBILE = 164f;
    private static final float IMAGE_WIDTH_TABLET_LANDSCAPE = 154f;
    private static final float IMAGE_HEIGHT_TABLET_LANDSCAPE = 240f;
    private static final float IMAGE_WIDTH_TABLET__PORTRAIT = 154f;
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

    private final AppCMSPresenter appCMSPresenter;
    private List<AppCMSSearchResult> appCMSSearchResults;

    public AppCMSSearchItemAdapter(Context context, AppCMSPresenter appCMSPresenter, List<AppCMSSearchResult> appCMSSearchResults) {
        this.appCMSPresenter = appCMSPresenter;
        this.appCMSSearchResults = appCMSSearchResults;
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_result_item,
                viewGroup,
                false);
        return new ViewHolder(view, imageWidth, imageHeight, textSize, textWidth, textTopMargin);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final int adapterPosition = i;
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String permalink = appCMSSearchResults.get(adapterPosition).getPermalink();
                String closeAction = viewHolder.view.getContext().getString(R.string.app_cms_action_close_key);
                String action = viewHolder.view.getContext().getString(R.string.app_cms_action_videopage_key);
                String title = appCMSSearchResults.get(adapterPosition).getTitle();
                Log.d(TAG, "Launching " + permalink + ":" + action);
                if (!appCMSPresenter.launchButtonSelectedAction(permalink, action, title, null, true)) {
                    Log.e(TAG, "Could not launch action: " +
                            " permalink: " +
                            permalink +
                            " action: " +
                            action);
                }
            }
        });

        if (!TextUtils.isEmpty(appCMSSearchResults.get(adapterPosition).getTitle())) {
            viewHolder.filmTitle.setText(appCMSSearchResults.get(adapterPosition).getTitle());
        }

        if (appCMSSearchResults.get(adapterPosition).getThumbnail() != null &&
                !TextUtils.isEmpty(appCMSSearchResults.get(adapterPosition).getPosterImage().getUrl())) {
            Picasso.with(viewHolder.view.getContext())
                    .load(appCMSSearchResults.get(adapterPosition).getPosterImage().getUrl())
                    .resize(imageWidth, imageHeight)
                    .centerCrop()
                    .into(viewHolder.filmThumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return appCMSSearchResults != null ? appCMSSearchResults.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        FrameLayout parentLayout;
        ImageView filmThumbnail;
        TextView filmTitle;
        public ViewHolder(View view,
                          int imageWidth,
                          int imageHeight,
                          int textSize,
                          int textWidth,
                          int textTopMargin) {
            super(view);
            this.view = view;
            this.parentLayout = (FrameLayout) view.findViewById(R.id.search_result_item_view);
            this.filmThumbnail = new ImageView(view.getContext());
            FrameLayout.LayoutParams filmImageThumbnailLayoutParams =
                    new FrameLayout.LayoutParams(imageWidth, imageHeight);
            this.filmThumbnail.setLayoutParams(filmImageThumbnailLayoutParams);
            this.parentLayout.addView(this.filmThumbnail);
            this.filmTitle = new TextView(view.getContext());
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

    public void setData(List<AppCMSSearchResult> results) {
        appCMSSearchResults = results;
        notifyDataSetChanged();
    }

    private float getImageWidth(Context context) {
        if (BaseView.isTablet(context)) {
            if (BaseView.isLandscape(context)) {
                return DEVICE_WIDTH * (IMAGE_WIDTH_TABLET_LANDSCAPE / STANDARD_TABLET_HEIGHT_PX);
            } else {
                return DEVICE_WIDTH * (IMAGE_WIDTH_TABLET__PORTRAIT / STANDARD_TABLET_WIDTH_PX);
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
                return DEVICE_HEIGHT * (IMAGE_HEIGHT_TABLET_LANDSCAPE / STANDARD_MOBILE_WIDTH_PX);
            } else {
                return DEVICE_HEIGHT * (IMAGE_HEIGHT_TABLET_PORTRAIT / STANDARD_TABLET_HEIGHT_PX);
            }
        }
        return DEVICE_HEIGHT * (IMAGE_HEIGHT_MOBILE / STANDARD_MOBILE_HEIGHT_PX);
    }

    private float getTextTopMargin(Context context) {
        if (BaseView.isTablet(context)) {
            if (BaseView.isLandscape(context)) {
                return DEVICE_HEIGHT * (TEXT_TOPMARGIN_TABLET_LANDSCAPE / STANDARD_MOBILE_WIDTH_PX);
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
