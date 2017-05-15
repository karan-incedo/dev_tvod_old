package air.com.snagfilms.views.customviews;

import android.content.Context;
import android.widget.FrameLayout;

import javax.inject.Inject;

import air.com.snagfilms.models.data.appcms.page.Page;

/**
 * Created by viewlift on 5/4/17.
 */

public class PageView extends FrameLayout {
    private final Page page;

    @Inject
    public PageView(Context context, Page page) {
        super(context);
        this.page = page;
    }
}
