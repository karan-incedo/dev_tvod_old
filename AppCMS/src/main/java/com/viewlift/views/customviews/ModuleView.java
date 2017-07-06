package com.viewlift.views.customviews;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Module;

/**
 * Created by viewlift on 5/17/17.
 */

public class ModuleView<T extends Module> extends BaseView {
    private static final String TAG = "ModuleView";

    private final T module;

    public ModuleView(Context context, T module) {
        super(context);
        this.module = module;
        init();
    }

    @Override
    public void init() {
        int width = (int) getViewWidth(getContext(), module.getLayout(), LayoutParams.MATCH_PARENT);
        int height = (int) getViewHeight(getContext(), module.getLayout(), LayoutParams.MATCH_PARENT);
        Log.d(TAG, "Module Key: " + module.getView() + " Width: " + width + " Height; " + height);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(width, height);
        this.setLayoutParams(layoutParams);
        if (module.getComponents() != null) {
            initializeComponentHasViewList(module.getComponents().size());
        }
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
}
