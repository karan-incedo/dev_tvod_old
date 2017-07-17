package com.viewlift.views.customviews;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.views.adapters.AppCMSBaseAdapter;
import com.viewlift.views.adapters.AppCMSViewAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by viewlift on 5/4/17.
 */

public class PageView extends BaseView {
    private final AppCMSPageUI appCMSPageUI;
    private List<ListWithAdapter> adapterList;
    private List<ViewWithComponentId> viewsWithComponentIds;
    private boolean userLoggedIn;

    @Inject
    public PageView(Context context, AppCMSPageUI appCMSPageUI) {
        super(context);
        this.appCMSPageUI = appCMSPageUI;
        this.viewsWithComponentIds = new ArrayList<>();
        init();
    }

    @Override
    public void init() {
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);
        adapterList = new ArrayList<>();
    }

    public void addListWithAdapter(ListWithAdapter listWithAdapter) {
        adapterList.add(listWithAdapter);
    }

    public void notifyAdaptersOfUpdate() {
        for (ListWithAdapter listWithAdapter : adapterList) {
            if (listWithAdapter.getAdapter() instanceof AppCMSBaseAdapter) {
                ((AppCMSBaseAdapter) listWithAdapter.getAdapter())
                        .resetData(listWithAdapter.getListView());
            }
        }
    }

    public void updateDataList(List<ContentDatum> contentData, int index) {
        if (0 <= index && index < adapterList.size()) {
            ListWithAdapter listWithAdapter = adapterList.get(index);
            if (listWithAdapter.getAdapter() instanceof AppCMSBaseAdapter) {
                ((AppCMSBaseAdapter) listWithAdapter.getAdapter())
                        .updateData(listWithAdapter.getListView(), contentData);
            }
        }
    }

    public void showModule(ModuleList module) {
        for (int i = 0; i < childrenContainer.getChildCount(); i++) {
            View child = childrenContainer.getChildAt(i);
            if (child instanceof ModuleView) {
                if (module == ((ModuleView) child).getModule()) {
                    child.setVisibility(VISIBLE);
                }
            }
        }
    }

    @Override
    protected Component getChildComponent(int index) {
        return null;
    }

    @Override
    protected Layout getLayout() {
        return null;
    }

    @Override
    protected ViewGroup createChildrenContainer() {
        childrenContainer = new LinearLayout(getContext());
        LinearLayout.LayoutParams childContainerLayoutParams =
                new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        childrenContainer.setLayoutParams(childContainerLayoutParams);
        ((LinearLayout) childrenContainer).setOrientation(LinearLayout.VERTICAL);

        NestedScrollView nestedScrollView = new NestedScrollView(getContext());
        NestedScrollView.LayoutParams nestedScrollViewLayoutParams =
                new NestedScrollView.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        nestedScrollView.setLayoutParams(nestedScrollViewLayoutParams);
        nestedScrollView.addView(childrenContainer);
        addView(nestedScrollView);
        return childrenContainer;
    }

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
    }

    public void addViewWithComponentId(ViewWithComponentId viewWithComponentId) {
        viewsWithComponentIds.add(viewWithComponentId);
    }

    public View findViewFromComponentId(String id) {
        if (!TextUtils.isEmpty(id)) {
            for (ViewWithComponentId viewWithComponentId : viewsWithComponentIds) {
                if (!TextUtils.isEmpty(viewWithComponentId.id) && viewWithComponentId.id.equals(id)) {
                    return viewWithComponentId.view;
                }
            }
        }
        return null;
    }
}
