package com.viewlift.views.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.plans.SubscriptionMetaDataView;
import com.viewlift.views.customviews.plans.ViewPlansMetaDataView;
import com.viewlift.views.utilities.ImageLoader;
import com.viewlift.views.utilities.ImageUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.viewlift.views.customviews.ViewCreator.setTypeFace;

/*
 * Created by viewlift on 5/5/17.
 */

@SuppressLint("ViewConstructor")
public class CollectionGridItemView extends BaseView {
    private static final String TAG = "CollectionItemView";

    private final Layout parentLayout;
    private final boolean useParentLayout;
    private final Component component;
    private final String moduleId;
    protected int defaultWidth;
    protected int defaultHeight;
    AppCMSUIKeyType viewTypeKey;
    private List<ItemContainer> childItems;
    private List<View> viewsToUpdateOnClickEvent;
    private boolean selectable;
    private boolean createMultipleContainersForChildren;
    private boolean createRoundedCorners;

    @Inject
    public CollectionGridItemView(Context context,
                                  Layout parentLayout,
                                  boolean useParentLayout,
                                  Component component,
                                  String moduleId,
                                  int defaultWidth,
                                  int defaultHeight,
                                  boolean createMultipleContainersForChildren,
                                  boolean createRoundedCorners,
                                  AppCMSUIKeyType viewTypeKey) {
        super(context);
        this.parentLayout = parentLayout;
        this.useParentLayout = useParentLayout;
        this.component = component;
        this.moduleId = moduleId;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.viewsToUpdateOnClickEvent = new ArrayList<>();
        this.createMultipleContainersForChildren = createMultipleContainersForChildren;
        this.createRoundedCorners = createRoundedCorners;
        this.viewTypeKey = viewTypeKey;
        init();
    }

    @Override
    public void init() {
        int width = (int) getGridWidth(getContext(),
                component.getLayout(),
                (int) getViewWidth(getContext(),
                        component.getLayout(),
                        defaultWidth));
        int height = (int) getGridHeight(getContext(),
                component.getLayout(),
                (int) getViewHeight(getContext(),
                        component.getLayout(),
                        defaultHeight));

        FrameLayout.LayoutParams layoutParams;
        int paddingHorizontal = 0;
        if (component.getStyles() != null) {
            paddingHorizontal = (int) convertHorizontalValue(getContext(), component.getStyles().getPadding());
        } else if (getTrayPadding(getContext(), component.getLayout()) != -1.0f) {
            int trayPadding = (int) getTrayPadding(getContext(), component.getLayout());
            paddingHorizontal = (int) convertHorizontalValue(getContext(), trayPadding);
        }
        int horizontalMargin = paddingHorizontal;
        int verticalMargin = 0;
        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(width, height);
        marginLayoutParams.setMargins(horizontalMargin,
                verticalMargin,
                horizontalMargin,
                verticalMargin);
        layoutParams = new FrameLayout.LayoutParams(marginLayoutParams);
        setLayoutParams(layoutParams);
        childItems = new ArrayList<>();
        if (component.getComponents() != null) {
            initializeComponentHasViewList(component.getComponents().size());
        }
    }

    @Override
    protected Component getChildComponent(int index) {
        if (component.getComponents() != null &&
                0 <= index &&
                index < component.getComponents().size()) {
            return component.getComponents().get(index);
        }
        return null;
    }

    @Override
    protected Layout getLayout() {
        return component.getLayout();
    }

    @Override
    protected ViewGroup createChildrenContainer() {
        if (createMultipleContainersForChildren && BaseView.isTablet(getContext()) && BaseView.isLandscape(getContext())) {
            if (component != null &&
                    component.getView() != null &&
                    component.getView().equalsIgnoreCase(getContext().getResources().getString(R.string.app_cms_page_event_carousel_module_key))) {
                childrenContainer = new CardView(getContext());
                CardView.LayoutParams childContainerLayoutParams =
                        new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT);
                childrenContainer.setLayoutParams(childContainerLayoutParams);

                if (createRoundedCorners) {
                    ((CardView) childrenContainer).setRadius(14);
                    setBackgroundResource(android.R.color.transparent);
                    if (!component.getAction().equalsIgnoreCase("purchasePlan")) {
                        childrenContainer.setBackgroundResource(android.R.color.transparent);
                    }
                } else {
                    childrenContainer.setBackgroundResource(android.R.color.transparent);
                }
            } else {
                childrenContainer = new LinearLayout(getContext());
                ((LinearLayout) childrenContainer).setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams childContainerLayoutParams =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT);
                childrenContainer.setLayoutParams(childContainerLayoutParams);
                CardView imageChildView = new CardView(getContext());
                LinearLayout.LayoutParams imageChildViewLayoutParams =
                        new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                imageChildViewLayoutParams.weight = 2;
                imageChildView.setLayoutParams(imageChildViewLayoutParams);
                imageChildView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
                childrenContainer.addView(imageChildView);
                CardView detailsChildView = new CardView(getContext());
                LinearLayout.LayoutParams detailsChildViewLayoutParams =
                        new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                detailsChildViewLayoutParams.weight = 1;
                detailsChildView.setLayoutParams(detailsChildViewLayoutParams);
                detailsChildView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
                childrenContainer.addView(detailsChildView);
            }
        } else {
            childrenContainer = new CardView(getContext());
            CardView.LayoutParams childContainerLayoutParams =
                    new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
            childrenContainer.setLayoutParams(childContainerLayoutParams);

            if (createRoundedCorners) {
                int cornerRadius = 34;
                if (BaseView.isTablet(getContext()))
                    cornerRadius = 24;
                ((CardView) childrenContainer).setRadius(cornerRadius);
                setBackgroundResource(android.R.color.transparent);
                if (component.getAction() != null && !component.getAction().equalsIgnoreCase("purchasePlan")) {
                    childrenContainer.setBackgroundResource(android.R.color.transparent);
                }
            } else {
                childrenContainer.setBackgroundResource(android.R.color.transparent);
            }
        }
        addView(childrenContainer);
        return childrenContainer;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void addChild(ItemContainer itemContainer) {
        if (childrenContainer == null) {
            createChildrenContainer();
        }
        childItems.add(itemContainer);

        if (createMultipleContainersForChildren && BaseView.isTablet(getContext()) && BaseView.isLandscape(getContext())) {
            if (component != null &&
                    component.getView() != null &&
                    component.getView().equalsIgnoreCase(getContext().getResources().getString(R.string.app_cms_page_event_carousel_module_key))) {
                childrenContainer.addView(itemContainer.childView);
            } else if (getContext().getString(R.string.app_cms_page_carousel_image_key).equalsIgnoreCase(itemContainer.component.getKey())) {
                ((ViewGroup) childrenContainer.getChildAt(0)).addView(itemContainer.childView);
            } else {
                ((ViewGroup) childrenContainer.getChildAt(1)).addView(itemContainer.childView);
            }
        } else {
            childrenContainer.addView(itemContainer.childView);
        }
    }

    public View getChild(int index) {
        if (0 <= index && index < childItems.size()) {
            return childItems.get(index).childView;
        }
        return null;
    }

    public int getNumberOfChildren() {
        return childItems.size();
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    /**
     * @param context
     * @param view
     * @param data
     * @param jsonValueKeyMap
     * @param onClickHandler
     * @param componentViewType
     * @param themeColor
     * @param appCMSPresenter
     * @param position
     */
    public void bindChild(Context context,
                          View view,
                          final ContentDatum data,
                          Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                          final OnClickHandler onClickHandler,
                          final String componentViewType,
                          int themeColor,
                          AppCMSPresenter appCMSPresenter, int position, Settings settings) {

        final Component childComponent = matchComponentToView(view);

        AppCMSUIKeyType moduleType = jsonValueKeyMap.get(componentViewType);

        if (moduleType == null) {
            moduleType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        Map<String, ViewCreator.UpdateDownloadImageIconAction> updateDownloadImageIconActionMap =
                appCMSPresenter.getUpdateDownloadImageIconActionMap();

        if (childComponent != null) {
            if (data != null) {
                view.setOnClickListener(v -> onClickHandler.click(CollectionGridItemView.this,
                        childComponent, data, position));
            }
            boolean bringToFront = true;
            AppCMSUIKeyType appCMSUIcomponentViewType = jsonValueKeyMap.get(componentViewType);
            AppCMSUIKeyType componentType = jsonValueKeyMap.get(childComponent.getType());
            AppCMSUIKeyType componentKey = jsonValueKeyMap.get(childComponent.getKey());
            if (componentType == AppCMSUIKeyType.PAGE_IMAGE_KEY) {
                if (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_PLAN_FEATURE_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_BADGE_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_PLAY_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_PHOTO_PLAYER_IMAGE ||
                        componentKey == AppCMSUIKeyType.PAGE_PHOTO_TEAM_IMAGE ||
                        componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_BADGE_IMAGE ||
                        componentKey == AppCMSUIKeyType.PAGE_PHOTO_GALLERY_IMAGE_KEY) {
                    int placeholder = R.drawable.vid_image_placeholder_land;
                    int childViewWidth = (int) getViewWidth(getContext(),
                            childComponent.getLayout(),
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    int childViewHeight = (int) getViewHeight(getContext(),
                            childComponent.getLayout(),
                            getViewHeight(getContext(), component.getLayout(), ViewGroup.LayoutParams.WRAP_CONTENT));

                    if (useParentLayout) {
                        childViewWidth = (int) getGridWidth(getContext(),
                                parentLayout,
                                (int) getViewWidth(getContext(),
                                        parentLayout,
                                        defaultWidth));
                        childViewHeight = (int) getGridHeight(getContext(),
                                parentLayout,
                                (int) getViewHeight(getContext(),
                                        parentLayout,
                                        defaultHeight));
                    }

                    if (childViewWidth < 0 &&
                            componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_IMAGE_KEY) {
                        childViewWidth = (16 * childViewHeight) / 9;
                    }
                    if (0 < childViewWidth && 0 < childViewHeight) {
                        if (childViewWidth < childViewHeight) {
                            childViewHeight = (int) ((float) childViewWidth * 4.0f / 3.0f);
                        } else {
                            childViewHeight = (int) ((float) childViewWidth * 9.0f / 16.0f);
                        }
                    }

                    if (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY ||
                            componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_IMAGE_KEY ||
                            componentKey == AppCMSUIKeyType.PAGE_PLAN_FEATURE_IMAGE_KEY ||
                            componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY) {
                        if (childViewHeight > childViewWidth) {
                            placeholder = R.drawable.vid_image_placeholder_port;
                            ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                            Glide.with((ImageView) view).load(R.drawable.vid_image_placeholder_port);
                        } else {
                            ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                            try {
                                if (appCMSUIcomponentViewType == AppCMSUIKeyType.PAGE_AUDIO_TRAY_MODULE_KEY) {
                                    Glide.with((ImageView) view).load(R.drawable.vid_image_placeholder_square);
                                    placeholder = R.drawable.vid_image_placeholder_square;
                                } else {
                                    Glide.with((ImageView) view).load(R.drawable.vid_image_placeholder_16x9);
                                    placeholder = R.drawable.vid_image_placeholder_16x9;
                                }
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (data != null && data.getGist() != null &&
                            data.getGist().getContentType() != null &&
                            data.getGist().getContentType().equalsIgnoreCase(context.getString(R.string.content_type_audio))
                            && data.getGist().getPosterImageUrl() != null &&
                            appCMSUIcomponentViewType != AppCMSUIKeyType.PAGE_PLAYLIST_MODULE_KEY &&
                            appCMSPresenter.isVideoDownloaded(data.getGist().getId())) {
                        int size = childViewWidth;
                        if (data.getGist() != null &&
                                data.getGist().getPosterImageUrl() != null && (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY)) {
                            String imageUrl = data.getGist().getPosterImageUrl();
                            if (appCMSPresenter.isVideoDownloaded(data.getGist().getId())) {
                                if (data.getGist().getPosterImageUrl() != null) {
                                    imageUrl = data.getGist().getPosterImageUrl();
                                }
                            }
                            RequestOptions requestOptions = new RequestOptions()
                                    .override(size, size).placeholder(placeholder)
                                    .fitCenter();
//                            RequestOptions requestOptions = new RequestOptions().placeholder(placeholder);
                            if (!ImageUtils.loadImage((ImageView) view, imageUrl, ImageLoader.ScaleType.START) && context != null && appCMSPresenter != null && appCMSPresenter.getCurrentActivity() != null && !appCMSPresenter.getCurrentActivity().isFinishing()) {
                                Glide.with(context.getApplicationContext())
                                        .load(imageUrl)
                                        .apply(requestOptions)
//                                        .override(size,size)
                                        .into(((ImageView) view));
                            }
                        } else {
                            ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                            Glide.with((ImageView) view).load(R.drawable.vid_image_placeholder_square);
                        }
                    } else if (data != null && data.getGist() != null &&
                            data.getGist().getContentType() != null &&
                            data.getGist().getContentType().equalsIgnoreCase(context.getString(R.string.content_type_audio))
                            && appCMSUIcomponentViewType == AppCMSUIKeyType.PAGE_PLAYLIST_MODULE_KEY) {
                        int size = childViewWidth;
                        /*if (childViewHeight< childViewWidth ) {
                            size = childViewHeight;
                        }*/
                        int horizontalMargin = 0;
                        horizontalMargin = (int) getHorizontalMargin(getContext(), childComponent.getLayout());
                        int verticalMargin = (int) getVerticalMargin(getContext(), parentLayout, size, 0);
                        if (verticalMargin < 0) {
                            verticalMargin = (int) convertVerticalValue(getContext(), getYAxis(getContext(), getLayout(), 0));
                        }
                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                        LayoutParams llParams = new LayoutParams(size, size);
                        llParams.setMargins(horizontalMargin,
                                verticalMargin,
                                horizontalMargin,
                                verticalMargin);
                        view.setLayoutParams(llParams);

                        if (data.getGist() != null &&
                                data.getGist().getImageGist() != null &&
                                data.getGist().getImageGist().get_1x1() != null && (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY)) {
                            String imageUrl = data.getGist().getImageGist().get_1x1();
                            if (appCMSPresenter.isVideoDownloaded(data.getGist().getId())) {
                                if (data.getGist().getVideoImageUrl() != null) {
                                    imageUrl = data.getGist().getVideoImageUrl();
                                }
                            }
                            RequestOptions requestOptions = new RequestOptions()
                                    .override(size, size).placeholder(placeholder)
                                    .fitCenter();
//                            RequestOptions requestOptions = new RequestOptions().placeholder(placeholder);
                            if (!ImageUtils.loadImage((ImageView) view, imageUrl, ImageLoader.ScaleType.START) && context != null && appCMSPresenter != null && appCMSPresenter.getCurrentActivity() != null && !appCMSPresenter.getCurrentActivity().isFinishing()) {


                                Glide.with(context.getApplicationContext())
                                        .load(imageUrl)
                                        .apply(requestOptions)
//                                        .override(size,size)
                                        .into(((ImageView) view));
                            }
                        } else {
                            ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                            Glide.with((ImageView) view).load(R.drawable.vid_image_placeholder_square);
                        }
                    } else if (
                            settings != null && settings.getItems() != null &&
                                    !TextUtils.isEmpty(settings.getItems().get(position).getImageUrl()) &&
                                    (componentKey == AppCMSUIKeyType.PAGE_PLAN_FEATURE_IMAGE_KEY)) {
                        bringToFront = false;
                        String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                settings.getItems().get(position).getImageUrl(),
                                childViewWidth,
                                childViewHeight);

//                        String imageUrl = settings.getItems().get(position).getImageUrl();
                        try {
                            if (!ImageUtils.loadImage((ImageView) view, imageUrl, ImageLoader.ScaleType.START)) {
                                ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_CENTER);
                                ((ImageView) view).setAdjustViewBounds(true);
                                Glide.with(context)
                                        .load(imageUrl)
                                        .into((ImageView) view);
                            } else {
                                ((ImageView) view).setBackgroundResource(placeholder);
                            }
                        } catch (Exception e) {
                            //
                        }

                    } else if (childViewHeight > childViewWidth &&
                            childViewHeight > 0 &&
                            childViewWidth > 0 && data != null && data.getGist() != null && data.getGist().getPosterImageUrl() != null &&
                            !TextUtils.isEmpty(data.getGist().getPosterImageUrl()) &&
                            (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY ||
                                    componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY)) {
                        bringToFront = false;
                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);

                        String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                data.getGist().getPosterImageUrl(),
                                childViewWidth,
                                childViewHeight);

                        if (appCMSPresenter.isVideoDownloaded(data.getGist().getId())) {
                            if (data.getGist().getVideoImageUrl() != null) {
                                imageUrl = data.getGist().getVideoImageUrl();
                            }
                        }
                        try {
                            if (!ImageUtils.loadImage((ImageView) view, imageUrl, ImageLoader.ScaleType.START)) {
                                RequestOptions requestOptions = new RequestOptions()
                                        .override(childViewWidth, childViewHeight).placeholder(placeholder)
                                        .fitCenter();
//                                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                                Glide.with(context)
                                        .load(imageUrl)
                                        .apply(requestOptions)
                                        .into((ImageView) view);
                            } else {
                                ((ImageView) view).setBackgroundResource(placeholder);
                            }
                        } catch (Exception e) {
                            //
                        }
                    } else if (childViewHeight > 0 &&
                            childViewWidth > 0 &&
                            data != null &&
                            data.getGist() != null &&
                            ((data.getGist().getVideoImageUrl() != null &&
                                    !TextUtils.isEmpty(data.getGist().getVideoImageUrl())) ||
                                    (data.getGist().getImageGist() != null &&
                                            data.getGist().getImageGist().get_16x9() != null &&
                                            !TextUtils.isEmpty(data.getGist().getImageGist().get_16x9()))) &&
                            (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY ||
                                    componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY)) {

                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_CENTER);

                        if (moduleType == AppCMSUIKeyType.PAGE_AC_TEAM_SCHEDULE_MODULE_KEY) {
                            bringToFront = true;
                        } else {
                            bringToFront = false;
                        }

                        String imageUrl = null;
                        if (data.getGist().getVideoImageUrl() != null) {
                            imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                    data.getGist().getVideoImageUrl(),
                                    childViewWidth,
                                    childViewHeight);
                            if (appCMSPresenter.isVideoDownloaded(data.getGist().getId())) {
                                if (data.getGist().getVideoImageUrl() != null) {
                                    imageUrl = data.getGist().getVideoImageUrl().equalsIgnoreCase("file:///") ? data.getGist().getPosterImageUrl() : data.getGist().getVideoImageUrl();
                                }
                            }
                        } else {
                            imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                    data.getGist().getImageGist().get_16x9(),
                                    childViewWidth,
                                    childViewHeight);
                        }
                        //Log.d(TAG, "Loading image: " + imageUrl);
                        try {
                            if (!ImageUtils.loadImage((ImageView) view, imageUrl, ImageLoader.ScaleType.START)) {
                                RequestOptions requestOptions = new RequestOptions()
                                        .override(childViewWidth, childViewHeight)
                                        .placeholder(placeholder)
                                        .fitCenter();
//                                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

                                Glide.with(context)
                                        .load(imageUrl)
                                        .apply(requestOptions)
                                        .into((ImageView) view);

                                ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                            }
                        } catch (Exception e) {
                            //
                        }
                    } else if (data != null &&
                            data.getGist() != null &&
                            componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_IMAGE_KEY) {
                        System.out.println("image dimen3- width" + childViewHeight + " width" + childViewWidth);
                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);

                        bringToFront = false;
                        int deviceWidth = getContext().getResources().getDisplayMetrics().widthPixels;
                        String imageUrl = "";
                        if (data.getGist() != null &&
                                data.getGist().getContentType() != null &&
                                ((data.getGist().getContentType().toLowerCase().contains(context.getString(R.string.content_type_audio).toLowerCase())) ||
                                        (data.getGist().getContentType().toLowerCase().contains(context.getString(R.string.app_cms_article_key_type).toLowerCase())))
                                && data.getGist().getImageGist() != null
                                && data.getGist().getImageGist().get_16x9() != null) {
                            imageUrl = data.getGist().getImageGist().get_16x9();
                        } else if (data.getGist() != null && data.getGist().getVideoImageUrl() != null) {
                            imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                    data.getGist().getVideoImageUrl(),
                                    deviceWidth,
                                    childViewHeight);
                        } else if (data.getGist() != null && data.getGist().getImageGist() != null && data.getGist().getImageGist().get_16x9() != null) {
                            imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                    data.getGist().getImageGist().get_16x9(),
                                    deviceWidth,
                                    childViewHeight);
                        }

                        try {
                            final int imageWidth = deviceWidth;
                            final int imageHeight = childViewHeight;

                            if (!ImageUtils.loadImageWithLinearGradient((ImageView) view,
                                    imageUrl,
                                    imageWidth,
                                    imageHeight)) {

                                Transformation gradientTransform = new GradientTransformation(imageWidth,
                                        imageHeight,
                                        appCMSPresenter,
                                        imageUrl);

                                RequestOptions requestOptions = new RequestOptions()
                                        .transform(gradientTransform)
                                        .placeholder(placeholder)
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                        .override(imageWidth, imageHeight);
//                                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

                                Glide.with(context)
                                        .load(imageUrl)
                                        .apply(requestOptions)
                                        .into((ImageView) view);
                            }
                        } catch (IllegalArgumentException e) {
                            //Log.e(TAG, "Failed to load image with Glide: " + e.toString());
                        }
                    } /*else if (data != null && data.getGist() != null &&
                                data.getGist().getImageGist() != null &&
                            componentKey == AppCMSUIKeyType.PAGE_MYLIBRARY_01_MODULE_KEY &&
                            0 < childViewWidth &&
                            0 < childViewHeight) {
                        if (childViewWidth < childViewHeight &&
                                data.getGist().getImageGist().get_3x4() != null &&
                                data.getGist().getBadgeImages().get_3x4() != null &&
                                componentKey == AppCMSUIKeyType.PAGE_BADGE_IMAGE_KEY &&
                                0 < childViewWidth &&
                                0 < childViewHeight) {
                            ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);

                            String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                    data.getGist().getBadgeImages().get_3x4(),
                                    childViewWidth,
                                    childViewHeight);

                            if (!ImageUtils.loadImage((ImageView) view, imageUrl, ImageLoader.ScaleType.START)) {
                                RequestOptions requestOptions = new RequestOptions()
                                        .override(childViewWidth, childViewHeight)
                                        .fitCenter()
                                        .placeholder(placeholder);
//                                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                                Glide.with(context)
                                        .load(imageUrl)
                                        .apply(requestOptions)
                                        .into((ImageView) view);
                            }
                        }
                        view.setVisibility(VISIBLE);
                        bringToFront = true;
                    }*/else if (data != null && data.getGist() != null &&
                            data.getGist().getImageGist() != null &&
                            data.getGist().getBadgeImages() != null &&
                            componentKey == AppCMSUIKeyType.PAGE_BADGE_IMAGE_KEY &&
                            0 < childViewWidth &&
                            0 < childViewHeight) {
                        if (childViewWidth < childViewHeight &&
                                data.getGist().getImageGist().get_3x4() != null &&
                                data.getGist().getBadgeImages().get_3x4() != null &&
                                componentKey == AppCMSUIKeyType.PAGE_BADGE_IMAGE_KEY &&
                                0 < childViewWidth &&
                                0 < childViewHeight) {
                            ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);

                            String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                    data.getGist().getBadgeImages().get_3x4(),
                                    childViewWidth,
                                    childViewHeight);

                            if (!ImageUtils.loadImage((ImageView) view, imageUrl, ImageLoader.ScaleType.START)) {
                                RequestOptions requestOptions = new RequestOptions()
                                        .override(childViewWidth, childViewHeight)
                                        .fitCenter()
                                        .placeholder(placeholder);
//                                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                                Glide.with(context)
                                        .load(imageUrl)
                                        .apply(requestOptions)
                                        .into((ImageView) view);
                            }
                        } else if (data.getGist().getImageGist().get_16x9() != null &&
                                data.getGist().getBadgeImages().get_16x9() != null) {
                            String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                    data.getGist().getBadgeImages().get_16x9(),
                                    childViewWidth,
                                    childViewHeight);
                            ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_CENTER);

                            if (appCMSPresenter.isVideoDownloaded(data.getGist().getId())) {
                                if (data.getGist().getVideoImageUrl() != null) {
                                    imageUrl = data.getGist().getVideoImageUrl();
                                }
                            }
                            if (!ImageUtils.loadImage((ImageView) view, imageUrl, ImageLoader.ScaleType.START)) {
                                RequestOptions requestOptions = new RequestOptions()
                                        .override(childViewWidth, childViewHeight)
                                        .placeholder(R.drawable.vid_image_placeholder_16x9)
                                        .fitCenter();
//                                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                                Glide.with(context)
                                        .load(imageUrl)
                                        .apply(requestOptions)
                                        .into((ImageView) view);
                            }
                        }
                        view.setVisibility(VISIBLE);
                        bringToFront = true;
                    } else if (componentKey == AppCMSUIKeyType.PAGE_BADGE_IMAGE_KEY) {
                        view.setVisibility(GONE);
                        bringToFront = false;
                    } else if (componentKey == AppCMSUIKeyType.PAGE_PHOTO_GALLERY_IMAGE_KEY) {

                        String imageUrl = data.getGist().getVideoImageUrl();
                        ImageView imageView = (ImageView) view;
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        RequestOptions requestOptions = new RequestOptions()
                                .override(childViewWidth, childViewHeight)
                                .placeholder(placeholder);

                        Glide.with(context)
                                .load(imageUrl)
                                .apply(requestOptions)
                                .into(imageView);
                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                    } else if (componentKey == AppCMSUIKeyType.PAGE_PHOTO_PLAYER_IMAGE && moduleType == AppCMSUIKeyType.PAGE_AC_ROSTER_MODULE_KEY) {
                        String imageUrl = "";
                        if (data != null && data.getPlayersData() != null && data.getPlayersData().getData() != null) {
                            if (data.getPlayersData().getData().getImages() != null && data.getPlayersData().getData().getImages().get_3x4() != null) {
                                imageUrl = data.getPlayersData().getData().getImages().get_3x4().getUrl();
                            }
                        }
                        ImageView imageView = (ImageView) view;
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imageView.setAdjustViewBounds(true);
                        RequestOptions requestOptions = new RequestOptions()
//                                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)

//                                .override(childViewWidth, childViewHeight)
                                .placeholder(placeholder);

                        Glide.with(context)
                                .load(imageUrl)
                                .apply(requestOptions)

                                .into(imageView);
//                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                    } else if (componentKey == AppCMSUIKeyType.PAGE_PHOTO_TEAM_IMAGE && moduleType == AppCMSUIKeyType.PAGE_AC_ROSTER_MODULE_KEY) {
                        String imageUrl = "";
                        if (data != null && data.getPlayersData() != null && data.getPlayersData().getData() != null) {
                            if (data.getPlayersData().getData().getImages() != null && data.getPlayersData().getData().getImages().get_1x1() != null) {
                                imageUrl = data.getPlayersData().getData().getImages().get_1x1().getUrl();
                            }
                        }
                        ImageView imageView = (ImageView) view;
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        RequestOptions requestOptions = new RequestOptions()
                                .override(childViewWidth, childViewHeight)
                                .placeholder(placeholder);

                        Glide.with(context)
                                .load(imageUrl)
                                .apply(requestOptions)
                                .into(imageView);
                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                    } else if (data!=null && data.getGist()!=null && data.getGist().getLandscapeImageUrl() != null) {
                        String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                data.getGist().getLandscapeImageUrl(),
                                childViewWidth,
                                childViewHeight);
                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_CENTER);

                        if (appCMSPresenter.isVideoDownloaded(data.getGist().getId())) {
                            if (data.getGist().getVideoImageUrl() != null) {
                                imageUrl = data.getGist().getVideoImageUrl();
                            }
                        }
                        if (!ImageUtils.loadImage((ImageView) view, imageUrl, ImageLoader.ScaleType.START)) {
                            RequestOptions requestOptions = new RequestOptions()
                                    .override(childViewWidth, childViewHeight)
                                    .placeholder(R.drawable.vid_image_placeholder_16x9)
                                    .fitCenter();
//                                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                            Glide.with(context)
                                    .load(imageUrl)
                                    .apply(requestOptions)
                                    .into((ImageView) view);
                        }
                    }

                    if (appCMSUIcomponentViewType == AppCMSUIKeyType.PAGE_AUDIO_TRAY_MODULE_KEY) {
                        placeholder = R.drawable.vid_image_placeholder_square;
                        int size = (int) getViewWidth(context, childComponent.getLayout(), ViewGroup.LayoutParams.MATCH_PARENT);

                        int horizontalMargin = 0;
                        horizontalMargin = (int) getHorizontalMargin(getContext(), childComponent.getLayout());
                        int verticalMargin = (int) getVerticalMargin(getContext(), parentLayout, size, 0);
                        if (verticalMargin < 0) {
                            verticalMargin = (int) convertVerticalValue(getContext(), getYAxis(getContext(), getLayout(), 0));
                        }
                        LayoutParams llParams = new LayoutParams(size, size);
                        llParams.setMargins(horizontalMargin,
                                verticalMargin,
                                horizontalMargin,
                                verticalMargin);
                        view.setLayoutParams(llParams);

                        if (data.getGist() != null &&
                                data.getGist().getImageGist() != null &&
                                data.getGist().getImageGist().get_1x1() != null && (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY)) {
                            String imageUrl = data.getGist().getImageGist().get_1x1();
                            ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);

                            if (appCMSPresenter.isVideoDownloaded(data.getGist().getId())) {
                                if (data.getGist().getVideoImageUrl() != null) {
                                    imageUrl = data.getGist().getVideoImageUrl();
                                }
                            }

                            RequestOptions requestOptions = new RequestOptions()
                                    .override(size, size)
                                    .placeholder(placeholder)
                                    .fitCenter();
                            if (!ImageUtils.loadImage((ImageView) view, imageUrl, ImageLoader.ScaleType.START) && context != null && appCMSPresenter != null && appCMSPresenter.getCurrentActivity() != null && !appCMSPresenter.getCurrentActivity().isFinishing()) {
                                Glide.with(context.getApplicationContext())
                                        .load(imageUrl).apply(requestOptions)
//                                        .override(size,size)
                                        .into(((ImageView) view));
                            }
                        } else {
                            ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                            Glide.with(((ImageView) view)).load(placeholder);
                        }

                    }
                    if (moduleType == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY) {
                        view.setOnClickListener(v -> onClickHandler.click(CollectionGridItemView.this,
                                childComponent, data, position));
                    }
                }
                //Bellow is end of thumbnail Image
            } else if (componentType == AppCMSUIKeyType.PAGE_SEPARATOR_VIEW_KEY) {

                if (componentKey == AppCMSUIKeyType.PAGE_FIGHTER_SELECTOR_VIEW_KEY) {
//                    if(data.getFights().getFightId().equals(appCMSPresenter.getFocusedFightId())) {
                    if (data.isSelected()) {
                        (view).setBackgroundColor(Color.parseColor(
                                childComponent.getBackgroundColor()));

                    } else {
                        (view).setBackgroundColor(Color.parseColor(
                                "#00000000"));
                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_FIGHTER_SELECTOR_ARROW_VIEW_KEY) {
                    if (data.isSelected()) {
                        (view).setBackgroundResource(R.drawable.fight_select_bg);

                    } else {
                        (view).setBackgroundResource(0);
                    }
                }
            } else if (componentType == AppCMSUIKeyType.PAGE_BUTTON_KEY) {
                if (componentKey == AppCMSUIKeyType.PAGE_PLAY_IMAGE_KEY) {
                    ((TextView) view).setText("");
                    inVisibleIfSeries(data, view);  //View is invisible if ContentType is Seriese
                } else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_PURCHASE_BUTTON_KEY) {
                    ((TextView) view).setText(childComponent.getText());
                    view.setBackgroundColor(ContextCompat.getColor(getContext(),
                            R.color.disabledButtonColor));
                    viewsToUpdateOnClickEvent.add(view);
                } else if (componentKey == AppCMSUIKeyType.PAGE_GAME_TICKETS_KEY) {
                    if (data.getGist() != null && data.getGist().getEventSchedule() != null &&
                            data.getGist().getEventSchedule().get(0) != null && data.getGist().getEventSchedule().get(0).getEventTime() > 0) {
                        long eventDate = data.getGist().getEventSchedule().get(0).getEventTime();
                        long currentTimeMillis = System.currentTimeMillis();

                        long remainingTime = appCMSPresenter.getTimeIntervalForEvent(eventDate * 1000L, "EEE MMM dd HH:mm:ss");

                        System.out.println("ticket event time-" + eventDate);

                        System.out.println("ticket current Time-" + currentTimeMillis);
                        System.out.println("ticket current differ-" + remainingTime);

                        if (data != null && data.getGist() != null && data.getGist().getTicketUrl() != null &&
                                !TextUtils.isEmpty(data.getGist().getTicketUrl())) {
                            view.setVisibility(View.VISIBLE);
                        } else {
                            view.setVisibility(View.GONE);

                        }
                        ((TextView) view).setText(childComponent.getText());
                        ((TextView) view).setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());
                        viewsToUpdateOnClickEvent.add(view);
                        view.setOnClickListener(view1 -> {

                            String url = "";
                            if (data != null && data.getGist() != null &&
                                    data.getGist().getTicketUrl() != null) {
                                url = data.getGist().getTicketUrl();
                            }
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            appCMSPresenter.getCurrentActivity().startActivity(browserIntent);
                        });
                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_GRID_OPTION_KEY) {
                    if (viewTypeKey == AppCMSUIKeyType.PAGE_ARTICLE_TRAY_KEY) {
                        ((Button) view).setBackground(context.getDrawable(R.drawable.dots_more_grey));
                        ((Button) view).getBackground().setTint(appCMSPresenter.getGeneralTextColor());
                        ((Button) view).getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_DOWNLOAD_BUTTON_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_AUDIO_DOWNLOAD_BUTTON_KEY) {

                    String userId = appCMSPresenter.getLoggedInUser();

                    if (appCMSPresenter.isVideoDownloaded(data.getGist().getId())) {
                        ((ImageButton) view).setImageResource(R.drawable.ic_downloaded_big);
                    } else {
                        try {
                            int radiusDifference = 7;
                            if (BaseView.isTablet(context)) {
                                radiusDifference = 4;
                            }
                            if (data.getGist().getMediaType() != null &&
                                    data.getGist().getMediaType().toLowerCase().contains(context.getString(R.string.media_type_audio).toLowerCase())) {
                                radiusDifference = 5;
                                if (BaseView.isTablet(context)) {
                                    radiusDifference = 3;
                                }
                            }
                            ViewCreator.UpdateDownloadImageIconAction updateDownloadImageIconAction =
                                    updateDownloadImageIconActionMap.get(data.getGist().getId());
                            if (updateDownloadImageIconAction == null) {
                                updateDownloadImageIconAction = new ViewCreator.UpdateDownloadImageIconAction((ImageButton) view, appCMSPresenter,
                                        data, userId, radiusDifference, moduleId);
                                updateDownloadImageIconActionMap.put(data.getGist().getId(), updateDownloadImageIconAction);
                            }

                            view.setTag(data.getGist().getId());

                            updateDownloadImageIconAction.updateDownloadImageButton((ImageButton) view);

                            appCMSPresenter.getUserVideoDownloadStatus(
                                    data.getGist().getId(), updateDownloadImageIconAction, userId);
                        } catch (Exception e) {

                        }
                    }
                } /*else if (componentKey == AppCMSUIKeyType.PAGE_AUDIO_DOWNLOAD_BUTTON_KEY) {
                 *//*view.setOnClickListener(v -> onClickHandler.click(CollectionGridItemView.this,
                            childComponent, data, position));*//*
                    if (appCMSPresenter.isVideoDownloaded(data.getGist().getId())) {
                        ((ImageButton) view).setImageResource(R.drawable.ic_downloaded_big);
                        view.setOnClickListener(null);
                    } else if (appCMSPresenter.isVideoDownloading(data.getGist().getId())) {
                        int radiusDifference = 5;
                        if (BaseView.isTablet(context)) {
                            radiusDifference = 2;
                        }
                        appCMSPresenter.updateDownloadingStatus(
                                data.getGist().getId(),
                                (ImageButton) view,
                                appCMSPresenter,
                                new ViewCreator.UpdateDownloadImageIconAction(
                                        (ImageButton) view,
                                        appCMSPresenter,
                                        data,
                                        appCMSPresenter.getLoggedInUser(),
                                        radiusDifference,
                                        moduleId),
                                appCMSPresenter.getLoggedInUser(),
                                false,
                                radiusDifference,
                                moduleId);
                        view.setOnClickListener(null);
                    }
                } */ else {
                    view.setOnClickListener(v -> onClickHandler.click(CollectionGridItemView.this,
                            childComponent, data, position));
                }
            } else if (componentType == AppCMSUIKeyType.PAGE_GRID_OPTION_KEY) {
                view.setOnClickListener(v ->
                        onClickHandler.click(CollectionGridItemView.this,
                                childComponent, data, position));
            } else if (componentType == AppCMSUIKeyType.PAGE_LABEL_KEY &&
                    view instanceof TextView) {
//                if (TextUtils.isEmpty(((TextView) view).getText())) {
                if (componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_TITLE_KEY &&
                        !TextUtils.isEmpty(data.getGist().getTitle())) {
                    ((TextView) view).setText(data.getGist().getTitle());
                    if (childComponent.getNumberOfLines() != 0) {
                        ((TextView) view).setSingleLine(false);
                        ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                        ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                    }
                    //((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                    if (component != null &&
                            component.getView() != null &&
                            component.getView().equalsIgnoreCase(context.getResources().getString(R.string.app_cms_page_event_carousel_module_key))) {

                        if (BaseView.isTablet(view.getContext())) {
                            if (isLandscape(getContext()) == true) {
                                ((TextView) view).setBackgroundColor(Color.TRANSPARENT);
                                ((TextView) view).setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());
                            } else {
                                setBorder(((TextView) view));
                                ((TextView) view).setTextColor(Color.parseColor("#FFFFFF"));
                            }
                        } else {
                            ((TextView) view).setBackgroundColor(Color.TRANSPARENT);
                            ((TextView) view).setTextColor(appCMSPresenter.getGeneralTextColor());
                        }
                    } else {
                        if (BaseView.isTablet(view.getContext()) && isLandscape(getContext())) {
                            if (appCMSPresenter.getAppCMSMain() != null &&
                                    appCMSPresenter.getAppCMSMain().getBrand() != null &&
                                    appCMSPresenter.getAppCMSMain().getBrand().getCta() != null &&
                                    appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary() != null &&
                                    appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor() != null
                                    ) {
                                ((TextView) view).setTextColor(appCMSPresenter.getBrandSecondaryCtaTextColor());
                            } else {
                                ((TextView) view).setTextColor(Color.parseColor(
                                        childComponent.getTextColor()));
                            }
                        } else {
                            ((TextView) view).setTextColor(Color.parseColor(
                                    childComponent.getTextColor()));
                        }
                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_INFO_KEY) {
                    if (data.getGist().getMediaType() != null && data.getGist().getMediaType().equalsIgnoreCase("AUDIO")) {
                        if (data.getCreditBlocks() != null && data.getCreditBlocks().size() > 0 && data.getCreditBlocks().get(0).getCredits() != null && data.getCreditBlocks().get(0).getCredits().size() > 0 && data.getCreditBlocks().get(0).getCredits().get(0).getTitle() != null) {
                            String artist = appCMSPresenter.getArtistNameFromCreditBlocks(data.getCreditBlocks());
                            ((TextView) view).setMaxLines(1);
                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                            ((TextView) view).setText(artist);
                            view.setPadding(10,
                                    0,
                                    10,
                                    0);
                        }

                    } else if (data.getSeason() != null && 0 < data.getSeason().size()) {
                        ViewCreator.setViewWithShowSubtitle(getContext(), data, view, true);
                    } else {
                        ViewCreator.setViewWithSubtitle(getContext(), data, view);
                    }
                    if (TextUtils.isEmpty(((TextView) view).getText().toString())) {
                        view.setVisibility(INVISIBLE);
                    } else {
                        view.setVisibility(VISIBLE);
                    }
                    if (BaseView.isTablet(view.getContext()) && BaseView.isLandscape(context)) {
                        if (appCMSPresenter.getAppCMSMain() != null &&
                                appCMSPresenter.getAppCMSMain().getBrand() != null &&
                                appCMSPresenter.getAppCMSMain().getBrand().getCta() != null &&
                                appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary() != null &&
                                appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor() != null
                                ) {
                            ((TextView) view).setTextColor(appCMSPresenter.getBrandSecondaryCtaTextColor());
                        } else {
                            ((TextView) view).setTextColor(Color.parseColor(
                                    childComponent.getTextColor()));
                        }
                    } else {
                        ((TextView) view).setTextColor(Color.parseColor("#FFFFFF"));
                    }

                } else if (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_TITLE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_ARTICLE_FEED_BOTTOM_TEXT_KEY) {
                    if (childComponent.getNumberOfLines() != 0) {
                        ((TextView) view).setSingleLine(false);
                        ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                        ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                    }
                    if (componentKey == AppCMSUIKeyType.PAGE_ARTICLE_FEED_BOTTOM_TEXT_KEY) {
                        ((TextView) view).setGravity(Gravity.RIGHT);
                        StringBuffer publishDate = new StringBuffer();
                        if (data.getContentDetails().getAuthor().getPublishDate() != null) {
                            publishDate.append("|");
                            publishDate.append(data.getContentDetails().getAuthor().getPublishDate().toString());
                        }
                        ((TextView) view).setText(data.getContentDetails().getAuthor().getName() + publishDate.toString());
                    } else {
                        ((TextView) view).setText(data.getGist().getTitle());
                    }
                    ((TextView) view).setTextColor(appCMSPresenter.getGeneralTextColor());
                        /*if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                            int textColor = Color.parseColor(getColor(getContext(),
                                    childComponent.getTextColor()));
                            ((TextView) view).setTextColor(textColor);
                        }*/

                }else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_FEATURE_TITLE_KEY &&
                        settings != null && settings.getItems() != null &&
                        !TextUtils.isEmpty(settings.getItems().get(position).getTitle())) {
                    ((TextView) view).setText(settings.getItems().get(position).getTitle());
                    if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                        int textColor = Color.parseColor(getColor(getContext(),
                                childComponent.getTextColor()));
                        ((TextView) view).setTextColor(textColor);
                    }
                }else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_DESCRIPTION_KEY &&
                        !TextUtils.isEmpty(data.getDescription())) {
                    ((TextView) view).setText(data.getDescription());

                }  else if (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_DESCRIPTION_KEY) {
                    if (childComponent.getNumberOfLines() != 0) {
                        ((TextView) view).setSingleLine(false);
                        ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                        ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                    }
                    ((TextView) view).setText(data.getGist().getDescription());
                    if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                        int textColor = Color.parseColor(getColor(getContext(),
                                childComponent.getTextColor()));
                        ((TextView) view).setTextColor(textColor);
                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_READ_MORE_KEY) {
                    ((TextView) view).setText(childComponent.getText());
                    if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                        int textColor = Color.parseColor(getColor(getContext(),
                                childComponent.getTextColor()));
                        ((TextView) view).setTextColor(textColor);
                    }
                } /*else if (componentKey == AppCMSUIKeyType.PAGE_HOME_TEAM_TITLE_KEY) {
                    if (data.getGist() != null && data.getGist().getHomeTeam() != null && data.getGist().getHomeTeam().getGist() != null && data.getGist().getHomeTeam().getGist().getTitle() != null) {

                        ((TextView) view).setText(data.getGist().getHomeTeam().getGist().getTitle());
                        ((TextView) view).setTextColor(appCMSPresenter.getBrandSecondaryCtaTextColor());

                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_AWAY_TEAM_TITLE_KEY) {
                    if (data.getGist() != null && data.getGist().getAwayTeam() != null && data.getGist().getAwayTeam().getGist() != null && data.getGist().getAwayTeam().getGist().getTitle() != null) {
                        ((TextView) view).setText(data.getGist().getAwayTeam().getGist().getTitle());
                        ((TextView) view).setTextColor(appCMSPresenter.getBrandSecondaryCtaTextColor());
                    }
                }*/ else if (componentKey == AppCMSUIKeyType.PAGE_GAME_DATE_KEY) {
                    if (data.getGist() != null && data.getGist().getEventSchedule() != null
                            && data.getGist().getEventSchedule().get(0) != null
                            && data.getGist().getEventSchedule().get(0).getEventDate() != 0) {

                        if (childComponent.getNumberOfLines() != 0) {
                            ((TextView) view).setSingleLine(false);
                            ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                        }

                        StringBuilder thumbInfo = new StringBuilder();
                        thumbInfo.append(getDateFormat((data.getGist().getEventSchedule().get(0).getEventDate() * 1000L), "EEEE"))
                                .append(" | ")
                                .append(getDateFormat((data.getGist().getEventSchedule().get(0).getEventDate() * 1000L), "MMM dd"))
                                .append(", ")
                                .append(getDateFormat((data.getGist().getEventSchedule().get(0).getEventDate() * 1000L), "yyyy"));

//                        thumbInfo.append(" | Doors open at ")
//                                .append(getDateFormat((data.getGist().getEventSchedule().get(0).getEventDate()), "hh:mm aa"));

                        ((TextView) view).setText(thumbInfo);
                        if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                            int textColor = Color.parseColor(getColor(getContext(),
                                    childComponent.getTextColor()));
                            ((TextView) view).setTextColor(textColor);
                        } else {
                            ((TextView) view).setTextColor(appCMSPresenter.getGeneralTextColor());

                        }
                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_TRAY_SCHEDULE_TITLE_KEY) {
                    if (data.getGist() != null && data.getGist().getTitle() != null) {

                        if (childComponent.getNumberOfLines() != 0) {
                            ((TextView) view).setSingleLine(false);
                            ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                        }

                        ((TextView) view).setText(data.getGist().getTitle());
                        if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                            int textColor = Color.parseColor(getColor(getContext(),
                                    childComponent.getTextColor()));
                            ((TextView) view).setTextColor(textColor);
                        } else {
                            ((TextView) view).setTextColor(appCMSPresenter.getGeneralTextColor());

                        }

                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_PLAYER_TEAM_TITLE_TXT_KEY) {
                    if (data.getPlayersData() != null && data.getPlayersData().getFirstName() != null
                            ) {
                        StringBuilder strPlayerName = new StringBuilder();
                        strPlayerName.append(data.getPlayersData().getFirstName());
                        if (childComponent.getNumberOfLines() != 0) {
                            ((TextView) view).setSingleLine(false);
                            ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                        }
                        if (data.getPlayersData().getLastName() != null) {
                            strPlayerName.append("\n" + data.getPlayersData().getLastName());
                        }
                        ((TextView) view).setText(strPlayerName);
                        if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                            int textColor = Color.parseColor(getColor(getContext(),
                                    childComponent.getTextColor()));
                            ((TextView) view).setTextColor(textColor);
                        } else {
                            ((TextView) view).setTextColor(appCMSPresenter.getGeneralTextColor());
                        }
                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_PLAYER_RECORD_LABEL_KEY) {
                    if (data.getPlayersData() != null && data.getPlayersData().getData() != null
                            && data.getPlayersData().getData().getMetadata() != null && data.getPlayersData().getData().getMetadata().get(0) != null
                            && data.getPlayersData().getData().getMetadata().get(0).getValue() != null
                            ) {

                        if (childComponent.getNumberOfLines() != 0) {
                            ((TextView) view).setSingleLine(false);
                            ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                        }

                        ((TextView) view).setText(data.getPlayersData().getData().getMetadata().get(0).getValue());
                        ((TextView) view).setTextColor(appCMSPresenter.getGeneralTextColor());
                        if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                            int textColor = Color.parseColor(getColor(getContext(),
                                    childComponent.getTextColor()));
                            ((TextView) view).setTextColor(textColor);
                        }

                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_HEIGHT_VALUE_TEXT) {
                    if (data.getPlayersData() != null && data.getPlayersData().getHeight() != null
                            ) {

                        if (childComponent.getNumberOfLines() != 0) {
                            ((TextView) view).setSingleLine(false);
                            ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                        }
                        ((TextView) view).setText(data.getPlayersData().getHeight());
                        if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                            int textColor = Color.parseColor(getColor(getContext(),
                                    childComponent.getTextColor()));
                            ((TextView) view).setTextColor(textColor);
                        } else {
                            ((TextView) view).setTextColor(appCMSPresenter.getGeneralTextColor());
                        }

                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_WEIGHT_VALUE_TEXT) {
                    if (data.getPlayersData() != null && data.getPlayersData().getWeight() != null
                            ) {

                        if (childComponent.getNumberOfLines() != 0) {
                            ((TextView) view).setSingleLine(false);
                            ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                        }
                        ((TextView) view).setText(data.getPlayersData().getWeight());
                        if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                            int textColor = Color.parseColor(getColor(getContext(),
                                    childComponent.getTextColor()));
                            ((TextView) view).setTextColor(textColor);
                        } else {
                            ((TextView) view).setTextColor(appCMSPresenter.getGeneralTextColor());
                        }
                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_PLAYER_SCORE_TEXT) {
                    if (data.getPlayersData() != null && data.getPlayersData().getData() != null
                            && data.getPlayersData().getData().getMetadata() != null && data.getPlayersData().getData().getMetadata().get(1) != null
                            && data.getPlayersData().getData().getMetadata().get(1).getValue() != null
                            ) {

                        if (childComponent.getNumberOfLines() != 0) {
                            ((TextView) view).setSingleLine(false);
                            ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                        }
                        ((TextView) view).setText("(" + data.getPlayersData().getData().getMetadata().get(1).getValue() + "pts)");
                        if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                            int textColor = Color.parseColor(getColor(getContext(),
                                    childComponent.getTextColor()));
                            ((TextView) view).setTextColor(textColor);
                        } else {
                            ((TextView) view).setTextColor(appCMSPresenter.getGeneralTextColor());
                        }

                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_HEIGHT_LABEL_TEXT || componentKey == AppCMSUIKeyType.PAGE_WEIGHT_LABEL_TEXT) {

                    if (!TextUtils.isEmpty(childComponent.getText())) {
                        ((TextView) view).setText(childComponent.getText());
                    }
                    if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                        int textColor = Color.parseColor(getColor(getContext(),
                                childComponent.getTextColor()));
                        ((TextView) view).setTextColor(textColor);
                    } else {
                        ((TextView) view).setTextColor(appCMSPresenter.getGeneralTextColor());
                    }

                } else if (componentKey == AppCMSUIKeyType.PAGE_GAME_TIME_KEY) {
                    if (data.getGist() != null && data.getGist().getEventSchedule() != null && data.getGist().getEventSchedule().get(0) != null
                            && data.getGist().getEventSchedule().get(0).getEventDate() != 0) {

                        String date = getDateFormat((data.getGist().getEventSchedule().get(0).getEventDate()), "hh:mm aa");
                        ;

                        ((TextView) view).setText(date + " " + data.getGist().getEventSchedule().get(0).getEventTimeZone());
                        ((TextView) view).setTextColor(appCMSPresenter.getGeneralTextColor());

                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_ARTICLE_TITLE_KEY && !TextUtils.isEmpty(data.getGist().getTitle())) {
                    ((TextView) view).setSingleLine(false);
                    ((TextView) view).setMaxLines(2);
                    ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                    ((TextView) view).setText(data.getGist().getTitle());

                    if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                        int textColor = Color.parseColor(getColor(getContext(),
                                childComponent.getTextColor()));
                        ((TextView) view).setTextColor(textColor);
                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_WATCHLIST_DURATION_KEY_BG) {

                    final int SECONDS_PER_MINS = 60;
                    if ((data.getGist().getRuntime() / SECONDS_PER_MINS) < 2) {
                        StringBuilder runtimeText = new StringBuilder()
                                .append(data.getGist().getRuntime() / SECONDS_PER_MINS)
                                .append(" ")
                                .append(context.getString(R.string.min_abbreviation));

                        ((TextView) view).setText(runtimeText);
                    } else {
                        StringBuilder runtimeText = new StringBuilder()
                                .append(data.getGist().getRuntime() / SECONDS_PER_MINS)
                                .append(" ")
                                .append(context.getString(R.string.mins_abbreviation));

                        ((TextView) view).setText(runtimeText);
                    }
                    ((TextView) view).setBackgroundColor(Color.parseColor("#7D000000"));
                    ((TextView) view).setTextColor(Color.parseColor("#ffffff"));
                    inVisibleIfSeries(data, view);  //View is invisible if ContentType is Seriese
                } else if (componentKey == AppCMSUIKeyType.PAGE_ARTICLE_DESCRIPTION_KEY && !TextUtils.isEmpty(data.getGist().getDescription())) {
                    ((TextView) view).setSingleLine(false);
                    ((TextView) view).setMaxLines(3);
                    ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                    ((TextView) view).setText(data.getGist().getDescription());
                    if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                        int textColor = Color.parseColor(getColor(getContext(),
                                childComponent.getTextColor()));
                        ((TextView) view).setTextColor(textColor);
                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_API_SUMMARY_TEXT_KEY && !TextUtils.isEmpty(data.getGist().getSummaryText())) {
                    if (childComponent.getNumberOfLines() != 0) {
                        ((TextView) view).setSingleLine(false);
                        ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                        ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                    }
                    ((TextView) view).setText(data.getGist().getSummaryText());
                } else if (componentKey == AppCMSUIKeyType.PAGE_DELETE_DOWNLOAD_VIDEO_SIZE_KEY) {
                    ((TextView) view).setText(appCMSPresenter.getDownloadedFileSize(data.getGist().getId()));
                } else if (componentKey == AppCMSUIKeyType.PAGE_HISTORY_WATCHED_TIME_KEY) {
                    ((TextView) view).setText(appCMSPresenter.getLastWatchedTime(data));
                } else if (componentKey == AppCMSUIKeyType.PAGE_HISTORY_DURATION_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_DOWNLOAD_DURATION_KEY) {
                    final int SECONDS_PER_MINS = 60;
                    if ((data.getGist().getRuntime() / SECONDS_PER_MINS) < 2) {
                        StringBuilder runtimeText = new StringBuilder()
                                .append(data.getGist().getRuntime() / SECONDS_PER_MINS);
                        if (appCMSUIcomponentViewType != AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY) {
                            runtimeText.append(" ")
                                    .append(context.getString(R.string.min_abbreviation));
                        }
                        ((TextView) view).setText(runtimeText);
                    } else {
                        StringBuilder runtimeText = new StringBuilder()
                                .append(data.getGist().getRuntime() / SECONDS_PER_MINS);
                        if (appCMSUIcomponentViewType != AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY) {
                            runtimeText.append(" ")
                                    .append(context.getString(R.string.mins_abbreviation));
                        }
                        ((TextView) view).setText(runtimeText);
                    }
                    int textBgColor = Color.parseColor(ViewCreator.getColorWithOpacity(context, "000000", 76));
                    ((TextView) view).setBackgroundColor(textBgColor);
                    if (appCMSUIcomponentViewType == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY)
                        ((TextView) view).setVisibility(VISIBLE);

                } else if (componentKey == AppCMSUIKeyType.PAGE_WATCHLIST_DURATION_KEY) {
                    final int SECONDS_PER_MINS = 60;
                    if ((data.getGist().getRuntime() / SECONDS_PER_MINS) < 2) {
                        StringBuilder runtimeText = new StringBuilder()
                                .append(data.getGist().getRuntime() / SECONDS_PER_MINS)
                                .append(" ")
                                //min value is being set in unit tag under PAGE_WATCHLIST_DURATION_UNIT_KEY component key so removing
                                //unit abbrevation from here .Its causing double visibilty of time unit
                                .append(context.getString(R.string.min_abbreviation));
                        ((TextView) view).setText(runtimeText);
                        ((TextView) view).setVisibility(View.VISIBLE);

                    } else {
                        StringBuilder runtimeText = new StringBuilder()
                                .append(data.getGist().getRuntime() / SECONDS_PER_MINS)
                                .append(" ")
                                .append(context.getString(R.string.mins_abbreviation));
                        ((TextView) view).setText(runtimeText);
                        ((TextView) view).setVisibility(View.VISIBLE);
                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_AUDIO_DURATION_KEY) {
//                        Log.d("bindView","Audio Duration Key :"+data.getGist().getTitle());
                    String time = appCMSPresenter.audioDuration((int) data.getGist().getRuntime());
                    ((TextView) view).setText(time);
                } else if (componentKey == AppCMSUIKeyType.PAGE_WATCHLIST_DURATION_UNIT_KEY) {
                    ((TextView) view).setText(context.getResources().getQuantityString(R.plurals.min_duration_unit,
                            (int) (data.getGist().getRuntime() / 60)));

                    ViewCreator.UpdateDownloadImageIconAction updateDownloadImageIconAction =
                            updateDownloadImageIconActionMap.get(data.getGist().getId());
                    if (updateDownloadImageIconAction != null) {
                        view.setClickable(true);
                        view.setOnClickListener(updateDownloadImageIconAction.getAddClickListener());
                    }
                    ((TextView) view).setVisibility(View.VISIBLE);

                } else if (componentKey == AppCMSUIKeyType.PAGE_GRID_THUMBNAIL_INFO) {

                    if (data.getGist().getMediaType() != null && data.getGist().getMediaType().toLowerCase().contains(context.getString(R.string.app_cms_photo_gallery_key_type).toLowerCase())) {
                        StringBuilder thumbInfo = new StringBuilder();
                        if (data.getGist().getPublishDate() != null) {
                            thumbInfo.append(getDateFormat(Long.parseLong(data.getGist().getPublishDate()), "MMM dd"));
                        }
                        int noOfPhotos = 0;
                        if (data.getStreamingInfo() != null && data.getStreamingInfo().getPhotogalleryAssets() != null && data.getStreamingInfo().getPhotogalleryAssets().size() > 0) {
                            if (thumbInfo.length() > 0) {
                                thumbInfo.append(" | ");
                            }
                            noOfPhotos = data.getStreamingInfo().getPhotogalleryAssets().size();
                            thumbInfo.append(context.getResources().getQuantityString(R.plurals.no_of_photos, noOfPhotos, noOfPhotos));
                        }

                        ((TextView) view).setText(thumbInfo);
                    } else if (appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.SPORTS) {
                        String thumbInfo = null;
                        if (data.getGist().getPublishDate() != null) {
                            thumbInfo = getDateFormat(Long.parseLong(data.getGist().getPublishDate()), "MMM dd");
                        }
                        if (data.getGist() != null && data.getGist().getReadTime() != null) {
                            StringBuilder readTimeText = new StringBuilder()
                                    .append(data.getGist().getReadTime().trim())
                                    .append("min")
                                    .append(" read ");

                            if (thumbInfo != null && thumbInfo.length() > 0) {
                                readTimeText.append("|")
                                        .append(" ")
                                        .append(thumbInfo);
                            }
                            ((TextView) view).setText(readTimeText);
                        } else {
                            long runtime = data.getGist().getRuntime();
                            if (thumbInfo != null && runtime > 0) {
                                ((TextView) view).setText(AppCMSPresenter.convertSecondsToTime(runtime) + " | " + thumbInfo);
                            } else {
                                if (thumbInfo != null) {
                                    ((TextView) view).setText(thumbInfo);
                                } else if (runtime > 0) {
                                    ((TextView) view).setText(AppCMSPresenter.convertSecondsToTime(runtime));
                                }
                            }

                        }
                    } else {
                        ((TextView) view).setVisibility(GONE);
                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_GRID_PHOTO_GALLERY_THUMBNAIL_INFO) {
                    StringBuilder thumbInfo = new StringBuilder();
                    if (data.getGist().getPublishDate() != null) {
                        thumbInfo.append(getDateFormat(Long.parseLong(data.getGist().getPublishDate()), "MMM dd"));
                    }
                    int noOfPhotos = 0;
                    if (data.getStreamingInfo() != null && data.getStreamingInfo().getPhotogalleryAssets() != null && data.getStreamingInfo().getPhotogalleryAssets().size() > 0) {
                        if (thumbInfo.length() > 0) {
                            thumbInfo.append(" | ");
                        }
                        noOfPhotos = data.getStreamingInfo().getPhotogalleryAssets().size();
                        thumbInfo.append(context.getResources().getQuantityString(R.plurals.no_of_photos, noOfPhotos, noOfPhotos));
                    }

                        ((TextView) view).setText(thumbInfo);
                    } else if (componentKey == AppCMSUIKeyType.PAGE_API_TITLE ||
                            componentKey == AppCMSUIKeyType.PAGE_EPISODE_TITLE_KEY) {
                        if (data.getGist() != null && data.getGist().getTitle() != null) {
                            ((TextView) view).setText(data.getGist().getTitle());
                            ((TextView) view).setSingleLine(true);
                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                            ((TextView) view).setVisibility(View.VISIBLE);
                        }
                    }  else if (componentKey == AppCMSUIKeyType.PAGE_EXPIRE_TIME_TITLE) {
                    if (data.getGist() != null && data.getGist().getTransactionDateEpoch() > 0) {
                        ((TextView) view).setSingleLine(true);
                        ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                        ((TextView) view).setVisibility(View.VISIBLE);
                        long eventDate = data.getGist().getTransactionDateEpoch();

                        long remainingTime = appCMSPresenter.getTimeIntervalForEvent(eventDate * 1000L, "EEE MMM dd HH:mm:ss");

                        String expirationTime=appCMSPresenter.getRentExpirationFormat(remainingTime);
                        ((TextView) view).setBackground(context.getResources().getDrawable(R.drawable.rectangle_with_round_corners,null));
                        ((TextView) view).setText(expirationTime);

                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_HISTORY_DESCRIPTION_KEY ||
                            componentKey == AppCMSUIKeyType.PAGE_WATCHLIST_DESCRIPTION_KEY ||
                            componentKey == AppCMSUIKeyType.PAGE_DOWNLOAD_DESCRIPTION_KEY) {
                        if (data != null && data.getGist() != null && data.getGist().getDescription() != null) {
                            ((TextView) view).setSingleLine(false);
                            ((TextView) view).setMaxLines(2);
                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                            ((TextView) view).setText(data.getGist().getDescription());

                        try {
                            ViewTreeObserver titleTextVto = view.getViewTreeObserver();
                            ViewCreatorMultiLineLayoutListener viewCreatorTitleLayoutListener =
                                    new ViewCreatorMultiLineLayoutListener((TextView) view,
                                            data.getGist().getTitle(),
                                            data.getGist().getDescription(),
                                            appCMSPresenter,
                                            true,
                                            appCMSPresenter.getBrandPrimaryCtaColor(),
                                            appCMSPresenter.getGeneralTextColor(),
                                            false);
                            titleTextVto.addOnGlobalLayoutListener(viewCreatorTitleLayoutListener);
                        } catch (Exception e) {
                        }
                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_PLAYLIST_AUDIO_ARTIST_TITLE) {

                    String artist = appCMSPresenter.getArtistNameFromCreditBlocks(data.getCreditBlocks());
                    ((TextView) view).setText(artist);
                    ((TextView) view).setTextColor(Color.parseColor(childComponent.getTextColor()));

                } else if (componentKey == AppCMSUIKeyType.PAGE_API_DESCRIPTION) {
                    if (childComponent.getNumberOfLines() != 0) {
                        ((TextView) view).setSingleLine(false);
                        ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                        ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                    }
                    ((TextView) view).setText(data.getGist().getDescription());
                    try {
                        ViewTreeObserver titleTextVto = view.getViewTreeObserver();
                        ViewCreatorMultiLineLayoutListener viewCreatorTitleLayoutListener =
                                new ViewCreatorMultiLineLayoutListener((TextView) view,
                                        data.getGist().getTitle(),
                                        data.getGist().getDescription(),
                                        appCMSPresenter,
                                        false,
                                        appCMSPresenter.getBrandPrimaryCtaColor(),
                                        appCMSPresenter.getGeneralTextColor(),
                                        true);
                        titleTextVto.addOnGlobalLayoutListener(viewCreatorTitleLayoutListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ((TextView) view).setVisibility(View.VISIBLE);
                }else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_FEATURE_TEXT_KEY) {
                    /*if (data != null && data.getPlanDetails() != null && data.getPlanDetails().get(0) != null &&
                            data.getPlanDetails().get(0).getFeatureDetails() != null &&
                            data.getPlanDetails().get(0).getFeatureDetails().size() != 0 &&
                            data.getPlanDetails().get(0).getFeatureDetails().get(0) != null &&
                            data.getPlanDetails().get(0).getFeatureDetails().get(0).getTextToDisplay() != null) {
                        ((TextView) view).setSingleLine(false);
                        ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                        ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                        ((TextView) view).setText(data.getPlanDetails().get(0).getFeatureDetails().get(0).getTextToDisplay().toUpperCase());

                    }*/

                    StringBuilder planDuration = new StringBuilder();
                    if (data.getRenewalCycleType().contains(context.getString(R.string.app_cms_plan_renewal_cycle_type_monthly))) {
                        if (data.getRenewalCyclePeriodMultiplier() == 1) {
                            planDuration.append(context.getString(R.string.plan_type_month));
                        } else {
                            planDuration.append(data.getRenewalCyclePeriodMultiplier());
                            planDuration.append(" ");
                            planDuration.append(context.getString(R.string.plan_type_month));
                            if (data.getRenewalCyclePeriodMultiplier() > 1)
                                planDuration.append("s");
                        }
                    }
                    if (data.getRenewalCycleType().contains(context.getString(R.string.app_cms_plan_renewal_cycle_type_yearly))) {
                        if (data.getRenewalCyclePeriodMultiplier() == 1) {
                            planDuration.append(context.getString(R.string.plan_type_yearly));
                        } else {
                            planDuration.append(data.getRenewalCyclePeriodMultiplier());
                            planDuration.append(" ");
                            planDuration.append(context.getString(R.string.plan_type_yearly));
                            if (data.getRenewalCyclePeriodMultiplier() > 1)
                                planDuration.append("s");
                        }
                    }
                    if (data.getRenewalCycleType().contains(context.getString(R.string.app_cms_plan_renewal_cycle_type_daily))) {
                        if (data.getRenewalCyclePeriodMultiplier() == 1) {
                            planDuration.append(context.getString(R.string.plan_type_day));
                        } else {
                            planDuration.append(data.getRenewalCyclePeriodMultiplier());
                            planDuration.append(" ");
                            planDuration.append(context.getString(R.string.plan_type_day));
                            if (data.getRenewalCyclePeriodMultiplier() > 1)
                                planDuration.append("s");
                        }
                    }
                    ((TextView) view).setText(planDuration.toString().toUpperCase());
                } else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_TITLE_KEY) {
                    ((TextView) view).setText(data.getName());
                    if (componentType.equals(AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_03_KEY) ||
                            componentType.equals(AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY)) {
                        ((TextView) view).setTextColor(themeColor);
                    } else {
                        ((TextView) view).setTextColor(Color.parseColor(childComponent.getTextColor()));
                    }
                }else if (componentKey == AppCMSUIKeyType.PAGE_SINGLE_PLAN_SUBSCRIBE_TEXT_KEY) {
                    ((TextView) view).setText(childComponent.getText());
                } else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_PRICEINFO_KEY) {

                    if (data != null) {
                        int planIndex = 0;
                        for (int i = 0; i < data.getPlanDetails().size(); i++) {
                            if (data.getPlanDetails().get(i) != null &&
                                    data.getPlanDetails().get(i).getIsDefault()) {
                                planIndex = i;
                            }
                        }
                        Currency currency = null;
                        if (data.getPlanDetails() != null &&
                                !data.getPlanDetails().isEmpty() &&
                                data.getPlanDetails().get(planIndex) != null &&
                                data.getPlanDetails().get(planIndex).getRecurringPaymentCurrencyCode() != null) {
                            try {
                                currency = Currency.getInstance(data.getPlanDetails().get(planIndex).getRecurringPaymentCurrencyCode());
                            } catch (Exception e) {
                                //Log.e(TAG, "Could not parse locale");
                            }
                        }

                        if (data.getPlanDetails() != null &&
                                !data.getPlanDetails().isEmpty() &&
                                data.getPlanDetails().get(planIndex) != null &&
                                data.getPlanDetails().get(planIndex).getStrikeThroughPrice() != 0) {

                            double recurringPaymentAmount = data.getPlanDetails().get(planIndex).getRecurringPaymentAmount();
                            String formattedRecurringPaymentAmount = context.getString(R.string.cost_with_fraction,
                                    recurringPaymentAmount);
                            if (recurringPaymentAmount - (int) recurringPaymentAmount == 0) {
                                formattedRecurringPaymentAmount = context.getString(R.string.cost_without_fraction,
                                        recurringPaymentAmount);
                            }

                            double strikeThroughPaymentAmount = data.getPlanDetails()
                                    .get(planIndex).getStrikeThroughPrice();
                            String formattedStrikeThroughPaymentAmount = context.getString(R.string.cost_with_fraction,
                                    strikeThroughPaymentAmount);
                            if (strikeThroughPaymentAmount - (int) strikeThroughPaymentAmount == 0) {
                                formattedStrikeThroughPaymentAmount = context.getString(R.string.cost_without_fraction,
                                        strikeThroughPaymentAmount);
                            }

                            StringBuilder stringBuilder = new StringBuilder();
                            if (currency != null) {
                                stringBuilder.append(currency.getSymbol());
                            }
                            stringBuilder.append(formattedStrikeThroughPaymentAmount);

                            if (data.getPlanDetails().get(0).getRecurringPaymentAmount() != 0) {
                                int strikeThroughLength = stringBuilder.length();
                                stringBuilder.append("     ");
                                if (currency != null) {
                                    stringBuilder.append(currency.getSymbol());
                                }
                                stringBuilder.append(String.valueOf(formattedRecurringPaymentAmount));

                                SpannableString spannableString =
                                        new SpannableString(stringBuilder.toString());
                                spannableString.setSpan(new StrikethroughSpan(), 0,
                                        strikeThroughLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                ((TextView) view).setText(spannableString);
                            } else {
                                ((TextView) view).setText(stringBuilder.toString());
                            }
                            if (!appCMSPresenter.isSinglePlanFeatureAvailable()) {
                                FrameLayout.LayoutParams layPar = (FrameLayout.LayoutParams) ((TextView) view).getLayoutParams();
                                layPar.gravity = Gravity.TOP;
                                view.setLayoutParams(layPar);
                            }
                        } else {
                            double recurringPaymentAmount = data.getPlanDetails()
                                    .get(planIndex).getRecurringPaymentAmount();
                            String formattedRecurringPaymentAmount = context.getString(R.string.cost_with_fraction,
                                    recurringPaymentAmount);
                            if (recurringPaymentAmount - (int) recurringPaymentAmount == 0) {
                                formattedRecurringPaymentAmount = context.getString(R.string.cost_without_fraction,
                                        recurringPaymentAmount);
                            }

                            StringBuilder planAmt = new StringBuilder();
                            if (currency != null) {
                                String currencySymbol = currency.getSymbol();
                                if (currencySymbol.contains("US$"))
                                    currencySymbol = "$";
                                planAmt.append(currencySymbol);
                            }
                            planAmt.append(formattedRecurringPaymentAmount);
                            StringBuilder planDuration = new StringBuilder();
                            if (appCMSUIcomponentViewType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY ||
                                    appCMSUIcomponentViewType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY) {
                                if (data.getRenewalCycleType().contains(context.getString(R.string.app_cms_plan_renewal_cycle_type_monthly))) {
                                    planDuration.append(" ");
                                    planDuration.append(context.getString(R.string.forward_slash));
                                    planDuration.append(" ");
                                    if (data.getRenewalCyclePeriodMultiplier() == 1) {
                                        planDuration.append(context.getString(R.string.plan_type_month));
                                    } else {
                                        planDuration.append(data.getRenewalCyclePeriodMultiplier());
                                        planDuration.append(" ");
                                        planDuration.append(context.getString(R.string.plan_type_month));
                                        if (data.getRenewalCyclePeriodMultiplier() > 1)
                                            planDuration.append("s");
                                    }
                                }
                                if (data.getRenewalCycleType().contains(context.getString(R.string.app_cms_plan_renewal_cycle_type_yearly))) {
                                    planDuration.append(" ");
                                    planDuration.append(context.getString(R.string.forward_slash));
                                    planDuration.append(" ");
                                    if (data.getRenewalCyclePeriodMultiplier() == 1) {
                                        planDuration.append(context.getString(R.string.plan_type_year));
                                    } else {
                                        planDuration.append(data.getRenewalCyclePeriodMultiplier());
                                        planDuration.append(" ");
                                        planDuration.append(context.getString(R.string.plan_type_year));
                                        if (data.getRenewalCyclePeriodMultiplier() > 1)
                                            planDuration.append("s");
                                    }
                                }
                                if (data.getRenewalCycleType().contains(context.getString(R.string.app_cms_plan_renewal_cycle_type_daily))) {
                                    planDuration.append(" ");
                                    planDuration.append(context.getString(R.string.forward_slash));
                                    planDuration.append(" ");
                                    if (data.getRenewalCyclePeriodMultiplier() == 1) {
                                        planDuration.append(context.getString(R.string.plan_type_day));
                                    } else {
                                        planDuration.append(data.getRenewalCyclePeriodMultiplier());
                                        planDuration.append(" ");
                                        planDuration.append(context.getString(R.string.plan_type_day));
                                        if (data.getRenewalCyclePeriodMultiplier() > 1)
                                            planDuration.append("s");
                                    }
                                }
                            }
                            if (appCMSUIcomponentViewType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY) {
                                StringBuilder plan = new StringBuilder();
//                                String pay = "PAY";
//                                plan.append(pay);
//                                plan.append(" ");
                                plan.append(planAmt.toString());
//                                plan.append(planDuration.toString());
                                Spannable text = new SpannableString(plan.toString());
                                float payFont = 1.0f;
                                float durationFont = 1.0f;
                                float priceFont = 1.3f;
                                if (BaseView.isTablet(context)) {
                                    payFont = 1.1f;
                                    durationFont = 1.1f;
                                    priceFont = 1.5f;
                                }
//                                text.setSpan(new RelativeSizeSpan(payFont), 0, pay.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                text.setSpan(new StyleSpan(Typeface.BOLD), 0, pay.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                text.setSpan(new RelativeSizeSpan(priceFont), 0, planAmt.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                text.setSpan(new StyleSpan(Typeface.BOLD), 0, planAmt.toString().length(),
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                text.setSpan(new RelativeSizeSpan(durationFont), planAmt.toString().length() + 1, plan.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                ((TextView) view).setText(text, TextView.BufferType.SPANNABLE);

                                if (!appCMSPresenter.isSinglePlanFeatureAvailable()) {
                                    FrameLayout.LayoutParams layPar = (FrameLayout.LayoutParams) ((TextView) view).getLayoutParams();
                                    layPar.gravity = Gravity.TOP;
                                    view.setLayoutParams(layPar);
                                }
                            } else {
                                StringBuilder plan = new StringBuilder();
                                plan.append(planAmt.toString());
                                if (appCMSUIcomponentViewType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY)
                                    plan.append(planDuration.toString());
                                ((TextView) view).setText(plan.toString());
                            }
                            ((TextView) view).setPaintFlags(((TextView) view).getPaintFlags());
                        }
                        ((TextView) view).setTextColor(appCMSPresenter.getGeneralTextColor());
                    } else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_BESTVALUE_KEY) {
                        ((TextView) view).setText(childComponent.getText());
                        ((TextView) view).setTextColor(Color.parseColor(
                                childComponent.getTextColor()));
                    } else {
                        ((TextView) view).setTextColor(appCMSPresenter.getGeneralTextColor());
                    }

                } else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_BESTVALUE_KEY) {
                    ((TextView) view).setText(childComponent.getText());
                    ((TextView) view).setTextColor(Color.parseColor(
                            childComponent.getTextColor()));
                } else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_BESTVALUE_KEY) {
                    ((TextView) view).setText(childComponent.getText());
                    ((TextView) view).setTextColor(Color.parseColor(
                            childComponent.getTextColor()));
                } else if (componentKey == AppCMSUIKeyType.PAGE_FIGHTER_LABEL_KEY) {
                    String fighter1Name = data.getFights().getFighter1_LastName();
                    String fighter2Name = data.getFights().getFighter2_LastName();
                    if (data.getFights().getWinnerId() != null && !TextUtils.isEmpty(data.getFights().getWinnerId())) {
                        if (data.getFights().getWinnerId().equalsIgnoreCase(data.getFights().getFighter1_Id())) {
                            fighter1Name = fighter1Name + "(Won)";
                        } else if (data.getFights().getWinnerId().equalsIgnoreCase(data.getFights().getFighter2Id())) {
                            fighter2Name = fighter2Name + "(Won)";
                        }
                    }
                    if (data.getFights().getFighter1_LastName() != null && data.getFights().getFighter2_LastName() != null) {
                        ((TextView) view).setText(data.getFights().getFightSerialNo() + " " + fighter1Name + "/" + fighter2Name);
                    }
                    ((TextView) view).setTextColor(Color.parseColor(
                            childComponent.getTextColor()));
                    ((TextView) view).setGravity(Gravity.CENTER_VERTICAL);
                } else if (childComponent.getText() != null && !TextUtils.isEmpty(childComponent.getText())) {
                    ((TextView) view).setText(childComponent.getText());
                    ((TextView) view).setTextColor(Color.parseColor(
                            childComponent.getTextColor()));
                }

                if (!TextUtils.isEmpty(component.getFontFamily())) {
                    setTypeFace(context,
                            appCMSPresenter,
                            jsonValueKeyMap,
                            component,
                            (TextView) view);
                }
//                }
            } else if (componentType == AppCMSUIKeyType.PAGE_LABEL_KEY) {
                if (componentKey == AppCMSUIKeyType.PAGE_RECORD_TYPE_KEY) {
                    String record = "";
                    String score = "";
                    if (data.getPlayersData() != null && data.getPlayersData().getData() != null
                            && data.getPlayersData().getData().getMetadata() != null) {
                        for (int j = 0; j < data.getPlayersData().getData().getMetadata().size(); j++) {
                            if (data.getPlayersData().getData().getMetadata().get(j).getName().equalsIgnoreCase("mma_record")) {
                                record = data.getPlayersData().getData().getMetadata().get(j).getValue();
                            } else if (data.getPlayersData().getData().getMetadata().get(j).getName().equalsIgnoreCase("pfl_record")) {
                                score = data.getPlayersData().getData().getMetadata().get(j).getValue();
                            }
                        }
                    }

                    for (int i = 0; i < childComponent.getComponents().size(); i++) {
                        TextView textView = new TextView(context);


                        if (jsonValueKeyMap.get(childComponent.getComponents().get(i).getKey()) == AppCMSUIKeyType.PAGE_PLAYER_SCORE_TEXT) {
                            if (score != null && !TextUtils.isEmpty(score)) {
                                textView.setText("(" + score + ")");
                            }
//                            textView.setText("(" + score + "pts)");
                        } else if (jsonValueKeyMap.get(childComponent.getComponents().get(i).getKey()) == AppCMSUIKeyType.PAGE_PLAYER_RECORD_LABEL_KEY) {
                            textView.setText(record);

                        }

                        if (childComponent.getComponents().get(i).getNumberOfLines() != 0) {
                            textView.setSingleLine(false);
                            textView.setMaxLines(childComponent.getComponents().get(i).getNumberOfLines());
                            textView.setEllipsize(TextUtils.TruncateAt.END);
                        }

                        textView.setTextColor(appCMSPresenter.getGeneralTextColor());
                        if (!TextUtils.isEmpty(childComponent.getComponents().get(i).getTextColor())) {
                            int textColor = Color.parseColor(getColor(getContext(),
                                    childComponent.getComponents().get(i).getTextColor()));
                            textView.setTextColor(textColor);
                        }
                        if (!TextUtils.isEmpty(component.getFontFamily())) {
                            setTypeFace(context,
                                    appCMSPresenter,
                                    jsonValueKeyMap,
                                    component,
                                    textView);
                        }
                        if (getFontSize(context, childComponent.getComponents().get(i).getLayout()) > 0) {
                            textView.setTextSize(getFontSize(context, childComponent.getComponents().get(i).getLayout()));
                        }
                        ((LinearLayout) view).addView(textView);

                    }
                }

            } else if (componentType == AppCMSUIKeyType.PAGE_PLAN_META_DATA_VIEW_KEY) {
                if (view instanceof ViewPlansMetaDataView) {
                    ((ViewPlansMetaDataView) view).setData(data);
                }

                if (view instanceof SubscriptionMetaDataView) {
                    ((SubscriptionMetaDataView) view).setData(data);
                }
            } else if (componentType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_03_KEY ||
                    componentType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY) {
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            } else if (componentType == AppCMSUIKeyType.PAGE_PROGRESS_VIEW_KEY) {
                if (view instanceof ProgressBar) {
                    ContentDatum historyData = null;
                    if (data != null && data.getGist() != null && data.getGist().getId() != null) {
                        historyData = appCMSPresenter.getUserHistoryContentDatum(data.getGist().getId());
                    }

                    int progress = 0;

                    if (historyData != null) {
                        data.getGist().setWatchedPercentage(historyData.getGist().getWatchedPercentage());
                        data.getGist().setWatchedTime(historyData.getGist().getWatchedTime());
                        if (historyData.getGist().getWatchedPercentage() > 0) {
                            progress = historyData.getGist().getWatchedPercentage();
                            view.setVisibility(View.VISIBLE);
                            ((ProgressBar) view).setProgress(progress);
                        } else {
                            long watchedTime = historyData.getGist().getWatchedTime();
                            long runTime = historyData.getGist().getRuntime();
                            if (watchedTime > 0 && runTime > 0) {
                                long percentageWatched = (long) (((double) watchedTime / (double) runTime) * 100.0);
                                progress = (int) percentageWatched;
                                ((ProgressBar) view).setProgress(progress);
                                view.setVisibility(View.VISIBLE);
                            } else {
                                view.setVisibility(View.INVISIBLE);
                                ((ProgressBar) view).setProgress(0);
                            }
                        }
                    } else {
                        view.setVisibility(View.INVISIBLE);
                    }
                }
            }

            if (shouldShowView(childComponent) && bringToFront) {
                view.bringToFront();
            }
            view.forceLayout();
        } else {
            Log.d("bindView", "CollectionGridItemView else");
        }
    }

    private void setBorder(View itemView) {
        GradientDrawable planBorder = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.TRANSPARENT, Color.parseColor("#000000")});
        planBorder.setShape(GradientDrawable.RECTANGLE);
        planBorder.setCornerRadius(0f);
        itemView.setBackground(planBorder);
    }

    public Component matchComponentToView(View view) {
        for (ItemContainer itemContainer : childItems) {
            if (itemContainer.childView == view) {
                return itemContainer.component;
            }
        }
        return null;
    }

    public List<View> getViewsToUpdateOnClickEvent() {
        return viewsToUpdateOnClickEvent;
    }

    private String getDateFormat(long timeMilliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMilliSeconds);
        return formatter.format(calendar.getTime());
    }

    public List<ItemContainer> getChildItems() {
        return childItems;
    }

    private String getColor(Context context, String color) {
        if (color.indexOf(context.getString(R.string.color_hash_prefix)) != 0) {
            return context.getString(R.string.color_hash_prefix) + color;
        }
        return color;
    }

    public String getSubstring(String value, int maxLength) {
        if (!TextUtils.isEmpty(value)) {
            if (value.length() >= maxLength) {
                return value.substring(0, maxLength) + "...";
            }
        }
        return value;
    }

    public interface OnClickHandler {
        void click(CollectionGridItemView collectionGridItemView,
                   Component childComponent,
                   ContentDatum data, int clickPosition);

        void play(Component childComponent, ContentDatum data);
    }

    public static class ItemContainer {
        View childView;
        Component component;

        public View getChildView() {
            return childView;
        }

        public Component getComponent() {
            return component;
        }

        public static class Builder {
            private ItemContainer itemContainer;

            public Builder() {
                itemContainer = new ItemContainer();
            }

            Builder childView(View childView) {
                itemContainer.childView = childView;
                return this;
            }

            public Builder component(Component component) {
                itemContainer.component = component;
                return this;
            }

            public ItemContainer build() {
                return itemContainer;
            }
        }
    }

    private static class GradientTransformation extends BitmapTransformation {
        private final String ID;

        private int imageWidth, imageHeight;
        private AppCMSPresenter appCMSPresenter;
        private String imageUrl;

        public GradientTransformation(int imageWidth,
                                      int imageHeight,
                                      AppCMSPresenter appCMSPresenter,
                                      String imageUrl) {
            this.imageWidth = imageWidth;
            this.imageHeight = imageHeight;
            this.appCMSPresenter = appCMSPresenter;
            this.imageUrl = imageUrl;
            this.ID = imageUrl;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof GradientTransformation;
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
            try {
                byte[] ID_BYTES = ID.getBytes(STRING_CHARSET_NAME);
                messageDigest.update(ID_BYTES);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "Could not update disk cache key: " + e.getMessage());
            }
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform,
                                   int outWidth, int outHeight) {
            int width = toTransform.getWidth();
            int height = toTransform.getHeight();

            boolean scaleImageUp = false;

            Bitmap sourceWithGradient;
            if (width < imageWidth &&
                    height < imageHeight) {
                scaleImageUp = true;
                float widthToHeightRatio =
                        (float) width / (float) height;
                width = (int) (imageHeight * widthToHeightRatio);
                height = imageHeight;
                sourceWithGradient =
                        Bitmap.createScaledBitmap(toTransform,
                                width,
                                height,
                                false);
            } else {
                sourceWithGradient =
                        Bitmap.createBitmap(width,
                                height,
                                Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(sourceWithGradient);
            if (!scaleImageUp) {
                canvas.drawBitmap(toTransform, 0, 0, null);
            }

            Paint paint = new Paint();
            LinearGradient shader = new LinearGradient(0,
                    0,
                    0,
                    height,
                    0xFFFFFFFF,
                    0xFF000000,
                    Shader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
            canvas.drawRect(0, 0, width, height, paint);
            paint = null;
            return sourceWithGradient;
        }

        @Override
        public int hashCode() {
            return ID.hashCode();
        }
    }

    public void inVisibleIfSeries(final ContentDatum data, final View view) {
        if ((data != null &&
                data.getGist() != null &&
                data.getGist().getContentType() != null &&
                data.getGist().getContentType().equalsIgnoreCase("SERIES") )
        || data.getGist().getRuntime() == 0) {
            view.setVisibility(GONE);
        } /*else {
            view.setVisibility(VISIBLE);
        }*/
    }

}
