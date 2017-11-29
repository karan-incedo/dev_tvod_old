package com.viewlift.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.InternalEvent;
import com.viewlift.views.customviews.OnInternalEvent;

import net.nightwhistler.htmlspanner.HtmlSpanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppCMSTraySeasonItemAdapter extends RecyclerView.Adapter<AppCMSTraySeasonItemAdapter.ViewHolder>
        implements OnInternalEvent, AppCMSBaseAdapter {

    private static final String TAG = "TraySeasonItemAdapter";

    private static final int SECONDS_PER_MINS = 60;
    protected List<ContentDatum> adapterData;
    protected List<Component> components;
    protected AppCMSPresenter appCMSPresenter;
    protected Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    protected String defaultAction;
    protected boolean isHistory;
    protected boolean isDownload;
    protected boolean isWatchlist;
    RecyclerView mRecyclerView;
    private List<OnInternalEvent> receivers;
    private int tintColor;
    private String userId;
    private InternalEvent<Integer> hideRemoveAllButtonEvent;
    private InternalEvent<Integer> showRemoveAllButtonEvent;
    private String moduleId;

    public AppCMSTraySeasonItemAdapter(Context context,
                                       List<ContentDatum> adapterData,
                                       List<Component> components,
                                       AppCMSPresenter appCMSPresenter,
                                       Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                       String viewType) {
        this.adapterData = adapterData;
        this.sortData();
        this.components = components;
        this.appCMSPresenter = appCMSPresenter;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.defaultAction = getDefaultAction(context);

        this.tintColor = Color.parseColor(getColor(context,
                appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()));

        this.receivers = new ArrayList<>();
    }

    private void sortData() {
        if (adapterData != null) {
            // TODO: 10/3/17 Positioning of elements in adapter will be sorted at a later date.
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tray_season_item, parent,
                false);
        AppCMSTraySeasonItemAdapter.ViewHolder viewHolder = new AppCMSTraySeasonItemAdapter.ViewHolder(view);
        applyStyles(viewHolder);
        return viewHolder;
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (adapterData != null && !adapterData.isEmpty()) {
            ContentDatum contentDatum = adapterData.get(position);

            StringBuffer imageUrl;

            if (contentDatum.getGist() != null) {
                imageUrl = new StringBuffer(holder.itemView.getContext()
                        .getString(R.string.app_cms_image_with_resize_query,
                                contentDatum.getGist().getVideoImageUrl(),
                                holder.appCMSEpisodeVideoImage.getWidth(),
                                holder.appCMSEpisodeVideoImage.getHeight()));
            } else {
                imageUrl = new StringBuffer();
            }

            loadImage(holder.itemView.getContext(), imageUrl.toString(), holder.appCMSEpisodeVideoImage);

            holder.itemView.setOnClickListener(v ->
                    click(adapterData.get(position)));
            holder.appCMSEpisodeButton.setOnClickListener(null);

            holder.appCMSEpisodeVideoImage.setOnClickListener(v -> click(adapterData.get(position)));

            holder.appCMSEpisodePlayButton.setOnClickListener(v ->
                    play(adapterData.get(position),
                            holder.itemView.getContext()
                                    .getString(R.string.app_cms_action_watchvideo_key)));

            if (contentDatum.getGist() != null) {
                holder.appCMSEpisodeTitle.setText(contentDatum.getGist().getTitle());
            }

            if (contentDatum.getGist() != null && contentDatum.getGist().getDescription() != null) {
                Spannable rawHtmlSpannable = new HtmlSpanner().fromHtml(contentDatum.getGist().getDescription());
                holder.appCMSEpisodeDescription.setText(rawHtmlSpannable);
            }

            holder.appCMSEpisodeTitle.setOnClickListener(v -> click(contentDatum));

            if (contentDatum.getGist() != null) {
                holder.appCMSEpisodeDuration
                        .setText(String.valueOf(contentDatum.getGist().getRuntime() / SECONDS_PER_MINS)
                                + " " + String.valueOf(holder.itemView.getContext().getString(R.string.mins_abbreviation)));
            }
            if (contentDatum.getGist().getWatchedPercentage() > 0) {
                holder.appCMSEpisodeProgress.setVisibility(View.VISIBLE);
                holder.appCMSEpisodeProgress.setProgress(contentDatum.getGist().getWatchedPercentage());
            } else {
                long watchedTime = contentDatum.getGist().getWatchedTime();
                long runTime = contentDatum.getGist().getRuntime();

                if (watchedTime > 0 && runTime > 0) {
                    long percentageWatched = watchedTime * 100 / runTime;
                    holder.appCMSEpisodeProgress.setProgress((int) percentageWatched);
                    holder.appCMSEpisodeProgress.setVisibility(View.VISIBLE);
                } else {
                    holder.appCMSEpisodeProgress.setVisibility(View.INVISIBLE);
                    holder.appCMSEpisodeProgress.setProgress(0);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return adapterData != null && !adapterData.isEmpty() ? adapterData.size() : 1;
    }

    @Override
    public void addReceiver(OnInternalEvent e) {
        //
    }

    @Override
    public void sendEvent(InternalEvent<?> event) {
        for (OnInternalEvent internalEvent : receivers) {
            internalEvent.receiveEvent(event);
        }
    }

    @Override
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public String getModuleId() {
        return moduleId;
    }

    private void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(Uri.decode(url))
                .into(imageView);
    }

    @Override
    public void receiveEvent(InternalEvent<?> event) {
        if (event.getEventData() instanceof List<?>) {
            try {
                adapterData = (List<ContentDatum>) event.getEventData();
            } catch (Exception e) {

            }
        } else {
            adapterData.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public void cancel(boolean cancel) {
        //
    }

    private String getHlsUrl(ContentDatum data) {
        if (data.getStreamingInfo() != null &&
                data.getStreamingInfo().getVideoAssets() != null &&
                data.getStreamingInfo().getVideoAssets().getHls() != null) {
            return data.getStreamingInfo().getVideoAssets().getHls();
        }
        return null;
    }

    private void applyStyles(AppCMSTraySeasonItemAdapter.ViewHolder viewHolder) {
        for (Component component : components) {
            AppCMSUIKeyType componentType = jsonValueKeyMap.get(component.getType());
            if (componentType == null) {
                componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }

            AppCMSUIKeyType componentKey = jsonValueKeyMap.get(component.getKey());
            if (componentKey == null) {
                componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }

            switch (componentType) {
                case PAGE_BUTTON_KEY:
                    switch (componentKey) {
                        case PAGE_PLAY_KEY:
                        case PAGE_PLAY_IMAGE_KEY:
                            viewHolder.appCMSEpisodePlayButton
                                    .setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(),
                                            R.drawable.play_icon));
                            viewHolder.appCMSEpisodePlayButton.getBackground().setTint(tintColor);
                            viewHolder.appCMSEpisodePlayButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);

                            break;

                        default:
                            break;
                    }
                    break;

                case PAGE_LABEL_KEY:
                case PAGE_TEXTVIEW_KEY:
                    int textColor = ContextCompat.getColor(viewHolder.itemView.getContext(),
                            R.color.colorAccent);
                    if (!TextUtils.isEmpty(component.getTextColor())) {
                        textColor = Color.parseColor(getColor(viewHolder.itemView.getContext(),
                                component.getTextColor()));
                    } else if (component.getStyles() != null) {
                        if (!TextUtils.isEmpty(component.getStyles().getColor())) {
                            textColor = Color.parseColor(getColor(viewHolder.itemView.getContext(),
                                    component.getStyles().getColor()));
                        } else if (!TextUtils.isEmpty(component.getStyles().getTextColor())) {
                            textColor =
                                    Color.parseColor(getColor(viewHolder.itemView.getContext(),
                                            component.getStyles().getTextColor()));
                        }
                    }

                    switch (componentKey) {
                        case PAGE_WATCHLIST_DURATION_KEY:
                            viewHolder.appCMSEpisodeDuration.setTextColor(textColor);
                            viewHolder.appCMSEpisodeSize.setTextColor(textColor);

                            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                                viewHolder.appCMSEpisodeDuration
                                        .setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(),
                                                component.getBackgroundColor())));
                                viewHolder.appCMSEpisodeSize
                                        .setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(),
                                                component.getBackgroundColor())));
                            }

                            if (!TextUtils.isEmpty(component.getFontFamily())) {
                                setTypeFace(viewHolder.itemView.getContext(),
                                        jsonValueKeyMap,
                                        component,
                                        viewHolder.appCMSEpisodeDuration);
                                setTypeFace(viewHolder.itemView.getContext(),
                                        jsonValueKeyMap,
                                        component,
                                        viewHolder.appCMSEpisodeSize);
                            }

                            if (component.getFontSize() != 0) {
                                viewHolder.appCMSEpisodeDuration.setTextSize(component.getFontSize());
                                viewHolder.appCMSEpisodeSize.setTextSize(component.getFontSize());
                            }
                            break;

                        case PAGE_API_TITLE:
                        case PAGE_EPISODE_TITLE_KEY:
                            viewHolder.appCMSEpisodeTitle.setTextColor(textColor);

                            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                                viewHolder.appCMSEpisodeTitle
                                        .setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(),
                                                component.getBackgroundColor())));
                            }

                            if (!TextUtils.isEmpty(component.getFontFamily())) {
                                setTypeFace(viewHolder.itemView.getContext(),
                                        jsonValueKeyMap,
                                        component,
                                        viewHolder.appCMSEpisodeTitle);
                            }

                            if (component.getFontSize() != 0) {
                                viewHolder.appCMSEpisodeTitle.setTextSize(component.getFontSize());
                            }
                            break;

                        case PAGE_API_DESCRIPTION:
                            viewHolder.appCMSEpisodeDescription.setTextColor(textColor);
                            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                                viewHolder.appCMSEpisodeDescription
                                        .setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(),
                                                component.getBackgroundColor())));
                            }

                            if (!TextUtils.isEmpty(component.getFontFamily())) {
                                setTypeFace(viewHolder.itemView.getContext(),
                                        jsonValueKeyMap,
                                        component,
                                        viewHolder.appCMSEpisodeDescription);
                            }

                            if (component.getFontSize() != 0) {
                                viewHolder.appCMSEpisodeTitle.setTextSize(component.getFontSize());
                            }
                            break;

                        default:
                            break;
                    }
                    break;

                case PAGE_SEPARATOR_VIEW_KEY:
                case PAGE_SEGMENTED_VIEW_KEY:
                    if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                        viewHolder.appCMSEpisodeSeparatorView
                                .setBackgroundColor(Color.parseColor(getColor(
                                        viewHolder.itemView.getContext(),
                                        component.getBackgroundColor())));
                    }
                    break;

                case PAGE_PROGRESS_VIEW_KEY:
                    viewHolder.appCMSEpisodeProgress.setMax(100);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void resetData(RecyclerView listView) {
        //
    }

    @Override
    public void updateData(RecyclerView listView, List<ContentDatum> contentData) {
        //
    }

    private void click(ContentDatum data) {
        //Log.d(TAG, "Clicked on item: " + data.getGist().getTitle());

        String permalink = data.getGist().getPermalink();
        String action = defaultAction;
        String title = data.getGist().getTitle();
        String hlsUrl = getHlsUrl(data);

        String[] extraData = new String[3];
        extraData[0] = permalink;
        extraData[1] = hlsUrl;
        extraData[2] = data.getGist().getId();

        List<String> relatedVideos = null;
        if (data.getContentDetails() != null &&
                data.getContentDetails().getRelatedVideoIds() != null) {
            relatedVideos = data.getContentDetails().getRelatedVideoIds();
        }
        //Log.d(TAG, "Launching " + permalink + ": " + action);
        if (!appCMSPresenter.launchButtonSelectedAction(permalink,
                action,
                title,
                extraData,
                data,
                false,
                -1,
                relatedVideos)) {
            //Log.e(TAG, "Could not launch action: " +
//                    " permalink: " +
//                    permalink +
//                    " action: " +
//                    action +
//                    " hlsUrl: " +
//                    hlsUrl);
        }
    }

    private void play(ContentDatum data, String action) {
        if (!appCMSPresenter.launchVideoPlayer(data,
                -1,
                null,
                data.getGist().getWatchedTime(),
                null)) {
            //Log.e(TAG, "Could not launch action: " +
//                    " action: " +
//                    action);
        }
    }

    private String getDefaultAction(Context context) {
        return context.getString(R.string.app_cms_action_detailvideopage_key);
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
            Typeface face;

            switch (fontWeight) {
                case PAGE_TEXT_BOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.opensans_bold_ttf));
                    break;

                case PAGE_TEXT_SEMIBOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.opensans_semibold_ttf));
                    break;

                case PAGE_TEXT_EXTRABOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.opensans_extrabold_ttf));
                    break;

                default:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.opensans_regular_ttf));
                    break;
            }
            textView.setTypeface(face);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;

        @BindView(R.id.app_cms_episode_button_view)
        LinearLayout appCMSEpisodeButton;

        @BindView(R.id.app_cms_episode_video_image)
        ImageButton appCMSEpisodeVideoImage;

        @BindView(R.id.app_cms_episode_play_button)
        ImageButton appCMSEpisodePlayButton;

        @BindView(R.id.app_cms_episode_title)
        TextView appCMSEpisodeTitle;

        @BindView(R.id.app_cms_episode_description)
        TextView appCMSEpisodeDescription;

        @BindView(R.id.app_cms_episode_video_size)
        TextView appCMSEpisodeSize;

        @BindView(R.id.app_cms_episode_separator_view)
        View appCMSEpisodeSeparatorView;

        @BindView(R.id.app_cms_episode_duration)
        TextView appCMSEpisodeDuration;

        @BindView(R.id.app_cms_episode_download_status_button)
        ImageView appCMSEpisodeDownloadStatusButton;

        @BindView(R.id.app_cms_episode_progress)
        ProgressBar appCMSEpisodeProgress;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
