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

import rx.functions.Action1;
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
        int width = getGridWidth(getContext(),
                component.getLayout(),
                LayoutParams.WRAP_CONTENT);
        int height = getGridHeight(getContext(),
                component.getLayout(),
                LayoutParams.WRAP_CONTENT);
        int margin = (int) convertDpToPixel(component.getTrayPadding(), getContext());
        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(width, height);
        marginLayoutParams.setMargins(margin, margin, margin, margin);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(marginLayoutParams);
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
                          ContentDatum data,
                          Map<AppCMSUIKeyType, String> jsonValueKeyMap) {
        Component childComponent = matchComponentToView(view);
        if (childComponent != null) {
            if (childComponent.getKey()
                    .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY))) {
                if (!TextUtils.isEmpty(data.getGist().getPosterImageUrl())) {
                    Picasso.with(context)
                            .load(data.getGist().getPosterImageUrl())
                            .resize(getViewWidth(context, childComponent.getLayout(), 1),
                                    getViewHeight(context, childComponent.getLayout(), 1))
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
                int width = tabletLandscape.getGridWidth();
                if (width != -1) {
                    return (int) convertDpToPixel(width, context);
                }
            } else {
                TabletPortrait tabletPortrait = layout.getTabletPortrait();
                int width = tabletPortrait.getGridWidth();
                if (width != -1) {
                    return (int) convertDpToPixel(width, context);
                }
            }
        } else {
            Mobile mobile = layout.getMobile();
            int width = mobile.getGridWidth();
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
                int height = tabletLandscape.getGridHeight();
                if (height != -1) {
                    return (int) convertDpToPixel(height, context);
                }
            } else {
                TabletPortrait tabletPortrait = layout.getTabletPortrait();
                int height = tabletPortrait.getGridHeight();
                if (height != -1) {
                    return (int) convertDpToPixel(height, context);
                }
            }
        } else {
            Mobile mobile = layout.getMobile();
            int height = mobile.getGridHeight();
            if (height != -1) {
                return (int) convertDpToPixel(height, context);
            }
        }
        return defaultHeight;
    }
}
