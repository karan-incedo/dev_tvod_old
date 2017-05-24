package com.viewlift.views.customviews;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.gson.JsonElement;

import com.viewlift.models.data.appcms.ui.page.ModuleList;

/**
 * Created by viewlift on 5/17/17.
 */

public class ModuleView extends BaseView {
    private static final String TAG = "ModuleView";

    private final ModuleList module;
    private boolean[] componentHasViewList;
    private int measurementCount;

    public ModuleView(Context context, ModuleList module) {
        super(context);
        this.module = module;
        init();
    }

    public void setComponentHasView(int index, boolean hasView) {
        if (componentHasViewList != null) {
            componentHasViewList[index] = hasView;
        }
    }

    @Override
    protected void init() {
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.setLayoutParams(layoutParams);
        if (module.getComponents() != null) {
            this.componentHasViewList = new boolean[module.getComponents().size()];
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (measurementCount < 2 && componentHasViewList != null) {
            int j = -1;
            for (int i = 0; i < module.getComponents().size(); i++) {
                if (componentHasViewList[i]) {
                    if (!TextUtils.isEmpty(module.getComponents().get(i).getText())) {
                        Log.d(TAG, "Setting margin for: " + module.getComponents().get(i).getText());
                    } else {
                        Log.d(TAG, "Setting margin for: " + module.getComponents().get(i).getImageName());
                    }
                    setViewMarginsFromComponent(getContext(),
                            module.getComponents().get(i).getLayout(),
                            childrenContainer.getChildAt(++j),
                            this.getRootView(),
                            measurementCount);
                    childrenContainer.getChildAt(j).requestLayout();
                }
            }
        }
        measurementCount++;
    }
}
