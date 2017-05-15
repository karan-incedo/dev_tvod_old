package air.com.snagfilms.views.customviews;

import android.content.Context;
import android.content.res.Configuration;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import air.com.snagfilms.models.data.appcms.page.Component;
import air.com.snagfilms.models.data.appcms.page.Layout;
import air.com.snagfilms.models.data.appcms.page.Page;

/**
 * Created by viewlift on 5/5/17.
 */

public class ViewCreator {
    public enum PageType {
        MODULAR("Modular Page"),
        WELCOME("Welcome Page");

        final String name;

        PageType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static PageType fromString(String value) {
            for (PageType pageType : PageType.values()) {
                if (pageType.toString().equals(value)) {
                    return pageType;
                }
            }
            throw new IllegalArgumentException("No enum constant " + value);
        }
    }

    public enum ComponentType {
        TRAY("tray"),
        COLLECTION_GRID("collectionGrid"),
        PROGRESS_VIEW("progressView"),
        BUTTON("button"),
        LABEL("label"),
        IMAGE("image");

        final String name;

        ComponentType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static ComponentType fromString(String value) {
            for (ComponentType componentType : ComponentType.values()) {
                if (componentType.toString().equals(value)) {
                    return componentType;
                }
            }
            throw new IllegalArgumentException("No enum constant " + value);
        }
    }

    public enum LayoutType {
        TABLET_LANDSCAPE("iPadLandscape"),
        TABLET_PORTRAIT("iPadPortrait"),
        PHONE("iPhone");

        final String name;

        LayoutType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static LayoutType fromString(String value) {
            for (LayoutType layoutType : LayoutType.values()) {
                if (layoutType.toString().equals(value)) {
                    return layoutType;
                }
            }
            throw new IllegalArgumentException("No enum constant " + value);
        }
    }

    public PageView generatePage(Context context, Page page) {
        PageView pageView = null;
        switch(PageType.fromString(page.getType())) {
            case MODULAR:
//                pageView = new PageView(context, page);
//                generateModularPage(context, page, pageView);
                break;
            case WELCOME:
                pageView = new PageView(context, page);
                generateModularPage(context, page, pageView);
                break;
            default:
        }
        return pageView;
    }

    protected void generateModularPage(Context context, Page page, PageView pageView) {
        List<Component> modulesList = page.getModuleList();
        for (Component module : modulesList) {
            View childView = generateComponent(context, module);
            if (childView != null) {
                pageView.addView(childView);
            }
        }
    }

    public View generateComponent(Context context, Component module) {
        View moduleView = null;

        switch (ComponentType.fromString(module.getType())) {
            case TRAY:
                // TODO: Generate LayoutParams
                moduleView = new ConstraintLayout(context);

                for (Component component : module.getComponent()) {
                    View childView = generateComponent(context, component);
                    if (childView != null) {
                        ((ViewGroup) moduleView).addView(childView);
                    }
                }
                break;
            case COLLECTION_GRID:
                break;
            case LABEL:
                break;
            case PROGRESS_VIEW:
                break;
            case BUTTON:
                break;
            case IMAGE:
                break;
            default:
        }

        return moduleView;
    }

    protected boolean isTablet(Context context) {
        int largeScreenLayout =
                (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
        int xLargeScreenLayout =
                (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);

        return (largeScreenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                xLargeScreenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE);
    }

    protected boolean isLandscape(Context context) {
        int layoutDirection = context.getResources().getConfiguration().getLayoutDirection();
        return layoutDirection == Configuration.ORIENTATION_LANDSCAPE;
    }

    protected Layout getLayoutForScreen(Context context, Component module) {
        if (isLandscape(context) && isTablet(context)) {
            return getLayoutForTabletLandscape(module);
        } else if (isTablet(context)) {
            return getLayoutForTabletPortrait(module);
        }
        return getLayoutForPhone(module);
    }

    protected Layout getLayoutForTabletLandscape(Component module) {
        for (Layout layout : module.getLayout()) {
            if (LayoutType.fromString(layout.getType()) == LayoutType.TABLET_LANDSCAPE) {
                return layout;
            }
        }
        return null;
    }

    protected Layout getLayoutForTabletPortrait(Component module) {
        for (Layout layout : module.getLayout()) {
            if (LayoutType.fromString(layout.getType()) == LayoutType.TABLET_PORTRAIT) {
                return layout;
            }
        }
        return null;
    }

    protected Layout getLayoutForPhone(Component module) {
        for (Layout layout : module.getLayout()) {
            if (LayoutType.fromString(layout.getType()) == LayoutType.PHONE) {
                return layout;
            }
        }
        return null;
    }
}
