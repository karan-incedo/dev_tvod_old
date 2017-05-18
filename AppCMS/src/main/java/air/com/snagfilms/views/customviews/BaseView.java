package air.com.snagfilms.views.customviews;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by viewlift on 5/17/17.
 */

public abstract class BaseView extends FrameLayout {
    private LinearLayout childrenContainer;

    public BaseView(Context context) {
        super(context);
    }

    public LinearLayout getChildrenContainer(Context context, int orientation) {
        if (childrenContainer == null) {
            return createChildrenContainer(context, orientation);
        }
        return childrenContainer;
    }

    protected LinearLayout createChildrenContainer(Context context, int orientation) {
        NestedScrollView nestedScrollView = new NestedScrollView(context);
        NestedScrollView.LayoutParams nestedScroolViewLayoutParams =
                new NestedScrollView.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        nestedScrollView.setLayoutParams(nestedScroolViewLayoutParams);
        childrenContainer = new LinearLayout(context);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        if (orientation == LinearLayout.HORIZONTAL) {
            width = LinearLayout.LayoutParams.WRAP_CONTENT;
            height = LinearLayout.LayoutParams.MATCH_PARENT;
        }
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(width, height, Gravity.CENTER);
        childrenContainer.setLayoutParams(layoutParams);
        childrenContainer.setOrientation(orientation);
        nestedScrollView.addView(childrenContainer);
        this.addView(nestedScrollView);
        return childrenContainer;
    }

    protected abstract void init();
}
