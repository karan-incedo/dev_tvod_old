package air.com.snagfilms.views.customviews;

import android.content.Context;
import android.widget.FrameLayout;

import javax.inject.Inject;

import air.com.snagfilms.models.data.appcms.ui.page.AppCMSPageUI;

/**
 * Created by viewlift on 5/4/17.
 */

public class PageView extends BaseView {
    private final AppCMSPageUI appCMSPageUI;

    @Inject
    public PageView(Context context, AppCMSPageUI appCMSPageUI) {
        super(context);
        this.appCMSPageUI = appCMSPageUI;
        init();
    }

    @Override
    protected void init() {
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(layoutParams);
    }
}
