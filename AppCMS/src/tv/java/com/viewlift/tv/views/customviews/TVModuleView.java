package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.ModuleWithComponents;
import com.viewlift.tv.utility.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by viewlift on 5/17/17.
 */

public class TVModuleView<T extends ModuleWithComponents> extends TVBaseView {
    private static final String TAG = "ModuleView";
    protected boolean[] componentHasViewList;
    List<ChildComponentAndView> childView;

    private final T module;

    public TVModuleView(Context context, T module) {
        super(context);
        this.module = module;
        this.childView = new ArrayList<>();
        init();
    }

    @Override
    public void init() {
        int width =  (int) Utils.getViewWidth(getContext(), module.getLayout(), LayoutParams.MATCH_PARENT);
        int height = (int) Utils.getViewHeight(getContext(), module.getLayout(), LayoutParams.WRAP_CONTENT);

        //Log.d(TAG, "Module Key: " + module.getView() + " Width: " + width + " Height; " + height);
        LayoutParams layoutParams =
                new LayoutParams(width, height);
        this.setLayoutParams(layoutParams);


        /*this.setPadding(MainUtils.getLeftPadding(getContext() , module.getLayout()) ,
                        MainUtils.getLeftPadding(getContext() , module.getLayout()),
                        MainUtils.getLeftPadding(getContext() , module.getLayout()),
                        MainUtils.getLeftPadding(getContext() , module.getLayout()));
*/
        if (module.getComponents() != null) {
            initializeComponentHasViewList(module.getComponents().size());
        }
        setPadding(0, 0, 0, 0);
    }

    public void setComponentHasView(int index, boolean hasView) {
        if (componentHasViewList != null) {
            componentHasViewList[index] = hasView;
        }
    }

    protected void initializeComponentHasViewList(int size) {
        componentHasViewList = new boolean[size];
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

    private FrameLayout childrenContainer;
    protected ViewGroup createChildrenContainer() {

         childrenContainer = new FrameLayout(getContext());
        int viewWidth =  (int) Utils.getViewWidth(getContext(), getLayout(), (float) LayoutParams.MATCH_PARENT);
        int viewHeight = (int) Utils.getViewHeight(getContext(), getLayout(), (float) LayoutParams.MATCH_PARENT);

        FrameLayout.LayoutParams childContainerLayoutParams =
                new FrameLayout.LayoutParams(viewWidth, viewHeight);
        childrenContainer.setLayoutParams(childContainerLayoutParams);
         addView(childrenContainer);
        return childrenContainer;
    }


    public ViewGroup getChildrenContainer() {
        if (childrenContainer == null) {
            return createChildrenContainer();
        }
        return childrenContainer;
    }

    public List<ChildComponentAndView> getChildViewList(){
        return childView;
    }

    public void addChildComponentAndView(View componentView, Component component) {
        ChildComponentAndView childComponentAndView = new ChildComponentAndView();
        childComponentAndView.childView = componentView;
        childComponentAndView.component = component;
        childView.add(childComponentAndView);
    }

    public static class ChildComponentAndView {
        Component component;
        View childView;
    }

}