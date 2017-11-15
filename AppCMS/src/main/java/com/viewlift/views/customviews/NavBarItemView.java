package com.viewlift.views.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.activity.AppCMSPageActivity;

/**
 * Created by viewlift on 5/26/17.
 */

public class NavBarItemView extends LinearLayout {
    private static final String TAG = "NavBarItemView";
    private ImageView navImage;
    private TextView navLabel;
    private String tag;
    private boolean hasFocus;
    private int highlightColor;
    private String HOME_TAB_ICON_KEY = "icon-home";
    private String SHOW_TAB_ICON_KEY = "icon-grid";
    private String LIVE_TAB_ICON_KEY = "icon-live";
    private String TEAM_TAB_ICON_KEY = "icon-bracket";
    private String MENU_TAB_ICON_KEY = "icon-menu";
    private String SEARCH_TAB_ICON_KEY = "icon-search";
    private ModuleList navTabBar;
    private AppCMSPresenter appCMSPresenter;

    public NavBarItemView(Context context, ModuleList navigationItem, AppCMSPresenter appCMSPresenter) {
        super(context);
        this.navTabBar = navigationItem;
        this.appCMSPresenter = appCMSPresenter;
        init();
    }


    public NavBarItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavBarItemView(Context context,
                          @Nullable AttributeSet attrs,
                          int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NavBarItemView(Context context,
                          AttributeSet attrs,
                          int defStyleAttr,
                          int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        hasFocus = false;
        setOrientation(VERTICAL);
        createChildren();
    }

    public void select(boolean hasFocus, AppCMSPageActivity.NavTabTag navigationTabBar) {

        this.hasFocus = hasFocus;
        Resources resources = getResources();
        int color = ContextCompat.getColor(getContext(), R.color.colorNavBarText);
        if (hasFocus) {
            color = highlightColor;
            if (navigationTabBar.getNavigationModuleItem().getIsBackgroundSelectable()) {
                setTabBg();
            }
        } else {
            this.setBackgroundResource(0);
        }

        for (int i = 0; i < navigationTabBar.getNavigationModuleItem().getComponents().size(); i++) {
            String type = navigationTabBar.getNavigationModuleItem().getComponents().get(i).getType();
            if (navLabel != null && type.equalsIgnoreCase("label") && navigationTabBar.getNavigationModuleItem().getComponents().get(i).isSelectable()) {
                navLabel.setTextColor(color);
            } else if (navImage != null && type.equalsIgnoreCase("image") && navigationTabBar.getNavigationModuleItem().getComponents().get(i).isSelectable()) {
                applyTintToDrawable(navImage.getDrawable(), color);
            }

        }

    }

    private void setTabBg() {
        int[] ButtonColors = {Color.parseColor("#f4181c"), Color.parseColor("#00000000")};
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP, ButtonColors);
        gradientDrawable.setCornerRadius(0f);

        this.setBackground(gradientDrawable);

    }


    public void createChildren() {

        for (int i = 0; i < navTabBar.getComponents().size(); i++) {

            String componentType = navTabBar.getComponents().get(i).getType();
            switch (componentType) {
                case "image":
                    navImage = new ImageView(getContext());
                    int navImageWidth =
                            (int) BaseView.convertDpToPixel(getContext().getResources().getDimension(R.dimen.nav_image_width), getContext());
                    int navImageHeight =
                            (int) BaseView.convertDpToPixel(getContext().getResources().getDimension(R.dimen.nav_image_height), getContext());

                    if (BaseView.isTablet(getContext())) {
                        navImageWidth =
                                (int) BaseView.convertDpToPixel(getContext().getResources().getDimension(R.dimen.nav_item_large_width), getContext());
                        navImageHeight =
                                (int) BaseView.convertDpToPixel(getContext().getResources().getDimension(R.dimen.nav_item_large_height), getContext());
                    }

                    LinearLayout.LayoutParams navImageLayoutParams =
                            new LinearLayout.LayoutParams(navImageWidth, navImageHeight);
                    navImageLayoutParams.gravity = Gravity.CENTER;
                    navImage.setLayoutParams(navImageLayoutParams);
                    addView(navImage);

                    break;

                case "label":
                    navLabel = new TextView(getContext());
                    LinearLayout.LayoutParams navLabelLayoutParams =
                            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                    navLabelLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                    navLabel.setLayoutParams(navLabelLayoutParams);
                    navLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorNavBarText));
                    navLabel.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
                    addView(navLabel);

                    break;

            }
        }

        LinearLayout.LayoutParams parentLayoutParams =
                new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        parentLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        this.setLayoutParams(parentLayoutParams);

        int navItemMargin = (int) BaseView.convertDpToPixel(getContext().getResources().getDimension(R.dimen.nav_item_margin), getContext());

        setPadding(0, navItemMargin, 0, 0);

    }

    public void setTabImage(String tabDisplayPath) {
        Resources resources = getResources();
        int drawableId;
        String drawableName = null;
        if (tabDisplayPath == null) {
            drawableName = resources.getString(R.string.app_cms_menu_icon_name);
            drawableId = resources.getIdentifier(drawableName,
                    "drawable",
                    getContext().getPackageName());

        } else {
            drawableId = resources.getIdentifier(tabDisplayPath.replace("-", "_"), "drawable", appCMSPresenter.getCurrentActivity().getPackageName());

        }
        if (navImage != null) {
            navImage.setImageDrawable(ContextCompat.getDrawable(getContext(), drawableId));
        }
    }

    public void setImage(String drawableName) {
        Resources resources = getResources();
        int drawableId = resources.getIdentifier(drawableName,
                "drawable",
                getContext().getPackageName());
        if (navImage != null) {
            navImage.setImageDrawable(ContextCompat.getDrawable(getContext(), drawableId));
        }
    }

    public void setLabel(String label) {
        if (navLabel != null) {
            navLabel.setText(label);
        }
    }

    public void hideLabel() {
        navLabel.setVisibility(GONE);
    }

    public boolean isItemSelected() {
        return hasFocus;
    }

    public void setHighlightColor(int highlightColor) {
        this.highlightColor = highlightColor;
    }

    private void applyTintToDrawable(@Nullable Drawable drawable, int color) {
        if (drawable != null) {
            drawable.setTint(color);
            drawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        }
    }

}
