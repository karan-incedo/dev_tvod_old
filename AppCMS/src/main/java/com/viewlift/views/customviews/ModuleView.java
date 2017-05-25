package com.viewlift.views.customviews;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.gson.JsonElement;

import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.ModuleList;

/**
 * Created by viewlift on 5/17/17.
 */

public class ModuleView extends BaseView {
    private static final String TAG = "ModuleView";

    private final ModuleList module;

    public ModuleView(Context context, ModuleList module) {
        super(context);
        this.module = module;
        init();
    }

    @Override
    protected void init() {
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
