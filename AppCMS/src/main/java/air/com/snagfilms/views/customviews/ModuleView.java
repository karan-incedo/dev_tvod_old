package air.com.snagfilms.views.customviews;

import android.content.Context;
import android.widget.FrameLayout;

import air.com.snagfilms.models.data.appcms.page.ModuleList;

/**
 * Created by viewlift on 5/17/17.
 */

public class ModuleView extends BaseView {
    private final ModuleList modules;

    public ModuleView(Context context, ModuleList modules) {
        super(context);
        this.modules = modules;
    }

    @Override
    protected void init() {
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(layoutParams);
    }
}
