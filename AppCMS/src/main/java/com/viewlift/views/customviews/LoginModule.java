package com.viewlift.views.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.ModuleWithComponents;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.Map;

/**
 * Created by viewlift on 6/28/17.
 */

@SuppressLint("ViewConstructor")
public class LoginModule extends ModuleView {
    private static final String TAG = "LoginModule";

    private static final int NUM_CHILD_VIEWS = 2;

    private final ModuleWithComponents moduleInfo;
    private final Module moduleAPI;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;
    private final ViewCreator viewCreator;
    private final AppCMSPresenter.LaunchType launchType;
    Context context;
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
    private AppCMSAndroidModules appCMSAndroidModules;
    //    private String loginAction;
    private String loginInSignUpAction;
    // variable to track event time

    @SuppressWarnings("unchecked")
    public LoginModule(Context context,
                       ModuleWithComponents moduleInfo,
                       Module moduleAPI,
                       Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                       AppCMSPresenter appCMSPresenter,
                       ViewCreator viewCreator,
                       AppCMSAndroidModules appCMSAndroidModules) {
        super(context, moduleInfo, false);
        this.moduleInfo = moduleInfo;
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
        this.context = context;
        this.appCMSAndroidModules = appCMSAndroidModules;
        init();
        RecyclerView view = appCMSPresenter.getCurrentActivity().findViewById(R.id.home_nested_scroll_view);
        if(view != null){
            view.setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        }
    }

    public void init() {
        if (moduleInfo != null &&
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

            LinearLayout loginModuleSwitcherContainer = new LinearLayout(getContext());
            loginModuleSwitcherContainer.setOrientation(LinearLayout.HORIZONTAL);
            MarginLayoutParams loginModuleContainerLayoutParams =
                    new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            loginModuleContainerLayoutParams.setMargins((int) convertDpToPixel(getContext().getResources().getInteger(R.integer.app_cms_login_selector_margin), getContext()),
                    0,
                    (int) convertDpToPixel(getContext().getResources().getInteger(R.integer.app_cms_login_selector_margin), getContext()),
                    0);
            loginModuleSwitcherContainer.setLayoutParams(loginModuleContainerLayoutParams);
            loginModuleSwitcherContainer.setBackgroundColor(bgColor);
            loginModuleSwitcherContainer.setPadding(0, 0, 0, 0);

            topLayoutContainer.addView(loginModuleSwitcherContainer);

            ModuleWithComponents module = appCMSAndroidModules.getModuleListMap().get(moduleInfo.getBlockName());
            if (module == null) {
                module = moduleInfo;
            } else if (moduleInfo != null) {
                module.setId(moduleInfo.getId());
                module.setSettings(moduleInfo.getSettings());
                module.setSvod(moduleInfo.isSvod());
                module.setType(moduleInfo.getType());
                module.setView(moduleInfo.getView());
                module.setBlockName(moduleInfo.getBlockName());
            }

            if (module != null && module.getComponents() != null) {
                for (Component component : module.getComponents()) {
                    if (jsonValueKeyMap.get(component.getType()) == AppCMSUIKeyType.PAGE_LOGIN_COMPONENT_KEY &&
                            (launchType == AppCMSPresenter.LaunchType.LOGIN_AND_SIGNUP ||
                                    launchType == AppCMSPresenter.LaunchType.INIT_SIGNUP)) {
                        buttonSelectors[0] = new Button(getContext());
                        LinearLayout.LayoutParams loginSelectorLayoutParams =
                                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                        loginSelectorLayoutParams.weight = 1;
                        buttonSelectors[0].setText(R.string.app_cms_log_in_pager_title);
                        buttonSelectors[0].setTextColor(textColor);
                        buttonSelectors[0].setBackgroundColor(bgColor);
                        buttonSelectors[0].setLayoutParams(loginSelectorLayoutParams);

                        buttonSelectors[0].setOnClickListener((v) -> {
                            selectChild(0);
                            unselectChild(1);
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
                        addChildComponents(moduleView, component, 0, appCMSAndroidModules);
                        topLayoutContainer.addView(moduleView);
                    } else if (jsonValueKeyMap.get(component.getType()) == AppCMSUIKeyType.PAGE_SIGNUP_COMPONENT_KEY &&
                            (launchType == AppCMSPresenter.LaunchType.SUBSCRIBE ||
                                    launchType == AppCMSPresenter.LaunchType.LOGIN_AND_SIGNUP ||
                                    launchType == AppCMSPresenter.LaunchType.INIT_SIGNUP||
                                    launchType == AppCMSPresenter.LaunchType.SIGNUP)) {
                        if (launchType == AppCMSPresenter.LaunchType.LOGIN_AND_SIGNUP ||
                                launchType == AppCMSPresenter.LaunchType.INIT_SIGNUP||
                                launchType == AppCMSPresenter.LaunchType.SIGNUP) {
                            buttonSelectors[1] = new Button(getContext());
                            LinearLayout.LayoutParams signupSelectorLayoutParams =
                                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                            signupSelectorLayoutParams.weight = 1;
                            buttonSelectors[1].setText(R.string.app_cms_sign_up_pager_title);
                            buttonSelectors[1].setTextColor(textColor);
                            buttonSelectors[1].setBackgroundColor(bgColor);
                            signupSelectorLayoutParams.gravity = Gravity.END;
                            buttonSelectors[1].setLayoutParams(signupSelectorLayoutParams);
                            buttonSelectors[1].setOnClickListener((v) -> {
                                selectChild(1);
                                unselectChild(0);
                                if (appCMSPresenter.isAppSVOD()) {
                                    if (TextUtils.isEmpty(appCMSPresenter.getRestoreSubscriptionReceipt())) {
                                        //appCMSPresenter.sendCloseOthersAction(null,
                                        //true,
                                        //false);
                                        appCMSPresenter.navigateToSubscriptionPlansPage(appCMSPresenter.getLoginFromNavPage());

                                    } else {
                                        appCMSPresenter.setLaunchType(AppCMSPresenter.LaunchType.SUBSCRIBE);
                                    }
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
                        } else {
                            TextView signUpTitle = new TextView(getContext());
                            LinearLayout.LayoutParams signUpSelectorLayoutParams =
                                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                            signUpSelectorLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                            signUpTitle.setLayoutParams(signUpSelectorLayoutParams);
                            signUpTitle.setText(R.string.app_cms_sign_up_pager_title);
                            signUpTitle.setTextColor(textColor);
                            signUpTitle.setBackgroundColor(bgColor);
                            signUpTitle.setGravity(Gravity.CENTER_HORIZONTAL);
                            loginModuleSwitcherContainer.addView(signUpTitle);
                        }

                        ModuleView moduleView = new ModuleView<>(getContext(), component, false);
                        setViewHeight(getContext(), component.getLayout(), LayoutParams.MATCH_PARENT);
                        childViews[1] = moduleView;
                        addChildComponents(moduleView, component, 1, appCMSAndroidModules);
                        topLayoutContainer.addView(moduleView);
                    }
                }
            }

            childContainer.addView(topLayoutContainer);

            if (launchType == AppCMSPresenter.LaunchType.LOGIN_AND_SIGNUP) {
                selectChild(0);
                unselectChild(1);
            } else if (launchType == AppCMSPresenter.LaunchType.INIT_SIGNUP||
                    launchType == AppCMSPresenter.LaunchType.SIGNUP) {
                selectChild(1);
                unselectChild(0);
            }
        }
    }

    private void selectChild(int childIndex) {
        if (childViews != null &&
                childIndex < childViews.length &&
                childViews[childIndex] != null) {
            childViews[childIndex].setVisibility(VISIBLE);
//            buttonSelectors[childIndex].setAlpha(1.0f);
            setAlphaTextColorForSelector(buttonSelectors[childIndex], 200);
            applyUnderlineToComponent(underlineViews[childIndex], underlineColor);
            visibleEmailInputView = emailInputViews[childIndex];
            visiblePasswordInputView = passwordInputViews[childIndex];

            if (childIndex == 1) {
                visibleEmailInputView.setText("");
                visiblePasswordInputView.setText("");
            }
        }
    }

    private void unselectChild(int childIndex) {
        if (childViews != null &&
                childIndex < childViews.length &&
                childViews[childIndex] != null) {
            childViews[childIndex].setVisibility(GONE);
//            buttonSelectors[childIndex].setAlpha(0.6f);
            setAlphaTextColorForSelector(buttonSelectors[childIndex], 100);
            applyUnderlineToComponent(underlineViews[childIndex], bgColor);
        }
    }

    private void addChildComponents(ModuleView moduleView,
                                    Component subComponent,
                                    final int childIndex,
                                    final AppCMSAndroidModules appCMSAndroidModules) {
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
                        appCMSAndroidModules,
                        null,
                        moduleInfo.getSettings(),
                        jsonValueKeyMap,
                        appCMSPresenter,
                        false,
                        "",
                        moduleInfo.getId());
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
                    AppCMSUIKeyType componentKey = jsonValueKeyMap.get(component.getKey());
                    if (componentKey == null) {
                        componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                    }
                    switch (componentType) {
                        case PAGE_BUTTON_KEY:
                            if (componentKey == AppCMSUIKeyType.PAGE_LOGIN_BUTTON_KEY ||
                                    (componentKey == AppCMSUIKeyType.PAGE_SIGNUP_BUTTON_KEY)) {
                                loginInSignUpAction = component.getAction();
                            }

                            componentView.setOnClickListener(v -> {
                                //Log.d(TAG, "Button clicked: " + component.getAction());

                                if (!appCMSPresenter.isPageLoading() &&
                                        visibleEmailInputView != null &&
                                        visiblePasswordInputView != null) {
                                    appCMSPresenter.showLoadingDialog(true);
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
                            });
                            break;

                        case PAGE_TEXTFIELD_KEY:
                            switch (componentKey) {
                                case PAGE_EMAILTEXTFIELD_KEY:
                                case PAGE_EMAILTEXTFIELD2_KEY:
                                    emailInputViews[childIndex] = ((TextInputLayout) componentView).getEditText();
                                    appCMSPresenter.setCursorDrawableColor(emailInputViews[childIndex]);
                                    if (launchType == AppCMSPresenter.LaunchType.SUBSCRIBE) {
                                        visibleEmailInputView = emailInputViews[1];
                                    }
                                    break;

                                case PAGE_PASSWORDTEXTFIELD_KEY:
                                case PAGE_PASSWORDTEXTFIELD2_KEY:
                                    passwordInputViews[childIndex] = ((TextInputLayout) componentView).getEditText();
                                    passwordInputViews[childIndex]
                                            .setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    passwordInputViews[childIndex]
                                            .setImeOptions(EditorInfo.IME_ACTION_SEND | EditorInfo.IME_ACTION_GO);
                                    passwordInputViews[childIndex]
                                            .setTransformationMethod(PasswordTransformationMethod.getInstance());
                                    appCMSPresenter.setCursorDrawableColor(passwordInputViews[childIndex]);

                                    passwordInputViews[childIndex].setOnEditorActionListener((v, actionId, event) -> {
                                        boolean isImeActionSent = false;
                                        if (actionId == EditorInfo.IME_ACTION_SEND) {
                                            if (!appCMSPresenter.isPageLoading() &&
                                                    visibleEmailInputView != null &&
                                                    visiblePasswordInputView != null) {
                                                appCMSPresenter.showLoadingDialog(true);
                                                String[] authData = new String[2];
                                                authData[0] = visibleEmailInputView.getText().toString();
                                                authData[1] = visiblePasswordInputView.getText().toString();
                                                appCMSPresenter.launchButtonSelectedAction(null,
                                                        loginInSignUpAction,
                                                        null,
                                                        authData,
                                                        null,
                                                        true,
                                                        0,
                                                        null);
                                            }
                                            isImeActionSent = true;
                                        }
                                        return isImeActionSent;
                                    });

                                    appCMSPresenter.noSpaceInEditTextFilter(passwordInputViews[childIndex], context);
                                    if (launchType == AppCMSPresenter.LaunchType.SUBSCRIBE) {
                                        visiblePasswordInputView = passwordInputViews[1];
                                    }
                                    break;

                                default:
                                    break;
                            }
                            break;

                        case PAGE_SEPARATOR_VIEW_KEY:
                            break;

                        default:
                            componentView.setBackgroundColor(bgColor);
                            break;
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

    void setAlphaTextColorForSelector(Button button, int alpha) {
        String textColor = appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor();
        int color = Color.parseColor(textColor);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        button.setTextColor(Color.argb(alpha, r, g, b));
    }
}
