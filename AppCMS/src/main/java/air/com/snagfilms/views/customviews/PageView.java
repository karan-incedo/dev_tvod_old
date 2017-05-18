package air.com.snagfilms.views.customviews;

import android.content.Context;
import android.widget.FrameLayout;

import javax.inject.Inject;

import air.com.snagfilms.models.data.appcms.page.Page;

/**
 * Created by viewlift on 5/4/17.
 */

public class PageView extends BaseView {
    private final Page page;

    @Inject
    public PageView(Context context, Page page) {
        super(context);
        this.page = page;
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
