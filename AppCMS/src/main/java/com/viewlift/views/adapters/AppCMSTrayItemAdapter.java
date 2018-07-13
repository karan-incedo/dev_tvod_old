package com.viewlift.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.viewlift.Audio.model.MusicLibrary;
import com.viewlift.Audio.playback.AudioPlaylistHelper;
import com.viewlift.Audio.playback.PlaybackManager;
import com.viewlift.R;
import com.viewlift.casting.CastServiceProvider;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.StreamingInfo;
import com.viewlift.models.data.appcms.audio.AppCMSAudioDetailResult;
import com.viewlift.models.data.appcms.audio.AudioAssets;
import com.viewlift.models.data.appcms.audio.Mp3;
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.viewlift.models.data.appcms.downloads.DownloadVideoRealm;
import com.viewlift.models.data.appcms.playlist.AppCMSPlaylistResult;
import com.viewlift.models.data.appcms.playlist.AudioList;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.activity.AppCMSPlayAudioActivity;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.DownloadModule;
import com.viewlift.views.customviews.InternalEvent;
import com.viewlift.views.customviews.OnInternalEvent;
import com.viewlift.views.customviews.ViewCreator;
import com.viewlift.views.rxbus.DownloadTabSelectorBus;

//import net.nightwhistler.htmlspanner.HtmlSpanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

import static com.viewlift.Audio.ui.PlaybackControlsFragment.EXTRA_CURRENT_MEDIA_DESCRIPTION;

/*
 * Created by viewlift on 7/7/17.
 */

public class AppCMSTrayItemAdapter extends RecyclerView.Adapter<AppCMSTrayItemAdapter.ViewHolder>
        implements OnInternalEvent, AppCMSBaseAdapter {
    private static final String TAG = "AppCMSPresenter";

    private static final int SECONDS_PER_MIN = 60;

    protected ViewCreator.CollectionGridItemViewCreator collectionGridItemViewCreator;
    protected List<Component> components;
    protected AppCMSPresenter appCMSPresenter;
    protected Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private RecyclerView mRecyclerView;
    private List<ContentDatum> adapterData;
    private String defaultAction;
    private boolean isHistory;
    private boolean isDownload;
    private boolean isWatchlist;
    private List<OnInternalEvent> receivers;
    private int tintColor;
    private String userId;
    private InternalEvent<Integer> hideRemoveAllButtonEvent;
    private InternalEvent<Integer> showRemoveAllButtonEvent;

    private Map<String, Boolean> filmDownloadIconUpdatedMap;

    private String moduleId;
    private String mCurrentPlayListId;

    boolean isLoaded = false;

    public AppCMSTrayItemAdapter(Context context,
                                 ViewCreator.CollectionGridItemViewCreator collectionGridItemViewCreator,
                                 List<ContentDatum> adapterData,
                                 List<Component> components,
                                 AppCMSPresenter appCMSPresenter,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 String viewType,
                                 RecyclerView listView) {
        this.collectionGridItemViewCreator = collectionGridItemViewCreator;
        this.adapterData = adapterData;
        this.sortData();
        this.components = components;
        this.appCMSPresenter = appCMSPresenter;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.defaultAction = getDefaultAction(context);
        isLoaded = false;
        switch (jsonValueKeyMap.get(viewType)) {
            case PAGE_HISTORY_01_MODULE_KEY:
            case PAGE_HISTORY_02_MODULE_KEY:
                this.isHistory = true;
                Log.e(TAG, "Created new history tray");
                break;

            case PAGE_DOWNLOAD_01_MODULE_KEY:
            case PAGE_DOWNLOAD_02_MODULE_KEY:
                Log.e(TAG, "Created new download tray");
                this.isDownload = true;
                break;

            case PAGE_WATCHLIST_01_MODULE_KEY:
            case PAGE_WATCHLIST_02_MODULE_KEY:
                this.isWatchlist = true;
                break;

            default:
                break;
        }

        this.receivers = new ArrayList<>();
        this.tintColor = appCMSPresenter.getBrandPrimaryCtaColor();
        this.userId = appCMSPresenter.getLoggedInUser();

        this.hideRemoveAllButtonEvent = new InternalEvent<>(View.GONE);
        this.showRemoveAllButtonEvent = new InternalEvent<>(View.VISIBLE);

        this.filmDownloadIconUpdatedMap = new HashMap<>();

        if (isDownload) {
            DownloadTabSelectorBus.instanceOf().getSelectedTab().subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    if (o instanceof Integer) {
                        if ((int) o == DownloadModule.VIDEO_TAB) {
                            updateData(mRecyclerView, appCMSPresenter.getDownloadedMedia(context.getString(R.string.content_type_video)));
                        }
                        if ((int) o == DownloadModule.AUDIO_TAB) {

                            updateData(mRecyclerView, appCMSPresenter.getDownloadedMedia(context.getString(R.string.content_type_audio)));
                        }
                    }
                }
            });
        }

        createDownloadedAudioPlaylist(context);
    }

    private void sortData() {
        try {
            if (adapterData != null) {
                if (isWatchlist || isDownload) {
                    sortByAddedDate();
                } else if (isHistory) {
                    sortByMostRecentlyWatchedDate();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sortByAddedDate() {
        Collections.sort(adapterData, (o1, o2) -> Long.compare(o1.getAddedDate(),
                o2.getAddedDate()));
    }

    private void sortByMostRecentlyWatchedDate() {
        Collections.sort(adapterData, (o1, o2) -> Long.compare(Long.parseLong(o1.getGist().getUpdateDate()),
                Long.parseLong(o2.getGist().getUpdateDate())));
        Collections.reverse(adapterData);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.continue_watching_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        applyStyles(viewHolder);

        Log.e(TAG, "Created new view holder");

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
        StringBuilder logMessage = new StringBuilder("Binding tray item adapter view holder: \n\t" + position);
        if (adapterData != null && !adapterData.isEmpty() && position < adapterData.size()) {
            ContentDatum contentDatum = adapterData.get(position);
            if (contentDatum.getGist() != null) {
                if (contentDatum.getGist().getId() != null) {
                    logMessage.append("\n\t" + contentDatum.getGist().getId());
                }
                if (contentDatum.getGist().getTitle() != null) {
                    logMessage.append("\n\t" + contentDatum.getGist().getTitle());
                }
                if (contentDatum.getGist().getVideoImageUrl() != null) {
                    logMessage.append("\n\t" + contentDatum.getGist().getVideoImageUrl());
                }
            }
        }
        Log.e(TAG, logMessage.toString());

        if (adapterData != null && adapterData.size() == 0) {
            sendEvent(hideRemoveAllButtonEvent);
        }

        if (adapterData != null && !adapterData.isEmpty() && position < adapterData.size()) {
            ContentDatum contentDatum = adapterData.get(position);

            StringBuffer imageUrl;
            if (isDownload) {
                if (contentDatum.getGist() != null &&
                        contentDatum.getGist().getMediaType() != null &&
                        contentDatum.getGist().getMediaType().toLowerCase().contains(holder.itemView.getContext().getString(R.string.media_type_audio).toLowerCase()) && contentDatum.getGist().getPosterImageUrl() != null) {
                    imageUrl = new StringBuffer(contentDatum.getGist().getPosterImageUrl());

                } else if (contentDatum.getGist() != null && contentDatum.getGist().getVideoImageUrl() != null) {
                    imageUrl = new StringBuffer(contentDatum.getGist().getVideoImageUrl());
                } else {
                    imageUrl = new StringBuffer();
                }

                holder.appCMSContinueWatchingSize.setVisibility(View.VISIBLE);
                holder.appCMSContinueWatchingSize.setText(appCMSPresenter.getDownloadedFileSize(contentDatum.getGist().getId()));
                int radiusDifference = 5;
                if (BaseView.isTablet(holder.itemView.getContext())) {
                    radiusDifference = 3;
                }

                if (contentDatum.getGist() != null) {
                    holder.appCMSContinueWatchingDeleteButton.setTag(contentDatum.getGist().getId());
                    contentDatum.getGist().setDataLoaded(true);
//                    if (appCMSPresenter.isVideoDownloaded(contentDatum.getGist().getId())) {
//                        holder.appCMSContinueWatchingDeleteButton.setImageBitmap(null);
//                        holder.appCMSContinueWatchingDeleteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_deleteicon));
//                        holder.appCMSContinueWatchingDeleteButton.getBackground().setTint(tintColor);
//                        holder.appCMSContinueWatchingDeleteButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
//                        holder.appCMSContinueWatchingDeleteButton.invalidate();
//                        holder.appCMSContinueWatchingSize.setText(appCMSPresenter.getDownloadedFileSize(contentDatum.getGist().getId()));
//
////                        contentDatum.getGist().setLocalFileUrl(userVideoDownloadStatus.getVideoUri());
////                        try {
////                            if (userVideoDownloadStatus.getSubtitlesUri().trim().length() > 10 &&
////                                    contentDatum.getContentDetails() != null &&
////                                    contentDatum.getContentDetails().getClosedCaptions().get(0) != null) {
////                                contentDatum.getContentDetails().getClosedCaptions().get(0).setUrl(userVideoDownloadStatus.getSubtitlesUri());
////                            }
////                        } catch (Exception e) {
////                            //Log.e(TAG, e.getMessage());
////                        }
//                    } else // if (holder.appCMSContinueWatchingSize.getTag() != null && (boolean) holder.appCMSContinueWatchingSize.getTag()) {
                    {
                        appCMSPresenter.getUserVideoDownloadStatus(contentDatum.getGist().getId(),
                                videoDownloadStatus -> {

                                    if (videoDownloadStatus != null &&
                                            (videoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_PAUSED ||
                                                    videoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_PENDING ||
                                                    (!appCMSPresenter.isNetworkConnected() &&
                                                            videoDownloadStatus.getDownloadStatus() != DownloadStatus.STATUS_COMPLETED &&
                                                            videoDownloadStatus.getDownloadStatus() != DownloadStatus.STATUS_SUCCESSFUL))) {
                                        holder.appCMSContinueWatchingDeleteButton.setImageBitmap(null);
                                        holder.appCMSContinueWatchingDeleteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),
                                                R.drawable.ic_download_queued));
                                    }
                                },
                                appCMSPresenter.getLoggedInUser());
                    }

                    switch (appCMSPresenter.getVideoDownloadStatus(contentDatum.getGist().getId())) {
                        case STATUS_PENDING:
                        case STATUS_RUNNING:
                            Log.e(TAG, "Film downloading: " + contentDatum.getGist().getId());

                            Boolean filmDownloadUpdated = filmDownloadIconUpdatedMap.get(contentDatum.getGist().getId());
                            if (filmDownloadUpdated == null || !filmDownloadUpdated) {
                                filmDownloadIconUpdatedMap.put(contentDatum.getGist().getId(), true);


                                int finalRadiusDifference = radiusDifference;
                                appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                                        holder.appCMSContinueWatchingDeleteButton,
                                        appCMSPresenter,
                                        userVideoDownloadStatus -> {
                                            appCMSPresenter.getCurrentActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (userVideoDownloadStatus != null) {
                                                        if (userVideoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_SUCCESSFUL) {
                                                            holder.appCMSContinueWatchingDeleteButton.setImageBitmap(null);
                                                            holder.appCMSContinueWatchingDeleteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_deleteicon));
                                                            holder.appCMSContinueWatchingDeleteButton.getBackground().setTint(tintColor);
                                                            holder.appCMSContinueWatchingDeleteButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                                                            holder.appCMSContinueWatchingDeleteButton.invalidate();
                                                            holder.appCMSContinueWatchingSize.setText(appCMSPresenter.getDownloadedFileSize(userVideoDownloadStatus.getVideoSize()));
//                                                                holder.appCMSContinueWatchingSize.se
//
                                                            contentDatum.getGist().setLocalFileUrl(userVideoDownloadStatus.getVideoUri());
                                                            try {
                                                                if (userVideoDownloadStatus.getSubtitlesUri().trim().length() > 10 &&
                                                                        contentDatum.getContentDetails() != null &&
                                                                        contentDatum.getContentDetails().getClosedCaptions().get(0) != null) {
                                                                    contentDatum.getContentDetails().getClosedCaptions().get(0).setUrl(userVideoDownloadStatus.getSubtitlesUri());
                                                                }
                                                            } catch (Exception e) {
                                                                //Log.e(TAG, e.getMessage());
                                                            }

                                                        } else if (userVideoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_INTERRUPTED) {
                                                            holder.appCMSContinueWatchingDeleteButton.setImageBitmap(null);
                                                            holder.appCMSContinueWatchingDeleteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),
                                                                    android.R.drawable.stat_sys_warning));
                                                            holder.appCMSContinueWatchingSize.setText("Remove".toUpperCase());
                                                            holder.appCMSContinueWatchingSize.setOnClickListener(v -> delete(contentDatum, position));
                                                        } else if (userVideoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_RUNNING) {
                                                            holder.appCMSContinueWatchingSize.setText("Cancel");
                                                        } else if (userVideoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_PENDING) {
                                                            holder.appCMSContinueWatchingDeleteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),
                                                                    R.drawable.ic_download_queued));
                                                        }
                                                        contentDatum.getGist().setDownloadStatus(userVideoDownloadStatus.getDownloadStatus());
                                                    }
                                                }
                                            });

                                        },
                                        userId,
                                        true,
                                        radiusDifference,
                                        appCMSPresenter.getDownloadPageId());
                            } else {
                                appCMSPresenter.updateDownloadTimerTask(contentDatum.getGist().getId(),
                                        appCMSPresenter.getDownloadPageId(),
                                        holder.appCMSContinueWatchingDeleteButton);
                            }

                            holder.appCMSContinueWatchingSize.setText("Cancel".toUpperCase());
                            holder.appCMSContinueWatchingSize.setOnClickListener(v -> delete(contentDatum, position));

                            break;

                        case STATUS_FAILED:
                            Log.e(TAG, "Film download failed: " + contentDatum.getGist().getId());
                            holder.appCMSContinueWatchingDeleteButton.setImageBitmap(null);
                            holder.appCMSContinueWatchingDeleteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),
                                    android.R.drawable.stat_notify_error));
                            break;

                        case STATUS_SUCCESSFUL:
                            Log.e(TAG, "Film download successful: " + contentDatum.getGist().getId());
                            holder.appCMSContinueWatchingDeleteButton.setImageBitmap(null);
                            holder.appCMSContinueWatchingDeleteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),
                                    R.drawable.ic_deleteicon));
                            holder.appCMSContinueWatchingDeleteButton.getBackground().setTint(tintColor);
                            holder.appCMSContinueWatchingDeleteButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);

                            contentDatum.getGist().setDownloadStatus(DownloadStatus.STATUS_COMPLETED);
                            break;

                        case STATUS_INTERRUPTED:
                            Log.e(TAG, "Film download interrupted: " + contentDatum.getGist().getId());
                            holder.appCMSContinueWatchingDeleteButton.setImageBitmap(null);
                            holder.appCMSContinueWatchingDeleteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),
                                    android.R.drawable.stat_sys_warning));
                            holder.appCMSContinueWatchingSize.setText("Remove".toUpperCase());
                            holder.appCMSContinueWatchingSize.setOnClickListener(v -> delete(contentDatum, position));
                            break;

                        case STATUS_PAUSED:
                            holder.appCMSContinueWatchingDeleteButton.setImageBitmap(null);
                            holder.appCMSContinueWatchingDeleteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),
                                    R.drawable.ic_download_queued));
                            break;

                        default:
                            Log.e(TAG, "Film download status unknown: " + contentDatum.getGist().getId());
                            holder.appCMSContinueWatchingDeleteButton.setImageBitmap(null);
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
                    if (!adapterData.isEmpty()) {
                        click(adapterData.get(position));
                    }
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

            if (contentDatum.getGist() != null && contentDatum.getGist().getDescription() != null) {
               /* Spannable rawHtmlSpannable = new HtmlSpanner().fromHtml(contentDatum.getGist().getDescription());
                holder.appCMSContinueWatchingDescription.setText(rawHtmlSpannable);*/
                holder.appCMSContinueWatchingDescription.setText(Html.fromHtml(contentDatum.getGist().getDescription()));
            }

            holder.appCMSContinueWatchingDeleteButton.setOnClickListener(v -> delete(contentDatum, position));

            if (isHistory) {
                holder.appCMSContinueWatchingLastViewed.setVisibility(View.VISIBLE);
                holder.appCMSContinueWatchingLastViewed.setText(getLastWatchedTime(contentDatum));
                holder.appCMSContinueWatchingLastViewed.setTextColor(Color.parseColor(
                        (appCMSPresenter.getAppTextColor())));
            } else {
                holder.appCMSContinueWatchingLastViewed.setVisibility(View.GONE);
            }

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
                if ((contentDatum.getGist().getRuntime() / SECONDS_PER_MIN) == 0) {
                    StringBuilder runtimeText = new StringBuilder();
                    if ((contentDatum.getSeason() != null)) {
                        int totalEpisodes = 0;
                        int numSeasons = contentDatum.getSeason().size();

                        for (int i = 0; i < numSeasons; i++) {
                            if (contentDatum.getSeason().get(i).getEpisodes() != null) {
                                totalEpisodes += contentDatum.getSeason().get(i).getEpisodes().size();
                            }
                        }

                        runtimeText.append(totalEpisodes)
                                .append(" ")
                                .append(holder.itemView.getContext().getString(R.string.runtime_episodes_abbreviation));
                    } else {
                        runtimeText.append(contentDatum.getGist().getRuntime())
                                .append(" ")
                                .append(holder.itemView.getContext().getString(R.string.runtime_seconds_abbreviation));
                    }

                    holder.appCMSContinueWatchingDuration.setText(runtimeText);
                } else if ((contentDatum.getGist().getRuntime() / SECONDS_PER_MIN) < 2) {
                    StringBuilder runtimeText = new StringBuilder()
                            .append(contentDatum.getGist().getRuntime() / SECONDS_PER_MIN)
                            .append(" ")
                            .append(holder.itemView.getContext().getString(R.string.min_abbreviation));

                    holder.appCMSContinueWatchingDuration.setText(runtimeText);
                } else {
                    StringBuilder runtimeText = new StringBuilder()
                            .append(contentDatum.getGist().getRuntime() / SECONDS_PER_MIN)
                            .append(" ")
                            .append(holder.itemView.getContext().getString(R.string.mins_abbreviation));

                    holder.appCMSContinueWatchingDuration.setText(runtimeText);
                }
            }

            if (contentDatum.getGist().getWatchedPercentage() > 0) {
                holder.appCMSContinueWatchingProgress.setVisibility(View.VISIBLE);
                holder.appCMSContinueWatchingProgress.setProgress(contentDatum.getGist()
                        .getWatchedPercentage());
            } else {
                long watchedTime = contentDatum.getGist().getWatchedTime();
                long runTime = contentDatum.getGist().getRuntime();
                if (watchedTime > 0 && runTime > 0) {
                    long percentageWatched = watchedTime * 100 / runTime;
                    holder.appCMSContinueWatchingProgress.setProgress((int) percentageWatched);
                    holder.appCMSContinueWatchingProgress.setVisibility(View.VISIBLE);
                } else {
                    holder.appCMSContinueWatchingProgress.setVisibility(View.INVISIBLE);
                    holder.appCMSContinueWatchingProgress.setProgress(0);
                }
            }
        } else {
            sendEvent(hideRemoveAllButtonEvent);

            holder.appCMSNotItemLabel.setVisibility(View.VISIBLE);
            holder.appCMSNotItemLabel.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                    .getBrand().getGeneral().getTextColor()));
            if (isHistory) {
                holder.appCMSNotItemLabel.setText(holder.itemView.getContext().getString(R.string.empty_history_list_message));
            } else if (isDownload) {
                holder.appCMSNotItemLabel.setText(holder.itemView.getContext().getString(R.string.empty_download_video_message));
            } else {
                holder.appCMSNotItemLabel.setText(holder.itemView.getContext().getString(R.string.empty_watchlist_message));
            }

            holder.appCMSContinueWatchingVideoImage.setVisibility(View.GONE);
            holder.appCMSContinueWatchingPlayButton.setVisibility(View.GONE);
            holder.appCMSContinueWatchingTitle.setVisibility(View.GONE);
            holder.appCMSContinueWatchingDescription.setVisibility(View.GONE);
            holder.appCMSContinueWatchingDeleteButton.setVisibility(View.GONE);
            holder.appCMSContinueWatchingLastViewed.setVisibility(View.GONE);
            holder.appCMSContinueWatchingDuration.setVisibility(View.GONE);
            holder.appCMSContinueWatchingSize.setVisibility(View.GONE);
            holder.appCMSContinueWatchingSeparatorView.setVisibility(View.GONE);
            holder.appCMSContinueWatchingProgress.setVisibility(View.GONE);
            holder.appCMSContinueWatchingDownloadStatusButton.setVisibility(View.GONE);
        }
    }

    private String getLastWatchedTime(ContentDatum contentDatum) {
        String lastWatchedMessage = "";
      if(contentDatum!=null && contentDatum.getGist() != null &&
              contentDatum.getGist().getUpdateDate() != null) {
          long currentTime = System.currentTimeMillis();
          long lastWatched = Long.parseLong(contentDatum.getGist().getUpdateDate());

          if (currentTime == 0) {
              lastWatched = 0;
          }

          long seconds = TimeUnit.MILLISECONDS.toSeconds(currentTime - lastWatched);
          long minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime - lastWatched);
          long hours = TimeUnit.MILLISECONDS.toHours(currentTime - lastWatched);
          long days = TimeUnit.MILLISECONDS.toDays(currentTime - lastWatched);

          int weeks = (int) ((currentTime - lastWatched) / (1000 * 60 * 60 * 24 * 7));
          int months = (weeks / 4);
          int years = months / 12;



          if (years > 0) {
              if (years > 1) {
                  lastWatchedMessage = years + " years ago";
              } else {
                  lastWatchedMessage = years + " year ago";
              }
          } else if (months > 0 && months < 12) {
              if (months > 1) {
                  lastWatchedMessage = months + " months ago";
              } else {
                  lastWatchedMessage = months + " month ago";
              }
          } else if (weeks > 0 && weeks < 4) {
              if (weeks > 1) {
                  lastWatchedMessage = weeks + " weeks ago";
              } else {
                  lastWatchedMessage = weeks + " week ago";
              }
          } else if (days > 0 && days < 6) {
              if (days > 1) {
                  lastWatchedMessage = days + " days ago";
              } else {
                  lastWatchedMessage = days + " day ago";
              }
          } else if (hours > 0 && hours < 24) {
              if (hours > 1) {
                  lastWatchedMessage = hours + " hours ago";
              } else {
                  lastWatchedMessage = hours + " hour ago";
              }
          } else if (minutes > 0 && minutes < 60) {
              if (minutes > 1) {
                  lastWatchedMessage = minutes + " mins ago";
              } else {
                  lastWatchedMessage = minutes + " min ago";
              }
          } else if (seconds < 60) {
              if (seconds > 3) {
                  lastWatchedMessage = seconds + " secs ago";
              } else {
                  lastWatchedMessage = "Just now";
              }
          }


      }
        return lastWatchedMessage;
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    private ContentDatum getNextContentDatum(int position) {
        if (position + 1 == adapterData.size()) {
            return null;
        }

        ContentDatum contentDatum = adapterData.get(++position);
        if (contentDatum.getGist() != null) {
            if (!contentDatum.getGist().getDownloadStatus().equals(DownloadStatus.STATUS_COMPLETED)) {
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
    private List<String> getListOfUpcomingMovies(int position, Object downloadStatus) {
        if (position + 1 == adapterData.size()) {
            return Collections.emptyList();
        }

        List<String> contentDatumList = new ArrayList<>();
        for (int i = position; i < adapterData.size(); i++) {
            ContentDatum contentDatum = adapterData.get(i);
            if (contentDatum.getGist() != null &&
                    contentDatum.getGist().getDownloadStatus().equals(downloadStatus)) {
                contentDatumList.add(contentDatum.getGist().getId());
            }
        }

        return contentDatumList;
    }


    private void playDownloaded(ContentDatum data, Context context, int position) {
        if (data.getGist().getDownloadStatus() != DownloadStatus.STATUS_COMPLETED &&
                data.getGist().getDownloadStatus() != DownloadStatus.STATUS_SUCCESSFUL) {
            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.DOWNLOAD_INCOMPLETE,
                    null,
                    false,
                    null,
                    null);
            return;
        }

        if (data.getGist() != null &&
                data.getGist().getMediaType() != null &&
                data.getGist().getMediaType().toLowerCase().contains(context.getString(R.string.media_type_audio).toLowerCase()) &&
                data.getGist().getContentType() != null &&
                data.getGist().getContentType().toLowerCase().contains(context.getString(R.string.content_type_audio).toLowerCase())) {

            playPlaylistItem(data, context, position);
            appCMSPresenter.showLoadingDialog(false);

            return;
        }

        // Fix for SVFA-2707
        if (appCMSPresenter.isAppSVOD() &&
                !appCMSPresenter.isUserSubscribed() &&
                (data == null ||
                        data.getGist() == null ||
                        (data.getGist() != null && !data.getGist().getFree()))) {
            appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.SUBSCRIPTION_PREMIUM_CONTENT_REQUIRED,
                    null);
            return;
        }

        List<String> relatedVideoIds = getListOfUpcomingMovies(position, DownloadStatus.STATUS_SUCCESSFUL);
        if (data.getGist().getDownloadStatus() != DownloadStatus.STATUS_COMPLETED &&
                data.getGist().getDownloadStatus() != DownloadStatus.STATUS_SUCCESSFUL) {
            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.DOWNLOAD_INCOMPLETE,
                    null,
                    false,
                    null,
                    null);
            return;
        }

        @SuppressWarnings("unused")
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
        //Log.d(TAG, "Launching " + permalink + ": " + action + ":File:" + data.getGist().getLocalFileUrl());
        if (Boolean.parseBoolean(extraData[3])) {
            relatedVideoIds = getListOfUpcomingMovies(position, DownloadStatus.STATUS_COMPLETED);
        }
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
                        0,
                        relatedVideoIds)) {
            //Log.e(TAG, "Could not launch action: " +
//                    " permalink: " +
//                    permalink +
//                    " action: " +
//                    action +
//                    " hlsUrl: " +
//                    hlsUrl);
        }
    }

    private void playIncompleteDownload(ContentDatum data, Context mContext) {
        if (data.getGist() != null &&
                data.getGist().getMediaType() != null &&
                data.getGist().getMediaType().toLowerCase().contains(mContext.getString(R.string.media_type_audio).toLowerCase()) &&
                data.getGist().getContentType() != null &&
                data.getGist().getContentType().toLowerCase().contains(mContext.getString(R.string.content_type_audio).toLowerCase())) {
            appCMSPresenter.getCurrentActivity().sendBroadcast(new Intent(AppCMSPresenter
                    .PRESENTER_PAGE_LOADING_ACTION));
            // on click from playlist adapter .Get playlist from temp list and set into current playlist
            if ((AudioPlaylistHelper.getInstance().getCurrentPlaylistId() == null) || (AudioPlaylistHelper.getInstance().getCurrentPlaylistId() != null && !AudioPlaylistHelper.getInstance().getCurrentPlaylistId().equalsIgnoreCase(mCurrentPlayListId))) {
                AudioPlaylistHelper.getInstance().setCurrentPlaylistId(mCurrentPlayListId);
                AudioPlaylistHelper.getInstance().setCurrentPlaylistData(AudioPlaylistHelper.getInstance().getTempPlaylistData());
                AudioPlaylistHelper.getInstance().setPlaylist(MusicLibrary.createPlaylistByIDList(AudioPlaylistHelper.getInstance().getTempPlaylistData().getAudioList()));
            }
            data.getGist().setAudioPlaying(true);

            updateData(mRecyclerView, adapterData);
            AudioPlaylistHelper.getInstance().playAudioOnClickItem(data.getGist().getId(), 0);
            return;
        }
    }

    private void playPlaylistItem(ContentDatum data, Context context, int clickPosition) {

        try {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
            if (resultCode == ConnectionResult.SUCCESS) {
                appCMSPresenter.showLoadingDialog(true);

                AppCMSAudioDetailResult appCMSAudioDetailResult = convertToAudioResult(data);
                //        if at the time of click from download list device already connected to casatin device than get audio details from server
                //        and cast audio url to casting device

                if (CastServiceProvider.getInstance(context).isCastingConnected()) {
                    AudioPlaylistHelper.getInstance().playAudioOnClickItem(appCMSAudioDetailResult.getId(), 0);
                } else {
                    AppCMSPageAPI audioApiDetail = appCMSAudioDetailResult.convertToAppCMSPageAPI(appCMSAudioDetailResult.getId());
                    AudioPlaylistHelper.getInstance().createMediaMetaDataForAudioItem(appCMSAudioDetailResult);
                    PlaybackManager.setCurrentMediaData(AudioPlaylistHelper.getInstance().getMetadata(appCMSAudioDetailResult.getId()));
                    if (appCMSPresenter.getCallBackPlaylistHelper() != null) {
                        appCMSPresenter.getCallBackPlaylistHelper().onPlaybackStart(AudioPlaylistHelper.getInstance().getMediaMetaDataItem(appCMSAudioDetailResult.getId()), 0);
                    } else if (appCMSPresenter.getCurrentActivity() != null) {
                        AudioPlaylistHelper.getInstance().onMediaItemSelected(AudioPlaylistHelper.getInstance().getMediaMetaDataItem(appCMSAudioDetailResult.getId()), 0);
                    }
                    AudioPlaylistHelper.getInstance().setCurrentAudioPLayingData(audioApiDetail.getModules().get(0).getContentData().get(0));
                    Intent intent = new Intent(context, AppCMSPlayAudioActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    MediaControllerCompat controller = MediaControllerCompat.getMediaController(appCMSPresenter.getCurrentActivity());
                    if (controller != null) {
                        MediaMetadataCompat metadata = controller.getMetadata();
                        if (metadata != null) {
                            intent.putExtra(EXTRA_CURRENT_MEDIA_DESCRIPTION,
                                    metadata);
                        }
                    }
                    context.startActivity(intent);
                }
            }else{
                int PLAY_SERVICES_RESOLUTION_REQUEST = 1001;
                if (apiAvailability.isUserResolvableError(resultCode)) {
                    apiAvailability.getErrorDialog((Activity)context, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                            .show();
                } /*else {
                    Log.i(TAG, "This device is not supported.");
                    Toast.makeText(context, "This device is not supported.", Toast.LENGTH_SHORT).show();
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            appCMSPresenter.showLoadingDialog(false);
        }

        // TODO : To handle playlist from download
//        try {
//            if (data.getGist() != null &&
//                    data.getGist().getMediaType() != null &&
//                    data.getGist().getMediaType().toLowerCase().contains(context.getString(R.string.media_type_audio).toLowerCase()) &&
//                    data.getGist().getContentType() != null &&
//                    data.getGist().getContentType().toLowerCase().contains(context.getString(R.string.content_type_audio).toLowerCase())) {
//                appCMSPresenter.getCurrentActivity().sendBroadcast(new Intent(AppCMSPresenter
//                        .PRESENTER_PAGE_LOADING_ACTION));
//
//                // on click from playlist adapter .Get playlist from temp list and set into current playlist
//                if ((AudioPlaylistHelper.getInstance().getCurrentPlaylistId() == null) ||
//                        (AudioPlaylistHelper.getInstance().getCurrentPlaylistId() != null &&
//                                !AudioPlaylistHelper.getInstance().getCurrentPlaylistId().equalsIgnoreCase(mCurrentPlayListId))) {
//
//                    AudioPlaylistHelper.getInstance().setCurrentPlaylistId(mCurrentPlayListId);
//                    AudioPlaylistHelper.getInstance().setCurrentPlaylistData(AudioPlaylistHelper.getInstance().getTempPlaylistData());
//                    AudioPlaylistHelper.getInstance().setPlaylist(MusicLibrary.createPlaylistByIDList(AudioPlaylistHelper.getInstance().getTempPlaylistData().getAudioList()));
//                }
//            /*if (adapterData.size() > oldClick) {
//                if (oldClick != clickPosition) {
//                    if (oldClick == -1) {
//                        oldClick = clickPosition;
//                        data.getGist().setAudioPlaying(true);
//                    } else {
//                        adapterData.get(oldClick).getGist().setAudioPlaying(false);
//                        oldClick = clickPosition;
//                        data.getGist().setAudioPlaying(true);
//                    }
//                }
//            }*/
//                updateData(mRecyclerView, adapterData);
//                AudioPlaylistHelper.getInstance().playAudioOnClickItem(data.getGist().getId(), 0);
//
//                return;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            appCMSPresenter.showLoadingDialog(false);
//        }
    }

    @Override
    public int getItemCount() {
        return adapterData != null && !adapterData.isEmpty() ? adapterData.size() : 1;
    }

    @Override
    public void addReceiver(OnInternalEvent e) {
        receivers.add(e);
        if (adapterData == null || adapterData.isEmpty() || adapterData.size() == 0) {
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

    @Override
    public String getModuleId() {
        return moduleId;
    }

    @Override
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    private void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(Uri.decode(url))
                .apply(new RequestOptions().override(imageView.getWidth(), imageView.getHeight()).placeholder(R.drawable.vid_image_placeholder_land))
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
                    int textColor = appCMSPresenter.getGeneralTextColor();
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

                    viewHolder.appCMSContinueWatchingSize.setTextColor(textColor);

                    switch (componentKey) {
                        case PAGE_WATCHLIST_DURATION_KEY:
                            viewHolder.appCMSContinueWatchingDuration.setTextColor(textColor);

                            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
//                                viewHolder.appCMSContinueWatchingDuration
//                                        .setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(),
//                                                component.getBackgroundColor())));
                                viewHolder.appCMSContinueWatchingSize
                                        .setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(),
                                                component.getBackgroundColor())));
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
                            viewHolder.appCMSContinueWatchingDuration.setVisibility(View.VISIBLE);
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
                        case PAGE_HISTORY_DESCRIPTION_KEY:
                        case PAGE_WATCHLIST_DESCRIPTION_KEY:
                        case PAGE_DOWNLOAD_DESCRIPTION_KEY:
                            viewHolder.appCMSContinueWatchingDescription.setTextColor(textColor);
                           /* if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                                viewHolder.appCMSContinueWatchingDescription.setBackgroundColor(Color.parseColor(getColor(viewHolder.itemView.getContext(), component.getBackgroundColor())));
                            }*/
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
                    adapterData = appCMSHistoryResult.convertToAppCMSPageAPI(null).getModules()
                            .get(0).getContentData();
                    sortData();
                    notifyDataSetChanged();
                }
            });
        } else if (isWatchlist) {
            appCMSPresenter.getWatchlistData(appCMSWatchlistResult -> {
                if (appCMSWatchlistResult != null) {
                    sortData();
                    notifyDataSetChanged();
                }
            });
        } else if (isDownload) {
            if (adapterData != null && !adapterData.isEmpty()) {
                sortData();
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void updateData(RecyclerView listView, List<ContentDatum> contentData) {
        adapterData = contentData;
        sortData();
        notifyDataSetChanged();

        if (adapterData == null || adapterData.isEmpty() || adapterData.size() == 0) {
            sendEvent(hideRemoveAllButtonEvent);
        } else {
            sendEvent(showRemoveAllButtonEvent);
        }
    }

    @Override
    public void setClickable(boolean clickable) {

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
                0,
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
                data.getGist().getId(),
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

    @SuppressWarnings("unused")
    private void showDelete(ContentDatum contentDatum) {
        //Log.d(TAG, "Show delete button");
    }

    private void delete(final ContentDatum contentDatum, int position) {
        if ((isHistory) && (contentDatum.getGist() != null)) {
            //Log.d(TAG, "Deleting history item: " + contentDatum.getGist().getTitle());
            appCMSPresenter.editHistory(contentDatum.getGist().getId(),
                    appCMSDeleteHistoryResult -> {
                        adapterData.remove(contentDatum);
                        notifyDataSetChanged();
                        if (adapterData.size() == 0) {
                            sendEvent(hideRemoveAllButtonEvent);
                        }
                    }, false);
        }

        if ((isDownload) && (contentDatum.getGist() != null)) {
            //Log.d(TAG, "Deleting download item: " + contentDatum.getGist().getTitle());
            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.DELETE_ONE_DOWNLOAD_ITEM,
                    appCMSPresenter.getCurrentActivity().getString(R.string.app_cms_delete_one_download_video_item_message),
                    true, () ->
                            appCMSPresenter.removeDownloadedFile(contentDatum.getGist().getId(),
                                    userVideoDownloadStatus -> {

                                        ((ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position))
                                                .appCMSContinueWatchingDeleteButton.setImageBitmap(null);
                                        notifyItemRangeRemoved(position, getItemCount());
                                        adapterData.remove(contentDatum);
                                        notifyItemRangeChanged(position, getItemCount());
                                        if (adapterData.size() == 0) {
                                            sendEvent(hideRemoveAllButtonEvent);
                                        }
                                    }),
                    null);
        }

        if ((isWatchlist) && (contentDatum.getGist() != null)) {
            //Log.d(TAG, "Deleting watchlist item: " + contentDatum.getGist().getTitle());
            appCMSPresenter.editWatchlist(contentDatum,
                    addToWatchlistResult -> {
                        adapterData.remove(contentDatum);
                        notifyDataSetChanged();
                        if (adapterData.size() == 0) {
                            sendEvent(hideRemoveAllButtonEvent);
                        }
                    },
                    false,
                    true);
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

        @BindView(R.id.app_cms_continue_watching_last_viewed)
        TextView appCMSContinueWatchingLastViewed;

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

    private AppCMSAudioDetailResult convertToAudioResult(ContentDatum contentDatum) {
        AppCMSAudioDetailResult appCMSAudioDetailResult = new AppCMSAudioDetailResult();
        appCMSAudioDetailResult.setId(contentDatum.getGist().getId());


        Mp3 mp3 = new Mp3();
        mp3.setUrl(contentDatum.getGist().getLocalFileUrl());

        AudioAssets audioAssets = new AudioAssets();
        audioAssets.setMp3(mp3);

        StreamingInfo streamingInfo = new StreamingInfo();
        streamingInfo.setAudioAssets(audioAssets);

        appCMSAudioDetailResult.setGist(contentDatum.getGist());
        appCMSAudioDetailResult.setStreamingInfo(streamingInfo);
        return appCMSAudioDetailResult;
    }

    private void createDownloadedAudioPlaylist(Context context) {
        try {
            AppCMSPlaylistResult appCMSPlaylistResult = new AppCMSPlaylistResult();
            List<AudioList> audioLists = new ArrayList<>();
            for (ContentDatum contentDatum : appCMSPresenter.getDownloadedMedia(context.getString(R.string.content_type_audio))) {
                AudioList listItem = new AudioList();
                listItem.setCreditBlocks(contentDatum.getCreditBlocks());
                listItem.setGist(contentDatum.getGist());
                audioLists.add(listItem);
            }
            appCMSPlaylistResult.setAudioList(audioLists);
            mCurrentPlayListId = context.getResources().getString(R.string.app_cms_page_download_audio_playlist_key);
            appCMSPlaylistResult.setId(mCurrentPlayListId);

            if (appCMSPlaylistResult.getAudioList() != null && appCMSPlaylistResult.getAudioList().size() > 0) {
//            AudioPlaylistHelper.getInstance().setCurrentPlaylistId(appCMSPlaylistResult.getId());
//                                AudioPlaylistHelper.getInstance().setTempPlaylist(MusicLibrary.createPlaylistByIDList(appCMSPlaylistResult.getAudioList()));
                AudioPlaylistHelper.getInstance().setTempPlaylistData(appCMSPlaylistResult);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
