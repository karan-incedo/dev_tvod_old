package com.viewlift.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 7/7/17.
 */

public class AppCMSTrayItemAdapter extends RecyclerView.Adapter<AppCMSTrayItemAdapter.ViewHolder> {
    private static final String TAG = "AppCMSTrayAdapter";

    private static final int SECONDS_PER_MINS = 60;

    protected List<ContentDatum> adapterData;
    protected List<Component> components;
    protected boolean continueWatching;
    protected AppCMSPresenter appCMSPresenter;
    protected Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    protected String defaultAction;

    public AppCMSTrayItemAdapter(Context context,
                                 List<ContentDatum> adapterData,
                                 List<Component> components,
                                 boolean continueWatching,
                                 AppCMSPresenter appCMSPresenter,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap) {
        this.adapterData = adapterData;
        this.components = components;
        this.continueWatching = continueWatching;
        this.appCMSPresenter = appCMSPresenter;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.defaultAction = getDefaultAction(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.continue_watching_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        applyStyles(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ContentDatum contentDatum = adapterData.get(position);

        Picasso.with(holder.itemView.getContext())
                .load(contentDatum.getGist().getVideoImageUrl())
                .into(holder.appCMSContinueWatchingVideoImage);

        holder.appCMSContinueWatchingVideoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(contentDatum);
            }
        });

        if (continueWatching) {
            holder.appCMSContinueWatchingProgressbar.setVisibility(View.VISIBLE);
        } else {
            holder.appCMSContinueWatchingProgressbar.setVisibility(View.GONE);
        }

        holder.appCMSContinueWatchingPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(contentDatum);
            }
        });

        holder.appCMSContinueWatchingTitle.setText(contentDatum.getGist().getTitle());

        holder.appCMSContinueWatchingDescription.setText(contentDatum.getGist().getDescription());

        holder.appCMSContinueWatchingSelectToDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDelete(contentDatum);
            }
        });

        holder.appCMSContinueWatchingDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(contentDatum);
            }
        });

        holder.appCMSContinueWatchingDuration.setText(String.valueOf(contentDatum.getGist().getRuntime() / SECONDS_PER_MINS));

        holder.appCMSContinueWatchingProgressbar.setProgress(contentDatum.getGist().getWatchedPercentage());
    }

    @Override
    public int getItemCount() {
        return adapterData != null ? adapterData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;

        @BindView(R.id.app_cms_continue_watching_video_image)
        ImageButton appCMSContinueWatchingVideoImage;
        @BindView(R.id.app_cms_continue_watching_play_button)
        ImageButton appCMSContinueWatchingPlayButton;
        @BindView(R.id.app_cms_continue_watching_progressbar)
        ProgressBar appCMSContinueWatchingProgressbar;
        @BindView(R.id.app_cms_continue_watching_title)
        TextView appCMSContinueWatchingTitle;
        @BindView(R.id.app_cms_continue_watching_description)
        TextView appCMSContinueWatchingDescription;
        @BindView(R.id.app_cms_continue_watching_select_to_delete_button)
        ImageButton appCMSContinueWatchingSelectToDeleteButton;
        @BindView(R.id.app_cms_continue_watching_delete_button)
        ImageButton appCMSContinueWatchingDeleteButton;
        @BindView(R.id.app_cms_continue_watching_duration)
        TextView appCMSContinueWatchingDuration;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    private String getHlsUrl(ContentDatum data) {
        if (data.getStreamingInfo() != null &&
                data.getStreamingInfo().getVideoAssets() != null &&
                data.getStreamingInfo().getVideoAssets().getHls() != null) {
            return data.getStreamingInfo().getVideoAssets().getHls();
        }
        return null;
    }

    private void applyStyles(ViewHolder viewHolder) {
        for (Component component : components) {
            AppCMSUIKeyType componentType = jsonValueKeyMap.get(component.getType());
            if (componentType == null) {
                componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }

            AppCMSUIKeyType componentKey = jsonValueKeyMap.get(component.getKey());
            if (componentKey == null) {
                componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }

            int tintColor = Color.parseColor(getColor(viewHolder.itemView.getContext(),
                    appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()));

            switch (componentType) {
                case PAGE_BUTTON_KEY:
                    switch (componentKey) {
                        case PAGE_PLAY_KEY:
                        case PAGE_PLAY_IMAGE_KEY:
                            viewHolder.appCMSContinueWatchingPlayButton.getBackground().setTint(tintColor);
                            break;
                        default:
                            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                                viewHolder.appCMSContinueWatchingDeleteButton
                                        .setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(),
                                                component.getBackgroundColor())));
                                viewHolder.appCMSContinueWatchingSelectToDeleteButton
                                        .setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(),
                                                component.getBackgroundColor())));
                            } else {
                                applyBorderToComponent(viewHolder.itemView.getContext(),
                                        viewHolder.appCMSContinueWatchingDeleteButton,
                                        component);
                                applyBorderToComponent(viewHolder.itemView.getContext(),
                                        viewHolder.appCMSContinueWatchingSelectToDeleteButton,
                                        component);
                            }
                            viewHolder.appCMSContinueWatchingDeleteButton.getBackground().setTint(tintColor);
                            viewHolder.appCMSContinueWatchingSelectToDeleteButton.getBackground().setTint(tintColor);
                            viewHolder.appCMSContinueWatchingDeleteButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                            viewHolder.appCMSContinueWatchingSelectToDeleteButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                    }
                    break;
                case PAGE_LABEL_KEY:
                case PAGE_TEXTVIEW_KEY:
                    int textColor = ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.colorAccent);
                    if (!TextUtils.isEmpty(component.getTextColor())) {
                        textColor = Color.parseColor(getColor(viewHolder.itemView.getContext(), component.getTextColor()));
                    } else if (component.getStyles() != null) {
                        if (!TextUtils.isEmpty(component.getStyles().getColor())) {
                            textColor = Color.parseColor(getColor(viewHolder.itemView.getContext(), component.getStyles().getColor()));
                        } else if (!TextUtils.isEmpty(component.getStyles().getTextColor())) {
                            textColor =
                                    Color.parseColor(getColor(viewHolder.itemView.getContext(), component.getStyles().getTextColor()));
                        }
                    }
                    switch (componentKey) {
                        case PAGE_API_TITLE:
                            viewHolder.appCMSContinueWatchingTitle.setTextColor(textColor);
                            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                                viewHolder.appCMSContinueWatchingTitle.setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(), component.getBackgroundColor())));
                            }
                            if (!TextUtils.isEmpty(component.getFontFamily())) {
                                setTypeFace(viewHolder.itemView.getContext(),
                                        jsonValueKeyMap,
                                        component,
                                        viewHolder.appCMSContinueWatchingTitle);
                            }
                            break;
                        case PAGE_API_DESCRIPTION:
                            viewHolder.appCMSContinueWatchingDescription.setTextColor(textColor);
                            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                                viewHolder.appCMSContinueWatchingDescription.setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(), component.getBackgroundColor())));
                            }
                            if (!TextUtils.isEmpty(component.getFontFamily())) {
                                setTypeFace(viewHolder.itemView.getContext(),
                                        jsonValueKeyMap,
                                        component,
                                        viewHolder.appCMSContinueWatchingDescription);
                            }
                            break;
                        default:
                    }

                    break;
                case PAGE_SEPARATOR_VIEW_KEY:
                case PAGE_SEGMENTED_VIEW_KEY:
                    if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                        viewHolder.appCMSContinueWatchingProgressbar
                                .setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(), component.getBackgroundColor())));
                    }
                    break;
                case PAGE_PROGRESS_VIEW_KEY:
                    if (!TextUtils.isEmpty(component.getProgressColor())) {
                        int color = Color.parseColor(getColor(viewHolder.itemView.getContext(), component.getProgressColor()));
                        viewHolder.appCMSContinueWatchingProgressbar.setProgressDrawable(new ColorDrawable(color));
                    }
                    break;
                default:
            }
        }
    }

    private void click(ContentDatum data) {
        Log.d(TAG, "Clicked on item: " + data.getGist().getTitle());
        String permalink = data.getGist().getPermalink();
        String action = defaultAction;
        String title = data.getGist().getTitle();
        String hlsUrl = getHlsUrl(data);
        String[] extraData = new String[3];
        extraData[0] = permalink;
        extraData[1] = hlsUrl;
        extraData[2] = data.getGist().getId();
        Log.d(TAG, "Launching " + permalink + ": " + action);
        if (!appCMSPresenter.launchButtonSelectedAction(permalink,
                action,
                title,
                extraData,
                false)) {
            Log.e(TAG, "Could not launch action: " +
                    " permalink: " +
                    permalink +
                    " action: " +
                    action +
                    " hlsUrl: " +
                    hlsUrl);
        }
    }

    private void play(ContentDatum data) {
        Log.d(TAG, "Playing item: " + data.getGist().getTitle());
        String filmId = data.getGist().getId();
        String permaLink = data.getGist().getPermalink();
        String title = data.getGist().getTitle();
        if (!appCMSPresenter.launchVideoPlayer(filmId, permaLink, title)) {
            Log.e(TAG, "Could not launch play action: " +
                    " filmId: " +
                    filmId +
                    " permaLink: " +
                    permaLink +
                    " title: " +
                    title);
        }
    }

    private String getDefaultAction(Context context) {
        return context.getString(R.string.app_cms_action_videopage_key);
    }

    private void showDelete(ContentDatum contentDatum) {
        Log.d(TAG, "Show delete button");
    }

    private void delete(ContentDatum contentDatum) {
        Log.d(TAG, "Delete item: " + contentDatum.getGist().getTitle());
    }

    private void applyBorderToComponent(Context context, View view, Component component) {
        if (component.getBorderWidth() != null && component.getBorderColor() != null) {
            if (component.getBorderWidth() > 0 && !TextUtils.isEmpty(component.getBorderColor())) {
                GradientDrawable ageBorder = new GradientDrawable();
                ageBorder.setShape(GradientDrawable.RECTANGLE);
                ageBorder.setStroke(component.getBorderWidth(),
                        Color.parseColor(getColor(context, component.getBorderColor())));
                ageBorder.setColor(ContextCompat.getColor(context, android.R.color.transparent));
                view.setBackground(ageBorder);
            }
        }
    }

    private String getColor(Context context, String color) {
        if (color.indexOf(context.getString(R.string.color_hash_prefix)) != 0) {
            return context.getString(R.string.color_hash_prefix) + color;
        }
        return color;
    }

    private void setTypeFace(Context context,
                             Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                             Component component,
                             TextView textView) {
        if (jsonValueKeyMap.get(component.getFontFamily()) == AppCMSUIKeyType.PAGE_TEXT_OPENSANS_FONTFAMILY_KEY) {
            AppCMSUIKeyType fontWeight = jsonValueKeyMap.get(component.getFontWeight());
            if (fontWeight == null) {
                fontWeight = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }
            Typeface face = null;
            switch (fontWeight) {
                case PAGE_TEXT_BOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_bold_ttf));
                    break;
                case PAGE_TEXT_SEMIBOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_semibold_ttf));
                    break;
                case PAGE_TEXT_EXTRABOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_extrabold_ttf));
                    break;
                default:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_regular_ttf));
            }
            textView.setTypeface(face);
        }
    }
}
