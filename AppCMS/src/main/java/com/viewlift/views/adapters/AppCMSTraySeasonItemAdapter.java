package com.viewlift.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.api.AppCMSTransactionDataValue;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.Season_;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
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
    Context mContext;

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
        this.mContext = context;
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
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
                        appCMSPresenter.setShowDatum(data);
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

                            /**
                             * if pricing type is TVOD than first call rental API and check video end date
                             * if video end date is greater than current date than play video else show message
                             */
                            if (data != null &&
                                    data.getPricing() != null &&
                                    data.getPricing().getType() != null)
//                                    data.getPricing().getType().equalsIgnoreCase("TVOD")))
                            {
                                int finalCurrentPlayingIndex = currentPlayingIndex;
                                List<String> finalRelatedVideoIds = relatedVideoIds;
                                String finalAction = action;
                                appCMSPresenter.getTransactionData(data.getGist().getId(), updatedContentDatum -> {
                                    boolean isPlayable = true;
                                    AppCMSTransactionDataValue objTransactionData = null;

                                    if (updatedContentDatum != null &&
                                            updatedContentDatum.size() > 0) {
                                        if (updatedContentDatum.get(0).size() == 0) {
                                            isPlayable = false;
                                        } else {
                                            objTransactionData = updatedContentDatum.get(0).get(data.getGist().getId());

                                        }
                                    }
//                                    if(updatedContentDatum==null){
//                                        isPlayable=true;
//                                    }
                                    if (!isPlayable) {
                                        appCMSPresenter.showNoPurchaseDialog(mContext.getString(R.string.rental_title), mContext.getString(R.string.rental_description));

                                    } else {

                                        String rentalPeriod = "";
                                        if (data.getPricing().getRent() != null &&
                                                data.getPricing().getRent().getRentalPeriod() != null) {
                                            rentalPeriod = data.getPricing().getRent().getRentalPeriod();
                                        }
                                        if (objTransactionData != null) {
                                            rentalPeriod = String.valueOf(objTransactionData.getRentalPeriod());
                                        }


                                        boolean isShowRentalPeriodDialog = true;
                                        /**
                                         * if transaction getdata api containf transaction end date .It means Rent API called before
                                         * and we have shown rent period dialog before so dont need to show rent dialog again. else sow rent period dilaog
                                         */
                                        if (objTransactionData.getTransactionEndDate() > 0) {
                                            isShowRentalPeriodDialog = false;
                                        } else {
                                            isShowRentalPeriodDialog = true;
                                        }

                                        if (isShowRentalPeriodDialog) {
                                            appCMSPresenter.showRentTimeDialog(retry -> {
                                                if (retry) {
                                                    appCMSPresenter.getRentalData(data.getGist().getId(), rentalResponse -> {


                                                        launchScreeenPlayer(data, finalCurrentPlayingIndex, relatedVideoIds, finalAction, title, permalink);

                                                        System.out.println("response ");
                                                    }, null, false, 0);
                                                } else {
//                                                appCMSPresenter.sendCloseOthersAction(null, true, false);
                                                }
                                            }, rentalPeriod);
                                        } else {
                                            launchScreeenPlayer(data, finalCurrentPlayingIndex, relatedVideoIds, finalAction, title, permalink);

                                        }

                                    }
                                    System.out.println("response ");
                                }, null, false);
                            } else if (data.getGist() == null ||
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
                    position, null);
        }
    }


    public void launchVideoPLayer() {

    }

    @Override
    public void resetData(RecyclerView listView) {
        //
    }


    void launchScreeenPlayer(ContentDatum data, int finalCurrentPlayingIndex, List<String> relatedVideoIds, String finalAction, String title, String permalink) {

        if (data.getGist() == null ||
                data.getGist().getContentType() == null) {
            if (!appCMSPresenter.launchVideoPlayer(data,
                    data.getGist().getId(),
                    finalCurrentPlayingIndex,
                    relatedVideoIds,
                    -1,
                    finalAction)) {
                //Log.e(TAG, "Could not launch action: " +
                //                                                " permalink: " +
                //                                                permalink +
                //                                                " action: " +
                //                                                action);
            }
        } else {
            if (!appCMSPresenter.launchButtonSelectedAction(permalink,
                    finalAction,
                    title,
                    null,
                    data,
                    false,
                    finalCurrentPlayingIndex,
                    relatedVideoIds)) {
                //Log.e(TAG, "Could not launch action: " +
                //                                                " permalink: " +
                //                                                permalink +
                //                                                " action: " +
                //                                                action);
            }
        }
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
