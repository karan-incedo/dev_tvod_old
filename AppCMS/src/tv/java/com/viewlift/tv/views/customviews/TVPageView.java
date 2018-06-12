package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
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

    /**
     * only required in Video detail page where we need to scroll the page a bit
     * in case the tray item cut off from the bottom.
     */
    private ScrollView scrollView;

    @Inject
    public TVPageView(Context context, AppCMSPageUI appCMSPageUI) {
        super(context);
        this.appCMSPageUI = appCMSPageUI;
        this.moduleViewMap = new HashMap<>();
        init(context);
    }

/*
    @Override
*/
    public void init(Context context) {
        LayoutParams layoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);
      //  createChildrenContainer();
        adapterList = new CopyOnWriteArrayList<>();

        /* Check if the current page being populated is a video page, then
        * only attach a Scroll View to the PageView as a child*/
        if (isVideoDetailPage()) {
            scrollView = new ScrollView(context);
            scrollView.setId(R.id.scrollview);
            scrollView.setSmoothScrollingEnabled(true);
            ScrollView.LayoutParams scrollLayoutParams = new ScrollView.LayoutParams(
                    ScrollView.LayoutParams.MATCH_PARENT,
                    ScrollView.LayoutParams.MATCH_PARENT);
            scrollView.setLayoutParams(scrollLayoutParams);
            // to hide the scroll bar in the right
            scrollView.setVerticalScrollBarEnabled(false);
            this.addView(scrollView);
        }
    }

    /**
     * Used to the get the member {@link #scrollView}
     * @return {@link #scrollView}
     */
    public ScrollView getScrollView() {
        return scrollView;
    }

    /**
     * Used to set the value of {@link #scrollView}
     * @param scrollView the instance of {@link #scrollView}
     */
    public void setScrollView(ScrollView scrollView) {
        this.scrollView = scrollView;
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

        // Check if the page is a video detail page and add scroll view else not
        if (isVideoDetailPage()) {
            scrollView.addView(childrenContainer);
        } else {
            addView(childrenContainer);
        }
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

    /**
     * Method is used to determine whether the current page is a Video page
     * based on the Module type
     * @return true/false if the current page is a video page
     */
    private boolean isVideoDetailPage () {
        boolean isVideoDetailPage = false;
        if (appCMSPageUI != null
                && appCMSPageUI.getModuleList() != null
                && !appCMSPageUI.getModuleList().isEmpty()) {
            for (ModuleList moduleList : appCMSPageUI.getModuleList()) {
                if (moduleList.getType().toLowerCase().contains("VideoPlayerWithInfo".toLowerCase())) {
                    isVideoDetailPage = true;
                    break;
                }
            }
        }
        return isVideoDetailPage;
    }
}