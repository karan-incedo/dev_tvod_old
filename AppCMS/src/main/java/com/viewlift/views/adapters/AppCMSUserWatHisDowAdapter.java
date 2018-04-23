package com.viewlift.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viewlift.Audio.playback.AudioPlaylistHelper;
import com.viewlift.Audio.playback.PlaybackManager;
import com.viewlift.R;
import com.viewlift.casting.CastServiceProvider;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.StreamingInfo;
import com.viewlift.models.data.appcms.audio.AppCMSAudioDetailResult;
import com.viewlift.models.data.appcms.audio.AudioAssets;
import com.viewlift.models.data.appcms.audio.Mp3;
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.viewlift.models.data.appcms.downloads.DownloadVideoRealm;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.activity.AppCMSPlayAudioActivity;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.CollectionGridItemView;
import com.viewlift.views.customviews.DownloadModule;
import com.viewlift.views.customviews.InternalEvent;
import com.viewlift.views.customviews.OnInternalEvent;
import com.viewlift.views.customviews.ViewCreator;
import com.viewlift.views.rxbus.DownloadTabSelectorBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observer;
import rx.functions.Action1;

import static com.viewlift.Audio.ui.PlaybackControlsFragment.EXTRA_CURRENT_MEDIA_DESCRIPTION;
import static com.viewlift.models.data.appcms.downloads.DownloadStatus.STATUS_RUNNING;

/*
 * Created by viewlift on 5/5/17.
 */

public class AppCMSUserWatHisDowAdapter extends RecyclerView.Adapter<AppCMSUserWatHisDowAdapter.ViewHolder>
        implements AppCMSBaseAdapter, OnInternalEvent {
    private static final String TAG = "UserWatHisDowAdapter";


    protected Context mContext;
    protected Layout parentLayout;
    protected Component component;
    protected AppCMSPresenter appCMSPresenter;
    protected Settings settings;
    protected ViewCreator viewCreator;
    protected Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    Module moduleAPI;
    List<ContentDatum> adapterData;
    CollectionGridItemView.OnClickHandler onClickHandler;
    int defaultWidth;
    int defaultHeight;
    boolean useMarginsAsPercentages;
    String componentViewType;
    AppCMSAndroidModules appCMSAndroidModules;
    private boolean useParentSize;
    private AppCMSUIKeyType viewTypeKey;
    private boolean isClickable;
    private String videoAction;
    private String trayAction;
    private String deleteSingleItemHistoryAction;
    private String deleteSingleItemWatchlistAction;
    private String deleteSingleItemDownloadAction;
    boolean emptyList = false;

    private List<OnInternalEvent> receivers;
    private InternalEvent<Integer> hideRemoveAllButtonEvent;
    private InternalEvent<Integer> showRemoveAllButtonEvent;
    CollectionGridItemView view = null;
    private String moduleId;
    RecyclerView mRecyclerView;
    private boolean isHistoryPage;
    private boolean isDonwloadPage;
    private boolean isWatchlistPage;
    private Map<String, Boolean> filmDownloadIconUpdatedMap;

    public AppCMSUserWatHisDowAdapter(Context context,
                                      ViewCreator viewCreator,
                                      AppCMSPresenter appCMSPresenter,
                                      Layout parentLayout,
                                      boolean useParentSize,
                                      Component component,
                                      Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                      Module moduleAPI,
                                      int defaultWidth,
                                      int defaultHeight,
                                      String viewType,
                                      AppCMSAndroidModules appCMSAndroidModules) {
        this.mContext = context;
        this.viewCreator = viewCreator;
        this.appCMSPresenter = appCMSPresenter;
        this.parentLayout = parentLayout;
        this.useParentSize = useParentSize;
        this.component = component;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.moduleAPI = moduleAPI;
        this.receivers = new ArrayList<>();
        this.hideRemoveAllButtonEvent = new InternalEvent<>(View.GONE);
        this.showRemoveAllButtonEvent = new InternalEvent<>(View.VISIBLE);

        if (moduleAPI != null && moduleAPI.getContentData() != null) {
            this.adapterData = moduleAPI.getContentData();
        } else {
            this.adapterData = new ArrayList<>();
        }
        if (this.adapterData.size() == 0) {
            emptyList = true;
        }

        this.componentViewType = viewType;
        this.viewTypeKey = jsonValueKeyMap.get(componentViewType);
        if (this.viewTypeKey == null) {
            this.viewTypeKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.useMarginsAsPercentages = true;
        this.videoAction = getVideoAction(context);
        this.trayAction = getTrayAction(context);
        this.deleteSingleItemHistoryAction = getDeleteSingleItemHistoryAction(context);
        this.deleteSingleItemWatchlistAction = getDeleteSingleItemWatchlistAction(context);
        this.deleteSingleItemDownloadAction = getDeleteSingleItemDownloadAction(context);
        this.isClickable = true;
        this.setHasStableIds(false);
        this.appCMSAndroidModules = appCMSAndroidModules;
        detectViewTypes(jsonValueKeyMap, viewType);
        sortData();
        if (isDonwloadPage) {
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
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    private void detectViewTypes(Map<String, AppCMSUIKeyType> jsonValueKeyMap, String viewType) {

        switch (jsonValueKeyMap.get(viewType)) {
            case PAGE_HISTORY_01_MODULE_KEY:
            case PAGE_HISTORY_02_MODULE_KEY:
                this.isHistoryPage = true;
                break;

            case PAGE_DOWNLOAD_01_MODULE_KEY:
            case PAGE_DOWNLOAD_02_MODULE_KEY:
                this.filmDownloadIconUpdatedMap = new HashMap<>();
                this.isDonwloadPage = true;
                break;

            case PAGE_WATCHLIST_01_MODULE_KEY:
            case PAGE_WATCHLIST_02_MODULE_KEY:
                this.isWatchlistPage = true;
                break;

            default:
                break;
        }
    }

    private void sortData() {
        if (adapterData != null) {
            if (isWatchlistPage || isDonwloadPage) {
                sortByAddedDate();
            } else if (isHistoryPage) {
                sortByUpdateDate();
            }
        }
    }

    private void sortByAddedDate() {
        Collections.sort(adapterData, (o1, o2) -> Long.compare(o1.getAddedDate(),
                o2.getAddedDate()));
    }

    private void sortByUpdateDate() {
        Collections.sort(adapterData, (o1, o2) -> Long.compare(Long.valueOf(o1.getGist().getUpdateDate()),
                Long.valueOf(o2.getGist().getUpdateDate())));
        Collections.reverse(adapterData);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = viewCreator.createCollectionGridItemView(parent.getContext(),
                parentLayout,
                useParentSize,
                component,
                appCMSPresenter,
                moduleAPI,
                appCMSAndroidModules,
                settings,
                jsonValueKeyMap,
                defaultWidth,
                defaultHeight,
                useMarginsAsPercentages,
                true,
                this.componentViewType,
                false,
                false, viewTypeKey);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(5, 5, 5, 5);
        view.setLayoutParams(lp);

        if (emptyList) {
            TextView emptyView = new TextView(mContext);
            String textColor = appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor();
            emptyView.setTextColor(Color.parseColor(textColor));
            emptyView.setTextSize(24f);
            if (isHistoryPage) {
                emptyView.setText(mContext.getString(R.string.empty_history_list_message));
                return new ViewHolder(emptyView);
            }
            if (isWatchlistPage) {
                emptyView.setText(mContext.getString(R.string.empty_watchlist_message));
                return new ViewHolder(emptyView);
            }
            if (isDonwloadPage) {
                if (appCMSPresenter.getDownloadTabSelected() == DownloadModule.VIDEO_TAB) {
                    emptyView.setText(mContext.getString(R.string.empty_download_video_message));
                }
                if (appCMSPresenter.getDownloadTabSelected() == DownloadModule.AUDIO_TAB) {
                    emptyView.setText(mContext.getString(R.string.empty_download_audio_message));
                }

                return new ViewHolder(emptyView);
            }
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (adapterData != null && adapterData.size() == 0) {
            sendEvent(hideRemoveAllButtonEvent);
        }
        if (!emptyList) {
            if (0 <= position && position < adapterData.size()) {
                for (int i = 0; i < holder.componentView.getNumberOfChildren(); i++) {
                    if (holder.componentView.getChild(i) instanceof TextView) {
                        ((TextView) holder.componentView.getChild(i)).setText("");
                    }
                }
                bindView(holder.componentView, adapterData.get(position), position);

                if (isDonwloadPage) {
                    downloadView(adapterData.get(position), holder.componentView, position);
                }
            }
        }
    }

    private void downloadView(ContentDatum contentDatum, CollectionGridItemView componentView, int position) {
        String userId = appCMSPresenter.getLoggedInUser();
        TextView videoSize = null;
        ImageButton deleteDownloadButton = null;
        ImageView thumbnailImage = null;
        for (int i = 0; i < componentView.getChildItems().size(); i++) {
            CollectionGridItemView.ItemContainer itemContainer = componentView.getChildItems().get(i);
            if (itemContainer.getComponent().getKey() != null) {
                if (itemContainer.getComponent().getKey().contains(mContext.getString(R.string.app_cms_page_download_size_key))) {
                    videoSize = (TextView) itemContainer.getChildView();
                }
                if (itemContainer.getComponent().getKey().contains(mContext.getString(R.string.app_cms_page_delete_download_key))) {
                    deleteDownloadButton = (ImageButton) itemContainer.getChildView();
                }
                if (itemContainer.getComponent().getKey().contains(mContext.getString(R.string.app_cms_page_thumbnail_image_key))) {
                    thumbnailImage = (ImageView) itemContainer.getChildView();
                }
            }
        }
        if (videoSize != null && deleteDownloadButton != null && thumbnailImage != null) {
            videoSize.setVisibility(View.VISIBLE);
            videoSize.setText(appCMSPresenter.getDownloadedFileSize(contentDatum.getGist().getId()));
            int radiusDifference = 5;
            if (BaseView.isTablet(componentView.getContext())) {
                radiusDifference = 3;
            }
            if (contentDatum.getGist() != null) {
                deleteDownloadButton.setTag(contentDatum.getGist().getId());
                final ImageButton deleteButton = deleteDownloadButton;
                final TextView videoSizeText = videoSize;

                /**
                 * if content already downloaded then just update the status , no need to call update status for running progress
                 */
                if (appCMSPresenter.isVideoDownloaded(contentDatum.getGist().getId())) {
                    deleteDownloadButton.setImageBitmap(null);
                    deleteDownloadButton.setBackground(ContextCompat.getDrawable(mContext,
                            R.drawable.ic_deleteicon));
                    deleteDownloadButton.getBackground().setTint(Color.parseColor(AppCMSPresenter.getColor(mContext,
                            appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor())));
                    deleteDownloadButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                    contentDatum.getGist().setDownloadStatus(DownloadStatus.STATUS_COMPLETED);


                    deleteDownloadButton.invalidate();

                    if (contentDatum.getGist() != null && contentDatum.getGist().getMediaType() != null && contentDatum.getGist().getPosterImageUrl() != null && contentDatum.getGist().getMediaType().equalsIgnoreCase(mContext.getResources().getString(R.string.media_type_audio))) {
                        loadImage(mContext, contentDatum.getGist().getPosterImageUrl(), thumbnailImage);

                    } else if (contentDatum.getGist() != null && contentDatum.getGist().getVideoImageUrl() != null) {
                        loadImage(mContext, contentDatum.getGist().getVideoImageUrl(), thumbnailImage);
                    }

                } else {
                    videoSize.setText("Cancel".toUpperCase());

                    appCMSPresenter.getUserVideoDownloadStatus(contentDatum.getGist().getId(),
                            videoDownloadStatus -> {
                                if (videoDownloadStatus != null) {
                                    if (videoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_PAUSED ||
                                            videoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_PENDING ||
                                            (!appCMSPresenter.isNetworkConnected() &&
                                                    videoDownloadStatus.getDownloadStatus() != DownloadStatus.STATUS_COMPLETED &&
                                                    videoDownloadStatus.getDownloadStatus() != DownloadStatus.STATUS_SUCCESSFUL)) {
                                        deleteButton.setImageBitmap(null);
                                        deleteButton.setBackground(ContextCompat.getDrawable(componentView.getContext(),
                                                R.drawable.ic_download_queued));
                                        videoSizeText.setText("Cancel".toUpperCase());

                                    }
                                    if (videoDownloadStatus != null && videoDownloadStatus.getDownloadStatus() != null) {
                                        contentDatum.getGist().setDownloadStatus(videoDownloadStatus.getDownloadStatus());
                                    }
                                }
                            },
                            appCMSPresenter.getLoggedInUser());

                    switch (contentDatum.getGist().getDownloadStatus()) {
                        case STATUS_PENDING:
                            deleteButton.setImageBitmap(null);
                            deleteButton.setBackground(ContextCompat.getDrawable(componentView.getContext(),
                                    R.drawable.ic_download_queued));
                            videoSizeText.setText("Cancel".toUpperCase());

                            break;
                        case STATUS_RUNNING:
                            int finalRadiusDifference = radiusDifference;
                            if (contentDatum.getGist() != null && deleteDownloadButton != null) {
                                if (deleteDownloadButton.getBackground() != null) {
                                    deleteDownloadButton.getBackground().setTint(ContextCompat.getColor(mContext, R.color.transparentColor));
                                    deleteDownloadButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                                }

                                ImageButton finalDeleteDownloadButton = deleteDownloadButton;
                                ImageView finalThumbnailImage = thumbnailImage;
                                TextView finalVideoSize = videoSize;

                                Log.e(TAG, "Film downloading: " + contentDatum.getGist().getId());

                                Boolean filmDownloadUpdated = filmDownloadIconUpdatedMap.get(contentDatum.getGist().getId());
                                if (filmDownloadUpdated == null || !filmDownloadUpdated) {
                                    filmDownloadIconUpdatedMap.put(contentDatum.getGist().getId(), true);

                                    appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                                            deleteDownloadButton,
                                            appCMSPresenter,
                                            userVideoDownloadStatus -> {
                                                if (userVideoDownloadStatus != null) {
                                                    if (userVideoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_SUCCESSFUL) {
                                                        finalDeleteDownloadButton.setImageBitmap(null);
                                                        finalDeleteDownloadButton.setBackground(ContextCompat.getDrawable(mContext,
                                                                R.drawable.ic_deleteicon));
                                                        finalDeleteDownloadButton.getBackground().setTint(Color.parseColor(AppCMSPresenter.getColor(mContext,
                                                                appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor())));
                                                        finalDeleteDownloadButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                                                        finalDeleteDownloadButton.setBackground(ContextCompat.getDrawable(mContext,
                                                                R.drawable.ic_deleteicon));
                                                        finalDeleteDownloadButton.invalidate();

                                                        if (contentDatum.getGist().getMediaType().equalsIgnoreCase(mContext.getResources().getString(R.string.media_type_audio))) {
                                                            contentDatum.getGist().setPosterImageUrl(userVideoDownloadStatus.getPosterUri());
                                                            loadImage(mContext, userVideoDownloadStatus.getPosterUri(), finalThumbnailImage);

                                                        } else {
                                                            loadImage(mContext, userVideoDownloadStatus.getThumbUri(), finalThumbnailImage);

                                                        }
                                                        try {
                                                            finalVideoSize.setText(appCMSPresenter.getDownloadedFileSize(userVideoDownloadStatus.getVideoSize()));
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            finalVideoSize.setVisibility(View.GONE);
                                                        }
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

                                                        notifyDataSetChanged();
//                                                        notifyItemChanged(position);

                                                    } else if (userVideoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_INTERRUPTED) {
                                                        finalDeleteDownloadButton.setImageBitmap(null);
                                                        finalDeleteDownloadButton.setBackground(ContextCompat.getDrawable(mContext,
                                                                android.R.drawable.stat_sys_warning));
                                                        finalVideoSize.setText("Re-Try".toUpperCase());
                                                        finalVideoSize.setOnClickListener(v -> restartDownloadVideo(contentDatum, position, finalDeleteDownloadButton, finalRadiusDifference));
                                                    } else if (userVideoDownloadStatus.getDownloadStatus() == STATUS_RUNNING) {
                                                        finalVideoSize.setText("Cancel".toUpperCase());
                                                    } else if (userVideoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_PENDING) {
                                                        finalDeleteDownloadButton.setBackground(ContextCompat.getDrawable(mContext,
                                                                R.drawable.ic_download_queued));
                                                        finalVideoSize.setText("Cancel".toUpperCase());

                                                    }
                                                    contentDatum.getGist().setDownloadStatus(userVideoDownloadStatus.getDownloadStatus());
                                                }
                                            },
                                            userId, true, radiusDifference, appCMSPresenter.getDownloadPageId());
                                } else {
                                    appCMSPresenter.updateDownloadTimerTask(contentDatum.getGist().getId(),
                                            appCMSPresenter.getDownloadPageId(),
                                            deleteDownloadButton);

                                }
//                                finalVideoSize.setText("Cancel".toUpperCase());
                                finalVideoSize.setOnClickListener(v -> deleteDownloadVideo(contentDatum, position));

                            }
                            break;

                        case STATUS_FAILED:
                            Log.e(TAG, "Film download failed: " + contentDatum.getGist().getId());
                            deleteDownloadButton.setImageBitmap(null);
                            deleteDownloadButton.setBackground(ContextCompat.getDrawable(componentView.getContext(),
                                    android.R.drawable.stat_notify_error));
                            break;

                        case STATUS_SUCCESSFUL:
                            Log.e(TAG, "Film download successful: " + contentDatum.getGist().getId());
                            deleteDownloadButton.setImageBitmap(null);
                            deleteDownloadButton.setBackground(ContextCompat.getDrawable(mContext,
                                    R.drawable.ic_deleteicon));
                            deleteDownloadButton.getBackground().setTint(Color.parseColor(AppCMSPresenter.getColor(mContext,
                                    appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor())));
                            deleteDownloadButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                            contentDatum.getGist().setDownloadStatus(DownloadStatus.STATUS_COMPLETED);
                            break;

                        case STATUS_INTERRUPTED:
                            Log.e(TAG, "Film download interrupted: " + contentDatum.getGist().getId());
                            deleteDownloadButton.setImageBitmap(null);
                            deleteDownloadButton.setBackground(ContextCompat.getDrawable(mContext,
                                    android.R.drawable.stat_sys_warning));
                            videoSize.setText("Re-Try".toUpperCase());
                            int finalRadiusDifference1 = radiusDifference;
                            ImageButton finaldeleteDownloadButton1 = deleteDownloadButton;
                            videoSize.setOnClickListener(v -> restartDownloadVideo(contentDatum, position,
                                    finaldeleteDownloadButton1, finalRadiusDifference1));

//                        videoSize.setText("Remove".toUpperCase());
//                        videoSize.setOnClickListener(v -> deleteDownloadVideo(contentDatum, position));
                            break;

                        case STATUS_PAUSED:
                            deleteDownloadButton.setImageBitmap(null);
                            deleteDownloadButton.setBackground(ContextCompat.getDrawable(componentView.getContext(),
                                    R.drawable.ic_download_queued));
                            videoSizeText.setText("Cancel".toUpperCase());

                            break;
                        default:
                            Log.e(TAG, "Film download status unknown: " + contentDatum.getGist().getId());
                            deleteDownloadButton.setImageBitmap(null);
                            break;
                    }
                }
                DownloadVideoRealm downloadVideoRealm = appCMSPresenter.getRealmController()
                        .getDownloadByIdBelongstoUser(contentDatum.getGist().getId(), userId);
                if (downloadVideoRealm != null && contentDatum != null && contentDatum.getGist() != null) {
                    if (downloadVideoRealm.getWatchedTime() > contentDatum.getGist().getWatchedTime()) {
                        contentDatum.getGist().setWatchedTime(downloadVideoRealm.getWatchedTime());
                    }
                }
            }
        }
    }

    private synchronized void restartDownloadVideo(final ContentDatum contentDatum, int position,
                                                   final ImageButton downloadProgressImage, final int radiusDifference) {


        if ((isDonwloadPage) && (contentDatum.getGist() != null)) {
            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.RE_START_DOWNLOAD_ITEM,
                    appCMSPresenter.getCurrentActivity().getString(R.string.app_cms_re_download_item_message),
                    true, () ->
                            appCMSPresenter.reStartDownloadedFile(contentDatum.getGist().getId(),
                                    userVideoDownloadStatus -> {
                                        notifyItemChanged(position);
                                        //notifyItemRangeChanged(position, getItemCount());
                                        if (adapterData.size() == 0) {
                                            sendEvent(hideRemoveAllButtonEvent);
                                            notifyItemRangeChanged(position, getItemCount());
                                            notifyDataSetChanged();
                                            updateData(mRecyclerView, adapterData);
                                        }
                                    }, downloadProgressImage, radiusDifference)
                    ,
                    () ->  // cancelButton Action code
                            appCMSPresenter.removeDownloadedFile(contentDatum.getGist().getId(),
                                    userVideoDownloadStatus -> {
//                                    ((AppCMSWatchlistItemAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position))
//                                            .appCMSContinueWatchingDeleteButton.setImageBitmap(null);
                                        notifyItemRangeRemoved(position, getItemCount());
                                        adapterData.remove(contentDatum);
                                        notifyItemRangeChanged(position, getItemCount());
                                        if (adapterData.size() == 0) {
                                            sendEvent(hideRemoveAllButtonEvent);
                                            notifyDataSetChanged();
                                            updateData(mRecyclerView, adapterData);
                                        }
                                    }));
        }
    }

    private void deleteDownloadVideo(final ContentDatum contentDatum, int position) {
        String deleteMsg = appCMSPresenter.getCurrentActivity().getString(R.string.app_cms_delete_one_download_video_item_message);
        if (contentDatum.getGist() != null
                && contentDatum.getGist().getContentType() != null
                && contentDatum.getGist().getContentType().toLowerCase().equalsIgnoreCase(mContext.getString(R.string.media_type_audio).toLowerCase())) {
            deleteMsg = appCMSPresenter.getCurrentActivity().getString(R.string.app_cms_delete_one_download_audio_item_message);
        }
        appCMSPresenter.showDialog(AppCMSPresenter.DialogType.DELETE_ONE_DOWNLOAD_ITEM,
                deleteMsg, true, () ->
                        appCMSPresenter.removeDownloadedFile(contentDatum.getGist().getId(),
                                userVideoDownloadStatus -> {
//                                    ((AppCMSWatchlistItemAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position))
//                                            .appCMSContinueWatchingDeleteButton.setImageBitmap(null);
                                    notifyItemRangeRemoved(position, getItemCount());
                                    adapterData.remove(contentDatum);
                                    notifyItemRangeChanged(position, getItemCount());
                                    if (adapterData.size() == 0) {
                                        emptyList = true;
                                        sendEvent(hideRemoveAllButtonEvent);
                                        notifyDataSetChanged();
                                        updateData(mRecyclerView, adapterData);
                                    }
                                }),
                null);
    }

    private void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(Uri.decode(url))
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        if (isHistoryPage || isDonwloadPage || isWatchlistPage) {
            if (emptyList || adapterData.size() == 0)
                return 1;
        }
        return (adapterData != null ? adapterData.size() : 0);
    }

    @Override
    public void resetData(RecyclerView listView) {

    }

    @Override
    public void updateData(RecyclerView listView, List<ContentDatum> contentData) {
        if (contentData.size() == 0) {
            emptyList = true;
        } else {
            emptyList = false;
        }

        listView.setAdapter(null);
        adapterData = null;
        adapterData = contentData;
        listView.setAdapter(this);
        listView.invalidate();
        notifyDataSetChanged();
        if (adapterData == null || adapterData.isEmpty() || adapterData.size() == 0) {
            sendEvent(hideRemoveAllButtonEvent);
            if (isDonwloadPage)
                if (appCMSPresenter.getCurrentActivity().findViewById(R.id.remove_all_download_id) != null)
                    appCMSPresenter.getCurrentActivity().findViewById(R.id.remove_all_download_id).setVisibility(View.GONE);
        } else {
            sendEvent(showRemoveAllButtonEvent);
            if (isDonwloadPage)
                if (appCMSPresenter.getCurrentActivity().findViewById(R.id.remove_all_download_id) != null)
                    appCMSPresenter.getCurrentActivity().findViewById(R.id.remove_all_download_id).setVisibility(View.VISIBLE);

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    void bindView(CollectionGridItemView itemView,
                  final ContentDatum data, int position) throws IllegalArgumentException {
        if (onClickHandler == null) {

            onClickHandler = new CollectionGridItemView.OnClickHandler() {
                @Override
                public void click(CollectionGridItemView collectionGridItemView,
                                  Component childComponent,
                                  ContentDatum data, int clickPosition) {
                    if (isClickable) {

                        if (data.getGist() != null) {
                            //Log.d(TAG, "Clicked on item: " + data.getGist().getTitle());
                            String permalink = data.getGist().getPermalink();
                            String action = trayAction;
                            if (childComponent != null && !TextUtils.isEmpty(childComponent.getAction())) {
                                action = childComponent.getAction();
                            }
                            String title = data.getGist().getTitle();
                            String hlsUrl = getHlsUrl(data);

                            @SuppressWarnings("MismatchedReadAndWriteOfArray")
                            String[] extraData = new String[3];
                            extraData[0] = permalink;
                            extraData[1] = hlsUrl;
                            extraData[2] = data.getGist().getId();
                            //Log.d(TAG, "Launching " + permalink + ": " + action);
                            List<String> relatedVideoIds = null;
                            if (data.getContentDetails() != null &&
                                    data.getContentDetails().getRelatedVideoIds() != null) {
                                relatedVideoIds = data.getContentDetails().getRelatedVideoIds();
                            }
                            int currentPlayingIndex = -1;
                            if (relatedVideoIds == null) {
                                currentPlayingIndex = 0;
                            }
                            /*navigate to article detail page*/
                            if (data.getGist() != null && data.getGist().getMediaType() != null
                                    && data.getGist().getMediaType().toLowerCase().contains(itemView.getContext().getString(R.string.app_cms_article_key_type).toLowerCase())) {
                                appCMSPresenter.setCurrentArticleIndex(-1);
                                appCMSPresenter.navigateToArticlePage(data.getGist().getId(), data.getGist().getTitle(), false, null, false);
                                return;
                            }

                            if (action.contains(deleteSingleItemDownloadAction)) {
                                /*delete a single downloaded video*/
                                deleteDownloadVideo(data, position);
                                return;
                            }
                            if (action.contains(deleteSingleItemWatchlistAction)) {
                                /*delete video from user watchlist*/
                                appCMSPresenter.showDialog(AppCMSPresenter.DialogType.DELETE_ONE_WATCHLIST_ITEM,
                                        appCMSPresenter.getCurrentActivity().getString(R.string.app_cms_delete_one_watchlist_item_message),
                                        true, () ->
                                                appCMSPresenter.editWatchlist(data,
                                                        addToWatchlistResult -> {
                                                            adapterData.remove(data);

                                                            if (adapterData.size() == 0) {
                                                                emptyList = true;
                                                                sendEvent(hideRemoveAllButtonEvent);
                                                                updateData(mRecyclerView, adapterData);
                                                            }
                                                            notifyDataSetChanged();
                                                        }, false,
                                                        false),
                                        null);
                                return;
                            }
                            if (action.contains(deleteSingleItemHistoryAction)) {
                                /*delete video from user history*/
                                appCMSPresenter.showDialog(AppCMSPresenter.DialogType.DELETE_ONE_HISTORY_ITEM,
                                        appCMSPresenter.getCurrentActivity().getString(R.string.app_cms_delete_one_history_item_message),
                                        true, () ->
                                                appCMSPresenter.editHistory(data.getGist().getId(),
                                                        appCMSDeleteHistoryResult -> {
                                                            adapterData.remove(data);
                                                            if (adapterData.size() == 0) {
                                                                emptyList = true;
                                                                sendEvent(hideRemoveAllButtonEvent);
                                                                updateData(mRecyclerView, adapterData);
                                                            }
                                                            notifyDataSetChanged();
                                                        }, false),
                                        null);
                                return;
                            }
                            if (action.contains(videoAction)) {
                                if (isDonwloadPage) {
                                    if (data.getGist() != null &&
                                            data.getGist().getMediaType() != null &&
                                            data.getGist().getMediaType().toLowerCase().contains(itemView.getContext().getString(R.string.media_type_audio).toLowerCase()) &&
                                            data.getGist().getContentType() != null &&
                                            data.getGist().getContentType().toLowerCase().contains(itemView.getContext().getString(R.string.content_type_audio).toLowerCase())) {
                                        /*play audio if already downloaded*/
                                        playDownloadedAudio(data);

                                        return;
                                    } else {
                                        /*play movie if already downloaded*/
                                        playDownloaded(data, clickPosition);
                                        return;
                                    }
                                } else {
                                    /*play movie from web URL*/
                                    appCMSPresenter.launchVideoPlayer(data,
                                            data.getGist().getId(),
                                            currentPlayingIndex,
                                            relatedVideoIds,
                                            -1,
                                            action);
                                }
                            }
                            if (action != null && !TextUtils.isEmpty(action)) {

                                if (isDonwloadPage && action.contains(trayAction)) {
                                    if (data.getGist() != null &&
                                            data.getGist().getMediaType() != null &&
                                            data.getGist().getMediaType().toLowerCase().contains(itemView.getContext().getString(R.string.media_type_audio).toLowerCase()) &&
                                            data.getGist().getContentType() != null &&
                                            data.getGist().getContentType().toLowerCase().contains(itemView.getContext().getString(R.string.content_type_audio).toLowerCase())) {
                                        /*play audio if already downloaded*/
                                        playDownloadedAudio(data);
                                        return;
                                    } else {
                                        /*play movie if already downloaded*/
                                        playDownloaded(data, clickPosition);
                                        return;
                                    }
                                }
                                if (action.contains(trayAction) &&
                                        data.getGist() != null &&
                                        data.getGist().getContentType() != null &&
                                        data.getGist().getContentType().equalsIgnoreCase("SERIES")) {
                                    action = mContext.getString(R.string.app_cms_action_showvideopage_key);
                                }
                                /*open video detail page*/
                                appCMSPresenter.launchButtonSelectedAction(permalink,
                                        action,
                                        title,
                                        null,
                                        data,
                                        false,
                                        currentPlayingIndex,
                                        relatedVideoIds);

                            }

                        }
                    }
                }

                @Override
                public void play(Component childComponent, ContentDatum data) {
                }
            };

        }

        for (int i = 0; i < itemView.getNumberOfChildren(); i++) {
            itemView.bindChild(itemView.getContext(),
                    itemView.getChild(i),
                    data,
                    jsonValueKeyMap,
                    onClickHandler,
                    componentViewType,
                    Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor()), appCMSPresenter, position);
        }
    }


    private String getDeleteSingleItemHistoryAction(Context context) {
        return context.getString(R.string.app_cms_delete_single_history_action);
    }

    private String getDeleteSingleItemWatchlistAction(Context context) {
        return context.getString(R.string.app_cms_delete_single_watchlist_action);
    }

    private String getDeleteSingleItemDownloadAction(Context context) {
        return context.getString(R.string.app_cms_delete_single_download_action);
    }

    private String getTrayAction(Context context) {
        return context.getString(R.string.app_cms_action_detailvideopage_key);
    }

    private String getVideoAction(Context context) {
        return context.getString(R.string.app_cms_action_watchvideo_key);
    }

    private String getHlsUrl(ContentDatum data) {
        if (data.getStreamingInfo() != null &&
                data.getStreamingInfo().getVideoAssets() != null &&
                data.getStreamingInfo().getVideoAssets().getHls() != null) {
            return data.getStreamingInfo().getVideoAssets().getHls();
        }
        return null;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        CollectionGridItemView componentView;
        TextView emptyView;

        public ViewHolder(TextView itemView) {
            super(itemView);
            this.emptyView = itemView;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (CollectionGridItemView) itemView;
        }

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
    public void receiveEvent(InternalEvent<?> event) {
        adapterData.clear();
        if (adapterData.size() == 0) {
            emptyList = true;
            sendEvent(hideRemoveAllButtonEvent);
            updateData(mRecyclerView, adapterData);
        } else {
            notifyDataSetChanged();
        }
    }

    @Override
    public void cancel(boolean cancel) {

    }

    @Override
    public String getModuleId() {
        return moduleId;
    }

    @Override
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    private List<String> getListOfUpcomingMovies(int position, Object downloadStatus) {
        if (position + 1 == adapterData.size()) {
            return Collections.emptyList();
        }

        List<String> contentDatumList = new ArrayList<>();
        for (int i = position + 1; i < adapterData.size(); i++) {
            ContentDatum contentDatum = adapterData.get(i);
            if (contentDatum.getGist() != null &&
                    contentDatum.getGist().getDownloadStatus().equals(downloadStatus)
                    && contentDatum.getGist().getContentType() != null
                    && contentDatum.getGist().getContentType().toLowerCase().equalsIgnoreCase(mContext.getString(R.string.media_type_video).toLowerCase())) {
                contentDatumList.add(contentDatum.getGist().getId());
            }
        }

        return contentDatumList;
    }

    private void playDownloaded(ContentDatum data, int position) {
        List<String> relatedVideoIds = new ArrayList<>();
        if (data.getGist().getDownloadStatus() != DownloadStatus.STATUS_COMPLETED &&
                data.getGist().getDownloadStatus() != DownloadStatus.STATUS_SUCCESSFUL) {
            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.DOWNLOAD_INCOMPLETE,
                    null,
                    false,
                    null,
                    null);
            return;
        }

        String permalink = data.getGist().getPermalink();
        String action = mContext.getString(R.string.app_cms_action_watchvideo_key);
        String title = data.getGist() != null ? data.getGist().getTitle() : null;
        String hlsUrl = data.getGist().getLocalFileUrl();

        String[] extraData = new String[4];
        extraData[0] = permalink;
        extraData[1] = hlsUrl;
        extraData[2] = data.getGist() != null ? data.getGist().getId() : null;
        extraData[3] = "true"; // to know that this is an offline video
        if (Boolean.parseBoolean(extraData[3])) {
            relatedVideoIds = getListOfUpcomingMovies(position, DownloadStatus.STATUS_COMPLETED);
            if (relatedVideoIds != null && relatedVideoIds.size() != 0) {
                /*remove current playing film id from the list*/
                relatedVideoIds.remove(data.getGist().getId());
            }
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
                        -1,
                        relatedVideoIds)) {
        }
    }

    @Override
    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    private void playDownloadedAudio(ContentDatum contentDatum) {
        try {

            if (contentDatum.getGist().getDownloadStatus() != DownloadStatus.STATUS_COMPLETED &&
                    contentDatum.getGist().getDownloadStatus() != DownloadStatus.STATUS_SUCCESSFUL) {
                appCMSPresenter.showDialog(AppCMSPresenter.DialogType.DOWNLOAD_INCOMPLETE,
                        null,
                        false,
                        null,
                        null);
                return;
            }
            appCMSPresenter.showLoadingDialog(true);
            AppCMSAudioDetailResult appCMSAudioDetailResult = convertToAudioResult(contentDatum);

        /*
        if at the time of click from download list device already connected to casatin device than get audio details from server
        and cast audio url to casting device
         */
            if (CastServiceProvider.getInstance(mContext).isCastingConnected()) {
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
                Intent intent = new Intent(mContext, AppCMSPlayAudioActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                MediaControllerCompat controller = MediaControllerCompat.getMediaController(appCMSPresenter.getCurrentActivity());
                if (controller != null) {
                    MediaMetadataCompat metadata = controller.getMetadata();
                    if (metadata != null) {
                        intent.putExtra(EXTRA_CURRENT_MEDIA_DESCRIPTION,
                                metadata);
                    }
                }
                mContext.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            appCMSPresenter.showLoadingDialog(false);
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
}
