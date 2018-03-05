package com.viewlift.views.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.utilities.ImageUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/*
 * Created by viewlift on 5/5/17.
 */

@SuppressLint("ViewConstructor")
public class CollectionGridItemView extends BaseView {
    private static final String TAG = "CollectionItemView";

    private final Layout parentLayout;
    private final boolean userParentLayout;
    private final Component component;
    protected int defaultWidth;
    protected int defaultHeight;
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
                                  int defaultWidth,
                                  int defaultHeight,
                                  boolean createMultipleContainersForChildren,
                                  boolean createRoundedCorners) {
        super(context);
        this.parentLayout = parentLayout;
        this.userParentLayout = useParentLayout;
        this.component = component;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.viewsToUpdateOnClickEvent = new ArrayList<>();
        this.createMultipleContainersForChildren = createMultipleContainersForChildren;
        this.createRoundedCorners = createRoundedCorners;
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
        int paddingRight = 0;
        if (component.getStyles() != null) {
            paddingRight = (int) convertHorizontalValue(getContext(), component.getStyles().getPadding());
            setPadding(0, 0, paddingRight, 0);
        } else if (getTrayPadding(getContext(), component.getLayout()) != -1.0f) {
            int trayPadding = (int) getTrayPadding(getContext(), component.getLayout());
            paddingRight = (int) convertHorizontalValue(getContext(), trayPadding);
            setPadding(0, 0, paddingRight, 0);
        }
        int horizontalMargin = paddingRight;
        horizontalMargin = (int) convertHorizontalValue(getContext(), getHorizontalMargin(getContext(), parentLayout));
        int verticalMargin = (int) convertVerticalValue(getContext(), getVerticalMargin(getContext(), parentLayout, height, 0));
        if (verticalMargin < 0) {
            verticalMargin = (int) convertVerticalValue(getContext(), getYAxis(getContext(), getLayout(), 0));
        }
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
        } else {
            childrenContainer = new CardView(getContext());
            CardView.LayoutParams childContainerLayoutParams =
                    new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
            childrenContainer.setLayoutParams(childContainerLayoutParams);

            if (createRoundedCorners) {
                ((CardView) childrenContainer).setRadius(14);
                setBackgroundResource(android.R.color.transparent);
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
            if (getContext().getString(R.string.app_cms_page_carousel_image_key).equalsIgnoreCase(itemContainer.component.getKey())) {
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

    public void bindChild(Context context,
                          View view,
                          final ContentDatum data,
                          Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                          final OnClickHandler onClickHandler,
                          final String componentViewType,
                          int themeColor,
                          AppCMSPresenter appCMSPresenter, int position) {

        final Component childComponent = matchComponentToView(view);
        if (childComponent != null) {
            view.setOnClickListener(v -> onClickHandler.click(CollectionGridItemView.this,
                    childComponent, data, position));
            boolean bringToFront = true;
            AppCMSUIKeyType appCMSUIcomponentViewType = jsonValueKeyMap.get(componentViewType);
            AppCMSUIKeyType componentType = jsonValueKeyMap.get(childComponent.getType());
            AppCMSUIKeyType componentKey = jsonValueKeyMap.get(childComponent.getKey());
            if (componentType == AppCMSUIKeyType.PAGE_IMAGE_KEY) {
                if (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_BADGE_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_PLAY_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_BADGE_IMAGE) {
                    int childViewWidth = (int) getViewWidth(getContext(),
                            childComponent.getLayout(),
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    int childViewHeight = (int) getViewHeight(getContext(),
                            childComponent.getLayout(),
                            getViewHeight(getContext(), component.getLayout(), ViewGroup.LayoutParams.WRAP_CONTENT));

                    if (userParentLayout) {
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

                    if (data.getGist() != null &&
                            data.getGist().getContentType() != null &&
                            data.getGist().getContentType().equalsIgnoreCase(context.getString(R.string.content_type_audio))
                            && appCMSUIcomponentViewType == AppCMSUIKeyType.PAGE_PLAYLIST_MODULE_KEY) {
                        if (data.getGist() != null &&
                                data.getGist().getImageGist() != null &&
                                data.getGist().getImageGist().get_1x1() != null && (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY)) {
                            String imageUrl = data.getGist().getImageGist().get_1x1();
                            if (appCMSPresenter.isVideoDownloaded(data.getGist().getId())) {
                                if (data.getGist().getVideoImageUrl() != null) {
                                    imageUrl = data.getGist().getVideoImageUrl();
                                }
                            }
                            int size = childViewWidth;
                            if (childViewWidth > childViewHeight) {
                                size = childViewHeight;
                            }

                            if (!ImageUtils.loadImage((ImageView) view, imageUrl) && context != null && appCMSPresenter != null && appCMSPresenter.getCurrentActivity() != null && !appCMSPresenter.getCurrentActivity().isFinishing()) {

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
                                ((ImageView) view).setLayoutParams(llParams);
                                Glide.with(context.getApplicationContext())
                                        .load(imageUrl)
//                                        .override(size,size)
                                        .into(((ImageView) view));
                            }
                        }
                    } else if (childViewHeight > childViewWidth &&
                            childViewHeight > 0 &&
                            childViewWidth > 0 &&
                            !TextUtils.isEmpty(data.getGist().getPosterImageUrl()) &&
                            (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY ||
                                    componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY)) {
                        bringToFront = false;
                        String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                data.getGist().getPosterImageUrl(),
                                childViewWidth,
                                childViewHeight);

                        if (appCMSPresenter.isVideoDownloaded(data.getGist().getId())) {
                            if (data.getGist().getVideoImageUrl() != null) {
                                imageUrl = data.getGist().getVideoImageUrl();
                            }
                        }
                        //Log.d(TAG, "Loading image: " + imageUrl);
                        try {
                            if (!ImageUtils.loadImage((ImageView) view, imageUrl) && appCMSPresenter != null && appCMSPresenter.getCurrentActivity() != null) {
                                Glide.with(context)
                                        .load(imageUrl)
                                        .override(childViewWidth, childViewHeight)
                                        .centerCrop()
                                        .into((ImageView) view);
                            }
                        } catch (Exception e) {
                            //
                        }
                    } else if (childViewHeight > 0 &&
                            childViewWidth > 0 &&
                            data != null &&
                            data.getGist() != null &&
                            data.getGist().getVideoImageUrl() != null &&
                            !TextUtils.isEmpty(data.getGist().getVideoImageUrl()) &&
                            (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY ||
                                    componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY)) {
                        bringToFront = false;
                        String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                data.getGist().getVideoImageUrl(),
                                childViewWidth,
                                childViewHeight);
                        if (appCMSPresenter.isVideoDownloaded(data.getGist().getId())) {
                            if (data.getGist().getVideoImageUrl() != null) {
                                imageUrl = data.getGist().getVideoImageUrl();
                            }
                        }
                        //Log.d(TAG, "Loading image: " + imageUrl);
                        try {
                            if (!ImageUtils.loadImage((ImageView) view, imageUrl) && appCMSPresenter != null && appCMSPresenter.getCurrentActivity() != null) {
                                Glide.with(context)
                                        .load(imageUrl)
                                        .override(childViewWidth, childViewHeight)
                                        .centerCrop()
                                        .into((ImageView) view);
                            }
                        } catch (Exception e) {
                            //
                        }
                    } else if (data != null &&
                            data.getGist() != null &&
                            data.getGist().getVideoImageUrl() != null &&
                            !TextUtils.isEmpty(data.getGist().getVideoImageUrl()) &&
                            componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_IMAGE_KEY) {
                        bringToFront = false;
                        int deviceWidth = getContext().getResources().getDisplayMetrics().widthPixels;
                        final String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                data.getGist().getVideoImageUrl(),
                                childViewWidth,
                                childViewHeight);
                        //Log.d(TAG, "Loading image: " + imageUrl);
                        try {
                            final int imageWidth = deviceWidth;
                            final int imageHeight = childViewHeight;

                            if (!ImageUtils.loadImageWithLinearGradient((ImageView) view,
                                    imageUrl,
                                    imageWidth,
                                    imageHeight) && context != null && appCMSPresenter != null && appCMSPresenter.getCurrentActivity() != null) {
                                Glide.with(context)
                                        .load(imageUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .transform(new BitmapTransformation(context) {
                                            @Override
                                            public String getId() {
                                                return imageUrl;
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
                                                toTransform.recycle();
                                                paint = null;
                                                return sourceWithGradient;
                                            }
                                        })
                                        .into((ImageView) view);
                            }
                        } catch (IllegalArgumentException e) {
                            //Log.e(TAG, "Failed to load image with Glide: " + e.toString());
                        }
                    } else if (data.getGist() != null &&
                            data.getGist().getImageGist() != null &&
                            data.getGist().getBadgeImages() != null &&
                            data.getGist().getImageGist().get_3x4() != null &&
                            data.getGist().getBadgeImages().get_3x4() != null &&
                            componentKey == AppCMSUIKeyType.PAGE_BADGE_IMAGE_KEY &&
                            0 < childViewWidth &&
                            0 < childViewHeight) {
                        String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                data.getGist().getBadgeImages().get_3x4(),
                                childViewWidth,
                                childViewHeight);

                        if (!ImageUtils.loadImage((ImageView) view, imageUrl) && appCMSPresenter != null && appCMSPresenter.getCurrentActivity() != null) {
                            Glide.with(context)
                                    .load(imageUrl)
                                    .override(childViewWidth, childViewHeight)
                                    .into((ImageView) view);
                        }
                    } else if (data.getGist() != null &&
                            data.getGist().getImageGist() != null & childViewHeight < childViewWidth &&
                            childViewHeight > 0 &&
                            childViewWidth > 0 &&
                            data.getGist().getImageGist().get_16x9() != null) {
                        String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                data.getGist().getImageGist().get_16x9(),
                                childViewWidth,
                                childViewHeight);
                        if (AppCMSUIKeyType.PAGE_AUDIO_TRAY_MODULE_KEY == jsonValueKeyMap.get(componentViewType)) {
                            if (data.getGist().getImageGist().get_1x1() == null) {
                                imageUrl = "";
                            } else {
                                imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                        data.getGist().getImageGist().get_1x1(),
                                        childViewWidth,
                                        childViewHeight);
                            }
                            ((ImageView) view).setLayoutParams(new LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                        }

                        if (appCMSPresenter.isVideoDownloaded(data.getGist().getId())) {
                            if (data.getGist().getVideoImageUrl() != null) {
                                imageUrl = data.getGist().getVideoImageUrl();
                            }
                        }
                        if (!ImageUtils.loadImage((ImageView) view, imageUrl) &&
                                context != null && appCMSPresenter != null &&
                                appCMSPresenter.getCurrentActivity() != null && !appCMSPresenter.getCurrentActivity().isFinishing()) {
                            Glide.with(context.getApplicationContext())
                                    .load(imageUrl)
                                    .override(childViewWidth, childViewHeight)
                                    .into((ImageView) view);
                        }
                    }
                    bringToFront = false;
                }
            } else if (componentType == AppCMSUIKeyType.PAGE_BUTTON_KEY) {
                if (componentKey == AppCMSUIKeyType.PAGE_PLAY_IMAGE_KEY) {
                    ((TextView) view).setText("");
                } else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_PURCHASE_BUTTON_KEY) {
                    ((TextView) view).setText(childComponent.getText());
                    view.setBackgroundColor(ContextCompat.getColor(getContext(),
                            R.color.disabledButtonColor));
                    viewsToUpdateOnClickEvent.add(view);
                } else if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_DOWNLOAD_BUTTON_KEY) {
                    String userId = appCMSPresenter.getLoggedInUser();

                    try {
                        Map<String, ViewCreator.UpdateDownloadImageIconAction> updateDownloadImageIconActionMap =
                                appCMSPresenter.getUpdateDownloadImageIconActionMap();

                        ViewCreator.UpdateDownloadImageIconAction updateDownloadImageIconAction =
                                updateDownloadImageIconActionMap.get(data.getGist().getId());
                        if (updateDownloadImageIconAction == null) {
                            updateDownloadImageIconAction = new ViewCreator.UpdateDownloadImageIconAction((ImageButton) view, appCMSPresenter,
                                    data, userId);
                            updateDownloadImageIconActionMap.put(data.getGist().getId(), updateDownloadImageIconAction);
                        }

                        updateDownloadImageIconAction.updateDownloadImageButton((ImageButton) view);

                        appCMSPresenter.getUserVideoDownloadStatus(
                                data.getGist().getId(), updateDownloadImageIconAction, userId);
                    } catch (Exception e) {

                    }
                } else if (componentKey == AppCMSUIKeyType.PAGE_AUDIO_DOWNLOAD_BUTTON_KEY) {
                    view.setOnClickListener(v -> onClickHandler.click(CollectionGridItemView.this,
                            childComponent, data, position));
                    if (appCMSPresenter.isVideoDownloaded(data.getGist().getId())) {
                        ((ImageButton) view).setImageResource(R.drawable.ic_downloaded);
                        view.setOnClickListener(null);
                    } else if (appCMSPresenter.isVideoDownloading(data.getGist().getId())) {
                        appCMSPresenter.updateDownloadingStatus(
                                data.getGist().getId(),
                                (ImageButton) view,
                                appCMSPresenter,
                                new ViewCreator.UpdateDownloadImageIconAction(
                                        (ImageButton) view,
                                        appCMSPresenter,
                                        data,
                                        appCMSPresenter.getLoggedInUser()),
                                appCMSPresenter.getLoggedInUser(),
                                false);
                        view.setOnClickListener(null);
                    }
                } else {
                    view.setOnClickListener(v -> onClickHandler.click(CollectionGridItemView.this,
                            childComponent, data, position));
                }
            } else if (componentType == AppCMSUIKeyType.PAGE_GRID_OPTION_KEY) {
                view.setOnClickListener(v ->
                        onClickHandler.click(CollectionGridItemView.this,
                                childComponent, data, position));
            } else if (componentType == AppCMSUIKeyType.PAGE_LABEL_KEY &&
                    view instanceof TextView) {
                if (TextUtils.isEmpty(((TextView) view).getText())) {
                    if (componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_TITLE_KEY &&
                            !TextUtils.isEmpty(data.getGist().getTitle())) {
                        ((TextView) view).setText(data.getGist().getTitle());
                        ((TextView) view).setMaxLines(1);
                        ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                    } else if (componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_INFO_KEY) {
                        String artist = "";
                        if (data.getGist().getMediaType() != null && data.getGist().getMediaType().equalsIgnoreCase("AUDIO") && data.getCreditBlocks() != null && data.getCreditBlocks().size() > 0 && data.getCreditBlocks().get(0).getCredits() != null && data.getCreditBlocks().get(0).getCredits().size() > 0 && data.getCreditBlocks().get(0).getCredits().get(0).getTitle() != null) {
                            for (int i = 0; i < data.getCreditBlocks().size(); i++) {
                                if (data.getCreditBlocks().get(i).getTitle().equalsIgnoreCase("Starring")) {
                                    if (data.getCreditBlocks().get(i).getCredits() != null && data.getCreditBlocks().get(i).getCredits().size() > 0 && data.getCreditBlocks().get(i).getCredits().get(0).getTitle() != null) {
                                        artist = data.getCreditBlocks().get(i).getCredits().get(0).getTitle();
                                        break;
                                    }
                                }
                            }
                            ((TextView) view).setText(artist);
                        } else if (data.getSeason() != null && 0 < data.getSeason().size()) {
                            ViewCreator.setViewWithShowSubtitle(getContext(), data, view, true);
                        } else {
                            ViewCreator.setViewWithSubtitle(getContext(), data, view);
                        }

                    } else if (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_TITLE_KEY) {
                        ((TextView) view).setText(data.getGist().getTitle());
                    } else if (componentKey == AppCMSUIKeyType.PAGE_DELETE_DOWNLOAD_VIDEO_SIZE_KEY) {
                        ((TextView) view).setText(appCMSPresenter.getDownloadedFileSize(data.getGist().getId()));
                    } else if (componentKey == AppCMSUIKeyType.PAGE_HISTORY_WATCHED_TIME_KEY) {
                        ((TextView) view).setText(appCMSPresenter.getLastWatchedTime(data));
                    } else if (componentKey == AppCMSUIKeyType.PAGE_HISTORY_DURATION_KEY ||
                            componentKey == AppCMSUIKeyType.PAGE_DOWNLOAD_DURATION_KEY ||
                            componentKey == AppCMSUIKeyType.PAGE_WATCHLIST_DURATION_KEY) {
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
                    } else if (componentKey == AppCMSUIKeyType.PAGE_AUDIO_DURATION_KEY) {
                        String time = appCMSPresenter.audioDuration((int) data.getGist().getRuntime());
                        ((TextView) view).setText(time);
                    } else if (componentKey == AppCMSUIKeyType.PAGE_GRID_THUMBNAIL_INFO) {
                        String thumbInfo = getDateFormat(Long.parseLong(data.getGist().getPublishDate()), "MMM dd");
                        ((TextView) view).setText(thumbInfo);
                    } else if (componentKey == AppCMSUIKeyType.PAGE_API_TITLE ||
                            componentKey == AppCMSUIKeyType.PAGE_EPISODE_TITLE_KEY) {
                        if (data.getGist() != null && data.getGist().getTitle() != null) {
                            ((TextView) view).setText(data.getGist().getTitle());
                            ((TextView) view).setSingleLine(true);
                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                        }
                    } else if (componentKey == AppCMSUIKeyType.PAGE_HISTORY_DESCRIPTION_KEY ||
                            componentKey == AppCMSUIKeyType.PAGE_WATCHLIST_DESCRIPTION_KEY ||
                            componentKey == AppCMSUIKeyType.PAGE_DOWNLOAD_DESCRIPTION_KEY) {
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
                                            Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor()),
                                            false);
                            titleTextVto.addOnGlobalLayoutListener(viewCreatorTitleLayoutListener);
                        } catch (Exception e) {
                        }
                    } else if (componentKey == AppCMSUIKeyType.PAGE_PLAYLIST_AUDIO_ARTIST_TITLE) {
                        String artist = appCMSPresenter.getArtistNameFromCreditBlocks(data.getCreditBlocks());
                        ((TextView) view).setText(artist);
                        ((TextView) view).setTextColor(Color.parseColor(childComponent.getTextColor()));

                    } else if (componentKey == AppCMSUIKeyType.PAGE_API_DESCRIPTION) {
                        ((TextView) view).setText(data.getGist().getDescription());
                        try {
                            ViewTreeObserver titleTextVto = view.getViewTreeObserver();
                            ViewCreatorMultiLineLayoutListener viewCreatorTitleLayoutListener =
                                    new ViewCreatorMultiLineLayoutListener((TextView) view,
                                            data.getGist().getTitle(),
                                            data.getGist().getDescription(),
                                            appCMSPresenter,
                                            false,
                                            Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor()),
                                            true);
                            titleTextVto.addOnGlobalLayoutListener(viewCreatorTitleLayoutListener);
                        } catch (Exception e) {

                        }
                    } else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_TITLE_KEY) {
                        ((TextView) view).setText(data.getName());
                        if (componentType.equals(AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY) ||
                                componentType.equals(AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY)) {
                            ((TextView) view).setTextColor(themeColor);
                        } else {
                            ((TextView) view).setTextColor(Color.parseColor(childComponent.getTextColor()));
                        }
                    } else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_PRICEINFO_KEY) {
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
                        } else {
                            double recurringPaymentAmount = data.getPlanDetails()
                                    .get(planIndex).getRecurringPaymentAmount();
                            String formattedRecurringPaymentAmount = context.getString(R.string.cost_with_fraction,
                                    recurringPaymentAmount);
                            if (recurringPaymentAmount - (int) recurringPaymentAmount == 0) {
                                formattedRecurringPaymentAmount = context.getString(R.string.cost_without_fraction,
                                        recurringPaymentAmount);
                            }

                            StringBuilder stringBuilder = new StringBuilder();
                            if (currency != null) {
                                stringBuilder.append(currency.getSymbol());
                            }

                            stringBuilder.append(formattedRecurringPaymentAmount);
                            if (appCMSUIcomponentViewType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY) {
                                if (data.getRenewalCycleType().contains(context.getString(R.string.app_cms_plan_renewal_cycle_type_monthly))) {
                                    stringBuilder.append(" ");
                                    stringBuilder.append(context.getString(R.string.forward_slash));
                                    stringBuilder.append(" ");
                                    stringBuilder.append(context.getString(R.string.plan_type_month));
                                }
                                if (data.getRenewalCycleType().contains(context.getString(R.string.app_cms_plan_renewal_cycle_type_yearly))) {
                                    stringBuilder.append(" ");
                                    stringBuilder.append(context.getString(R.string.forward_slash));
                                    stringBuilder.append(" ");
                                    stringBuilder.append(context.getString(R.string.plan_type_year));
                                }
                                if (data.getRenewalCycleType().contains(context.getString(R.string.app_cms_plan_renewal_cycle_type_daily))) {
                                    stringBuilder.append(" ");
                                    stringBuilder.append(context.getString(R.string.forward_slash));
                                    stringBuilder.append(" ");
                                    stringBuilder.append(context.getString(R.string.plan_type_day));
                                }
                            }
                            ((TextView) view).setText(stringBuilder.toString());
                            ((TextView) view).setPaintFlags(((TextView) view).getPaintFlags());
                        }

                        ((TextView) view).setTextColor(Color.parseColor(
                                childComponent.getTextColor()));

                    } else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_BESTVALUE_KEY) {
                        ((TextView) view).setText(childComponent.getText());
                        ((TextView) view).setTextColor(Color.parseColor(
                                childComponent.getTextColor()));
                    }
                }
            } else if (componentType == AppCMSUIKeyType.PAGE_PLAN_META_DATA_VIEW_KEY) {
                if (view instanceof ViewPlansMetaDataView) {
                    ((ViewPlansMetaDataView) view).setData(data);
                }

                if (view instanceof SubscriptionMetaDataView) {
                    ((SubscriptionMetaDataView) view).setData(data);
                }
            } else if (componentType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY ||
                    componentType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY) {
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }

            if (shouldShowView(childComponent) && bringToFront) {
                view.bringToFront();
            }
            view.forceLayout();
        }
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

    public interface OnClickHandler {
        void click(CollectionGridItemView collectionGridItemView,
                   Component childComponent,
                   ContentDatum data, int clickPosition);

        void play(Component childComponent, ContentDatum data);
    }

    public static class ItemContainer {
        View childView;
        Component component;

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

        public View getChildView() {
            return childView;
        }

        public Component getComponent() {
            return component;
        }
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

}
