package com.viewlift.views.customviews;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.inject.Inject;

import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.views.adapters.AppCMSCarouselItemAdapter;
import com.viewlift.views.adapters.AppCMSViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viewlift on 5/4/17.
 */

public class PageView extends BaseView {
    private final AppCMSPageUI appCMSPageUI;
    private List<AppCMSViewAdapter.ListWithAdapter> adapterList;

    @Inject
    public PageView(Context context, AppCMSPageUI appCMSPageUI) {
        super(context);
        this.appCMSPageUI = appCMSPageUI;
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

    public void addListWithAdapter(AppCMSViewAdapter.ListWithAdapter listWithAdapter) {
        adapterList.add(listWithAdapter);
    }

    public void notifyAdaptersOfUpdate() {
        for (AppCMSViewAdapter.ListWithAdapter listWithAdapter : adapterList) {
            if (listWithAdapter.getAdapter() instanceof AppCMSViewAdapter) {
                ((AppCMSViewAdapter) listWithAdapter.getAdapter()).resetData(listWithAdapter.getListView());
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
}
