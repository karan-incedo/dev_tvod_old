package com.viewlift.views.customviews;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/5/17.
 */

public class CollectionGridItemView extends BaseView {
    private static final String TAG = "CollectionItemView";

    private final Component component;
    private List<ItemContainer> childItems;

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

    @Inject
    public CollectionGridItemView(Context context,
                                  Component component) {
        super(context);
        this.component = component;
        init();
    }

    protected void init() {
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(layoutParams);
        this.childItems = new ArrayList<>();
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
    protected ViewGroup createChildrenContainer(Context context) {
        childrenContainer = new CardView(context);
        int containerWidth = getViewWidth(context,
                component.getLayout(),
                LayoutParams.WRAP_CONTENT);
        int containerHeight = getViewHeight(context,
                component.getLayout(),
                LayoutParams.WRAP_CONTENT);

        CardView.LayoutParams childContainerLayoutParams =
                new CardView.LayoutParams(containerWidth,
                        containerHeight);
        childrenContainer.setLayoutParams(childContainerLayoutParams);
        this.addView(childrenContainer);
        int padding = (int) convertDpToPixel(component.getTrayPadding(), context);
        this.setPadding(padding, padding, padding, padding);
        return childrenContainer;
    }

    public void addChild(Context context, ItemContainer itemContainer) {
        if (childrenContainer == null) {
            createChildrenContainer(context);
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
                          ContentDatum data,
                          Map<AppCMSUIKeyType, String> jsonValueKeyMap) {
        Component childComponent = matchComponentToView(view);
        if (childComponent != null) {
            if (childComponent.getKey()
                    .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY))) {
                if (!TextUtils.isEmpty(data.getGist().getPosterImageUrl())) {
                    Picasso.with(context)
                            .load(data.getGist().getPosterImageUrl())
                            .resize(super.getViewWidth(context, childComponent.getLayout(), 1),
                                    super.getViewHeight(context, childComponent.getLayout(), 1))
                            .into((ImageView) view);
                }
            } else if (childComponent.getKey()
                    .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_THUMBNAIL_TITLE_KEY))) {
                if (!TextUtils.isEmpty(data.getGist().getTitle())) {
                    ((TextView) view).setText(data.getGist().getTitle());
                }
            } else if (childComponent.getKey()
                    .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_INFO_KEY))) {
                view.setBackground(context.getDrawable(R.drawable.info_icon));
            } else if (childComponent.getKey()
                    .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_PLAY_KEY))) {
                view.setBackground(context.getDrawable(R.drawable.play_icon));
            }
        }
    }

    @Override
    protected int getViewWidth(Context context, Layout layout, int defaultWidth) {
        if (isTablet(context)) {
            if (isLandscape(context)) {
                TabletLandscape tabletLandscape = layout.getTabletLandscape();
                if (tabletLandscape != null &&
                        tabletLandscape.getGridWidth() != null &&
                        tabletLandscape.getGridWidth() > 0) {
                    return (int) convertDpToPixel(tabletLandscape.getGridWidth(), context);
                }
            } else {
                TabletPortrait tabletPortrait = layout.getTabletPortrait();
                if (tabletPortrait != null &&
                        tabletPortrait.getGridWidth() != null &&
                        tabletPortrait.getGridWidth() > 0) {
                    return (int) convertDpToPixel(tabletPortrait.getGridWidth(), context);
                }
            }
        } else {
            Mobile mobile = layout.getMobile();
            if (mobile != null && mobile.getGridWidth() != null && mobile.getGridWidth() > 0) {
                return (int) convertDpToPixel(mobile.getGridWidth(), context);
            }
        }
        return defaultWidth;
    }

    @Override
    protected int getViewHeight(Context context, Layout layout, int defaultHeight) {
        if (isTablet(context)) {
            if (isLandscape(context)) {
                TabletLandscape tabletLandscape = layout.getTabletLandscape();
                if (tabletLandscape != null &&
                        tabletLandscape.getGridHeight() != null &&
                        tabletLandscape.getGridHeight() > 0) {
                    return (int) convertDpToPixel(tabletLandscape.getGridHeight(), context);
                }
            } else {
                TabletPortrait tabletPortrait = layout.getTabletPortrait();
                if (tabletPortrait != null &&
                        tabletPortrait.getGridHeight() != null &&
                        tabletPortrait.getGridHeight() > 0) {
                    return (int) convertDpToPixel(tabletPortrait.getGridHeight(), context);
                }
            }
        } else {
            Mobile mobile = layout.getMobile();
            if (mobile != null &&
                    mobile.getGridHeight() != null &&
                    mobile.getGridHeight() > 0) {
                return (int) convertDpToPixel(mobile.getGridHeight(), context);
            }
        }
        return defaultHeight;
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
}
