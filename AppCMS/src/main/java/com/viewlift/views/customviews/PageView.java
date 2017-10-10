package com.viewlift.views.customviews;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.PopupMenuCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.views.adapters.AppCMSBaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Inject
    public PageView(Context context, AppCMSPageUI appCMSPageUI) {
        super(context);
        this.appCMSPageUI = appCMSPageUI;
        this.viewsWithComponentIds = new ArrayList<>();
        this.moduleViewMap = new HashMap<>();
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
        for (ListWithAdapter listWithAdapter1 : adapterList) {
            if (listWithAdapter.id.equals(listWithAdapter1.id)) {
                adapterList.remove(listWithAdapter);
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

        if (appCMSPageUI.getModuleList().get(1).getSettings().isShowPIP()) {
            int tempheight = (int) appCMSPageUI.getModuleList().get(1).getLayout().getMobile().getHeight();

            //  Toast.makeText(this.getContext(),"PIP appCMSPageUI.getModuleList().get(1).getSettings().isShowPIP() "+appCMSPageUI.getModuleList().get(1).getSettings().isShowPIP(),Toast.LENGTH_SHORT).show();
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                boolean pipPlayerVisible = false;
                PopupWindow dialog;

                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (scrollY > (tempheight + 300) && !pipPlayerVisible) {
                        Toast.makeText(v.getContext(), "Show PIP  " + appCMSPageUI.getModuleList().get(1).getSettings().isShowPIP(), Toast.LENGTH_SHORT).show();
                        if (dialog!=null)
                        {
                            dialog.dismiss();

                        }
                        dialog = new PopupWindow(v.getContext());

                        dialog.setContentView(new Button(v.getContext()));
                        dialog.showAtLocation(v,Gravity.BOTTOM,200,500);
                        pipPlayerVisible = true;
                    } else if (scrollY < (tempheight + 300)) {
                        pipPlayerVisible = false;
                        if (dialog!=null){
                            dialog.dismiss();
                        }

                    }

                }
            });

        }
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

    public void clearExistingViewLists() {
        moduleViewMap.clear();
        viewsWithComponentIds.clear();
    }

    public void addModuleViewWithModuleId(String moduleId, ModuleView moduleView) {
        moduleViewMap.put(moduleId, moduleView);
    }

    public ModuleView getModuleViewWithModuleId(String moduleId) {
        if (moduleViewMap.containsKey(moduleId)) {
            return moduleViewMap.get(moduleId);
        }
        return null;
    }
}
