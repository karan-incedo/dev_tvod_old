package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.models.data.appcms.ui.page.ModuleWithComponents;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.Map;

import com.viewlift.R;

/**
 * Created by viewlift on 6/28/17.
 */

public class LoginModule extends ModuleView {
    private static final String TAG = "LoginModule";

    private static final int NUM_CHILD_VIEWS = 2;

    private final ModuleWithComponents module;
    private final Module moduleAPI;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;
    private final ViewCreator viewCreator;
    private final AppCMSPresenter.LaunchType launchType;
    private Button[] buttonSelectors;
    private ModuleView[] childViews;
    private GradientDrawable[] underlineViews;
    private EditText[] emailInputViews;
    private EditText[] passwordInputViews;
    private int underlineColor;
    private int transparentColor;
    private int bgColor;
    private int loginBorderPadding;
    private EditText visibleEmailInputView;
    private EditText visiblePasswordInputView;
    private ImageButton closeButton;

    public LoginModule(Context context,
                       ModuleWithComponents module,
                       Module moduleAPI,
                       Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                       AppCMSPresenter appCMSPresenter,
                       ViewCreator viewCreator) {
        super(context, module, false);
        this.module = module;
        this.moduleAPI = moduleAPI;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.appCMSPresenter = appCMSPresenter;
        this.viewCreator = viewCreator;
        this.buttonSelectors = new Button[NUM_CHILD_VIEWS];
        this.childViews = new ModuleView[NUM_CHILD_VIEWS];
        this.underlineViews = new GradientDrawable[NUM_CHILD_VIEWS];
        this.emailInputViews = new EditText[NUM_CHILD_VIEWS];
        this.passwordInputViews = new EditText[NUM_CHILD_VIEWS];
        this.loginBorderPadding = context.getResources().getInteger(R.integer.app_cms_login_underline_padding);
        this.launchType = appCMSPresenter.getLaunchType();
        init();
    }

    public void init() {
        if (module != null &&
                moduleAPI != null &&
                jsonValueKeyMap != null &&
                appCMSPresenter != null &&
                viewCreator != null) {
            AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
            underlineColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getPageTitleColor());
            transparentColor = ContextCompat.getColor(getContext(), android.R.color.transparent);
            bgColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getBackgroundColor());
            int textColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getTextColor());
            ViewGroup childContainer = getChildrenContainer();
            childContainer.setBackgroundColor(bgColor);

            LinearLayout topLayoutContainer = new LinearLayout(getContext());
            MarginLayoutParams topLayoutContainerLayoutParams =
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            topLayoutContainerLayoutParams.setMargins(0, 0, 0, 0);
            topLayoutContainer.setLayoutParams(topLayoutContainerLayoutParams);
            topLayoutContainer.setPadding(0, 0, 0, 0);
            topLayoutContainer.setOrientation(LinearLayout.VERTICAL);

            FrameLayout loginModuleSwitcherContainer = new FrameLayout(getContext());
            MarginLayoutParams loginModuleContainerLayoutParams =
                    new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            loginModuleContainerLayoutParams.setMargins(0, 0, 0, 0);
            loginModuleSwitcherContainer.setLayoutParams(loginModuleContainerLayoutParams);
            loginModuleSwitcherContainer.setBackgroundColor(bgColor);
            loginModuleSwitcherContainer.setPadding(0, 0, 0, 0);

            final String closeAction = getContext().getString(R.string.app_cms_action_close_key);
            closeButton = new ImageButton(getContext());
            closeButton.setImageResource(R.drawable.cancel);
            closeButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            closeButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!appCMSPresenter.launchButtonSelectedAction(null,
                            closeAction,
                            null,
                            null,
                            null,
                            false,
                            0,
                            null)) {
                        Log.e(TAG, "Could not launch action: " +
                                " action: " +
                                closeAction);
                    }
                }
            });
            closeButton.setScaleType(ImageView.ScaleType.FIT_CENTER);

            int closeButtonImageSize = (int) getContext().getResources().getDimension(R.dimen.close_button_size);
            int closeButtonMargin =
                    (int) convertDpToPixel(getContext().getResources().getDimension(R.dimen.close_button_margin),
                            getContext());
            LinearLayout.LayoutParams closeButtonLayoutParams =
                    new LinearLayout.LayoutParams(closeButtonImageSize, closeButtonImageSize);
            closeButtonLayoutParams.setMargins(closeButtonMargin,
                    closeButtonMargin,
                    closeButtonMargin,
                    closeButtonMargin);
            closeButtonLayoutParams.gravity = Gravity.END;
            closeButton.setLayoutParams(closeButtonLayoutParams);
            closeButton.setPadding(0, 0, 0, 0);

            topLayoutContainer.addView(closeButton);
            topLayoutContainer.addView(loginModuleSwitcherContainer);

            for (Component component : module.getComponents()) {
                if (jsonValueKeyMap.get(component.getType()) == AppCMSUIKeyType.PAGE_LOGIN_COMPONENT_KEY &&
                        launchType == AppCMSPresenter.LaunchType.LOGIN_AND_SIGNUP) {
                    buttonSelectors[0] = new Button(getContext());
                    FrameLayout.LayoutParams loginSelectorLayoutParams =
                            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    loginSelectorLayoutParams.setMargins((int) convertDpToPixel(getContext().getResources().getInteger(R.integer.app_cms_login_selector_margin), getContext()),
                            0,
                            0,
                            0);
                    buttonSelectors[0].setText(R.string.app_cms_log_in_pager_title);
                    buttonSelectors[0].setTextColor(textColor);
                    buttonSelectors[0].setBackgroundColor(bgColor);
                    buttonSelectors[0].setLayoutParams(loginSelectorLayoutParams);

                    buttonSelectors[0].setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectChild(0);
                            unselectChild(1);
                        }
                    });

                    underlineViews[0] = new GradientDrawable();
                    underlineViews[0].setShape(GradientDrawable.LINE);
                    buttonSelectors[0].setCompoundDrawablePadding(loginBorderPadding);
                    Rect textBounds = new Rect();
                    Paint textPaint = buttonSelectors[0].getPaint();
                    textPaint.getTextBounds(buttonSelectors[0].getText().toString(),
                            0,
                            buttonSelectors[0].getText().length(),
                            textBounds);
                    Rect bounds = new Rect(0,
                            textBounds.top,
                            textBounds.width() + loginBorderPadding,
                            textBounds.bottom);
                    underlineViews[0].setBounds(bounds);
                    buttonSelectors[0].setCompoundDrawables(null, null, null, underlineViews[0]);
                    loginModuleSwitcherContainer.addView(buttonSelectors[0]);

                    ModuleView moduleView = new ModuleView<>(getContext(), component, false);
                    setViewHeight(getContext(), component.getLayout(), LayoutParams.MATCH_PARENT);
                    childViews[0] = moduleView;
                    addChildComponents(moduleView, component, 0);
                    topLayoutContainer.addView(moduleView);
                } else if (jsonValueKeyMap.get(component.getType()) == AppCMSUIKeyType.PAGE_SIGNUP_COMPONENT_KEY &&
                        (launchType == AppCMSPresenter.LaunchType.SUBSCRIBE ||
                        launchType == AppCMSPresenter.LaunchType.LOGIN_AND_SIGNUP)) {
                    if (launchType == AppCMSPresenter.LaunchType.LOGIN_AND_SIGNUP) {
                        buttonSelectors[1] = new Button(getContext());
                        FrameLayout.LayoutParams signupSelectorLayoutParams =
                                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        signupSelectorLayoutParams.setMargins(0,
                                0,
                                (int) convertDpToPixel(getContext().getResources().getInteger(R.integer.app_cms_login_selector_margin), getContext()),
                                0);
                        buttonSelectors[1].setText(R.string.app_cms_sign_up_pager_title);
                        buttonSelectors[1].setTextColor(textColor);
                        buttonSelectors[1].setBackgroundColor(bgColor);
                        signupSelectorLayoutParams.gravity = Gravity.END;
                        buttonSelectors[1].setLayoutParams(signupSelectorLayoutParams);
                        buttonSelectors[1].setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectChild(1);
                                unselectChild(0);
                            }
                        });

                        underlineViews[1] = new GradientDrawable();
                        underlineViews[1].setShape(GradientDrawable.LINE);
                        buttonSelectors[1].setCompoundDrawablePadding(loginBorderPadding);
                        Rect textBounds = new Rect();
                        Paint textPaint = buttonSelectors[1].getPaint();
                        textPaint.getTextBounds(buttonSelectors[1].getText().toString(),
                                0,
                                buttonSelectors[1].getText().length(),
                                textBounds);
                        Rect bounds = new Rect(0,
                                textBounds.top,
                                textBounds.width() + loginBorderPadding,
                                textBounds.bottom);
                        underlineViews[1].setBounds(bounds);
                        buttonSelectors[1].setCompoundDrawables(null, null, null, underlineViews[1]);
                        loginModuleSwitcherContainer.addView(buttonSelectors[1]);
                    }

                    ModuleView moduleView = new ModuleView<>(getContext(), component, false);
                    setViewHeight(getContext(), component.getLayout(), LayoutParams.MATCH_PARENT);
                    childViews[1] = moduleView;
                    addChildComponents(moduleView, component, 1);
                    topLayoutContainer.addView(moduleView);
                }
            }

            childContainer.addView(topLayoutContainer);

            if (launchType == AppCMSPresenter.LaunchType.LOGIN_AND_SIGNUP) {
                selectChild(0);
                unselectChild(1);
            }
        }
    }

    private void selectChild(int childIndex) {
        childViews[childIndex].setVisibility(VISIBLE);
        buttonSelectors[childIndex].setAlpha(1.0f);
        applyUnderlineToComponent(underlineViews[childIndex], underlineColor);
        visibleEmailInputView = emailInputViews[childIndex];
        visiblePasswordInputView = passwordInputViews[childIndex];
        if (childIndex == 1) {
            visibleEmailInputView.setText("");
            visiblePasswordInputView.setText("");
        }
    }

    private void unselectChild(int childIndex) {
        childViews[childIndex].setVisibility(GONE);
        buttonSelectors[childIndex].setAlpha(0.6f);
        applyUnderlineToComponent(underlineViews[childIndex], bgColor);
    }

    private void addChildComponents(ModuleView moduleView,
                                    Component subComponent,
                                    final int childIndex) {
        ViewCreator.ComponentViewResult componentViewResult = viewCreator.getComponentViewResult();
        ViewGroup subComponentChildContainer = moduleView.getChildrenContainer();
        float parentYAxis = 2 * getYAxis(getContext(), subComponent.getLayout(), 0.0f);
        if (componentViewResult != null && subComponentChildContainer != null) {
            for (int i = 1; i < subComponent.getComponents().size(); i++) {
                final Component component = subComponent.getComponents().get(i);
                viewCreator.createComponentView(getContext(),
                        component,
                        component.getLayout(),
                        moduleAPI,
                        null,
                        module.getSettings(),
                        jsonValueKeyMap,
                        appCMSPresenter,
                        false,
                        "");
                View componentView = componentViewResult.componentView;
                if (componentView != null) {
                    float componentYAxis = getYAxis(getContext(),
                            component.getLayout(),
                            0.0f);
                    if (!component.isyAxisSetManually()) {
                        setYAxis(getContext(),
                                component.getLayout(),
                                componentYAxis - parentYAxis);
                        component.setyAxisSetManually(true);
                    }
                    subComponentChildContainer.addView(componentView);
                    moduleView.setComponentHasView(i, true);
                    moduleView.setViewMarginsFromComponent(component,
                            componentView,
                            subComponent.getLayout(),
                            subComponentChildContainer,
                            false,
                            jsonValueKeyMap,
                            componentViewResult.useMarginsAsPercentagesOverride,
                            componentViewResult.useWidthOfScreen,
                            "");
                    AppCMSUIKeyType componentType = jsonValueKeyMap.get(component.getType());
                    if (componentType == null) {
                        componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                    }
                    switch (componentType) {
                        case PAGE_BUTTON_KEY:
                            componentView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG, "Button clicked: " + component.getAction());
                                    if (visibleEmailInputView != null && visiblePasswordInputView != null) {
                                        String[] authData = new String[2];
                                        authData[0] = visibleEmailInputView.getText().toString();
                                        authData[1] = visiblePasswordInputView.getText().toString();
                                        appCMSPresenter.launchButtonSelectedAction(null,
                                                component.getAction(),
                                                null,
                                                authData,
                                                null,
                                                true,
                                                0,
                                                null);
                                    }
                                }
                            });
                            break;
                        case PAGE_TEXTFIELD_KEY:
                            AppCMSUIKeyType componentKey = jsonValueKeyMap.get(component.getKey());
                            if (componentKey == null) {
                                componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                            }
                            switch (componentKey) {
                                case PAGE_EMAILTEXTFIELD_KEY:
                                case PAGE_EMAILTEXTFIELD2_KEY:
                                    emailInputViews[childIndex] = ((TextInputLayout) componentView).getEditText();
                                    if (launchType == AppCMSPresenter.LaunchType.SUBSCRIBE) {
                                        visibleEmailInputView = emailInputViews[1];
                                    }
                                    break;
                                case PAGE_PASSWORDTEXTFIELD_KEY:
                                case PAGE_PASSWORDTEXTFIELD2_KEY:
                                    passwordInputViews[childIndex] = ((TextInputLayout) componentView).getEditText();
                                    if (launchType == AppCMSPresenter.LaunchType.SUBSCRIBE) {
                                        visiblePasswordInputView = passwordInputViews[1];
                                    }
                                    break;
                                default:
                            }
                            break;
                        case PAGE_SEPARATOR_VIEW_KEY:
                            break;
                        default:
                            componentView.setBackgroundColor(bgColor);
                    }
                } else {
                    moduleView.setComponentHasView(i, false);
                }
            }
        }
    }

    private void applyUnderlineToComponent(GradientDrawable underline, int color) {
        underline.setStroke((int) convertDpToPixel(2, getContext()), color);
        underline.setColor(transparentColor);
    }
}
