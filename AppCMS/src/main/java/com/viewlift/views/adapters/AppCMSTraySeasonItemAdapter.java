package com.viewlift.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.CollectionGridItemView;
import com.viewlift.views.customviews.InternalEvent;
import com.viewlift.views.customviews.OnInternalEvent;
import com.viewlift.views.customviews.ViewCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    String componentViewType;
    private List<OnInternalEvent> receivers;
    private List<String> allEpisodeIds;
    private String moduleId;
    private ViewCreator.CollectionGridItemViewCreator collectionGridItemViewCreator;
    private CollectionGridItemView.OnClickHandler onClickHandler;
    private boolean isClickable;
    private MotionEvent lastTouchDownEvent;






    public AppCMSTraySeasonItemAdapter(Context context,
                                       ViewCreator.CollectionGridItemViewCreator collectionGridItemViewCreator,
                                       List<ContentDatum> adapterData,
                                       List<Component> components,
                                       List<String> allEpisodeIds,
                                       AppCMSPresenter appCMSPresenter,
                                       Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                       String viewType) {
        this.collectionGridItemViewCreator = collectionGridItemViewCreator;
        this.adapterData = adapterData;
        this.sortData();
        this.components = components;
        this.allEpisodeIds = allEpisodeIds;
        this.appCMSPresenter = appCMSPresenter;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.defaultAction = getDefaultAction(context);

        this.receivers = new ArrayList<>();

        this.isClickable = true;

        this.episodicContentType = context.getString(R.string.app_cms_episodic_key_type);
        this.fullLengthFeatureType = context.getString(R.string.app_cms_full_length_feature_key_type);

        this.componentViewType = viewType;
    }

    private void sortData() {
        if (adapterData != null) {
            // TODO: 10/3/17 Positioning of elements in adapter will be sorted at a later date.
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = collectionGridItemViewCreator.createView(parent.getContext());
        AppCMSTraySeasonItemAdapter.ViewHolder viewHolder = new AppCMSTraySeasonItemAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (adapterData != null && !adapterData.isEmpty()) {
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

                if (data.getGist() == null ||
                        data.getGist().getContentType() == null) {
                    if (!appCMSPresenter.launchVideoPlayer(data,
                            currentPlayingIndex,
                            relatedVideoIds,
                            -1,
                            action)) {
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
                    }
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
                    Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor()),
                    appCMSPresenter,
                    position);
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

    @Override
    public void setClickable(boolean clickable) {

    }

    private void click(ContentDatum data) {
        //Log.d(TAG, "Clicked on item: " + data.getGist().getTitle());

        String permalink = data.getGist().getPermalink();
        String action = defaultAction;
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
                    position);
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
            this.componentView = (CollectionGridItemView) itemView;
        }
    }
}
