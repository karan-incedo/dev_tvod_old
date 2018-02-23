package com.viewlift.views.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
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
import com.bumptech.glide.request.target.Target;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.presenters.AppCMSPresenter;
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
                                  boolean createRoundedCorners) {
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
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
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
                          AppCMSPresenter appCMSPresenter) {
        final Component childComponent = matchComponentToView(view);

        AppCMSUIKeyType moduleType = jsonValueKeyMap.get(componentViewType);

        if (moduleType == null) {
            moduleType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        Map<String, ViewCreator.UpdateDownloadImageIconAction> updateDownloadImageIconActionMap =
                appCMSPresenter.getUpdateDownloadImageIconActionMap();

        if (childComponent != null) {
            boolean bringToFront = true;
            AppCMSUIKeyType componentType = jsonValueKeyMap.get(childComponent.getType());
            AppCMSUIKeyType componentKey = jsonValueKeyMap.get(childComponent.getKey());
            if (componentType == AppCMSUIKeyType.PAGE_IMAGE_KEY) {
                if (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_BADGE_IMAGE_KEY) {
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

                    if (0 < childViewWidth && 0 < childViewHeight) {
                        if (childViewWidth < childViewHeight) {
                            childViewHeight = (int) ((float) childViewWidth * 4.0f / 3.0f);
                        } else {
                            childViewHeight = (int) ((float) childViewWidth * 9.0f / 16.0f);
                        }
                    }

                    if (childViewHeight > childViewWidth &&
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
                        //Log.d(TAG, "Loading image: " + imageUrl);
                        try {
                            if (!ImageUtils.loadImage((ImageView) view, imageUrl, ImageLoader.ScaleType.START)) {
                                RequestOptions requestOptions = new RequestOptions()
                                        .override(childViewWidth, childViewHeight)
                                        .fitCenter();
//                                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                                Glide.with(context)
                                        .load(imageUrl)
                                        .apply(requestOptions)
                                        .into((ImageView) view);
                            }
                        } catch (Exception e) {
                            //
                        }
                    } else if (childViewHeight > 0 &&
                            childViewWidth > 0 &&
                            !TextUtils.isEmpty(data.getGist().getVideoImageUrl()) &&
                            (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY ||
                                    componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY)) {
                        bringToFront = false;
                        String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                data.getGist().getVideoImageUrl(),
                                childViewWidth,
                                childViewHeight);
                        //Log.d(TAG, "Loading image: " + imageUrl);
                        try {
                            if (!ImageUtils.loadImage((ImageView) view, imageUrl, ImageLoader.ScaleType.START)) {
                                RequestOptions requestOptions = new RequestOptions()
                                        .override(childViewWidth, childViewHeight)
                                        .fitCenter();
//                                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                                Glide.with(context)
                                        .load(imageUrl)
                                        .apply(requestOptions)
                                        .into((ImageView) view);
                            }
                        } catch (Exception e) {
                            //
                        }
                    } else if (!TextUtils.isEmpty(data.getGist().getVideoImageUrl()) &&
                            componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_IMAGE_KEY) {
                        bringToFront = false;
                        int deviceWidth = getContext().getResources().getDisplayMetrics().widthPixels;
                        final String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                data.getGist().getVideoImageUrl(),
                                deviceWidth,
                                childViewHeight);
                        //Log.d(TAG, "Loading image: " + imageUrl);
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
                    } else if (data.getGist().getImageGist() != null &&
                            data.getGist().getBadgeImages() != null &&
                            componentKey == AppCMSUIKeyType.PAGE_BADGE_IMAGE_KEY &&
                            0 < childViewWidth &&
                            0 < childViewHeight) {
                        if (childViewWidth < childViewHeight &&
                                data.getGist().getImageGist().get_3x4() != null &&
                                data.getGist().getBadgeImages().get_3x4() != null) {
                            final String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                    data.getGist().getBadgeImages().get_3x4(),
                                    childViewWidth,
                                    childViewHeight);

                            if (!ImageUtils.loadImage((ImageView) view, imageUrl, ImageLoader.ScaleType.START)) {
                                RequestOptions requestOptions = new RequestOptions()
                                        .override(childViewWidth, childViewHeight)
                                        .fitCenter();
//                                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                                Glide.with(context)
                                        .load(imageUrl)
                                        .apply(requestOptions)
                                        .into((ImageView) view);
                            }
                        } else if (data.getGist().getImageGist().get_16x9() != null &&
                                data.getGist().getBadgeImages().get_16x9() != null) {
                            final String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                    data.getGist().getBadgeImages().get_16x9(),
                                    childViewWidth,
                                    childViewHeight);

                            if (!ImageUtils.loadImage((ImageView) view, imageUrl, ImageLoader.ScaleType.START)) {
                                RequestOptions requestOptions = new RequestOptions()
                                        .override(childViewWidth, childViewHeight)
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
                    }

                    if (moduleType == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY) {
                        view.setOnClickListener(v -> onClickHandler.click(CollectionGridItemView.this,
                                childComponent, data));
                    }
                }
            } else if (componentType == AppCMSUIKeyType.PAGE_BUTTON_KEY) {
                if (componentKey == AppCMSUIKeyType.PAGE_PLAY_IMAGE_KEY) {
                    ((TextView) view).setText("");
                } else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_PURCHASE_BUTTON_KEY) {
                    ((TextView) view).setText(childComponent.getText());
                    view.setBackgroundColor(ContextCompat.getColor(getContext(),
                            R.color.disabledButtonColor));
                    viewsToUpdateOnClickEvent.add(view);
                } else if (componentKey == AppCMSUIKeyType.PAGE_RESUME_WATCHING_KEY) {
                    int progress = getPercentageWatched(appCMSPresenter, data);
                    Bitmap compoundDrawableImage;
                    int viewHeight = (int) BaseView.getLeftDrawableHeight(context,
                            childComponent.getLayout(),
                            0.0f);
                    if (0 < progress) {
                        ((TextView) view).setText(getContext().getString(R.string.app_cms_resume_lecture_text));
                        compoundDrawableImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_replay_white_24dp);
                    } else {
                        ((TextView) view).setText(getContext().getString(R.string.app_cms_play_lecture_text));
                        compoundDrawableImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_circle_outline_white_24dp);
                    }

                    Bitmap scaledDrawableImage = Bitmap.createScaledBitmap(compoundDrawableImage,
                            viewHeight,
                            viewHeight,
                            true);
                    Rect compoundBounds = new Rect(0, 0, viewHeight, viewHeight);
                    Drawable compoundDrawable =
                            new BitmapDrawable(context.getResources(), scaledDrawableImage);
                    compoundDrawable.setBounds(compoundBounds);
                    ((TextView) view).setCompoundDrawables(compoundDrawable,
                            null,
                            null,
                            null);
                    ((TextView) view).setCompoundDrawablePadding(16);

                    if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                        int textColor =
                                Color.parseColor(ViewCreator.getColor(context, childComponent.getTextColor()));
                        ((TextView) view).setTextColor(textColor);
                    }

                    if (!TextUtils.isEmpty(childComponent.getIconColor())) {
                        int iconColor =
                                Color.parseColor(ViewCreator.getColor(context, childComponent.getIconColor()));
                        ViewCreator.applyTintToCompoundDrawables((TextView) view, iconColor);
                    }

                    if (childComponent.getFontSize() > 0) {
                        ((TextView) view).setTextSize(childComponent.getFontSize());
                    } else if (BaseView.getFontSize(getContext(), childComponent.getLayout()) > 0) {
                        ((TextView) view).setTextSize(BaseView.getFontSize(getContext(), childComponent.getLayout()));
                    }

                    ((TextView) view).setGravity(Gravity.TOP | Gravity.START);
                    view.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                    ((Button) view).setAllCaps(false);
                }

                if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_DOWNLOAD_BUTTON_KEY) {
                    String userId = appCMSPresenter.getLoggedInUser();

                    try {
                        int radiusDifference = 5;

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
                } else if (componentKey == AppCMSUIKeyType.PAGE_RESUME_WATCHING_KEY) {
                    view.setOnClickListener(v -> onClickHandler.play(childComponent, data));
                } else {
                    view.setOnClickListener(v -> onClickHandler.click(CollectionGridItemView.this,
                            childComponent, data));
                }
            } else if (componentType == AppCMSUIKeyType.PAGE_GRID_OPTION_KEY) {
                view.setOnClickListener(v ->
                        onClickHandler.click(CollectionGridItemView.this,
                                childComponent, data));
            } else if (componentType == AppCMSUIKeyType.PAGE_LABEL_KEY &&
                    view instanceof TextView) {
                if (TextUtils.isEmpty(((TextView) view).getText())) {
                    if (componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_TITLE_KEY &&
                            !TextUtils.isEmpty(data.getGist().getTitle())) {
                        ((TextView) view).setText(data.getGist().getTitle());
                        ((TextView) view).setMaxLines(1);
                        ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                    } else if (componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_INFO_KEY) {
                        if (data.getSeason() != null && 0 < data.getSeason().size()) {
                            ViewCreator.setViewWithShowSubtitle(getContext(), data, view, true);
                        } else {
                            ViewCreator.setViewWithSubtitle(getContext(), data, view);
                        }
                    } else if (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_TITLE_KEY) {
                        ((TextView) view).setText(data.getGist().getTitle());
                        if (0 < childComponent.getMinLines()) {
                            ((TextView) view).setSingleLine(false);
                            ((TextView) view).setMinLines(childComponent.getMinLines());
                            ((TextView) view).setMaxLines(childComponent.getMinLines());
                        }
                        if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                            ((TextView) view).setTextColor(
                                    Color.parseColor(ViewCreator.getColor(context, childComponent.getTextColor())));
                        }
                    } else if (componentKey == AppCMSUIKeyType.PAGE_PERCENTAGE_WATCHED_KEY) {
                        int progress = getPercentageWatched(appCMSPresenter, data);
                        ((TextView) view).setText(getContext().getString(R.string.app_cms_percent_complete_text, progress));
                        if (!TextUtils.isEmpty(childComponent.getTextColor())) {
                            ((TextView) view).setTextColor(
                                    Color.parseColor(ViewCreator.getColor(context, childComponent.getTextColor())));
                        }
                    } else if (componentKey == AppCMSUIKeyType.PAGE_WATCHLIST_DURATION_KEY) {
                        ((TextView) view).setText(String.valueOf(data.getGist().getRuntime() / 60));

                        ViewCreator.UpdateDownloadImageIconAction updateDownloadImageIconAction =
                                updateDownloadImageIconActionMap.get(data.getGist().getId());
                        if (updateDownloadImageIconAction != null) {
                            view.setClickable(true);
                            view.setOnClickListener(updateDownloadImageIconAction.getAddClickListener());
                        }

                    } else if (componentKey == AppCMSUIKeyType.PAGE_WATCHLIST_DURATION_UNIT_KEY) {
                        ((TextView) view).setText(context.getResources().getQuantityString(R.plurals.min_duration_unit,
                                (int) (data.getGist().getRuntime() / 60)));

                        ViewCreator.UpdateDownloadImageIconAction updateDownloadImageIconAction =
                                updateDownloadImageIconActionMap.get(data.getGist().getId());
                        if (updateDownloadImageIconAction != null) {
                            view.setClickable(true);
                            view.setOnClickListener(updateDownloadImageIconAction.getAddClickListener());
                        }
                    } else if (componentKey == AppCMSUIKeyType.PAGE_GRID_THUMBNAIL_INFO) {
                        String thumbInfo = getDateFormat(data.getGist().getPublishDate(), "MMM dd");
                        ((TextView) view).setText(thumbInfo);
                    } else if (componentKey == AppCMSUIKeyType.PAGE_API_TITLE ||
                            componentKey == AppCMSUIKeyType.PAGE_EPISODE_TITLE_KEY) {
                        ((TextView) view).setText(data.getGist().getTitle());
                        ((TextView) view).setSingleLine(true);
                        ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
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
                                            Color.parseColor(appCMSPresenter.getAppTextColor()),
                                            true);
                            titleTextVto.addOnGlobalLayoutListener(viewCreatorTitleLayoutListener);
                        } catch (Exception e) {

                        }
                    } else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_TITLE_KEY) {
                        ((TextView) view).setText(data.getName());
                        if ("AC SelectPlan 02".equals(componentViewType)) {
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
            } else if (componentType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_KEY) {
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            } else if (componentType == AppCMSUIKeyType.PAGE_PROGRESS_VIEW_KEY) {
                if (view instanceof ProgressBar) {
                    int progress = getPercentageWatched(appCMSPresenter, data);
                    ((ProgressBar) view).setProgress(progress);
                    if (childComponent.isAlwaysVisible() || 0 < progress) {
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setVisibility(View.INVISIBLE);
                    }
                }
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

    private String getDateFormat(long timeMilliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMilliSeconds);
        return formatter.format(calendar.getTime());
    }

    public interface OnClickHandler {
        void click(CollectionGridItemView collectionGridItemView,
                   Component childComponent,
                   ContentDatum data);

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
    }

    private int getPercentageWatched(AppCMSPresenter appCMSPresenter,
                                     ContentDatum data) {
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
            } else {
                long watchedTime = historyData.getGist().getWatchedTime();
                long runTime = historyData.getGist().getRuntime();
                if (watchedTime > 0 && runTime > 0) {
                    long percentageWatched = (long) (((double) watchedTime / (double) runTime) * 100.0);
                    progress = (int) percentageWatched;
                }
            }
        }

        return progress;
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
}
