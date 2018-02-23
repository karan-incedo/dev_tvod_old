package com.viewlift.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.viewlift.models.data.appcms.downloads.DownloadVideoRealm;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.CollectionGridItemView;
import com.viewlift.views.customviews.InternalEvent;
import com.viewlift.views.customviews.OnInternalEvent;
import com.viewlift.views.customviews.ViewCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.viewlift.models.data.appcms.downloads.DownloadStatus.STATUS_RUNNING;

/*
 * Created by viewlift on 5/5/17.
 */

public class AppCMSUserWatHisDowAdapter extends RecyclerView.Adapter<AppCMSUserWatHisDowAdapter.ViewHolder>
        implements AppCMSBaseAdapter, OnInternalEvent {
    private static final String TAG = "AppCMSUserWatHisDowAdapter";


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

    private String moduleId;
    RecyclerView mRecyclerView;

    public AppCMSUserWatHisDowAdapter(Context context,
                                      ViewCreator viewCreator,
                                      AppCMSPresenter appCMSPresenter,
                                      Settings settings,
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
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CollectionGridItemView view = viewCreator.createCollectionGridItemView(parent.getContext(),
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

        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(5,5,5,5);
        view.setLayoutParams(lp);

        if (emptyList) {
            TextView emptyView = new TextView(mContext);
            String textColor = appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor();
            emptyView.setTextColor(Color.parseColor(textColor));
            emptyView.setTextSize(24f);
            if (viewTypeKey == AppCMSUIKeyType.PAGE_HISTORY_MODULE_KEY) {
                emptyView.setText(mContext.getString(R.string.empty_history_list_message));
                return new ViewHolder(emptyView);
            }
            if (viewTypeKey == AppCMSUIKeyType.PAGE_WATCHLIST_MODULE_KEY) {
                emptyView.setText(mContext.getString(R.string.empty_watchlist_message));
                return new ViewHolder(emptyView);
            }
            if (viewTypeKey == AppCMSUIKeyType.PAGE_DOWNLOAD_MODULE_KEY) {
                emptyView.setText(mContext.getString(R.string.empty_download_message));
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
                if (viewTypeKey == AppCMSUIKeyType.PAGE_DOWNLOAD_MODULE_KEY) {
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

            if (contentDatum.getGist() != null) {
                switch (contentDatum.getGist().getDownloadStatus()) {
                    case STATUS_PENDING:
                    case STATUS_RUNNING:
                        if (contentDatum.getGist() != null) {
                            deleteDownloadButton.getBackground().setTint(ContextCompat.getColor(mContext, R.color.transparentColor));
                            deleteDownloadButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);

                            ImageButton finalDeleteDownloadButton = deleteDownloadButton;
                            ImageView finalThumbnailImage = thumbnailImage;
                            TextView finalVideoSize = videoSize;
                            appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                                    deleteDownloadButton,
                                    appCMSPresenter,
                                    userVideoDownloadStatus -> {
                                        if (userVideoDownloadStatus != null) {
                                            if (userVideoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_SUCCESSFUL) {
                                                finalDeleteDownloadButton.setImageBitmap(null);
                                                finalDeleteDownloadButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_deleteicon));
                                                finalDeleteDownloadButton.getBackground().setTint(Color.parseColor(appCMSPresenter.getColor(mContext,
                                                        appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor())));
                                                finalDeleteDownloadButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                                                finalDeleteDownloadButton.invalidate();
                                                loadImage(mContext, userVideoDownloadStatus.getThumbUri(), finalThumbnailImage);
                                                finalVideoSize.setText(appCMSPresenter.getDownloadedFileSize(userVideoDownloadStatus.getVideoSize()));

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
                                                finalDeleteDownloadButton.setImageBitmap(null);
                                                finalDeleteDownloadButton.setBackground(ContextCompat.getDrawable(mContext,
                                                        android.R.drawable.stat_sys_warning));
                                                finalVideoSize.setText("Remove".toUpperCase());
                                                finalVideoSize.setOnClickListener(v -> deleteDownloadVideo(contentDatum, position));
                                            } else if (userVideoDownloadStatus.getDownloadStatus() == STATUS_RUNNING) {
                                                finalVideoSize.setText("Cancel");
                                            }
                                            contentDatum.getGist().setDownloadStatus(userVideoDownloadStatus.getDownloadStatus());
                                        }
                                    },
                                    userId, true);

                            finalVideoSize.setText("Cancel".toUpperCase());
                            finalVideoSize.setOnClickListener(v -> deleteDownloadVideo(contentDatum, position));

                        }
                        break;

                    case STATUS_FAILED:
                        //
                        break;

                    case STATUS_SUCCESSFUL:
                        deleteDownloadButton.setBackground(ContextCompat.getDrawable(mContext,
                                R.drawable.ic_deleteicon));
                        deleteDownloadButton.getBackground().setTint(Color.parseColor(appCMSPresenter.getColor(mContext,
                                appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor())));
                        deleteDownloadButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                        contentDatum.getGist().setDownloadStatus(DownloadStatus.STATUS_COMPLETED);
                        break;

                    case STATUS_INTERRUPTED:
                        deleteDownloadButton.setBackground(ContextCompat.getDrawable(mContext,
                                android.R.drawable.stat_sys_warning));
                        videoSize.setText("Remove".toUpperCase());
                        videoSize.setOnClickListener(v -> deleteDownloadVideo(contentDatum, position));
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
        }
    }

    private void deleteDownloadVideo(final ContentDatum contentDatum, int position) {
        appCMSPresenter.showDialog(AppCMSPresenter.DialogType.DELETE_ONE_DOWNLOAD_ITEM,
                appCMSPresenter.getCurrentActivity().getString(R.string.app_cms_delete_one_download_item_message),
                true, () ->
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
        if (viewTypeKey == AppCMSUIKeyType.PAGE_HISTORY_MODULE_KEY ||
                viewTypeKey == AppCMSUIKeyType.PAGE_WATCHLIST_MODULE_KEY ||
                viewTypeKey == AppCMSUIKeyType.PAGE_DOWNLOAD_MODULE_KEY) {
            if (emptyList)
                return 1;
        }
        return (adapterData != null ? adapterData.size() : 0);
    }

    @Override
    public void resetData(RecyclerView listView) {
        notifyDataSetChanged();
    }

    @Override
    public void updateData(RecyclerView listView, List<ContentDatum> contentData) {
        listView.setAdapter(null);
        adapterData = null;
        notifyDataSetChanged();
        adapterData = contentData;
        notifyDataSetChanged();
        listView.setAdapter(this);
        listView.invalidate();
        notifyDataSetChanged();

        if (adapterData == null || adapterData.isEmpty() || adapterData.size() == 0) {
            sendEvent(hideRemoveAllButtonEvent);
        } else {
            sendEvent(showRemoveAllButtonEvent);
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
                                appCMSPresenter.navigateToArticlePage(data.getGist().getId(), data.getGist().getTitle(), false, null);
                                return;
                            }

                            if (action.contains(deleteSingleItemDownloadAction)) {
                                /*delete a single downloaded video*/
                                deleteDownloadVideo(data, position);
                            }
                            if (action.contains(deleteSingleItemWatchlistAction)) {
                                /*delete video from user watchlist*/
                                appCMSPresenter.editWatchlist(data.getGist().getId(),
                                        addToWatchlistResult -> {
                                            adapterData.remove(data);

                                            if (adapterData.size() == 0) {
                                                emptyList = true;
                                                sendEvent(hideRemoveAllButtonEvent);
                                                updateData(mRecyclerView, adapterData);
                                            }
                                            notifyDataSetChanged();
                                        }, false);
                                return;
                            }
                            if (action.contains(deleteSingleItemHistoryAction)) {
                                /*delete video from user history*/
                                appCMSPresenter.editHistory(data.getGist().getId(),
                                        appCMSDeleteHistoryResult -> {
                                            adapterData.remove(data);
                                            if (adapterData.size() == 0) {
                                                emptyList = true;
                                                sendEvent(hideRemoveAllButtonEvent);
                                                updateData(mRecyclerView, adapterData);
                                            }
                                            notifyDataSetChanged();
                                        }, false);
                                return;
                            }
                            if (action.contains(videoAction)) {
                                if (viewTypeKey == AppCMSUIKeyType.PAGE_DOWNLOAD_MODULE_KEY) {
                                    /*play movie if already downloaded*/
                                    playDownloaded(data, position);
                                } else {
                                    /*play movie from web URL*/
                                    appCMSPresenter.launchVideoPlayer(data,
                                            currentPlayingIndex,
                                            relatedVideoIds,
                                            -1,
                                            action);
                                }
                            }
                            if (action.contains(trayAction)) {
                                if (viewTypeKey == AppCMSUIKeyType.PAGE_DOWNLOAD_MODULE_KEY) {
                                    /*play movie if already downloaded*/
                                    playDownloaded(data, position);
                                    return;
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
                    contentDatum.getGist().getDownloadStatus().equals(downloadStatus)) {
                contentDatumList.add(contentDatum.getGist().getId());
            }
        }

        return contentDatumList;
    }

    private void playDownloaded(ContentDatum data, int position) {
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
}
