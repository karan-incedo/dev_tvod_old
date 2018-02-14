package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.views.customviews.ListWithAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;

/**
 * Created by viewlift on 5/4/17.
 */

public class TVPageView extends FrameLayout {
    private final AppCMSPageUI appCMSPageUI;
    private LinearLayout childrenContainer;
    private Map<String, TVModuleView> moduleViewMap;
    private CopyOnWriteArrayList adapterList;

    @Inject
    public TVPageView(Context context, AppCMSPageUI appCMSPageUI) {
        super(context);
        this.appCMSPageUI = appCMSPageUI;
        this.moduleViewMap = new HashMap<>();
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
        adapterList = new CopyOnWriteArrayList<>();
    }

    public void addListWithAdapter(ListWithAdapter listWithAdapter) {
        adapterList.add(listWithAdapter);
    }

    public void clearExistingViewLists() {
        moduleViewMap.clear();
        adapterList.clear();
    }

    public void addModuleViewWithModuleId(String moduleId, TVModuleView moduleView) {
        moduleViewMap.put(moduleId, moduleView);
    }

    public TVModuleView getModuleViewWithModuleId(String moduleId) {
        if (moduleViewMap.containsKey(moduleId)) {
            return moduleViewMap.get(moduleId);
        }
        return null;
    }

    public void notifyAdaptersOfUpdate() {
       /* for (ListWithAdapter listWithAdapter : adapterList) {
            if (listWithAdapter.getAdapter() instanceof AppCMSBaseAdapter) {
                ((AppCMSBaseAdapter) listWithAdapter.getAdapter())
                        .resetData(listWithAdapter.getListView());
            }
        }*/
    }
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