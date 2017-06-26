package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Mobile;
import com.viewlift.models.data.appcms.ui.page.TabletLandscape;
import com.viewlift.models.data.appcms.ui.page.TabletPortrait;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by viewlift on 5/5/17.
 */

public class CollectionGridItemView extends BaseView {
    private static final String TAG = "CollectionItemView";

    private final Layout parentLayout;
    private final boolean userParentLayout;
    private final Component component;
    private List<ItemContainer> childItems;

    protected int defaultWidth;
    protected int defaultHeight;

    public static class ItemContainer {
        View childView;
        Component component;
        public static class Builder {
            private ItemContainer itemContainer;
            public Builder() {
                itemContainer = new ItemContainer();
            }
            public Builder childView(View childView) {
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

    public interface OnClickHandler {
        void click(Component childComponent, ContentDatum data);
        void play(Component childComponent, ContentDatum data);
    }

    @Inject
    public CollectionGridItemView(Context context,
                                  Layout parentLayout,
                                  boolean useParentLayout,
                                  Component component,
                                  int defaultWidth,
                                  int defaultHeight) {
        super(context);
        this.parentLayout = parentLayout;
        this.userParentLayout = useParentLayout;
        this.component = component;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
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
        if (component.getStyles() != null) {
            int margin = (int) convertHorizontalValue(getContext(), component.getStyles().getPadding());
            MarginLayoutParams marginLayoutParams = new MarginLayoutParams(width, height);
            marginLayoutParams.setMargins(0, 0, margin, 0);
            layoutParams = new FrameLayout.LayoutParams(marginLayoutParams);
        } else if (getTrayPadding(getContext(), component.getLayout()) != -1.0f) {
            int trayPadding = (int) getTrayPadding(getContext(), component.getLayout());
            int margin = (int) convertHorizontalValue(getContext(), trayPadding);
            MarginLayoutParams marginLayoutParams = new MarginLayoutParams(width, height);
            marginLayoutParams.setMargins(0, 0, margin, 0);
            layoutParams = new FrameLayout.LayoutParams(marginLayoutParams);
        } else {
            layoutParams = new FrameLayout.LayoutParams(width, height);
        }
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
        childrenContainer = new CardView(getContext());
        CardView.LayoutParams childContainerLayoutParams =
                new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        childrenContainer.setLayoutParams(childContainerLayoutParams);
        childrenContainer.setBackgroundResource(android.R.color.transparent);
        this.addView(childrenContainer);
        return childrenContainer;
    }

    public void addChild(ItemContainer itemContainer) {
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

    public int getNumberOfChildren() {
        return childItems.size();
    }

    public void bindChild(Context context,
                          View view,
                          final ContentDatum data,
                          Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                          OnClickHandler onClickHandler) {
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

                    if (childViewHeight > childViewWidth &&
                            childViewHeight > 0 &&
                            childViewWidth > 0 &&
                            !TextUtils.isEmpty(data.getGist().getPosterImageUrl())) {
                        if (isLandscape(getContext())) {
                            Picasso.with(context)
                                    .load(data.getGist().getPosterImageUrl())
                                    .resize(childViewWidth, childViewHeight)
                                    .centerCrop()
                                    .onlyScaleDown()
                                    .into((ImageView) view);
                        } else {
                            Picasso.with(context)
                                    .load(data.getGist().getPosterImageUrl())
                                    .resize(childViewWidth, childViewHeight)
                                    .centerCrop()
                                    .onlyScaleDown()
                                    .into((ImageView) view);
                        }
                    } else if (childViewHeight > 0 &&
                            childViewWidth > 0 &&
                            !TextUtils.isEmpty(data.getGist().getVideoImageUrl())) {
                        if (isLandscape(getContext())) {
                            Picasso.with(context)
                                    .load(data.getGist().getVideoImageUrl())
                                    .into((ImageView) view);
                        } else {
                            Picasso.with(context)
                                    .load(data.getGist().getVideoImageUrl())
                                    .resize(childViewWidth, childViewHeight)
                                    .centerCrop()
                                    .onlyScaleDown()
                                    .into((ImageView) view);
                        }
                    } else if (!TextUtils.isEmpty(data.getGist().getVideoImageUrl())) {
                        int deviceWidth = getContext().getResources().getDisplayMetrics().widthPixels;
                        Picasso.with(context)
                                .load(data.getGist().getVideoImageUrl())
                                .resize(deviceWidth, childViewHeight)
                                .centerCrop()
                                .transform(new Transformation() {
                                    @Override
                                    public Bitmap transform(Bitmap source) {
                                        int width = source.getWidth();
                                        int height = source.getHeight();
                                        Bitmap sourceWithGradient =
                                                Bitmap.createBitmap(width,
                                                        height,
                                                        Bitmap.Config.ARGB_8888);
                                        Canvas canvas = new Canvas(sourceWithGradient);
                                        canvas.drawBitmap(source, 0, 0, null);
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
                                        source.recycle();
                                        paint = null;
                                        return sourceWithGradient;
                                    }

                                    @Override
                                    public String key() {
                                        return data.getGist().getVideoImageUrl();
                                    }
                                })
                                .into((ImageView) view);
                    }
                    bringToFront = false;
                }
            } else if (componentType == AppCMSUIKeyType.PAGE_BUTTON_KEY) {

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
        Component result = null;
        for (ItemContainer itemContainer : childItems) {
            if (itemContainer.childView == view) {
                return itemContainer.component;
            }
        }
        return result;
    }

    protected float getGridWidth(Context context, Layout layout, int defaultWidth) {
        if (isTablet(context)) {
            if (isLandscape(context)) {
                TabletLandscape tabletLandscape = layout.getTabletLandscape();
                float width = tabletLandscape.getGridWidth() != null ? tabletLandscape.getGridWidth() : -1.0f;
                if (width != -1.0f) {
                    return DEVICE_WIDTH * (width / STANDARD_TABLET_HEIGHT_PX);
                }
            } else {
                TabletPortrait tabletPortrait = layout.getTabletPortrait();
                float width = tabletPortrait.getGridWidth() != null ? tabletPortrait.getGridWidth() : -1.0f;
                if (width != -1.0f) {
                    return DEVICE_WIDTH * (width / STANDARD_TABLET_WIDTH_PX);
                }
            }
        } else {
            Mobile mobile = layout.getMobile();
            float width = mobile.getGridWidth() != null ? mobile.getGridWidth() : -1.0f;
            if (width != -1.0f) {
                return DEVICE_WIDTH * (width / STANDARD_MOBILE_WIDTH_PX);
            }
        }
        return defaultWidth;
    }

    protected float getGridHeight(Context context, Layout layout, int defaultHeight) {
        if (isTablet(context)) {
            if (isLandscape(context)) {
                TabletLandscape tabletLandscape = layout.getTabletLandscape();
                float height = tabletLandscape.getGridHeight() != null ? tabletLandscape.getGridHeight() : -1.0f;
                if (height != -1.0f) {
                    return DEVICE_HEIGHT * (height / STANDARD_TABLET_WIDTH_PX);
                }
            } else {
                TabletPortrait tabletPortrait = layout.getTabletPortrait();
                float height = tabletPortrait.getGridHeight() != null ? tabletPortrait.getGridHeight() : -1.0f;
                if (height != -1) {
                    return DEVICE_HEIGHT * (height / STANDARD_TABLET_HEIGHT_PX);
                }
            }
        } else {
            Mobile mobile = layout.getMobile();
            float height = mobile.getGridHeight() != null ? mobile.getGridHeight().intValue() : -1;
            if (height != -1.0f) {
                return DEVICE_HEIGHT * (height / STANDARD_MOBILE_HEIGHT_PX);
            }
        }
        return defaultHeight;
    }

    private float getTrayPadding(Context context, Layout layout) {
        if (isTablet(context)) {
            if (isLandscape(context)) {
                if (layout.getTabletLandscape().getTrayPadding() != null) {
                    return layout.getTabletLandscape().getTrayPadding();
                }
            } else {
                if (layout.getTabletPortrait().getTrayPadding() != null) {
                    return layout.getTabletPortrait().getTrayPadding();
                }
            }
        } else {
            if (layout.getMobile().getTrayPadding() != null) {
                return layout.getMobile().getTrayPadding();
            }
        }

        return -1.0f;
    }
}
