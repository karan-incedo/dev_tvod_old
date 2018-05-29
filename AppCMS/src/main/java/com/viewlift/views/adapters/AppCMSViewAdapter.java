package com.viewlift.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viewlift.Audio.playback.AudioPlaylistHelper;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.photogallery.IPhotoGallerySelectListener;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.CollectionGridItemView;
import com.viewlift.views.customviews.PhotoGalleryNextPreviousListener;
import com.viewlift.views.customviews.ViewCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
 * Created by viewlift on 5/5/17.
 */

public class AppCMSViewAdapter extends RecyclerView.Adapter<AppCMSViewAdapter.ViewHolder>
        implements AppCMSBaseAdapter {
    private static final String TAG = "AppCMSViewAdapter";
    static int selectedPosition = -1;
    private final String episodicContentType;
    private final String fullLengthFeatureType;
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
    CollectionGridItemView planItemView[];
    private boolean useParentSize;
    private String defaultAction;
    private AppCMSUIKeyType viewTypeKey;
    private boolean isSelected;
    private int unselectedColor;
    private int selectedColor;
    private boolean isClickable;
    private String videoAction;
    private String openOptionsAction;
    private String purchasePlanAction;
    private String showAction;
    private MotionEvent lastTouchDownEvent;
    private String watchVideoAction;
    private String watchTrailerAction;
    private String watchTrailerQuailifier;
    private Gist preGist;

    public AppCMSViewAdapter(Context context,
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
        if (moduleAPI != null && moduleAPI.getContentData() != null) {
            this.adapterData = moduleAPI.getContentData();
        } else {
            this.adapterData = new ArrayList<>();
        }

        this.componentViewType = viewType;
        this.viewTypeKey = jsonValueKeyMap.get(componentViewType);
        if (this.viewTypeKey == null) {
            this.viewTypeKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        if (this.viewTypeKey == AppCMSUIKeyType.PAGE_PHOTO_TRAY_MODULE_KEY) {
           /*remove data from 1st position since it contains photogallery details*/
            if (adapterData.get(0).getStreamingInfo() != null) {
                List<ContentDatum> data = new ArrayList<>();
                data.addAll(moduleAPI.getContentData());
                data.remove(0);
                adapterData = data;
            }
            selectedPosition = 0;
            if (adapterData.size() > 0) {
                preGist = adapterData.get(0).getGist();
            }
        }
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.useMarginsAsPercentages = true;
        this.defaultAction = getDefaultAction(context);
        this.videoAction = getVideoAction(context);
        this.showAction = getShowAction(context);
        this.openOptionsAction = getOpenOptionsAction(context);
        this.purchasePlanAction = getPurchasePlanAction(context);

        this.isSelected = false;
        this.unselectedColor = ContextCompat.getColor(context, android.R.color.white);
        this.selectedColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand()
                .getCta().getPrimary().getBackgroundColor());
        this.isClickable = true;

        this.setHasStableIds(false);

        this.watchVideoAction = context.getString(R.string.app_cms_action_watchvideo_key);
        this.watchTrailerAction = context.getString(R.string.app_cms_action_watchtrailervideo_key);
        this.watchTrailerQuailifier = context.getString(R.string.app_cms_action_qualifier_watchvideo_key);

        this.appCMSAndroidModules = appCMSAndroidModules;

        this.episodicContentType = context.getString(R.string.app_cms_episodic_key_type);
        this.fullLengthFeatureType = context.getString(R.string.app_cms_full_length_feature_key_type);
        planItemView = new CollectionGridItemView[adapterData.size()];
        //sortPlan(); as per MSEAN-1434
    }

    private IPhotoGallerySelectListener iPhotoGallerySelectListener;

    public void setPhotoGalleryImageSelectionListener(IPhotoGallerySelectListener iPhotoGallerySelectListener) {
        this.iPhotoGallerySelectListener = iPhotoGallerySelectListener;
    }

    public PhotoGalleryNextPreviousListener setPhotoGalleryImageSelectionListener(PhotoGalleryNextPreviousListener listener) {
        listener = new PhotoGalleryNextPreviousListener() {
            @Override
            public void previousPhoto(Button previousButton) {

                if (getSelectedPosition() == 0) {
                    return;
                } else if (getSelectedPosition() == 1) {
                    previousButton.setBackgroundColor(Color.parseColor("#c8c8c8"));
                    previousButton.setEnabled(false);
                }
                selectedPosition--;
                iPhotoGallerySelectListener.selectedImageData(adapterData.get(selectedPosition).getGist().getVideoImageUrl(), selectedPosition);
                if (preGist != null)
                    preGist.setSelectedPosition(false);
                preGist = adapterData.get(getSelectedPosition()).getGist();
                adapterData.get(getSelectedPosition()).getGist().setSelectedPosition(true);
                notifyDataSetChanged();
            }

            @Override
            public void nextPhoto(Button nextButton) {
                if (getSelectedPosition() == adapterData.size() - 1) {
                    return;
                } else if (getSelectedPosition() == adapterData.size() - 2 || getSelectedPosition() == 1) {
                    nextButton.setBackgroundColor(Color.parseColor("#c8c8c8"));
                    nextButton.setEnabled(false);
                }
                if (adapterData.size() == 0) {
                    nextButton.setBackgroundColor(Color.parseColor("#c8c8c8"));
                    nextButton.setEnabled(false);
                    return;
                }
                selectedPosition++;
                iPhotoGallerySelectListener.selectedImageData(adapterData.get(selectedPosition).getGist().getVideoImageUrl(), selectedPosition);
                if (preGist != null)
                    preGist.setSelectedPosition(false);
                preGist = adapterData.get(getSelectedPosition()).getGist();
                adapterData.get(getSelectedPosition()).getGist().setSelectedPosition(true);
                notifyDataSetChanged();
            }
        };


        return listener;
    }

    public static int getSelectedPosition() {
        return selectedPosition;
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
                useRoundedCorners(), this.viewTypeKey);

        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY) {
            applyBgColorToChildren(view, selectedColor);
        }

        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY ||
                viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY) {
            if (viewTypeKey != AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY) {
                setBorder(view, unselectedColor);
            }
        }
        if (viewTypeKey == AppCMSUIKeyType.PAGE_PHOTO_TRAY_MODULE_KEY) {
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }


        return new ViewHolder(view);
    }

    private boolean useRoundedCorners() {
        return mContext.getString(R.string.app_cms_page_subscription_selectionplan_02_key).equals(componentViewType);
    }

    private void applyBgColorToChildren(ViewGroup viewGroup, int bgColor) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                if (child instanceof CardView) {
                    ((CardView) child).setUseCompatPadding(true);
                    ((CardView) child).setPreventCornerOverlap(false);
                    ((CardView) child).setCardBackgroundColor(bgColor);
                } else {
                    child.setBackgroundColor(bgColor);
                }
                applyBgColorToChildren((ViewGroup) child, bgColor);
            }
        }
    }

    private void selectViewPlan(CollectionGridItemView collectionGridItemView, String selectedText) {
        collectionGridItemView.setSelectable(true);
        for (View collectionGridChild : collectionGridItemView.getViewsToUpdateOnClickEvent()) {
            if (collectionGridChild instanceof Button) {
                Component childComponent = collectionGridItemView.matchComponentToView(collectionGridChild);
                if (selectedText == null) {
                    selectedText = childComponent.getSelectedText();
                }
                ((TextView) collectionGridChild).setText(selectedText);
                ((TextView) collectionGridChild).setTextColor(Color.parseColor(AppCMSPresenter.getColor(mContext,
                        childComponent.getTextColor())));
                collectionGridChild.setBackgroundColor(selectedColor);
            }
        }
    }

    private void deselectViewPlan(CollectionGridItemView collectionGridItemView) {
        collectionGridItemView.setSelectable(false);
        for (View collectionGridChild : collectionGridItemView
                .getViewsToUpdateOnClickEvent()) {
            if (collectionGridChild instanceof Button) {
                Component childComponent = collectionGridItemView.matchComponentToView(collectionGridChild);
                ((TextView) collectionGridChild).setText(childComponent.getText());
                collectionGridChild.setBackgroundColor(ContextCompat.getColor(collectionGridItemView.getContext(),
                        R.color.disabledButtonColor));
            }
        }
    }

    private void deselectViewPlan01(CollectionGridItemView collectionGridItemView) {
        collectionGridItemView.setSelectable(false);
        for (View collectionGridChild : collectionGridItemView
                .getViewsToUpdateOnClickEvent()) {
            if (collectionGridChild instanceof Button) {
                Component childComponent = collectionGridItemView.matchComponentToView(collectionGridChild);
                ((TextView) collectionGridChild).setText(childComponent.getText());
                setBorder(collectionGridChild, ContextCompat.getColor(mContext, R.color.disabledButtonColor));
                ((TextView) collectionGridChild).setTextColor(ContextCompat.getColor(mContext, R.color.disabledButtonColor));
            }
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (0 <= position && position < adapterData.size()) {
            for (int i = 0; i < holder.componentView.getNumberOfChildren(); i++) {
                if (holder.componentView.getChild(i) instanceof TextView) {
                    ((TextView) holder.componentView.getChild(i)).setText("");
                }
            }
            if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY ||
                    viewTypeKey == AppCMSUIKeyType.PAGE_PHOTO_TRAY_MODULE_KEY) {
                planItemView[position] = holder.componentView;
            }
            bindView(holder.componentView, adapterData.get(position), position);
        }

        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY) {
            for (int i = 0; i < planItemView.length; i++) {
                if (planItemView[i] != null) {
                    if (selectedPosition == i) {
                        setBorder(planItemView[i], selectedColor);
                        String selectedText = null;
                        if (adapterData.get(i) != null &&
                                adapterData.get(i).getPlanDetails() != null &&
                                adapterData.get(i).getPlanDetails().get(0) != null &&
                                adapterData.get(i).getPlanDetails().get(0).getCallToAction() != null) {
                            selectedText = adapterData.get(i).getPlanDetails().get(0).getCallToAction();
                        }
                        selectViewPlan(planItemView[i], selectedText);
                    } else {
                        setBorder(planItemView[i], ContextCompat.getColor(mContext, android.R.color.white));
                        deselectViewPlan01(planItemView[i]);
                    }
                }
            }
        }
        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY ||
                viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY ||
                viewTypeKey == AppCMSUIKeyType.PAGE_PHOTO_TRAY_MODULE_KEY) {
            int selectableIndex = -1;
            for (int i = 0; i < adapterData.size(); i++) {
                if (holder.componentView.isSelectable()) {
                    selectableIndex = i;
                }
            }

            if (selectableIndex == -1) {
                selectableIndex = 0;
            }

            if (selectableIndex == position) {
                if (viewTypeKey != AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY &&
                        viewTypeKey != AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY &&
                        viewTypeKey != AppCMSUIKeyType.PAGE_PHOTO_TRAY_MODULE_KEY) {
                    holder.componentView.setSelectable(true);
                    holder.componentView.performClick();
                }
            } else {
                //
            }

            if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY ||
                    viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY ||
                    viewTypeKey == AppCMSUIKeyType.PAGE_PHOTO_TRAY_MODULE_KEY) {
                holder.componentView.setSelectable(true);
            }

            if (viewTypeKey == AppCMSUIKeyType.PAGE_PHOTO_TRAY_MODULE_KEY) {
                setBorder(planItemView[position], adapterData.get(position).getGist().isSelectedPosition() ? selectedColor : ContextCompat.getColor(mContext, android.R.color.white));
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

        //sortPlan(); as per MSEAN-1434

        notifyDataSetChanged();
        listView.setAdapter(this);
        listView.invalidate();
        notifyDataSetChanged();
    }

    @SuppressLint("ClickableViewAccessibility")
    void bindView(CollectionGridItemView itemView,
                  final ContentDatum data, int position) throws IllegalArgumentException {
        if (onClickHandler == null) {
            if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY) {
                onClickHandler = new CollectionGridItemView.OnClickHandler() {
                    @Override
                    public void click(CollectionGridItemView collectionGridItemView, Component childComponent, ContentDatum data,
                                      int clickPosition) {
                        if (appCMSPresenter.isSelectedSubscriptionPlan()) {
                            selectedPosition = clickPosition;
                        } else {
                            appCMSPresenter.setSelectedSubscriptionPlan(true);
                        }
                        for (int i = 0; i < planItemView.length; i++) {
                            if (planItemView[i] != null) {
                                if (selectedPosition == i) {
                                    setBorder(planItemView[i], selectedColor);
                                    String selectedText = null;
                                    if (adapterData.get(i) != null &&
                                            adapterData.get(i).getPlanDetails() != null &&
                                            adapterData.get(i).getPlanDetails().get(0) != null &&
                                            adapterData.get(i).getPlanDetails().get(0).getCallToAction() != null) {
                                        selectedText = adapterData.get(i).getPlanDetails().get(0).getCallToAction();
                                    }
                                    selectViewPlan(planItemView[i], selectedText);
                                } else {
                                    setBorder(planItemView[i], ContextCompat.getColor(mContext, android.R.color.white));
                                    deselectViewPlan01(planItemView[i]);
                                }
                            }
                        }
                        if (childComponent != null &&
                                childComponent.getAction() != null &&
                                purchasePlanAction != null) {
                            if (childComponent.getAction().contains(purchasePlanAction)) {
                                subcriptionPlanClick(collectionGridItemView, data);
                            }
                        }
                    }

                    @Override
                    public void play(Component childComponent, ContentDatum data) {
                        // NO-OP - Play is not implemented here
                    }
                };
            } else if (viewTypeKey == AppCMSUIKeyType.PAGE_ARTICLE_FEED_MODULE_KEY) {
                onClickHandler = new CollectionGridItemView.OnClickHandler() {
                    @Override
                    public void click(CollectionGridItemView collectionGridItemView,
                                      Component childComponent, ContentDatum data, int clickPosition) {
                        if (childComponent != null && childComponent.getKey() != null) {
                            if (jsonValueKeyMap.get(childComponent.getKey()) == AppCMSUIKeyType.PAGE_THUMBNAIL_READ_MORE_KEY) {
                                if (data.getGist() != null && data.getGist().getMediaType() != null
                                        && data.getGist().getMediaType().toLowerCase().contains(itemView.getContext().getString(R.string.app_cms_article_key_type).toLowerCase())) {
                                    appCMSPresenter.setCurrentArticleIndex(-1);
                                    appCMSPresenter.navigateToArticlePage(data.getGist().getId(), data.getGist().getTitle(), false, null, false);
                                    return;
                                }
                            }
                        }
                    }

                    @Override
                    public void play(Component childComponent, ContentDatum data) {

                    }
                };

            } else if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY) {
                onClickHandler = new CollectionGridItemView.OnClickHandler() {
                    @Override
                    public void click(CollectionGridItemView collectionGridItemView,
                                      Component childComponent,
                                      ContentDatum data, int clickPosition) {
                        if (isClickable) {
                            subcriptionPlanClick(collectionGridItemView, data);

                        }
                    }

                    @Override
                    public void play(Component childComponent, ContentDatum data) {
                        // NO-OP - Play is not implemented here
                    }
                };
            } else if (viewTypeKey == AppCMSUIKeyType.PAGE_PHOTO_TRAY_MODULE_KEY) {
                onClickHandler = new CollectionGridItemView.OnClickHandler() {
                    @Override
                    public void click(CollectionGridItemView collectionGridItemView, Component childComponent,
                                      ContentDatum data, int clickPosition) {
                        selectedPosition = clickPosition;
                        iPhotoGallerySelectListener.selectedImageData(data.getGist().getVideoImageUrl(), selectedPosition);
                        //selectViewPlan(planItemView[clickPosition], null);
                        for (int i = 0; i < planItemView.length; i++) {
                            if (planItemView[i] != null) {
                                if (clickPosition == i) {
                                    setBorder(planItemView[i], selectedColor);
                                } else {
                                    setBorder(planItemView[i], ContextCompat.getColor(mContext, android.R.color.white));
                                }
                            }
                        }
                    }

                    @Override
                    public void play(Component childComponent, ContentDatum data) {
                    }
                };

            } else {
                onClickHandler = new CollectionGridItemView.OnClickHandler() {
                    @Override
                    public void click(CollectionGridItemView collectionGridItemView,
                                      Component childComponent,
                                      ContentDatum data, int clickPosition) {
                        if (isClickable) {
                            if (data.getGist() != null) {
                                //Log.d(TAG, "Clicked on item: " + data.getGist().getTitle());
                                /*String permalink = data.getGist().getPermalink();*/
                                String permalink = "/player/donny-moss";
                                String action = videoAction;
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

                                String contentType = "";

                                if (data.getGist() != null && data.getGist().getContentType() != null) {
                                    contentType = data.getGist().getContentType();
                                }
                                /*get audio details on tray click item and play song*/
                                if (data.getGist() != null &&
                                        data.getGist().getMediaType() != null &&
                                        data.getGist().getMediaType().toLowerCase().contains(itemView.getContext().getString(R.string.media_type_audio).toLowerCase()) &&
                                        data.getGist().getContentType() != null &&
                                        data.getGist().getContentType().toLowerCase().contains(itemView.getContext().getString(R.string.content_type_audio).toLowerCase())) {
                                    List<String> audioPlaylistId = new ArrayList<String>();
                                    audioPlaylistId.add(data.getGist().getId());
                                    AudioPlaylistHelper.getInstance().setCurrentPlaylistId(data.getGist().getId());
                                    AudioPlaylistHelper.getInstance().setCurrentPlaylistData(null);
                                    AudioPlaylistHelper.getInstance().setPlaylist(audioPlaylistId);
                                    appCMSPresenter.getCurrentActivity().sendBroadcast(new Intent(AppCMSPresenter
                                            .PRESENTER_PAGE_LOADING_ACTION));
                                    AudioPlaylistHelper.getInstance().playAudioOnClickItem(data.getGist().getId(), 0);
                                    return;
                                }

                                /*Get playlist data and open playlist page*/
                                if (data.getGist() != null && data.getGist().getMediaType() != null
                                        && data.getGist().getMediaType().toLowerCase().contains(itemView.getContext().getString(R.string.media_type_playlist).toLowerCase())) {
                                    appCMSPresenter.navigateToPlaylistPage(data.getGist().getId(), data.getGist().getTitle(), false);
                                    return;
                                }
//                                if (data.getGist() != null && data.getGist().getContentType() != null
//                                        && data.getGist().getContentType().toLowerCase().contains(itemView.getContext().getString(R.string.app_cms_team_label).toLowerCase())) {
//                                    appCMSPresenter.navigateToTeamDetailPage(data.getGist().getDataId(), data.getGist().getTitle(), false);
//                                    return;
//                                }
                                if (action.contains(openOptionsAction)) {
                                    appCMSPresenter.launchButtonSelectedAction(permalink,
                                            openOptionsAction,
                                            title,
                                            null,
                                            data,
                                            false,
                                            currentPlayingIndex,
                                            relatedVideoIds);
                                    return;
                                }
                                if (contentType.equals(episodicContentType)) {
                                    action = showAction;
                                } else if (contentType.equals(fullLengthFeatureType)) {
                                    action = action != null && action.equalsIgnoreCase("openOptionDialog") ? action : videoAction;
                                }

                                if (data.getGist() != null && data.getGist().getMediaType() != null
                                        && data.getGist().getMediaType().toLowerCase().contains(itemView.getContext().getString(R.string.app_cms_article_key_type).toLowerCase())) {
                                    appCMSPresenter.setCurrentArticleIndex(-1);
                                    appCMSPresenter.navigateToSchedulePage("e79fc083-34af-4e53-a215-b45adb65404c", "Team Schedule", false, null, false);
                                    return;
                                }
                                //PHOTOGALLERY
                                if (data.getGist() != null && data.getGist().getMediaType() != null
                                        && data.getGist().getMediaType().contains("PHOTOGALLERY")) {
                                    appCMSPresenter.setCurrentPhotoGalleryIndex(clickPosition);
                                    appCMSPresenter.navigateToPhotoGalleryPage(data.getGist().getId(), data.getGist().getTitle(), adapterData, false);
                                    return;
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
                                            action.equalsIgnoreCase("openOptionDialog") ? data : null,
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
                                String filmId = data.getGist().getId();
                                String permaLink = data.getGist().getPermalink();
                                String title = data.getGist().getTitle();
                                List<String> relatedVideoIds = null;
                                if (data.getContentDetails() != null &&
                                        data.getContentDetails().getRelatedVideoIds() != null) {
                                    relatedVideoIds = data.getContentDetails().getRelatedVideoIds();
                                }
                                int currentPlayingIndex = -1;
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
        }

        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY ||
                viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY) {
            itemView.setOnClickListener(v -> onClickHandler.click(itemView,
                    component, data, position));
        }

        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY ||
                viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY
                || viewTypeKey == AppCMSUIKeyType.PAGE_PHOTO_TRAY_MODULE_KEY) {
            //
        } else {
            itemView.setOnTouchListener((View v, MotionEvent event) -> {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    lastTouchDownEvent = event;
                }

                return false;
            });
            itemView.setOnClickListener(v -> {
                if (isClickable && data != null && data.getGist() != null) {
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
                            //
                        }
                    }

                    String permalink = data.getGist().getPermalink();
                    String title = data.getGist().getTitle();
                    String action = videoAction;

                    String contentType = "";

                    if (data.getGist() != null && data.getGist().getContentType() != null) {
                        contentType = data.getGist().getContentType();
                    }

                    if (contentType.equals(episodicContentType)) {
                        action = showAction;
                    } else if (contentType.equals(fullLengthFeatureType)) {
                        action = videoAction;
                    }
                     /*get audio details on tray click item and play song*/
                    if (data.getGist() != null &&
                            data.getGist().getMediaType() != null &&
                            data.getGist().getMediaType().toLowerCase().contains(itemView.getContext().getString(R.string.media_type_audio).toLowerCase()) &&
                            data.getGist().getContentType() != null &&
                            data.getGist().getContentType().toLowerCase().contains(itemView.getContext().getString(R.string.content_type_audio).toLowerCase())) {
                        List<String> audioPlaylistId = new ArrayList<String>();
                        audioPlaylistId.add(data.getGist().getId());
                        AudioPlaylistHelper.getInstance().setCurrentPlaylistId(data.getGist().getId());
                        AudioPlaylistHelper.getInstance().setCurrentPlaylistData(null);
                        AudioPlaylistHelper.getInstance().setPlaylist(audioPlaylistId);
                        appCMSPresenter.getCurrentActivity().sendBroadcast(new Intent(AppCMSPresenter
                                .PRESENTER_PAGE_LOADING_ACTION));
                        AudioPlaylistHelper.getInstance().playAudioOnClickItem(data.getGist().getId(), 0);
                        return;
                    }

                                /*Get playlist data and open playlist page*/
                    if (data.getGist() != null && data.getGist().getMediaType() != null
                            && data.getGist().getMediaType().toLowerCase().contains(itemView.getContext().getString(R.string.media_type_playlist).toLowerCase())) {
                        appCMSPresenter.navigateToPlaylistPage(data.getGist().getId(), data.getGist().getTitle(), false);
                        return;
                    }

                    //Log.d(TAG, "Launching " + permalink + ":" + action);
                    List<String> relatedVideoIds = null;
                    if (data.getContentDetails() != null &&
                            data.getContentDetails().getRelatedVideoIds() != null) {
                        relatedVideoIds = data.getContentDetails().getRelatedVideoIds();
                    }
                    int currentPlayingIndex = -1;
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
//                                    " permalink: " +
//                                    permalink +
//                                    " action: " +
//                                    action);
                        }
                    } else {

                            if (!appCMSPresenter.launchButtonSelectedAction(permalink,
                                    action,
                                    title,
                                    null,
                                    null,
                                    false,
                                    currentPlayingIndex,
                                    relatedVideoIds)) {
                                //Log.e(TAG, "Could not launch action: " +
//                                    " permalink: " +
//                                    permalink +
//                                    " action: " +
//                                    action);
                            }

                    }
                }

            });

        }

        for (int i = 0; i < itemView.getNumberOfChildren(); i++) {
            itemView.bindChild(itemView.getContext(),
                    itemView.getChild(i),
                    data,
                    jsonValueKeyMap,
                    onClickHandler,
                    componentViewType,
                    appCMSPresenter.getBrandPrimaryCtaColor(),
                    appCMSPresenter,
                    position);
        }
    }

    void subcriptionPlanClick(CollectionGridItemView collectionGridItemView, ContentDatum data) {
        if (collectionGridItemView.isSelectable()) {
            //Log.d(TAG, "Initiating signup and subscription: " +
//                                        data.getIdentifier());

            double price = data.getPlanDetails().get(0).getStrikeThroughPrice();
            if (price == 0) {
                price = data.getPlanDetails().get(0).getRecurringPaymentAmount();
            }

            double discountedPrice = data.getPlanDetails().get(0).getRecurringPaymentAmount();

            boolean upgradesAvailable = false;
            for (ContentDatum plan : adapterData) {
                if (plan != null &&
                        plan.getPlanDetails() != null &&
                        !plan.getPlanDetails().isEmpty() &&
                        ((plan.getPlanDetails().get(0).getStrikeThroughPrice() != 0 &&
                                price < plan.getPlanDetails().get(0).getStrikeThroughPrice()) ||
                                (plan.getPlanDetails().get(0).getRecurringPaymentAmount() != 0 &&
                                        price < plan.getPlanDetails().get(0).getRecurringPaymentAmount()))) {
                    upgradesAvailable = true;
                }
            }

            appCMSPresenter.initiateSignUpAndSubscription(data.getIdentifier(),
                    data.getId(),
                    data.getPlanDetails().get(0).getCountryCode(),
                    data.getName(),
                    price,
                    discountedPrice,
                    data.getPlanDetails().get(0).getRecurringPaymentCurrencyCode(),
                    data.getPlanDetails().get(0).getCountryCode(),
                    data.getRenewable(),
                    data.getRenewalCycleType(),
                    upgradesAvailable);
        } else {
            collectionGridItemView.performClick();
        }
    }

    public boolean isClickable() {
        return isClickable;
    }

    @Override
    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    private String getDefaultAction(Context context) {
        return context.getString(R.string.app_cms_action_videopage_key);
    }

    private String getShowAction(Context context) {
        return context.getString(R.string.app_cms_action_showvideopage_key);
    }

    private String getOpenOptionsAction(Context context) {
        return context.getString(R.string.app_cms_action_open_option_dialog);
    }

    private String getPurchasePlanAction(Context context) {
        return context.getString(R.string.app_cms_action_purchase_plan);
    }

    private String getVideoAction(Context context) {
        return context.getString(R.string.app_cms_action_detailvideopage_key);
    }

    private String getHlsUrl(ContentDatum data) {
        if (data.getStreamingInfo() != null &&
                data.getStreamingInfo().getVideoAssets() != null &&
                data.getStreamingInfo().getVideoAssets().getHls() != null) {
            return data.getStreamingInfo().getVideoAssets().getHls();
        }
        return null;
    }

    private void setBorder(View itemView,
                           int color) {
        GradientDrawable planBorder = new GradientDrawable();
        planBorder.setShape(GradientDrawable.RECTANGLE);
        if (BaseView.isTablet(mContext)) {
            planBorder.setStroke(5, color);
            itemView.setPadding(3, 3, 3, 3);
        } else {
            planBorder.setStroke(7, color);
            itemView.setPadding(7, 7, 7, 7);
        }
        planBorder.setColor(ContextCompat.getColor(itemView.getContext(), android.R.color.transparent));

        itemView.setBackground(planBorder);
    }

    public void sortPlan() {
        if (mContext.getResources().getBoolean(R.bool.sort_plans_in_ascending_order)) {
            sortPlansByPriceInAscendingOrder();
        } else {
            sortPlansByPriceInDescendingOrder();
        }
    }

    private void sortPlansByPriceInDescendingOrder() {
        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY && adapterData != null) {

            Collections.sort(adapterData,
                    (datum1, datum2) -> {
                        if (datum1.getPlanDetails().get(0).getStrikeThroughPrice() == 0 &&
                                datum2.getPlanDetails().get(0).getStrikeThroughPrice() == 0) {
                            return Double.compare(datum2.getPlanDetails().get(0)
                                    .getRecurringPaymentAmount(), datum1.getPlanDetails().get(0)
                                    .getRecurringPaymentAmount());
                        }
                        return Double.compare(datum2.getPlanDetails().get(0)
                                .getStrikeThroughPrice(), datum1.getPlanDetails().get(0)
                                .getStrikeThroughPrice());
                    });
        }
    }

    private void sortPlansByPriceInAscendingOrder() {
        sortPlansByPriceInDescendingOrder();
        Collections.reverse(adapterData);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        int childCount = holder.componentView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = holder.componentView.getChild(i);
            if (child instanceof ImageView) {
                Glide.with(child.getContext()).clear(child);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CollectionGridItemView componentView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (CollectionGridItemView) itemView;
        }
    }


}
