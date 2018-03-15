package com.viewlift.views.customviews;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.AppCMSBaseAdapter;
import com.viewlift.views.adapters.AppCMSPageViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;

/**
 * Created by viewlift on 5/4/17.
 */

public class PageView extends BaseView {
    private final AppCMSPageUI appCMSPageUI;
    private List<ListWithAdapter> adapterList;
    private List<ViewWithComponentId> viewsWithComponentIds;
    private boolean userLoggedIn;
    private Map<String, ModuleView> moduleViewMap;
    private AppCMSPresenter appCMSPresenter;
    private SwipeRefreshLayout mainView;
    private AppCMSPageViewAdapter appCMSPageViewAdapter;

    private ViewDimensions fullScreenViewOriginalDimensions;

    private boolean shouldRefresh;

    @Inject
    public PageView(Context context,
                    AppCMSPageUI appCMSPageUI,
                    AppCMSPresenter appCMSPresenter) {
        super(context);
        this.appCMSPageUI = appCMSPageUI;
        this.viewsWithComponentIds = new ArrayList<>();
        this.moduleViewMap = new HashMap<>();
        this.appCMSPresenter = appCMSPresenter;
        this.appCMSPageViewAdapter = new AppCMSPageViewAdapter();
        this.shouldRefresh = true;
        init();
    }

    public void openViewInFullScreen(View view, ViewGroup viewParent) {
        shouldRefresh = false;
        if (fullScreenViewOriginalDimensions == null) {
            fullScreenViewOriginalDimensions = new ViewDimensions();
        }
        try {
            fullScreenViewOriginalDimensions.width = view.getLayoutParams().width;
            fullScreenViewOriginalDimensions.height = view.getLayoutParams().height;
        } catch (Exception e) {
            //
        }

        view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

        childrenContainer.setVisibility(GONE);
        viewParent.removeView(view);
        addView(view);
    }

    public void closeViewFromFullScreen(View view, ViewGroup viewParent) {
        shouldRefresh = true;
        if (fullScreenViewOriginalDimensions != null) {
            removeView(view);

            view.getLayoutParams().width = fullScreenViewOriginalDimensions.width;
            view.getLayoutParams().height = fullScreenViewOriginalDimensions.height;

            viewParent.addView(view);
            childrenContainer.setVisibility(VISIBLE);
        }
    }

    @Override
    public void init() {
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);
        adapterList = new CopyOnWriteArrayList<>();

    }

    public void addListWithAdapter(ListWithAdapter listWithAdapter) {
        for (ListWithAdapter listWithAdapter1 : adapterList) {
            if (listWithAdapter.id.equals(listWithAdapter1.id)) {
                adapterList.remove(listWithAdapter1);
            }
        }

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

    public void updateDataList(List<ContentDatum> contentData, String id) {
        for (int i = 0; i < adapterList.size(); i++) {
            if (adapterList.get(i).id != null &&
                    adapterList.get(i).id.equals(id)) {
                if (adapterList.get(i).adapter instanceof AppCMSBaseAdapter) {
                    ((AppCMSBaseAdapter) adapterList.get(i).adapter)
                            .updateData(adapterList.get(i).listView, contentData);
                }
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

    public void setAllChildrenVisible(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setVisibility(VISIBLE);
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                setAllChildrenVisible((ViewGroup) viewGroup.getChildAt(i));
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
        childrenContainer = new RecyclerView(getContext());
        childrenContainer.setId(R.id.home_nested_scroll_view);
        childrenContainer.setDescendantFocusability(RecyclerView.FOCUS_BLOCK_DESCENDANTS);
        FrameLayout.LayoutParams nestedScrollViewLayoutParams =
                new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        childrenContainer.setLayoutParams(nestedScrollViewLayoutParams);
        ((RecyclerView) childrenContainer).setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL,
                false));
        ((RecyclerView) childrenContainer).setAdapter(appCMSPageViewAdapter);

        mainView = new SwipeRefreshLayout(getContext());
        SwipeRefreshLayout.LayoutParams swipeRefreshLayoutParams =
                new SwipeRefreshLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        mainView.setLayoutParams(swipeRefreshLayoutParams);
        mainView.addView(childrenContainer);
        mainView.setOnRefreshListener(() -> {
            appCMSPresenter.setMiniPLayerVisibility(true);
            appCMSPresenter.refreshAPIData(() -> {
                        mainView.setRefreshing(false);
            },
                    true);
        });
        addView(mainView);
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

    public void clearExistingViewLists() {
        moduleViewMap.clear();
        viewsWithComponentIds.clear();
        appCMSPageViewAdapter.removeAllViews();
    }

    public void addModuleViewWithModuleId(String moduleId, ModuleView moduleView) {
        moduleViewMap.put(moduleId, moduleView);
        appCMSPageViewAdapter.addView(moduleView);
    }

    public ModuleView getModuleViewWithModuleId(String moduleId) {
        if (moduleViewMap.containsKey(moduleId)) {
            return moduleViewMap.get(moduleId);
        }
        return null;
    }

    public AppCMSPageUI getAppCMSPageUI() {
        return appCMSPageUI;
    }
    public void removeAllAddOnViews() {
        int index = 0;
        boolean removedChild = false;
        while (index < getChildCount() && !removedChild) {
            View child = getChildAt(index);
            if (child != mainView) {
                removeView(child);
                removedChild = true;
                removeAllAddOnViews();
            }
            index++;
        }
    }
    public void notifyAdapterDataSetChanged() {
        if (appCMSPageViewAdapter != null) {
            appCMSPageViewAdapter.notifyDataSetChanged();
        }
    }
    public View findChildViewById(int id) {
        if (appCMSPageViewAdapter != null) {
            return appCMSPageViewAdapter.findChildViewById(id);
        }
        return null;
    }

    private static class ViewDimensions {
        int width;
        int height;
    }
}
