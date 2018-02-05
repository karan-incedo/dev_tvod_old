package com.viewlift.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.viewlift.Audio.model.MusicLibrary;
import com.viewlift.Audio.playback.AudioPlaylistHelper;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.audio.AppCMSAudioDetailResult;
import com.viewlift.models.data.appcms.downloads.UserVideoDownloadStatus;
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
import java.util.List;
import java.util.Map;

import rx.functions.Action1;

/*
 * Created by viewlift on 5/5/17.
 */

public class AppCMSPlaylistAdapter extends RecyclerView.Adapter<AppCMSPlaylistAdapter.ViewHolder>
        implements AppCMSBaseAdapter, OnInternalEvent {
    private static final String TAG = AppCMSPlaylistAdapter.class.getSimpleName() + "TAG";


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

    private List<OnInternalEvent> receivers;
    private String mCurrentPlayListId;

    private String moduleId;
    RecyclerView mRecyclerView;
    String downloadAudioAction;
    CollectionGridItemView[] allViews;

    public AppCMSPlaylistAdapter(Context context,
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
        this.downloadAudioAction = getDownloadAudioAction(context);
        this.adapterData = new ArrayList<>();

        if (moduleAPI != null && moduleAPI.getContentData() != null) {
            adapterData.addAll(moduleAPI.getContentData());
            if (moduleAPI.getContentData().get(0).getGist() != null) {
                mCurrentPlayListId = moduleAPI.getContentData().get(0).getGist().getId();
            }
             /*removing 1st data in the list since it contains playlist GIST*/
            if (moduleAPI.getContentData().get(0).getGist() == null) {
                adapterData.remove(0);
            }
            allViews = new CollectionGridItemView[this.adapterData.size()];
        }
        this.componentViewType = viewType;
        this.viewTypeKey = jsonValueKeyMap.get(componentViewType);
        if (this.viewTypeKey == null) {
            this.viewTypeKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.useMarginsAsPercentages = true;
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
                false);
        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (0 <= position && position < adapterData.size()) {
            allViews[position] = holder.componentView;
            bindView(holder.componentView, adapterData.get(position), position);
            if (AudioPlaylistHelper.getInstance().getCurrentAudioPLayingData() != null) {
                if (adapterData.get(position).getGist().getId().equalsIgnoreCase(AudioPlaylistHelper.getInstance().getCurrentAudioPLayingData().getGist().getId())) {
                    adapterData.get(position).getGist().setAudioPlaying(AudioPlaylistHelper.getInstance().getCurrentAudioPLayingData().getGist().isAudioPlaying());
                } else {
                    adapterData.get(position).getGist().setAudioPlaying(false);
                }
            }
            if (adapterData.get(position).getGist().isAudioPlaying()) {
                holder.componentView.setBackgroundColor(Color.parseColor("#4B0502"));
            } else {
                holder.componentView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
            }
        }
    }

    @Override
    public int getItemCount() {
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

    }

    static int oldClick = -1;

    @SuppressLint("ClickableViewAccessibility")
    void bindView(CollectionGridItemView itemView,
                  final ContentDatum data, int position) throws IllegalArgumentException {
        if (onClickHandler == null) {
            if (viewTypeKey == AppCMSUIKeyType.PAGE_PLAYLIST_MODULE_KEY) {
                onClickHandler = new CollectionGridItemView.OnClickHandler() {
                    @Override
                    public void click(CollectionGridItemView collectionGridItemView,
                                      Component childComponent,
                                      ContentDatum data, int clickPosition) {
                        if (isClickable) {
                            if (data.getGist() != null) {
                                String action = null;
                                if (childComponent != null && !TextUtils.isEmpty(childComponent.getAction())) {
                                    action = childComponent.getAction();
                                }
                                if (action != null && action.contains(downloadAudioAction)) {
                                    ImageButton download = null;
                                    for (int i = 0; i < collectionGridItemView.getChildItems().size(); i++) {
                                        CollectionGridItemView.ItemContainer itemContainer = collectionGridItemView.getChildItems().get(i);
                                        if (itemContainer.getComponent().getKey() != null) {
                                            if (itemContainer.getComponent().getKey().contains(mContext.getString(R.string.app_cms_page_audio_download_button_key))) {
                                                download = (ImageButton) itemContainer.getChildView();
                                            }
                                        }
                                    }
                                    audioDownload(download, data, false);
                                }
                                if (action == null) {
                          /*get audio details on tray click item and play song*/
                                    if (data.getGist() != null &&
                                            data.getGist().getMediaType() != null &&
                                            data.getGist().getMediaType().toLowerCase().contains(itemView.getContext().getString(R.string.media_type_audio).toLowerCase()) &&
                                            data.getGist().getContentType() != null &&
                                            data.getGist().getContentType().toLowerCase().contains(itemView.getContext().getString(R.string.content_type_audio).toLowerCase())) {
                                        appCMSPresenter.getCurrentActivity().sendBroadcast(new Intent(AppCMSPresenter
                                                .PRESENTER_PAGE_LOADING_ACTION));
                                        // on click from playlist adapter .Get playlist from temp list and set into current playlist
                                        if (AudioPlaylistHelper.getInstance().getCurrentPlaylistId() != null && !AudioPlaylistHelper.getInstance().getCurrentPlaylistId().equalsIgnoreCase(mCurrentPlayListId)) {
                                            AudioPlaylistHelper.getInstance().setCurrentPlaylistId(mCurrentPlayListId);
                                            AudioPlaylistHelper.getInstance().setCurrentPlaylistData(AudioPlaylistHelper.getInstance().getTempPlaylistData());
                                            AudioPlaylistHelper.getInstance().setPlaylist(MusicLibrary.createPlaylistByIDList(AudioPlaylistHelper.getInstance().getTempPlaylistData().getAudioList()));
                                        }
                                        if (oldClick == -1) {
                                            oldClick = clickPosition;
                                            data.getGist().setAudioPlaying(true);
                                        } else {
                                            adapterData.get(oldClick).getGist().setAudioPlaying(false);
                                            oldClick = clickPosition;
                                            data.getGist().setAudioPlaying(true);
                                        }

                                        AudioPlaylistHelper.getInstance().playAudioOnClickItem(data.getGist().getId(), 0);
                                        updateData(mRecyclerView, adapterData);
                                        return;
                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void play(Component childComponent, ContentDatum data) {
                    }
                };

            }
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


    public static class ViewHolder extends RecyclerView.ViewHolder {
        CollectionGridItemView componentView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (CollectionGridItemView) itemView;
        }

    }

    @Override
    public void addReceiver(OnInternalEvent e) {
        receivers.add(e);
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
        notifyDataSetChanged();
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


    @Override
    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    private String getDownloadAudioAction(Context context) {
        return context.getString(R.string.app_cms_download_audio_action);
    }

    public void startDownloadPlaylist() {
        for (int i = 0; i < allViews.length; i++) {
            for (int j = 0; j < allViews[i].getChildItems().size(); j++) {
                CollectionGridItemView.ItemContainer itemContainer = allViews[i].getChildItems().get(j);
                if (itemContainer.getComponent().getKey() != null) {
                    if (itemContainer.getComponent().getKey().contains(mContext.getString(R.string.app_cms_page_audio_download_button_key))) {
                        ImageButton download = (ImageButton) itemContainer.getChildView();
                        audioDownload(download, adapterData.get(i), true);
                    }
                }
            }
        }
    }

    void audioDownload(ImageButton download, ContentDatum data, Boolean playlistDownload) {
        appCMSPresenter.getAudioDetail(data.getGist().getId(),
                0, null, false, false,
                new AppCMSPresenter.AppCMSAudioDetailAPIAction(false,
                        false,
                        false,
                        null,
                        data.getGist().getId(),
                        data.getGist().getId(),
                        null,
                        data.getGist().getId(),
                        false, null) {
                    @Override
                    public void call(AppCMSAudioDetailResult appCMSAudioDetailResult) {
                        AppCMSPageAPI audioApiDetail = appCMSAudioDetailResult.convertToAppCMSPageAPI(data.getGist().getId());
                        updateDownloadImageAndStartDownloadProcess(audioApiDetail.getModules().get(0).getContentData().get(0), download, playlistDownload);
                    }
                });


    }

    void updateDownloadImageAndStartDownloadProcess(ContentDatum contentDatum, ImageButton downloadView,
                                                    Boolean playlistDownload) {
        String userId = appCMSPresenter.getLoggedInUser();
        appCMSPresenter.getUserVideoDownloadStatus(
                contentDatum.getGist().getId(),
                new UpdateDownloadImageIconAction(downloadView,
                        appCMSPresenter,
                        contentDatum, userId, playlistDownload), userId);
    }

    /**
     * This class has been created to updated the Download Image Action and Status
     */
    private static class UpdateDownloadImageIconAction implements Action1<UserVideoDownloadStatus> {
        private final AppCMSPresenter appCMSPresenter;
        private final ContentDatum contentDatum;
        private final String userId;
        private ImageButton imageButton;
        private Boolean playlistDownload;
        private View.OnClickListener addClickListener;

        UpdateDownloadImageIconAction(ImageButton imageButton, AppCMSPresenter presenter,
                                      ContentDatum contentDatum, String userId, boolean playlistDownload) {
            this.imageButton = imageButton;
            this.appCMSPresenter = presenter;
            this.contentDatum = contentDatum;
            this.playlistDownload = playlistDownload;
            this.userId = userId;

            addClickListener = v -> {
                if (!appCMSPresenter.isNetworkConnected()) {
                    if (!appCMSPresenter.isUserLoggedIn()) {
                        appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK, null, false,
                                appCMSPresenter::launchBlankPage,
                                null);
                        return;
                    }
                    appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                            appCMSPresenter.getNetworkConnectivityDownloadErrorMsg(),
                            true,
                            () -> appCMSPresenter.navigateToDownloadPage(appCMSPresenter.getDownloadPageId(),
                                    null, null, false),
                            null);
                    return;
                }
                if ((appCMSPresenter.isUserSubscribed()) &&
                        appCMSPresenter.isUserLoggedIn()) {
                    appCMSPresenter.editDownload(UpdateDownloadImageIconAction.this.contentDatum, UpdateDownloadImageIconAction.this, true);
                } else {
                    if (appCMSPresenter.isUserLoggedIn()) {
                        appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.SUBSCRIPTION_REQUIRED,
                                () -> {
                                    appCMSPresenter.setAfterLoginAction(() -> {

                                    });
                                });
                    } else {
                        appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED,
                                () -> {
                                    appCMSPresenter.setAfterLoginAction(() -> {

                                    });
                                });
                    }
                }
                imageButton.setOnClickListener(null);
            }

            ;
        }

        @Override
        public void call(UserVideoDownloadStatus userVideoDownloadStatus) {
            if (userVideoDownloadStatus != null) {

                switch (userVideoDownloadStatus.getDownloadStatus()) {
                    case STATUS_FAILED:
                        appCMSPresenter.setDownloadInProgress(false);
                        appCMSPresenter.startNextDownload();
                        break;

                    case STATUS_PAUSED:
                        //
                        break;

                    case STATUS_PENDING:
                        appCMSPresenter.setDownloadInProgress(false);
                        appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                                UpdateDownloadImageIconAction.this.imageButton, appCMSPresenter, this, userId, false);
                        imageButton.setOnClickListener(null);
                        break;

                    case STATUS_RUNNING:
                        appCMSPresenter.setDownloadInProgress(true);
                        appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                                UpdateDownloadImageIconAction.this.imageButton, appCMSPresenter, this, userId, false);
                        imageButton.setOnClickListener(null);
                        break;

                    case STATUS_SUCCESSFUL:
                        appCMSPresenter.setDownloadInProgress(false);
                        appCMSPresenter.cancelDownloadIconTimerTask();
                        imageButton.setImageResource(R.drawable.ic_downloaded);
                        imageButton.setOnClickListener(null);
                        appCMSPresenter.notifyDownloadHasCompleted();
                        break;

                    case STATUS_INTERRUPTED:
                        appCMSPresenter.setDownloadInProgress(false);
                        imageButton.setImageResource(android.R.drawable.stat_sys_warning);
                        imageButton.setOnClickListener(null);
                        break;

                    default:
                        //Log.d(TAG, "No download Status available ");
                        break;
                }

            } else {
                appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                        UpdateDownloadImageIconAction.this.imageButton, appCMSPresenter, this, userId, false);
                imageButton.setImageResource(R.drawable.ic_download);
                imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                int fillColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor());
                imageButton.getDrawable().setColorFilter(new PorterDuffColorFilter(fillColor, PorterDuff.Mode.MULTIPLY));
                imageButton.setOnClickListener(addClickListener);
//                if (playlistDownload) {
//                addClickListener.onClick(imageButton);
//                }
            }
        }

        public void updateDownloadImageButton(ImageButton imageButton) {
            this.imageButton = imageButton;
        }

    }


}