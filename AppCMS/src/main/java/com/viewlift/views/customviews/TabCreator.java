package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;

import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.android.NavigationPrimary;
import com.viewlift.presenters.AppCMSPresenter;

import rx.functions.Action0;

/**
 * Created by viewlift on 12/1/17.
 */

public class TabCreator {
    public interface OnClickHandler {
        NavBarItemView getSelectedNavItem();
        void setSelectedMenuTabIndex(int selectedMenuTabIndex);
        void selectNavItemAndLaunchPage(NavBarItemView navBarItemView,
                                        String pageId,
                                        String pageTitle);
    }

    private LinearLayout appCMSTabNavContainer;
    private AppCMSPresenter appCMSPresenter;
    private OnClickHandler onClickHandler;

    public void create(Context context, int currentIndex, NavigationPrimary tabItem) {
        if (appCMSTabNavContainer.getChildAt(currentIndex) instanceof
                NavBarItemView) {
            final NavBarItemView navBarItemView =
                    (NavBarItemView) appCMSTabNavContainer.getChildAt(currentIndex);
            int highlightColor;
            try {
                highlightColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand()
                        .getGeneral().getBlockTitleColor());
            } catch (Exception e) {
                //Log.w(TAG, "Failed to set AppCMS branding color for navigation item: " +
//                            e.getMessage());
                highlightColor = ContextCompat.getColor(context, R.color.colorAccent);
            }

            String tabIcon = tabItem.getIcon();
            if (tabIcon != null) {
                tabIcon = tabIcon.replace("-", "_");
            }
            navBarItemView.setImage(tabIcon);
            navBarItemView.setHighlightColor(highlightColor);
            navBarItemView.setLabel(tabItem.getTitle());
            navBarItemView.setOnClickListener(v -> {
                if (onClickHandler.getSelectedNavItem() == navBarItemView) {
                    return;
                }

                appCMSPresenter.showMainFragmentView(true);
                onClickHandler.selectNavItemAndLaunchPage(navBarItemView,
                        tabItem.getPageId(),
                        tabItem.getTitle());
            });

            navBarItemView.setTag(tabItem.getPageId());
            if (navBarItemView.getParent() == null) {
                appCMSTabNavContainer.addView(navBarItemView);
            }
        }
    }

    public LinearLayout getAppCMSTabNavContainer() {
        return appCMSTabNavContainer;
    }

    public void setAppCMSTabNavContainer(LinearLayout appCMSTabNavContainer) {
        this.appCMSTabNavContainer = appCMSTabNavContainer;
    }

    public AppCMSPresenter getAppCMSPresenter() {
        return appCMSPresenter;
    }

    public void setAppCMSPresenter(AppCMSPresenter appCMSPresenter) {
        this.appCMSPresenter = appCMSPresenter;
    }

    public OnClickHandler getOnClickHandler() {
        return onClickHandler;
    }

    public void setOnClickHandler(OnClickHandler onClickHandler) {
        this.onClickHandler = onClickHandler;
    }
}
