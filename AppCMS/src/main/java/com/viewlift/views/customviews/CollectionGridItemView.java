package com.viewlift.views.customviews;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
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
        public void click(Component childComponent, ContentDatum data);
    }

    @Inject
    public CollectionGridItemView(Context context,
                                  Component component,
                                  int defaultWidth,
                                  int defaultHeight) {
        super(context);
        this.component = component;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        init();
    }

    protected void init() {
        int width = getGridWidth(getContext(),
                component.getLayout(),
                getViewWidth(getContext(),
                        component.getLayout(),
                        defaultWidth));
        int height = getGridHeight(getContext(),
                component.getLayout(),
                getViewHeight(getContext(),
                        component.getLayout(),
                        defaultHeight));
        FrameLayout.LayoutParams layoutParams;
        if (component.getStyles() != null) {
            int margin = (int) convertDpToPixel(component.getStyles().getPadding(), getContext());
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
                          Map<AppCMSUIKeyType, String> jsonValueKeyMap,
                          final OnClickHandler onClickHandler) {
        final Component childComponent = matchComponentToView(view);
        if (childComponent != null) {
            boolean bringToFront = true;
            if (view instanceof ImageView) {
                int childViewWidth = getViewWidth(getContext(),
                        childComponent.getLayout(),
                        0);
                int childViewHeight = getViewHeight(getContext(),
                        childComponent.getLayout(),
                        getViewHeight(getContext(),
                                component.getLayout(),
                                0));

                if (childViewHeight > childViewWidth &&
                        childViewHeight != 0 &&
                        childViewWidth != 0 &&
                        !TextUtils.isEmpty(data.getGist().getPosterImageUrl())) {
                    Picasso.with(context)
                            .load(data.getGist().getPosterImageUrl())
                            .resize(childViewWidth, childViewHeight)
                            .into((ImageView) view);
                } else if (!TextUtils.isEmpty(data.getGist().getVideoImageUrl())) {
                    Picasso.with(context)
                            .load(data.getGist().getVideoImageUrl())
                            .resize(childViewWidth, childViewHeight)
                            .into((ImageView) view);
                }
                bringToFront = false;
            } else if (view instanceof Button) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickHandler.click(childComponent, data);
                    }
                });
            } else if (view instanceof TextView) {
                if (TextUtils.isEmpty(((TextView) view).getText())) {
                    if (childComponent.getKey()
                            .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_CAROUSEL_TITLE_KEY)) &&
                            !TextUtils.isEmpty(data.getGist().getTitle())) {
                        ((TextView) view).setText(data.getGist().getTitle());
                    } else if (childComponent.getKey()
                            .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_CAROUSEL_INFO_KEY))) {
                        int runtime = data.getGist().getRuntime();
                        String year = data.getGist().getYear();
                        // TODO: Get genre
                        String genre = null;
                        boolean appendFirstSep = runtime > 0 && !TextUtils.isEmpty(year);
                        boolean appendSecondSep = false;
                        StringBuffer infoText = new StringBuffer();
                        if (runtime > 0) {
                            infoText.append(runtime + "MINS");
                        }
                        if (appendFirstSep) {
                            infoText.append(" | ");
                        }
                        if (!TextUtils.isEmpty(year)) {
                            infoText.append(year);
                        }
                        if (appendSecondSep) {
                            infoText.append(" | ");
                        }
                        if (!TextUtils.isEmpty(genre)) {
                            infoText.append(genre);
                        }
                        ((TextView) view).setText(infoText.toString());
                    } else if (!TextUtils.isEmpty(data.getGist().getTitle())) {
                        ((TextView) view).setText(data.getGist().getTitle());
                    }
                }
            }

            if (shouldShowView(childComponent) && bringToFront) {
                view.getParent().bringChildToFront(view);
            }
        }
    }

    private Component matchComponentToView(View view) {
        Component result = null;
        for (ItemContainer itemContainer : childItems) {
            if (itemContainer.childView == view) {
                return itemContainer.component;
            }
        }
        return result;
    }

    protected int getGridWidth(Context context, Layout layout, int defaultWidth) {
        if (isTablet(context)) {
            if (isLandscape(context)) {
                TabletLandscape tabletLandscape = layout.getTabletLandscape();
                int width = tabletLandscape.getGridWidth() != null ? tabletLandscape.getGridWidth().intValue() : -1;
                if (width != -1) {
                    return (int) convertDpToPixel(width, context);
                }
            } else {
                TabletPortrait tabletPortrait = layout.getTabletPortrait();
                int width = tabletPortrait.getGridWidth() != null ? tabletPortrait.getGridWidth().intValue() : -1;
                if (width != -1) {
                    return (int) convertDpToPixel(width, context);
                }
            }
        } else {
            Mobile mobile = layout.getMobile();
            int width = mobile.getGridWidth() != null ? mobile.getGridWidth().intValue() : -1;
            if (width != -1) {
                return (int) convertDpToPixel(width, context);
            }
        }
        return defaultWidth;
    }

    protected int getGridHeight(Context context, Layout layout, int defaultHeight) {
        if (isTablet(context)) {
            if (isLandscape(context)) {
                TabletLandscape tabletLandscape = layout.getTabletLandscape();
                int height = tabletLandscape.getGridHeight() != null ? (int) ((float) tabletLandscape.getGridHeight()) : -1;
                if (height != -1) {
                    return (int) convertDpToPixel(height, context);
                }
            } else {
                TabletPortrait tabletPortrait = layout.getTabletPortrait();
                int height = tabletPortrait.getGridHeight() != null ? (int) ((float) tabletPortrait.getGridHeight()) : -1;
                if (height != -1) {
                    return (int) convertDpToPixel(height, context);
                }
            }
        } else {
            Mobile mobile = layout.getMobile();
            int height = mobile.getGridHeight() != null ? mobile.getGridHeight().intValue() : -1;
            if (height != -1) {
                return (int) convertDpToPixel(height, context);
            }
        }
        return defaultHeight;
    }
}
