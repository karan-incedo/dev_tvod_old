package com.viewlift.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
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
    }

    private void sortData() {
        if (adapterData != null) {
            //
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
                holder.appCMSEpisodeDuration.setText(String.valueOf(contentDatum.getGist().getRuntime() / SECONDS_PER_MINS)
                        + " " + String.valueOf(holder.itemView.getContext().getString(R.string.mins_abbreviation)));
            }
            if (contentDatum.getGist().getWatchedPercentage() > 0) {
                holder.appCMSEpisodeProgress.setVisibility(View.VISIBLE);
                holder.appCMSEpisodeProgress.setProgress(contentDatum.getGist().getWatchedPercentage());
            } else {
                long watchedTime =
                        contentDatum.getGist().getWatchedTime();
                long runTime =
                        contentDatum.getGist().getRuntime();
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

    private void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(Uri.decode(url))
                .into(imageView);
    }

    @Override
    public void receiveEvent(InternalEvent<?> event) {
        adapterData.clear();
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
        //
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
        Log.d(TAG, "Clicked on item: " + data.getGist().getTitle());
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
        Log.d(TAG, "Launching " + permalink + ": " + action);
        if (!appCMSPresenter.launchButtonSelectedAction(permalink,
                action,
                title,
                extraData,
                data,
                false,
                -1,
                relatedVideos)) {
            Log.e(TAG, "Could not launch action: " +
                    " permalink: " +
                    permalink +
                    " action: " +
                    action +
                    " hlsUrl: " +
                    hlsUrl);
        }
    }

    private void play(ContentDatum data, String action) {
        if (!appCMSPresenter.launchVideoPlayer(data,
                -1,
                null,
                data.getGist().getWatchedTime(),
                null)) {
            Log.e(TAG, "Could not launch action: " +
                    " action: " +
                    action);
        }
    }

    private String getDefaultAction(Context context) {
        return context.getString(R.string.app_cms_action_detailvideopage_key);
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
