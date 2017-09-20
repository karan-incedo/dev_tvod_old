package com.viewlift.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.UiThread;
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
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.viewlift.models.data.appcms.downloads.DownloadVideoRealm;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.InternalEvent;
import com.viewlift.views.customviews.OnInternalEvent;

import net.nightwhistler.htmlspanner.HtmlSpanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by viewlift on 7/7/17.
 */

public class AppCMSTrayItemAdapter extends RecyclerView.Adapter<AppCMSTrayItemAdapter.ViewHolder>
        implements OnInternalEvent, AppCMSBaseAdapter {
    private static final String TAG = "AppCMSTrayAdapter";

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

    public AppCMSTrayItemAdapter(Context context,
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

        switch (jsonValueKeyMap.get(viewType)) {
            case PAGE_HISTORY_MODULE_KEY:
                this.isHistory = true;
                break;

            case PAGE_DOWNLOAD_MODULE_KEY:
                this.isDownload = true;
                break;

            case PAGE_WATCHLIST_MODULE_KEY:
                this.isWatchlist = true;
                break;

            default:
                break;
        }

        this.receivers = new ArrayList<>();
        this.tintColor = Color.parseColor(getColor(context,
                appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()));
        this.userId = appCMSPresenter.getLoggedInUser();

        this.hideRemoveAllButtonEvent = new InternalEvent<>(View.GONE);
        this.showRemoveAllButtonEvent = new InternalEvent<>(View.VISIBLE);

        this.setHasStableIds(false);

        sortData();
    }

    private void sortData() {
        if (adapterData != null) {
            if (isWatchlist || isDownload) {
                Collections.sort(adapterData, (o1, o2) -> Long.compare(o1.getAddedDate(),
                        o2.getAddedDate()));
            } else if (isHistory) {
                Collections.sort(adapterData, (o1, o2) -> Long.compare(o1.getUpdateDate(),
                        o2.getUpdateDate()));
                Collections.reverse(adapterData);
            }
        }
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

    /**
     * Important function to have reference of recyclerView for invalidating the Recycler views
     * items at the time of download progress going on (multi Download)
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @UiThread
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (adapterData != null && !adapterData.isEmpty()) {
            ContentDatum contentDatum = adapterData.get(position);

            StringBuffer imageUrl;
            if (isDownload) {
                if (contentDatum.getGist() != null && contentDatum.getGist().getVideoImageUrl() != null) {
                    imageUrl = new StringBuffer(contentDatum.getGist().getVideoImageUrl());
                } else {
                    imageUrl = new StringBuffer();
                }

                holder.appCMSContinueWatchingSize.setVisibility(View.VISIBLE);
                holder.appCMSContinueWatchingSize.setText(appCMSPresenter.getDownloadedFileSize(contentDatum.getGist().getId()));

                if (contentDatum.getGist() != null) {
                    switch (contentDatum.getGist().getDownloadStatus()) {
                        case STATUS_PENDING:
                        case STATUS_RUNNING:
                            if (contentDatum.getGist() != null) {
                                holder.appCMSContinueWatchingDeleteButton.getBackground().setTint(ContextCompat.getColor(holder.itemView.getContext(), R.color.transparentColor));
                                holder.appCMSContinueWatchingDeleteButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);

                                appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                                        holder.appCMSContinueWatchingDeleteButton,
                                        appCMSPresenter,
                                        userVideoDownloadStatus -> {
                                            if (userVideoDownloadStatus != null) {
                                                if (userVideoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_SUCCESSFUL) {
                                                    holder.appCMSContinueWatchingDeleteButton.setImageBitmap(null);
                                                    holder.appCMSContinueWatchingDeleteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_deleteicon));
                                                    holder.appCMSContinueWatchingDeleteButton.getBackground().setTint(tintColor);
                                                    holder.appCMSContinueWatchingDeleteButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                                                    holder.appCMSContinueWatchingDeleteButton.invalidate();
                                                    loadImage(holder.itemView.getContext(), userVideoDownloadStatus.getThumbUri(), holder.appCMSContinueWatchingVideoImage);
                                                    holder.appCMSContinueWatchingSize.setText(appCMSPresenter.getDownloadedFileSize(userVideoDownloadStatus.getVideoSize()));

                                                    contentDatum.getGist().setLocalFileUrl(userVideoDownloadStatus.getVideoUri());
                                                    try {
                                                        if (userVideoDownloadStatus.getSubtitlesUri().trim().length() > 10 &&
                                                                contentDatum.getContentDetails() != null &&
                                                                contentDatum.getContentDetails().getClosedCaptions().get(0) != null) {
                                                            contentDatum.getContentDetails().getClosedCaptions().get(0).setUrl(userVideoDownloadStatus.getSubtitlesUri());
                                                        }
                                                    } catch (Exception e) {
                                                        Log.e(TAG, e.getMessage());
                                                    }

                                                } else if (userVideoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_INTERRUPTED) {
                                                    holder.appCMSContinueWatchingDeleteButton.setImageBitmap(null);
                                                    holder.appCMSContinueWatchingDeleteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),
                                                            android.R.drawable.stat_sys_warning));
                                                    holder.appCMSContinueWatchingSize.setText("Remove".toUpperCase());
                                                    holder.appCMSContinueWatchingSize.setOnClickListener(v -> delete(contentDatum, position));
                                                } else if (userVideoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_RUNNING) {
                                                    holder.appCMSContinueWatchingSize.setText("Cancel");
                                                }
                                                contentDatum.getGist().setDownloadStatus(userVideoDownloadStatus.getDownloadStatus());
                                            }
                                        },
                                        userId, true);

                                holder.appCMSContinueWatchingSize.setText("Cancel".toUpperCase());
                                holder.appCMSContinueWatchingSize.setOnClickListener(v -> delete(contentDatum, position));

                            }
                            break;

                        case STATUS_FAILED:
                            //
                            break;

                        case STATUS_SUCCESSFUL:
                            holder.appCMSContinueWatchingDeleteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),
                                    R.drawable.ic_deleteicon));
                            holder.appCMSContinueWatchingDeleteButton.getBackground().setTint(tintColor);
                            holder.appCMSContinueWatchingDeleteButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                            appCMSPresenter.sendRefreshPageAction();
                            break;

                        case STATUS_INTERRUPTED:
                            holder.appCMSContinueWatchingDeleteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),
                                    android.R.drawable.stat_sys_warning));
                            holder.appCMSContinueWatchingSize.setText("Remove".toUpperCase());
                            holder.appCMSContinueWatchingSize.setOnClickListener(v -> delete(contentDatum, position));
                            break;

                        default:
                            break;
                    }
                    DownloadVideoRealm downloadVideoRealm = appCMSPresenter.getRealmController()
                            .getDownloadByIdBelongstoUser(contentDatum.getGist().getId(), userId);
                    if (downloadVideoRealm != null && contentDatum != null && contentDatum.getGist() != null) {
                        if (downloadVideoRealm.getWatchedTime() > contentDatum.getGist().getWatchedTime()) {
                            contentDatum.getGist().setWatchedTime(downloadVideoRealm.getWatchedTime());
                        }
                    }
                }

            } else {
                if (contentDatum.getGist() != null) {
                    imageUrl = new StringBuffer(holder.itemView.getContext().getString(R.string.app_cms_image_with_resize_query,
                            contentDatum.getGist().getVideoImageUrl(),
                            holder.appCMSContinueWatchingVideoImage.getWidth(),
                            holder.appCMSContinueWatchingVideoImage.getHeight()));
                } else {
                    imageUrl = new StringBuffer();
                }
            }
            loadImage(holder.itemView.getContext(), imageUrl.toString(), holder.appCMSContinueWatchingVideoImage);

            holder.itemView.setOnClickListener(v -> {
                if (isDownload) {
                    playDownloaded(contentDatum,
                            holder.itemView.getContext(),
                            position);
                } else {
                    click(adapterData.get(position));
                }
            });
            holder.appCMSContinueWatchingButton.setOnClickListener(null);

            holder.appCMSContinueWatchingVideoImage.setOnClickListener(v -> {
                if (isDownload) {
                    playDownloaded(contentDatum,
                            holder.itemView.getContext(),
                            position);
                } else {
                    click(adapterData.get(position));
                }
            });

            holder.appCMSContinueWatchingPlayButton.setOnClickListener(v -> {
                if (isDownload) {
                    playDownloaded(contentDatum,
                            holder.itemView.getContext(),
                            position);
                } else {
                    play(adapterData.get(position),
                            holder.itemView.getContext()
                                    .getString(R.string.app_cms_action_watchvideo_key));
                }
            });

            if (contentDatum.getGist() != null) {
                holder.appCMSContinueWatchingTitle.setText(contentDatum.getGist().getTitle());
            }

            if (contentDatum != null && contentDatum.getGist() != null && contentDatum.getGist().getDescription() != null) {
                Spannable rawHtmlSpannable = new HtmlSpanner().fromHtml(contentDatum.getGist().getDescription());
                holder.appCMSContinueWatchingDescription.setText(rawHtmlSpannable);
            }

            holder.appCMSContinueWatchingDeleteButton.setOnClickListener(v -> delete(contentDatum, position));

            holder.appCMSContinueWatchingTitle.setOnClickListener(v -> {
                if (isDownload) {
                    playDownloaded(contentDatum,
                            holder.itemView.getContext(),
                            position);
                } else {
                    click(contentDatum);
                }
            });

            if (contentDatum.getGist() != null) {
                holder.appCMSContinueWatchingDuration.setText(String.valueOf(contentDatum.getGist().getRuntime() / SECONDS_PER_MINS)
                        + " " + String.valueOf(holder.itemView.getContext().getString(R.string.mins_abbreviation)));
            }
            if (contentDatum.getGist().getWatchedPercentage() > 0) {
                holder.appCMSContinueWatchingProgress.setVisibility(View.VISIBLE);
                holder.appCMSContinueWatchingProgress
                        .setProgress(contentDatum.getGist().getWatchedPercentage());
            } else {
                long watchedTime =
                        contentDatum.getGist().getWatchedTime();
                long runTime =
                        contentDatum.getGist().getRuntime();
                if (watchedTime > 0 && runTime > 0) {
                    long percentageWatched = watchedTime * 100 / runTime;
                    holder.appCMSContinueWatchingProgress
                            .setProgress((int) percentageWatched);
                    holder.appCMSContinueWatchingProgress.setVisibility(View.VISIBLE);
                } else {
                    holder.appCMSContinueWatchingProgress.setVisibility(View.INVISIBLE);
                    holder.appCMSContinueWatchingProgress.setProgress(0);
                }
            }

        } else {
            holder.appCMSNotItemLabel.setVisibility(View.VISIBLE);
            holder.appCMSNotItemLabel.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                    .getBrand().getGeneral().getTextColor()));
            if (isHistory) {
                holder.appCMSNotItemLabel.setText(holder.itemView.getContext().getString(R.string.empty_history_list_message));
            } else if (isDownload) {
                holder.appCMSNotItemLabel.setText(holder.itemView.getContext().getString(R.string.empty_download_message));
            } else {
                holder.appCMSNotItemLabel.setText(holder.itemView.getContext().getString(R.string.empty_watchlist_message));
            }
            holder.appCMSContinueWatchingVideoImage.setVisibility(View.GONE);
            holder.appCMSContinueWatchingPlayButton.setVisibility(View.GONE);
            holder.appCMSContinueWatchingTitle.setVisibility(View.GONE);
            holder.appCMSContinueWatchingDescription.setVisibility(View.GONE);
            holder.appCMSContinueWatchingDeleteButton.setVisibility(View.GONE);
            holder.appCMSContinueWatchingDuration.setVisibility(View.GONE);
            holder.appCMSContinueWatchingSize.setVisibility(View.GONE);
            holder.appCMSContinueWatchingSeparatorView.setVisibility(View.GONE);
            holder.appCMSContinueWatchingProgress.setVisibility(View.GONE);
            holder.appCMSContinueWatchingDownloadStatusButton.setVisibility(View.GONE);
        }
    }

    private ContentDatum getNextContentDatum(int position) {
        if (position + 1 == adapterData.size()) {
            return null;
        }
        ContentDatum contentDatum = adapterData.get(++position);
        if (contentDatum.getGist() != null) {
            if (!contentDatum.getGist().getDownloadStatus().equals(DownloadStatus.STATUS_SUCCESSFUL)) {
                getNextContentDatum(position);
            }
        }
        return contentDatum;
    }

    /**
     * Return list of ids of all the completely downloaded movies which fall after the supplied
     * position
     *
     * @param position position after which movies are to be fetched for autoplay
     * @return list of ids of upcoming completed movies
     */
    private List<String> getListOfUpcomingMovies(int position) {
        if (position + 1 == adapterData.size()) {
            return Collections.emptyList();
        }

        List<String> contentDatumList = new ArrayList<>();
        for (int i = position + 1; i < adapterData.size(); i++) {
            ContentDatum contentDatum = adapterData.get(i);
            if (contentDatum.getGist() != null &&
                    contentDatum.getGist().getDownloadStatus().equals(DownloadStatus.STATUS_SUCCESSFUL)) {
                contentDatumList.add(contentDatum.getGist().getId());
            }
        }

        return contentDatumList;
    }

    private void playDownloaded(ContentDatum data, Context context, int position) {
        List<String> relatedVideoIds = getListOfUpcomingMovies(position);
        if (data.getGist().getDownloadStatus() != DownloadStatus.STATUS_SUCCESSFUL) {
            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.DOWNLOAD_INCOMPLETE,
                    null,
                    false,
                    null);
            return;
        }
        boolean networkAvailable = appCMSPresenter.isNetworkConnected();
        String permalink = data.getGist().getPermalink();
        String action = context.getString(R.string.app_cms_action_watchvideo_key);
        String title = data.getGist() != null ? data.getGist().getTitle() : null;
        String hlsUrl = data.getGist().getLocalFileUrl();
        String[] extraData = new String[4];
        extraData[0] = permalink;
        extraData[1] = hlsUrl;
        extraData[2] = data.getGist() != null ? data.getGist().getId() : null;
        extraData[3] = "true"; // to know that this is an offline video
        Log.d(TAG, "Launching " + permalink + ": " + action + ":File:" + data.getGist().getLocalFileUrl());

        if (permalink == null ||
                hlsUrl == null ||
                extraData[2] == null ||
                !appCMSPresenter.launchButtonSelectedAction(
                        permalink,
                        action,
                        title,
                        extraData,
                        data,
                        false,
                        -1,
                        relatedVideoIds)) {
            Log.e(TAG, "Could not launch action: " +
                    " permalink: " +
                    permalink +
                    " action: " +
                    action +
                    " hlsUrl: " +
                    hlsUrl);
        }
    }

    @Override
    public int getItemCount() {
        return adapterData != null && !adapterData.isEmpty() ? adapterData.size() : 1;
    }

    @Override
    public void addReceiver(OnInternalEvent e) {
        receivers.add(e);
        if (adapterData == null || adapterData.isEmpty()) {
            sendEvent(hideRemoveAllButtonEvent);
        } else {
            sendEvent(showRemoveAllButtonEvent);
        }
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

            switch (componentType) {
                case PAGE_BUTTON_KEY:
                    switch (componentKey) {
                        case PAGE_PLAY_KEY:
                        case PAGE_PLAY_IMAGE_KEY:
                            viewHolder.appCMSContinueWatchingPlayButton.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.play_icon));
                            viewHolder.appCMSContinueWatchingPlayButton.getBackground().setTint(tintColor);
                            viewHolder.appCMSContinueWatchingPlayButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);

                            break;

                        default:
                            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                                viewHolder.appCMSContinueWatchingDeleteButton
                                        .setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(),
                                                component.getBackgroundColor())));
                            } else {
                                applyBorderToComponent(viewHolder.itemView.getContext(),
                                        viewHolder.appCMSContinueWatchingDeleteButton,
                                        component);
                            }
                            viewHolder.appCMSContinueWatchingDeleteButton.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.ic_deleteicon));
                            viewHolder.appCMSContinueWatchingDeleteButton.getBackground().setTint(tintColor);
                            viewHolder.appCMSContinueWatchingDeleteButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                            break;
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
                        case PAGE_WATCHLIST_DURATION_KEY:
                            viewHolder.appCMSContinueWatchingDuration.setTextColor(textColor);
                            viewHolder.appCMSContinueWatchingSize.setTextColor(textColor);
                            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                                viewHolder.appCMSContinueWatchingDuration.setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(), component.getBackgroundColor())));
                                viewHolder.appCMSContinueWatchingSize.setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(), component.getBackgroundColor())));
                            }
                            if (!TextUtils.isEmpty(component.getFontFamily())) {
                                setTypeFace(viewHolder.itemView.getContext(),
                                        jsonValueKeyMap,
                                        component,
                                        viewHolder.appCMSContinueWatchingDuration);
                                setTypeFace(viewHolder.itemView.getContext(),
                                        jsonValueKeyMap,
                                        component,
                                        viewHolder.appCMSContinueWatchingSize);
                            }

                            if (component.getFontSize() != 0) {
                                viewHolder.appCMSContinueWatchingDuration.setTextSize(component.getFontSize());
                                viewHolder.appCMSContinueWatchingSize.setTextSize(component.getFontSize());
                            }
                            break;

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

                            if (component.getFontSize() != 0) {
                                viewHolder.appCMSContinueWatchingTitle.setTextSize(component.getFontSize());
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

                            if (component.getFontSize() != 0) {
                                viewHolder.appCMSContinueWatchingTitle.setTextSize(component.getFontSize());
                            }
                            break;

                        default:
                            break;
                    }
                    break;

                case PAGE_SEPARATOR_VIEW_KEY:
                case PAGE_SEGMENTED_VIEW_KEY:
                    if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                        viewHolder.appCMSContinueWatchingSeparatorView
                                .setBackgroundColor(Color.parseColor(getColor(
                                        viewHolder.itemView.getContext(),
                                        component.getBackgroundColor())));
                    }
                    break;

                case PAGE_PROGRESS_VIEW_KEY:
                    viewHolder.appCMSContinueWatchingProgress.setMax(100);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void resetData(RecyclerView listView) {
        if (isHistory) {
            appCMSPresenter.getHistoryData(appCMSHistoryResult -> {
                if (appCMSHistoryResult != null) {
                    listView.setAdapter(null);
                    List<ContentDatum> adapterDataTmp;
                    adapterData = appCMSHistoryResult.convertToAppCMSPageAPI(null).getModules()
                            .get(0).getContentData();
                    if (adapterData != null) {
                        adapterDataTmp = new ArrayList<>(adapterData);
                    } else {
                        adapterDataTmp = new ArrayList<>();
                    }
                    adapterData = null;
                    notifyDataSetChanged();
                    adapterData = adapterDataTmp;
                    sortData();
                    notifyDataSetChanged();
                    listView.setAdapter(this);
                    listView.invalidate();
                }
            });
        } else if (isWatchlist) {
            appCMSPresenter.getWatchlistData(appCMSWatchlistResult -> {
                if (appCMSWatchlistResult != null) {
                    listView.setAdapter(null);
                    List<ContentDatum> adapterDataTmp;
                    adapterData = appCMSWatchlistResult.convertToAppCMSPageAPI(null).getModules()
                            .get(0).getContentData();
                    if (adapterData != null) {
                        adapterDataTmp = new ArrayList<>(adapterData);
                    } else {
                        adapterDataTmp = new ArrayList<>();
                    }
                    adapterData = null;
                    notifyDataSetChanged();
                    adapterData = adapterDataTmp;
                    sortData();
                    notifyDataSetChanged();
                    listView.setAdapter(this);
                    listView.invalidate();
                }
            });
        }
    }

    @Override
    public void updateData(RecyclerView listView, List<ContentDatum> contentData) {
        adapterData = contentData;
        sortData();
        if (adapterData == null || adapterData.isEmpty()) {
            sendEvent(hideRemoveAllButtonEvent);
        } else {
            sendEvent(showRemoveAllButtonEvent);
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
                data.getGist().getWatchedTime())) {
            Log.e(TAG, "Could not launch action: " +
                    " action: " +
                    action);
        }
    }

    private String getDefaultAction(Context context) {
        return context.getString(R.string.app_cms_action_videopage_key);
    }

    private void showDelete(ContentDatum contentDatum) {
        Log.d(TAG, "Show delete button");
    }

    private void delete(final ContentDatum contentDatum, int position) {
        if ((isHistory) && (contentDatum.getGist() != null)) {
            Log.d(TAG, "Deleting history item: " + contentDatum.getGist().getTitle());
            appCMSPresenter.editHistory(contentDatum.getGist().getId(),
                    appCMSDeleteHistoryResult -> {
                        adapterData.remove(contentDatum);
                        notifyDataSetChanged();
                    }, false);
        }

        if ((isDownload) && (contentDatum.getGist() != null)) {
            Log.d(TAG, "Deleting download item: " + contentDatum.getGist().getTitle());
            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.DELETE_ONE_DOWNLOAD_ITEM,
                    appCMSPresenter.getCurrentActivity().getString(R.string.app_cms_delete_one_download_item_message),
                    true, () ->
                            appCMSPresenter.removeDownloadedFile(contentDatum.getGist().getId(),
                                    userVideoDownloadStatus -> {

                                        ((ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position))
                                                .appCMSContinueWatchingDeleteButton.setImageBitmap(null);
                                        notifyItemRangeRemoved(position, getItemCount());
                                        adapterData.remove(contentDatum);
                                        notifyItemRangeChanged(position, getItemCount());
                                    }));
        }

        if ((isWatchlist) && (contentDatum.getGist() != null)) {
            Log.d(TAG, "Deleting watchlist item: " + contentDatum.getGist().getTitle());
            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.DELETE_ONE_WATCHLIST_ITEM,
                    appCMSPresenter.getCurrentActivity().getString(R.string.app_cms_delete_one_watchlist_item_message),
                    true, () ->
                            appCMSPresenter.editWatchlist(contentDatum.getGist().getId(),
                                    addToWatchlistResult -> {
                                        adapterData.remove(contentDatum);
                                        notifyDataSetChanged();
                                    }, false));
        }
    }

    private void applyBorderToComponent(Context context, View view, Component component) {
        if (component.getBorderWidth() != 0 && component.getBorderColor() != null) {
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
            Typeface face;

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
                    break;
            }
            textView.setTypeface(face);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;

        @BindView(R.id.app_cms_continue_watching_button_view)
        LinearLayout appCMSContinueWatchingButton;

        @BindView(R.id.app_cms_continue_watching_video_image)
        ImageButton appCMSContinueWatchingVideoImage;

        @BindView(R.id.app_cms_continue_watching_play_button)
        ImageButton appCMSContinueWatchingPlayButton;

        @BindView(R.id.app_cms_continue_watching_title)
        TextView appCMSContinueWatchingTitle;

        @BindView(R.id.app_cms_continue_watching_description)
        TextView appCMSContinueWatchingDescription;

        @BindView(R.id.app_cms_continue_watching_delete_button)
        ImageButton appCMSContinueWatchingDeleteButton;

        @BindView(R.id.app_cms_continue_watching_video_size)
        TextView appCMSContinueWatchingSize;

        @BindView(R.id.app_cms_continue_watching_separator_view)
        View appCMSContinueWatchingSeparatorView;

        @BindView(R.id.app_cms_not_item_label)
        TextView appCMSNotItemLabel;

        @BindView(R.id.app_cms_continue_watching_duration)
        TextView appCMSContinueWatchingDuration;

        @BindView(R.id.app_cms_watchlist_download_status_button)
        ImageView appCMSContinueWatchingDownloadStatusButton;

        @BindView(R.id.app_cms_continue_watching_progress)
        ProgressBar appCMSContinueWatchingProgress;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
