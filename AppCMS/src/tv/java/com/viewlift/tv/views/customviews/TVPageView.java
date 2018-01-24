package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.views.adapters.AppCMSViewAdapter;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.ListWithAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by viewlift on 5/4/17.
 */

public class TVPageView extends FrameLayout {
    private final AppCMSPageUI appCMSPageUI;
    private LinearLayout childrenContainer;
    // private List<AppCMSViewAdapter.ListWithAdapter> adapterList;

    @Inject
    public TVPageView(Context context, AppCMSPageUI appCMSPageUI) {
        super(context);
        this.appCMSPageUI = appCMSPageUI;
        init();
    }

/*
    @Override
*/
    public void init() {
        LayoutParams layoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);
      //  createChildrenContainer();
       // adapterList = new ArrayList<>();
    }

    public void addListWithAdapter(ListWithAdapter listWithAdapter) {
       // adapterList.add(listWithAdapter);
    }

    public void notifyAdaptersOfUpdate() {
       /* for (AppCMSViewAdapter.ListWithAdapter listWithAdapter : adapterList) {
            if (listWithAdapter.getAdapter() instanceof AppCMSViewAdapter) {
                ((AppCMSViewAdapter) listWithAdapter.getAdapter()).resetData(listWithAdapter.getListView());
            }
        }*/
    }
/*

    @Override
    protected Component getChildComponent(int index) {
        return null;
    }

    @Override
    protected Layout getLayout() {
        return null;
    }
*/

/*
    @Override
*/
    protected ViewGroup createChildrenContainer() {
        childrenContainer = new LinearLayout(getContext());
        LinearLayout.LayoutParams childContainerLayoutParams =
                new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        childrenContainer.setLayoutParams(childContainerLayoutParams);
        ((LinearLayout) childrenContainer).setOrientation(LinearLayout.VERTICAL);
/*
        NestedScrollView nestedScrollView = new NestedScrollView(getContext());
        LayoutParams nestedScrollViewLayoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        nestedScrollView.setLayoutParams(nestedScrollViewLayoutParams);
        nestedScrollView.addView(childrenContainer);
        addView(nestedScrollView);*/
        addView(childrenContainer);
        return childrenContainer;
    }

    public ViewGroup getChildrenContainer() {
        if (childrenContainer == null) {
            return createChildrenContainer();
        }
        return childrenContainer;
    }


    private boolean isStandAlonePlayerEnabled = false;
    public void setIsStandAlonePlayerEnabled(boolean isPlayerEnabled){
        isStandAlonePlayerEnabled = isPlayerEnabled;
    }
    public boolean isStandAlonePlayerEnabled(){
        return isStandAlonePlayerEnabled;
    }


}
