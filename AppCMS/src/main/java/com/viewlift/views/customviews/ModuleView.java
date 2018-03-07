package com.viewlift.views.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.ModuleWithComponents;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by viewlift on 5/17/17.
 */

@SuppressLint("ViewConstructor")
public class ModuleView<T extends ModuleWithComponents> extends BaseView {
    private static final String TAG = "ModuleView";

    private final T module;

    private List<ChildComponentAndView> childComponentAndViewList;

    private List<HeightLayoutAdjuster> heightLayoutAdjusterList;

    public ModuleView(Context context,
                      T module,
                      boolean init) {
        super(context);
        this.module = module;
        this.childComponentAndViewList = new ArrayList<>();
        this.heightLayoutAdjusterList = new ArrayList<>();
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

        setFocusableInTouchMode(true);
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

    public void addHeightAdjuster(HeightLayoutAdjuster heightLayoutAdjuster) {
        heightLayoutAdjusterList.add(heightLayoutAdjuster);
    }

    public int getHeightAdjusterListSize() {
        return heightLayoutAdjusterList.size();
    }

    public HeightLayoutAdjuster getHeightLayoutAdjuster(int index) {
        if (0 <= index && index < heightLayoutAdjusterList.size()) {
            return heightLayoutAdjusterList.get(index);
        }
        return null;
    }

    public void resetHeightAdjusters() {
        for (HeightLayoutAdjuster heightLayoutAdjuster : heightLayoutAdjusterList) {
            heightLayoutAdjuster.reset = true;
        }
    }

    public void removeResetHeightAdjusters() {
        List<HeightLayoutAdjuster> updatedHeightLayoutAdjusterList = new ArrayList<>();
        for (HeightLayoutAdjuster heightLayoutAdjuster : heightLayoutAdjusterList) {
            if (!heightLayoutAdjuster.reset) {
                updatedHeightLayoutAdjusterList.add(heightLayoutAdjuster);
            }
        }
        heightLayoutAdjusterList = updatedHeightLayoutAdjusterList;
    }

    public List<ChildComponentAndView> getChildComponentAndViewList() {
        return childComponentAndViewList;
    }

    public void verifyHeightAdjustments() {
        List<Integer> heightLayoutAdjusterIndicesToRemove = new ArrayList<>();
        for (int i = 0; i < heightLayoutAdjusterList.size(); i++) {
            HeightLayoutAdjuster heightLayoutAdjuster1 = heightLayoutAdjusterList.get(i);
            if (!heightLayoutAdjuster1.reset) {
                Component component1 = heightLayoutAdjuster1.component;
                int y1Component1 = 0;
                int y2Component1;

                if (isTablet(getContext())) {
                    if (isLandscape(getContext())) {
                        if (component1.getLayout().getTabletLandscape().getTopMargin() > 0.0f) {
                            y1Component1 = (int) component1.getLayout().getTabletLandscape().getTopMargin();
                        } else if (component1.getLayout().getTabletLandscape().getYAxis() > 0.0f) {
                            y1Component1 = (int) component1.getLayout().getTabletLandscape().getYAxis();
                        }
                        y2Component1 = y1Component1 + (int) component1.getLayout().getTabletLandscape().getHeight();
                    } else {
                        if (component1.getLayout().getTabletPortrait().getTopMargin() > 0.0f) {
                            y1Component1 = (int) component1.getLayout().getTabletPortrait().getTopMargin();
                        } else if (component1.getLayout().getTabletPortrait().getYAxis() > 0.0f) {
                            y1Component1 = (int) component1.getLayout().getTabletPortrait().getYAxis();
                        }
                        y2Component1 = y1Component1 + (int) component1.getLayout().getTabletPortrait().getHeight();
                    }
                } else {
                    if (component1.getLayout().getMobile().getTopMargin() > 0.0f) {
                        y1Component1 = (int) component1.getLayout().getMobile().getTopMargin();
                    } else if (component1.getLayout().getTabletPortrait().getYAxis() > 0.0f) {
                        y1Component1 = (int) component1.getLayout().getMobile().getYAxis();
                    }
                    y2Component1 = y1Component1 + (int) component1.getLayout().getMobile().getHeight();
                }

                for (Component component2 : module.getComponents()) {
                    if (component2 != component1) {
                        int y1Component2 = 0;
                        int y2Component2;

                        if (isTablet(getContext())) {
                            if (isLandscape(getContext())) {
                                if (component2.getLayout().getTabletLandscape().getTopMargin() > 0.0f) {
                                    y1Component2 = (int) component2.getLayout().getTabletLandscape().getTopMargin();
                                } else if (component2.getLayout().getTabletLandscape().getYAxis() > 0.0f) {
                                    y1Component2 = (int) component2.getLayout().getTabletLandscape().getYAxis();
                                }
                                y2Component2 = y1Component2 + (int) component2.getLayout().getTabletLandscape().getHeight();
                            } else {
                                if (component2.getLayout().getTabletPortrait().getTopMargin() > 0.0f) {
                                    y1Component2 = (int) component2.getLayout().getTabletPortrait().getTopMargin();
                                } else if (component2.getLayout().getTabletPortrait().getYAxis() > 0.0f) {
                                    y1Component2 = (int) component2.getLayout().getTabletPortrait().getYAxis();
                                }
                                y2Component2 = y1Component2 + (int) component2.getLayout().getTabletPortrait().getHeight();
                            }
                        } else {
                            if (component2.getLayout().getMobile().getTopMargin() > 0.0f) {
                                y1Component2 = (int) component2.getLayout().getMobile().getTopMargin();
                            } else if (component2.getLayout().getTabletPortrait().getYAxis() > 0.0f) {
                                y1Component2 = (int) component2.getLayout().getMobile().getYAxis();
                            }
                            y2Component2 = y1Component2 + (int) component2.getLayout().getMobile().getHeight();
                        }

                        if ((y1Component2 <= y1Component1 && y1Component1 <= y2Component2) ||
                                (y1Component1 <= y1Component2 && y1Component2 <= y2Component1)) {
                            boolean component2Hidden = false;
                            for (HeightLayoutAdjuster heightLayoutAdjuster2 : heightLayoutAdjusterList) {
                                if (heightLayoutAdjuster2.component == component2 &&
                                        !heightLayoutAdjuster2.reset) {
                                    component2Hidden = true;
                                }
                            }
                            if (!component2Hidden) {
                                heightLayoutAdjusterIndicesToRemove.add(i);
                            }
                        }

                    }
                }
            }
        }

        List<HeightLayoutAdjuster> modifiedHeightLayoutAdjusters = new ArrayList<>();
        for (int i = 0; i < heightLayoutAdjusterList.size(); i++) {
            if (!heightLayoutAdjusterIndicesToRemove.contains(i)) {
                modifiedHeightLayoutAdjusters.add(heightLayoutAdjusterList.get(i));
            }
        }
        heightLayoutAdjusterList = modifiedHeightLayoutAdjusters;
    }

    public static class ChildComponentAndView {
        Component component;
        View childView;
    }

    public static class HeightLayoutAdjuster {
        int heightAdjustment;
        int topMargin;
        int yAxis;
        boolean reset;
        Component component;
    }
}
