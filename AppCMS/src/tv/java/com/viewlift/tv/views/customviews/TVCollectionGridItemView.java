package com.viewlift.tv.views.customviews;

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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.signature.StringSignature;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.tv.utility.Utils;
import com.viewlift.views.customviews.ViewCreator;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import static com.viewlift.tv.utility.Utils.getItemViewHeight;
import static com.viewlift.tv.utility.Utils.getItemViewWidth;
import static com.viewlift.tv.utility.Utils.getViewHeight;
import static com.viewlift.tv.utility.Utils.getViewWidth;

/**
 * Created by anas.azeem on 9/8/2017.
 * Owned by ViewLift, NYC
 */

public class TVCollectionGridItemView extends TVBaseView {
    private static final String TAG = "CollectionItemView";

    private final Layout parentLayout;
    private final boolean userParentLayout;
    private final Component component;
    private final String borderColor;
    protected int defaultWidth;
    protected int defaultHeight;
    private List<TVCollectionGridItemView.ItemContainer> childItems;
    private List<View> viewsToUpdateOnClickEvent;
    private boolean selectable;
    private CardView childrenContainer;

    @Inject
    public TVCollectionGridItemView(Context context,
                                  Layout parentLayout,
                                  boolean useParentLayout,
                                  Component component,
                                  int defaultWidth,
                                  int defaultHeight,
                                    String borderColor) {
        super(context);
        this.parentLayout = parentLayout;
        this.userParentLayout = useParentLayout;
        this.component = component;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.viewsToUpdateOnClickEvent = new ArrayList<>();
        this.borderColor = borderColor;
        init();
    }

    @Override
    public void init() {
        int width = (int) getItemViewWidth(getContext(),
                component.getLayout(),
                defaultWidth);
        int height = (int) getItemViewHeight(getContext(),
                component.getLayout(),
                defaultHeight);

        FrameLayout.LayoutParams layoutParams;
        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(width, height);
        /*marginLayoutParams.setMargins(horizontalMargin,
                verticalMargin,
                horizontalMargin,
                verticalMargin);*/
        layoutParams = new FrameLayout.LayoutParams(marginLayoutParams);
        setLayoutParams(layoutParams);
        childItems = new ArrayList<>();
        if (component.getComponents() != null) {
            initializeComponentHasViewList(component.getComponents().size());
        }
    }

    @Override
    protected ViewGroup createChildrenContainer() {
        childrenContainer = new CardView(getContext());
        CardView.LayoutParams childContainerLayoutParams =
                new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        childrenContainer.setLayoutParams(childContainerLayoutParams);
        childrenContainer.setBackgroundResource(android.R.color.transparent);
        addView(childrenContainer);
        return childrenContainer;
    }

    public void addChild(TVCollectionGridItemView.ItemContainer itemContainer) {
        if (childrenContainer == null) {
            createChildrenContainer();
        }
        childItems.add(itemContainer);
        childrenContainer.addView(itemContainer.childView);
    }


    public View getChild(int index) {
        if (0 <= index && index < childItems.size()) {
            return childItems.get(index).childView;
        }
        return null;
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
    public Layout getLayout() {
        return component.getLayout();
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
                          final TVCollectionGridItemView.OnClickHandler onClickHandler,
                          final AppCMSUIKeyType viewTypeKey) {
        final Component childComponent = matchComponentToView(view);
        if (childComponent != null) {
            boolean bringToFront = true;
            AppCMSUIKeyType componentType = jsonValueKeyMap.get(childComponent.getType());
            AppCMSUIKeyType componentKey = jsonValueKeyMap.get(childComponent.getKey());
            if (componentType == AppCMSUIKeyType.PAGE_IMAGE_KEY) {
                if (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_IMAGE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY) {
                    int childViewWidth = (int) getViewWidth(getContext(),
                            childComponent.getLayout(),
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    int childViewHeight = (int) getViewHeight(getContext(),
                            childComponent.getLayout(),
                            getViewHeight(getContext(),
                                    component.getLayout(),
                                    ViewGroup.LayoutParams.WRAP_CONTENT));

                    if (userParentLayout) {
                        childViewWidth = (int) getViewWidth(getContext(),
                                        parentLayout,
                                        defaultWidth);
                        childViewHeight = (int) getViewHeight(getContext(),
                                        parentLayout,
                                        defaultHeight);
                    }

                    if (childViewHeight > childViewWidth &&
                            childViewHeight > 0 &&
                            childViewWidth > 0 &&
                            !TextUtils.isEmpty(data.getGist().getPosterImageUrl())) {
                        String imageUrl =
                                context.getString(R.string.app_cms_image_with_resize_query,
                                data.getGist().getPosterImageUrl(),
                                childViewWidth,
                                childViewHeight);
                        Log.d(TAG, "Loading image: " + imageUrl);
                        Glide.with(context)
                                .load(imageUrl)
                                .override(childViewWidth, childViewHeight)
                                .centerCrop()
                                .into((ImageView) view);
                    } else if (childViewHeight > 0 &&
                            childViewWidth > 0 &&
                            !TextUtils.isEmpty(data.getGist().getVideoImageUrl())) {
                        String imageUrl =
                                context.getString(R.string.app_cms_image_with_resize_query,
                                data.getGist().getVideoImageUrl(),
                                childViewWidth,
                                childViewHeight);
                        Log.d(TAG, "Loading image: " + imageUrl);
                        Glide.with(context)
                                .load(imageUrl)
                                .override(childViewWidth, childViewHeight)
                                .centerCrop()
                                .into((ImageView) view);
                    } else if (!TextUtils.isEmpty(data.getGist().getVideoImageUrl())) {
                        int deviceWidth =
                                getContext().getResources().getDisplayMetrics().widthPixels;
                        final String imageUrl =
                                context.getString(R.string.app_cms_image_with_resize_query,
                                data.getGist().getVideoImageUrl(),
                                deviceWidth,
                                childViewHeight);
                        Log.d(TAG, "Loading image: " + imageUrl);
                        try {
                            final int imageWidth = deviceWidth;
                            final int imageHeight = childViewHeight;
                            StringBuilder imageMetaData = new StringBuilder();
                            imageMetaData.append(imageUrl);
                            imageMetaData.append(System.currentTimeMillis() / 60000);
                            Glide.with(context)
                                    .load(imageUrl)
                                    .placeholder(ContextCompat.getDrawable(context , R.drawable.video_image_placeholder))
                                    .signature(new StringSignature(imageMetaData.toString()))
                                    .transform(new BitmapTransformation(context) {
                                        @Override
                                        public String getId() {
                                            return imageUrl;
                                        }

                                        @Override
                                        protected Bitmap transform(BitmapPool pool,
                                                                   Bitmap toTransform,
                                                                   int outWidth,
                                                                   int outHeight) {
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
                                                canvas.drawBitmap(toTransform, 0, 0,
                                                        null);
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
                                            paint.setXfermode(new PorterDuffXfermode
                                                    (PorterDuff.Mode.MULTIPLY));
                                            canvas.drawRect(0, 0, width, height, paint);
                                            toTransform.recycle();
                                            paint = null;
                                            return sourceWithGradient;
                                        }
                                    })
                                    .into((ImageView) view);

                            view.setBackground(Utils.getTrayBorder(context, borderColor, childComponent));
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, "Failed to load image with Glide: " + e.toString());
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
                }
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickHandler.click(TVCollectionGridItemView.this,
                                childComponent,
                                data);
                    }
                });
            } else if (componentType == AppCMSUIKeyType.PAGE_LABEL_KEY) {
                if (TextUtils.isEmpty(((TextView) view).getText())) {
                    if (componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_TITLE_KEY &&
                            !TextUtils.isEmpty(data.getGist().getTitle())) {
                        ((TextView) view).setText(data.getGist().getTitle());
                        ((TextView) view).setMaxLines(1);
                        ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                    } else if (componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_INFO_KEY) {
                        ViewCreator.setViewWithSubtitle(getContext(), data, view);
                    } else if (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_TITLE_KEY) {
                        ((TextView) view).setText(data.getGist().getTitle());
                    } else if (componentKey == AppCMSUIKeyType.PAGE_WATCHLIST_DURATION_KEY) {
                        ((TextView) view).setText(String.valueOf(data.getGist().getRuntime() / 60));
                    } else if (componentKey == AppCMSUIKeyType.PAGE_API_TITLE) {
                        ((TextView) view).setText(data.getGist().getTitle());
                    } else if (componentKey == AppCMSUIKeyType.PAGE_API_DESCRIPTION) {
                        ((TextView) view).setText(data.getGist().getDescription());
                    } else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_TITLE_KEY) {
                        ((TextView) view).setText(data.getName());
                        ((TextView) view).setTextColor(Color.parseColor(
                                childComponent.getTextColor()));
                    } else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_PRICEINFO_KEY) {

                        int planIndex = 0;
                        for (int i = 0; i < data.getPlanDetails().size(); i++) {
                            if (data.getPlanDetails().get(i).getIsDefault()) {
                                planIndex = i;
                            }
                        }

                        Locale locale = null;

                        if (data.getPlanDetails() != null &&
                                !data.getPlanDetails().isEmpty() &&
                                data.getPlanDetails().get(planIndex) != null &&
                                data.getPlanDetails().get(planIndex).getCountryCode() != null) {
                            try {
                                locale = new Locale.Builder()
                                        .setRegion(data.getPlanDetails().get(planIndex)
                                                .getCountryCode())
                                        .build();
                            } catch (Exception e) {
                                Log.e(TAG, "Could not parse locale");
                            }
                        } else {
                            locale = getContext().getResources().getConfiguration().locale;
                        }

                        Currency currency = null;
                        if (currency != null) {
                            currency = Currency.getInstance(locale);
                        }

                        if (data.getPlanDetails().get(planIndex).getDiscountedPrice() != 0) {
                            StringBuilder stringBuilder = new StringBuilder();
                            if (currency != null) {
                                stringBuilder.append(currency.getSymbol());
                            }
                            stringBuilder.append(String.valueOf(data.getPlanDetails()
                                    .get(planIndex).getRecurringPaymentAmount()));

                            int strikeThroughLength = stringBuilder.length();
                            stringBuilder.append("     ");
                            if (currency != null) {
                                stringBuilder.append(currency.getSymbol());
                            }

                            stringBuilder.append(String.valueOf(data.getPlanDetails().get(0)
                                    .getDiscountedPrice()));

                            SpannableString spannableString =
                                    new SpannableString(stringBuilder.toString());
                            spannableString.setSpan(new StrikethroughSpan(), 0,
                                    strikeThroughLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            ((TextView) view).setText(spannableString);
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            if (currency != null) {
                                stringBuilder.append(currency.getSymbol());
                            }
                            stringBuilder.append(data.getPlanDetails().get(0)
                                    .getRecurringPaymentAmount());
                            ((TextView) view).setText(String.valueOf(stringBuilder.toString()));
                            ((TextView) view).setPaintFlags(((TextView) view).getPaintFlags());
                        }

                        ((TextView) view).setTextColor(Color.parseColor(
                                childComponent.getTextColor()));

                    } else if (componentKey == AppCMSUIKeyType.PAGE_PLAN_BESTVALUE_KEY) {
                        ((TextView) view).setText(childComponent.getText());
                        ((TextView) view).setTextColor(Color.parseColor(
                                childComponent.getTextColor()));
                    } else if (componentKey == AppCMSUIKeyType.PAGE_WATCHLIST_TITLE_LABEL) {
                        if (!TextUtils.isEmpty(data.getGist().getTitle())) {
                            ((TextView) view).setText(data.getGist().getTitle());
                            if (childComponent.getNumberOfLines() != 0) {
                                ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                            }
                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                        }
                        setTypeFace(context, jsonValueKeyMap, childComponent, ((TextView) view));
                        view.setFocusable(false);

                    } else if (componentKey == AppCMSUIKeyType.PAGE_WATCHLIST_DESCRIPTION_LABEL) {

                        if (!TextUtils.isEmpty(data.getGist().getDescription())) {
                            ((TextView) view).setText(data.getGist().getDescription());
                            if (childComponent.getNumberOfLines() != 0) {
                                ((TextView) view).setMaxLines(childComponent.getNumberOfLines());
                            }
                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                        }
                        setTypeFace(context, jsonValueKeyMap, childComponent, ((TextView) view));
                        view.setFocusable(false);
                    }
                }
            }
            view.forceLayout();
        }
    }

    public Component matchComponentToView(View view) {
        Component result = null;
        for (TVCollectionGridItemView.ItemContainer itemContainer : childItems) {
            if (itemContainer.childView == view) {
                return itemContainer.component;
            }
        }
        return result;
    }

    public List<View> getViewsToUpdateOnClickEvent() {
        return viewsToUpdateOnClickEvent;
    }

    public interface OnClickHandler {
        void click(TVCollectionGridItemView collectionGridItemView,
                   Component childComponent,
                   ContentDatum data);

        void play(Component childComponent, ContentDatum data);
    }

    public static class ItemContainer {
        View childView;
        Component component;

        public static class Builder {
            private TVCollectionGridItemView.ItemContainer itemContainer;

            public Builder() {
                itemContainer = new TVCollectionGridItemView.ItemContainer();
            }

            public TVCollectionGridItemView.ItemContainer.Builder childView(View childView) {
                itemContainer.childView = childView;
                return this;
            }

            public TVCollectionGridItemView.ItemContainer.Builder component(Component component) {
                itemContainer.component = component;
                return this;
            }

            public TVCollectionGridItemView.ItemContainer build() {
                return itemContainer;
            }
        }
    }
    public ViewGroup getChildrenContainer() {
        if (childrenContainer == null) {
            return createChildrenContainer();
        }
        return childrenContainer;
    }
}
