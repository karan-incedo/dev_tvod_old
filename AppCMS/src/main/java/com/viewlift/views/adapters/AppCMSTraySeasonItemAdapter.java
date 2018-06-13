package com.viewlift.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.Season_;
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.viewlift.models.data.appcms.downloads.DownloadVideoRealm;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.CollectionGridItemView;
import com.viewlift.views.customviews.InternalEvent;
import com.viewlift.views.customviews.OnInternalEvent;
import com.viewlift.views.customviews.ViewCreator;
import com.viewlift.views.rxbus.SeasonTabSelectorBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.viewlift.models.data.appcms.downloads.DownloadStatus.STATUS_RUNNING;

public class AppCMSTraySeasonItemAdapter extends RecyclerView.Adapter<AppCMSTraySeasonItemAdapter.ViewHolder>
        implements OnInternalEvent, AppCMSBaseAdapter {

    private static final String TAG = "TraySeasonItemAdapter";
    private final String episodicContentType;
    private final String fullLengthFeatureType;
    protected List<ContentDatum> adapterData;
    protected List<Component> components;
    protected AppCMSPresenter appCMSPresenter;
    protected Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    protected String defaultAction;
    private List<OnInternalEvent> receivers;
    private List<String> allEpisodeIds;
    private String moduleId;
    private ViewCreator.CollectionGridItemViewCreator collectionGridItemViewCreator;
    private CollectionGridItemView.OnClickHandler onClickHandler;
    private boolean isClickable;

    private MotionEvent lastTouchDownEvent;

    String componentViewType, seriesName;
    List<Season_> seasonList;
    RecyclerView mRecyclerView;
    private Map<String, Boolean> filmDownloadIconUpdatedMap;
    public AppCMSTraySeasonItemAdapter(Context context,
                                       ViewCreator.CollectionGridItemViewCreator collectionGridItemViewCreator,
                                       Module moduleAPI,
                                       List<Component> components,
                                       List<String> allEpisodeIds,
                                       AppCMSPresenter appCMSPresenter,
                                       Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                       String viewType,
                                       RecyclerView mRecyclerView) {
        this.collectionGridItemViewCreator = collectionGridItemViewCreator;
        seasonList = new ArrayList<>();
        seasonList.addAll(moduleAPI.getContentData().get(0).getSeason());
        Collections.reverse(seasonList);
        this.adapterData = seasonList.get(0).getEpisodes();
        this.sortData();
        this.seriesName = moduleAPI.getContentData().get(0).getGist().getTitle();
        this.components = components;
        this.allEpisodeIds = allEpisodeIds;
        this.appCMSPresenter = appCMSPresenter;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.defaultAction = getDefaultAction(context);

        this.receivers = new ArrayList<>();

        this.isClickable = true;
        this.mRecyclerView = mRecyclerView;

        this.episodicContentType = context.getString(R.string.app_cms_episodic_key_type);
        this.fullLengthFeatureType = context.getString(R.string.app_cms_full_length_feature_key_type);

        this.componentViewType = viewType;
        this.filmDownloadIconUpdatedMap = new HashMap<>();
        SeasonTabSelectorBus.instanceOf().getSelectedTab().subscribe(new Observer<List<ContentDatum>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<ContentDatum> adapterDataSeason) {
                adapterData = adapterDataSeason;
                updateData(mRecyclerView, adapterData);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }


    private void sortData() {
        if (adapterData != null) {
            // TODO: 10/3/17 Positioning of elements in adapter will be sorted at a later date.
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (adapterData.size() == 0) {
            return new ViewHolder(new View(parent.getContext()));
        }
        View view = collectionGridItemViewCreator.createView(parent.getContext());
        AppCMSTraySeasonItemAdapter.ViewHolder viewHolder = new AppCMSTraySeasonItemAdapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (adapterData != null && !adapterData.isEmpty()) {
            adapterData.get(position).setSeriesName(seriesName);
            if (0 <= position && position < adapterData.size()) {
                for (int i = 0; i < holder.componentView.getNumberOfChildren(); i++) {
                    if (holder.componentView.getChild(i) instanceof TextView) {
                        ((TextView) holder.componentView.getChild(i)).setText("");
                    }
                }
                bindView(holder.componentView, adapterData.get(position), position);
//                downloadView(adapterData.get(position), holder.componentView, position);
            }
        }
    }

    private void downloadView(ContentDatum contentDatum, CollectionGridItemView componentView, int position) {
        String userId = appCMSPresenter.getLoggedInUser();
        ImageButton deleteDownloadButton = null;
        for (int i = 0; i < componentView.getChildItems().size(); i++) {
            CollectionGridItemView.ItemContainer itemContainer = componentView.getChildItems().get(i);
            if (itemContainer.getComponent().getKey() != null) {
                if (itemContainer.getComponent().getKey().contains(componentView.getContext().getString(R.string.app_cms_page_delete_download_key))) {
                    deleteDownloadButton = (ImageButton) itemContainer.getChildView();
                }
            }
        }
        if (deleteDownloadButton != null) {
            int radiusDifference = 5;
            if (BaseView.isTablet(componentView.getContext())) {
                radiusDifference = 3;
            }
            if (contentDatum.getGist() != null) {
                deleteDownloadButton.setTag(contentDatum.getGist().getId());
                final ImageButton deleteButton = deleteDownloadButton;

                /**
                 * if content already downloaded then just update the status , no need to call update status for running progress
                 */
                if (appCMSPresenter.isVideoDownloaded(contentDatum.getGist().getId())) {
                    deleteDownloadButton.setImageBitmap(null);
                    deleteDownloadButton.setBackground(ContextCompat.getDrawable(componentView.getContext(),
                            R.drawable.ic_deleteicon));
                    deleteDownloadButton.getBackground().setTint(appCMSPresenter.getBrandPrimaryCtaColor());
                    deleteDownloadButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                    contentDatum.getGist().setDownloadStatus(DownloadStatus.STATUS_COMPLETED);
                    deleteDownloadButton.invalidate();
                    deleteDownloadButton.postInvalidate();
                } else {
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
                                        deleteButton.invalidate();
                                    }
                                    if (videoDownloadStatus != null && videoDownloadStatus.getDownloadStatus() != null) {
                                        contentDatum.getGist().setDownloadStatus(videoDownloadStatus.getDownloadStatus());
                                    }
                                }
                            },
                            appCMSPresenter.getLoggedInUser());

                    switch (contentDatum.getGist().getDownloadStatus()) {
                        case STATUS_FAILED:
                            Log.e(TAG, "Film download failed: " + contentDatum.getGist().getId());
                            deleteDownloadButton.setImageResource(android.R.drawable.stat_sys_warning);
                            break;
                        case STATUS_PAUSED:
                            deleteDownloadButton.setImageResource(R.drawable.ic_download_queued);
                            break;
                        case STATUS_PENDING:
                            deleteDownloadButton.setImageResource(R.drawable.ic_download_queued);

                            break;
                        case STATUS_RUNNING:
                            if (contentDatum.getGist() != null && deleteDownloadButton != null) {
                                if (deleteDownloadButton.getBackground() != null) {
                                    deleteDownloadButton.getBackground().setTint(ContextCompat.getColor(componentView.getContext(), R.color.transparentColor));
                                    deleteDownloadButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                                }

                                ImageButton finalDeleteDownloadButton = deleteDownloadButton;

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
                                                        finalDeleteDownloadButton.setBackground(ContextCompat.getDrawable(componentView.getContext(),
                                                                R.drawable.ic_deleteicon));
                                                        finalDeleteDownloadButton.getBackground().setTint(appCMSPresenter.getBrandPrimaryCtaColor());
                                                        finalDeleteDownloadButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                                                        finalDeleteDownloadButton.setBackground(ContextCompat.getDrawable(componentView.getContext(),
                                                                R.drawable.ic_deleteicon));
                                                        finalDeleteDownloadButton.invalidate();
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
                                                        notifyItemChanged(position);

                                                    } else if (userVideoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_INTERRUPTED) {
                                                        finalDeleteDownloadButton.setImageBitmap(null);
                                                        finalDeleteDownloadButton.setBackground(ContextCompat.getDrawable(componentView.getContext(),
                                                                android.R.drawable.stat_sys_warning));
                                                        finalDeleteDownloadButton.invalidate();
                                                    } else if (userVideoDownloadStatus.getDownloadStatus() == DownloadStatus.STATUS_PENDING) {
                                                        finalDeleteDownloadButton.setBackground(ContextCompat.getDrawable(componentView.getContext(),
                                                                R.drawable.ic_download_queued));
                                                        finalDeleteDownloadButton.invalidate();

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
                            }
                            break;

                        case STATUS_SUCCESSFUL:
                            Log.e(TAG, "Film download successful: " + contentDatum.getGist().getId());
                            deleteDownloadButton.setImageBitmap(null);
                            deleteDownloadButton.setBackground(ContextCompat.getDrawable(componentView.getContext(),
                                    R.drawable.ic_deleteicon));
                            deleteDownloadButton.getBackground().setTint(appCMSPresenter.getBrandPrimaryCtaColor());
                            deleteDownloadButton.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                            deleteDownloadButton.invalidate();
                            contentDatum.getGist().setDownloadStatus(DownloadStatus.STATUS_COMPLETED);

                            break;
                        case STATUS_COMPLETED:
                            if (contentDatum.getGist().getDownloadStatus() == DownloadStatus.STATUS_COMPLETED
                                    && contentDatum.getGist().getLocalFileUrl().contains("data")) {
                                DownloadVideoRealm videoRealm = appCMSPresenter.getRealmController().getDownloadById(contentDatum.getGist().getId());
                                contentDatum.getGist().setPosterImageUrl(videoRealm.getPosterFileURL());
                                contentDatum.getGist().setLocalFileUrl(videoRealm.getLocalURI());
                            }
                            break;


                        case STATUS_INTERRUPTED:
                            Log.e(TAG, "Film download interrupted: " + contentDatum.getGist().getId());
                            deleteDownloadButton.setImageResource(android.R.drawable.stat_sys_warning);
                            break;


                        default:
                            Log.e(TAG, "Film download status unknown: " + contentDatum.getGist().getId());
                            deleteDownloadButton.setImageBitmap(null);
                            deleteDownloadButton.setBackground(ContextCompat.getDrawable(componentView.getContext(),
                                    R.drawable.ic_download_queued));
                            deleteDownloadButton.invalidate();
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
    public String getModuleId() {
        return moduleId;
    }

    @Override
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
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

    private void bindView(CollectionGridItemView itemView,
                          final ContentDatum data,
                          int position) {
        if (onClickHandler == null) {
            onClickHandler = new CollectionGridItemView.OnClickHandler() {
                @Override
                public void click(CollectionGridItemView collectionGridItemView,
                                  Component childComponent,
                                  ContentDatum data,
                                  int position) {
                    if (isClickable) {
                        AppCMSUIKeyType componentKey = jsonValueKeyMap.get(childComponent.getKey());
                        /**
                         * if click happened from description text then no need to show play screen as more fragment open
                         */
                        if (componentKey == AppCMSUIKeyType.PAGE_API_DESCRIPTION) {
                            return;
                        }
                        if (data.getGist() != null) {
                            //Log.d(TAG, "Clicked on item: " + data.getGist().getTitle());
                            String permalink = data.getGist().getPermalink();
                            String action = defaultAction;
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
                            List<String> relatedVideoIds = allEpisodeIds;
                            int currentPlayingIndex = -1;
                            if (allEpisodeIds != null) {
                                int currentEpisodeIndex = allEpisodeIds.indexOf(data.getGist().getId());
                                if (currentEpisodeIndex < allEpisodeIds.size()) {
                                    currentPlayingIndex = currentEpisodeIndex;
                                }
                            }
                            if (relatedVideoIds == null) {
                                currentPlayingIndex = 0;
                            }

                            if (data.getGist() == null ||
                                    data.getGist().getContentType() == null) {
                                if (!appCMSPresenter.launchVideoPlayer(data,
                                        data.getGist().getId(),
                                        currentPlayingIndex,
                                        relatedVideoIds,
                                        -1,
                                        action)) {
                                    //Log.e(TAG, "Could not launch action: " +
                                    //                                                " permalink: " +
                                    //                                                permalink +
                                    //                                                " action: " +
                                    //                                                action);
                                }
                            } else {
                                if (!appCMSPresenter.launchButtonSelectedAction(permalink,
                                        action,
                                        title,
                                        null,
                                        data,
                                        false,
                                        currentPlayingIndex,
                                        relatedVideoIds)) {
                                    //Log.e(TAG, "Could not launch action: " +
                                    //                                                " permalink: " +
                                    //                                                permalink +
                                    //                                                " action: " +
                                    //                                                action);
                                }
                            }
                        }
                    }
                }

                @Override
                public void play(Component childComponent, ContentDatum data) {
                    if (isClickable) {
                        if (data.getGist() != null) {
                            //Log.d(TAG, "Playing item: " + data.getGist().getTitle());
                            List<String> relatedVideoIds = allEpisodeIds;
                            int currentPlayingIndex = -1;
                            if (allEpisodeIds != null) {
                                int currentEpisodeIndex = allEpisodeIds.indexOf(data.getGist().getId());
                                if (currentEpisodeIndex < allEpisodeIds.size()) {
                                    currentPlayingIndex = currentEpisodeIndex;
                                }
                            }
                            if (relatedVideoIds == null) {
                                currentPlayingIndex = 0;
                            }
                            if (!appCMSPresenter.launchVideoPlayer(data,
                                    data.getGist().getId(),
                                    currentPlayingIndex,
                                    relatedVideoIds,
                                    -1,
                                    null)) {
                                //Log.e(TAG, "Could not launch play action: " +
                                //                                            " filmId: " +
                                //                                            filmId +
                                //                                            " permaLink: " +
                                //                                            permaLink +
                                //                                            " title: " +
                                //                                            title);
                            }
                        }
                    }
                }
            };
        }

        itemView.setOnTouchListener((View v, MotionEvent event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                lastTouchDownEvent = event;
            }

            return false;
        });
        itemView.setOnClickListener(v -> {
            if (isClickable) {
                if (v instanceof CollectionGridItemView) {
                    try {
                        int eventX = (int) lastTouchDownEvent.getX();
                        int eventY = (int) lastTouchDownEvent.getY();
                        ViewGroup childContainer = ((CollectionGridItemView) v).getChildrenContainer();
                        int childrenCount = childContainer.getChildCount();
                        for (int i = 0; i < childrenCount; i++) {
                            View childView = childContainer.getChildAt(i);
                            if (childView instanceof Button) {
                                int[] childLocation = new int[2];
                                childView.getLocationOnScreen(childLocation);
                                int childX = childLocation[0] - 8;
                                int childY = childLocation[1] - 8;
                                int childWidth = childView.getWidth() + 8;
                                int childHeight = childView.getHeight() + 8;
                                if (childX <= eventX && eventX <= childX + childWidth) {
                                    if (childY <= eventY && eventY <= childY + childHeight) {
                                        childView.performClick();
                                        return;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {

                    }
                }

                String permalink = data.getGist().getPermalink();
                String title = data.getGist().getTitle();
                String action = defaultAction;

                //Log.d(TAG, "Launching " + permalink + ":" + action);
                List<String> relatedVideoIds = allEpisodeIds;
                int currentPlayingIndex = -1;
                if (allEpisodeIds != null) {
                    int currentEpisodeIndex = allEpisodeIds.indexOf(data.getGist().getId());
                    if (currentEpisodeIndex < allEpisodeIds.size()) {
                        currentPlayingIndex = currentEpisodeIndex;
                    }
                }
                if (relatedVideoIds == null) {
                    currentPlayingIndex = 0;
                }
            }
        });

        for (int i = 0; i < itemView.getNumberOfChildren(); i++) {
            itemView.bindChild(itemView.getContext(),
                    itemView.getChild(i),
                    data,
                    jsonValueKeyMap,
                    onClickHandler,
                    componentViewType,
                    Color.parseColor(appCMSPresenter.getAppTextColor()),
                    appCMSPresenter,
                    position,null);
        }
    }

    @Override
    public void resetData(RecyclerView listView) {
        //
    }


    @Override
    public void updateData(RecyclerView listView, List<ContentDatum> contentData) {
        listView.setAdapter(null);
        adapterData = null;
        adapterData = contentData;
        listView.setAdapter(this);
        listView.invalidate();
        notifyDataSetChanged();

    }

    @Override
    public void setClickable(boolean clickable) {

    }

    private String getDefaultAction(Context context) {
        return context.getString(R.string.app_cms_action_videopage_key);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CollectionGridItemView componentView;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof CollectionGridItemView)
                this.componentView = (CollectionGridItemView) itemView;
        }
    }
}
