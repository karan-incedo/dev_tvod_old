package com.viewlift.views.customviews;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.ModuleWithComponents;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viewlift on 5/17/17.
 */

public class ModuleView<T extends ModuleWithComponents> extends BaseView {
    private static final String TAG = "ModuleView";

    private final T module;

    private List<ChildComponentAndView> childComponentAndViewList;

    public ModuleView(Context context, T module, boolean init) {
        super(context);
        this.module = module;
        this.childComponentAndViewList = new ArrayList<>();
        if (init) {
            init();
        }
    }

    @Override
    public void init() {
        int width = (int) getViewWidth(getContext(), module.getLayout(), LayoutParams.MATCH_PARENT);
        int height = (int) getViewHeight(getContext(), module.getLayout(), LayoutParams.WRAP_CONTENT);
        if (BaseView.isLandscape(getContext())) {
            height *= TABLET_LANDSCAPE_HEIGHT_SCALE;
        }
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(width, height);
        this.setLayoutParams(layoutParams);
        if (module.getComponents() != null) {
            initializeComponentHasViewList(module.getComponents().size());
        }
        setPadding(0, 0, 0, 0);
    }

    @Override
    protected Component getChildComponent(int index) {
        if (module.getComponents() != null &&
                0 <= index &&
                index < module.getComponents().size()) {
            return module.getComponents().get(index);
        }
        return null;
    }

    @Override
    protected Layout getLayout() {
        return module.getLayout();
    }

    public T getModule() {
        return module;
    }

    public void addChildComponentAndView(Component component, View childView) {
        ChildComponentAndView childComponentAndView = new ChildComponentAndView();
        childComponentAndView.component = component;
        childComponentAndView.childView = childView;
        childComponentAndViewList.add(childComponentAndView);
    }

    public List<ChildComponentAndView> getChildComponentAndViewList() {
        return childComponentAndViewList;
    }

    public static class ChildComponentAndView {
        Component component;
        View childView;
    }
}
