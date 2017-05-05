package air.com.snagfilms.views.modules;

import android.content.Context;
import android.content.res.Configuration;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import air.com.snagfilms.models.data.appcms.page.Component;
import air.com.snagfilms.models.data.appcms.page.Layout;
import air.com.snagfilms.models.data.appcms.page.Layout_;
import air.com.snagfilms.models.data.appcms.page.ModuleList;
import air.com.snagfilms.models.data.appcms.page.Page;

/**
 * Created by viewlift on 5/4/17.
 */

public class PageView extends FrameLayout {
    private final Page page;

    public enum PageType {
        MODULAR("Modular Page"),
        WELCOME("Welcome Page");

        final String name;

        PageType(String name) {
            this.name = name;
        }
    }

    public enum ModuleType {
        TRAY("tray");

        final String name;

        ModuleType(String name) {
            this.name = name;
        }
    }

    public enum ComponentType {
        COLLECTION_GRID("collectionGrid"),
        PROGRESS_VIEW("progressView"),
        BUTTON("button"),
        LABEL("label"),
        IMAGE("image");

        final String name;

        ComponentType(String name) {
            this.name = name;
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
    }

    public PageView(Context context, Page page) {
        super(context);
        this.page = page;
        generatePage();
    }

    protected void generatePage() {
        switch(PageType.valueOf(page.getType())) {
            case MODULAR:
                generateModularPage();
                break;
            case WELCOME:
                generateModularPage();
                break;
            default:
        }
    }

    protected void generateModularPage() {
        List<ModuleList> modulesList = page.getModuleList();
        for (ModuleList moduleList : modulesList) {
            View childView = generateComponents(moduleList);
            if (childView != null) {
                addView(childView);
            }
        }
    }

    protected View generateComponents(ModuleList module) {
        View moduleView = null;

        switch (ModuleType.valueOf(module.getType())) {
            case TRAY:
                // TODO: Generate LayoutParams
                moduleView = new ConstraintLayout(getContext());

                for (Component component : module.getComponent()) {
                    View childView = generateComponent(component);
                    if (childView != null) {
                        ((ViewGroup) moduleView).addView(childView);
                    }
                }

                break;
            default:
        }

        return moduleView;
    }

    protected View generateComponent(Component component) {
        View componentView = null;

        switch (ComponentType.valueOf(component.getType())) {
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

        return componentView;
    }

    protected boolean isTablet() {
        int largeScreenLayout =
                (getContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
        int xLargeScreenLayout =
                (getContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);

        return (largeScreenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                xLargeScreenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE);
    }

    protected boolean isLandscape() {
        int layoutDirection = getContext().getResources().getConfiguration().getLayoutDirection();
        return layoutDirection == Configuration.ORIENTATION_LANDSCAPE;
    }

    protected Layout_ getLayoutForScreen(Component component) {
        if (isLandscape() && isTablet()) {
            return getLayoutForTabletLandscape(component);
        } else if (isTablet()) {
            return getLayoutForTabletPortrait(component);
        }
        return getLayoutForPhone(component);
    }

    protected Layout getLayoutForScreen(ModuleList module) {
        if (isLandscape() && isTablet()) {
            return getLayoutForTabletLandscape(module);
        } else if (isTablet()) {
            return getLayoutForTabletPortrait(module);
        }
        return getLayoutForPhone(module);
    }

    protected Layout_ getLayoutForTabletLandscape(Component component) {
        for (Layout_ layout : component.getLayout()) {
            if (LayoutType.valueOf(layout.getType()) == LayoutType.TABLET_LANDSCAPE) {
                return layout;
            }
        }
        return null;
    }

    protected Layout getLayoutForTabletLandscape(ModuleList module) {
        for (Layout layout : module.getLayout()) {
            if (LayoutType.valueOf(layout.getType()) == LayoutType.TABLET_LANDSCAPE) {
                return layout;
            }
        }
        return null;
    }

    protected Layout_ getLayoutForTabletPortrait(Component component) {
        for (Layout_ layout : component.getLayout()) {
            if (LayoutType.valueOf(layout.getType()) == LayoutType.TABLET_PORTRAIT) {
                return layout;
            }
        }
        return null;
    }

    protected Layout getLayoutForTabletPortrait(ModuleList module) {
        for (Layout layout : module.getLayout()) {
            if (LayoutType.valueOf(layout.getType()) == LayoutType.TABLET_PORTRAIT) {
                return layout;
            }
        }
        return null;
    }

    protected Layout_ getLayoutForPhone(Component component) {
        for (Layout_ layout : component.getLayout()) {
            if (LayoutType.valueOf(layout.getType()) == LayoutType.TABLET_PORTRAIT) {
                return layout;
            }
        }
        return null;
    }

    protected Layout getLayoutForPhone(ModuleList module) {
        for (Layout layout : module.getLayout()) {
            if (LayoutType.valueOf(layout.getType()) == LayoutType.PHONE) {
                return layout;
            }
        }
        return null;
    }
}
