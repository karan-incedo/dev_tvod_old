package com.viewlift.tv.views.fragment;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.android.NavigationUser;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.activity.AppCMSTVPlayVideoActivity;
import com.viewlift.tv.views.activity.AppCmsHomeActivity;
import com.viewlift.tv.views.component.AppCMSTVViewComponent;
import com.viewlift.tv.views.component.DaggerAppCMSTVViewComponent;
import com.viewlift.tv.views.customviews.TVModuleView;
import com.viewlift.tv.views.customviews.TVPageView;
import com.viewlift.tv.views.module.AppCMSTVPageViewModule;
import com.viewlift.views.binders.AppCMSBinder;

import java.util.List;

import rx.functions.Action1;

public class AppCmsLoginDialogFragment extends DialogFragment {

    private AppCMSPresenter appCMSPresenter;
    private AppCMSTVViewComponent appCmsViewComponent;
    private TVPageView tvPageView;
    private AppCmsSubNavigationFragment appCmsSubNavigationFragment;
    private Typeface extraBoldTypeFace;
    private Component extraBoldComp;
    private Typeface semiBoldTypeFace;
    private Component semiBoldComp;
    private Context mContext;
    FrameLayout pageHolder;
    RelativeLayout subNavContaineer;
    private TextView loginView;
    private TextView signupView;
    private ImageView loginIcon;
    private ImageView signupIcon;
    private View loginContaineer;
    private View signupContaineer;

    public AppCmsLoginDialogFragment() {
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public static AppCmsLoginDialogFragment newInstance(AppCMSBinder appCMSBinder) {
        AppCmsLoginDialogFragment fragment = new AppCmsLoginDialogFragment();
        Bundle args = new Bundle();
        args.putBinder("app_cms_binder_key", appCMSBinder);
        fragment.setArguments(args);
        return fragment;
    }

    private AppCMSBinder appCMSBinder;
    private boolean isLoginPage;
    private TextView subscriptionTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appCMSBinder = (AppCMSBinder) getArguments().getBinder("app_cms_binder_key");
            isLoginPage = getArguments().getBoolean("isLoginPage");
        }
        appCMSPresenter =
                ((AppCMSApplication) getActivity().getApplication()).getAppCMSPresenterComponent().appCMSPresenter();
        appCmsViewComponent = buildAppCMSViewComponent();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setTypeFaceValue(appCMSPresenter);

        if (appCmsViewComponent == null) {
            appCmsViewComponent = buildAppCMSViewComponent();
        }

        if (appCmsViewComponent != null) {
            tvPageView = appCmsViewComponent.appCMSTVPageView();
        } else {
            tvPageView = null;
        }

        if (tvPageView != null) {
            if (tvPageView.getParent() != null) {
                ((ViewGroup) tvPageView.getParent()).removeAllViews();
            }
        }
        if (container != null) {
            container.removeAllViews();
        }

        int layoutResourceId = R.layout.app_cms_login_dialog_fragment;
        if (appCMSPresenter.isLeftNavigationEnabled()) {
            layoutResourceId = R.layout.app_cms_left_nav_login_dialog_fragment;
        }
        View view = inflater.inflate(layoutResourceId, null);

        subscriptionTitle = (TextView) view.findViewById(R.id.nav_top_line);

        if (subscriptionTitle != null) {
            if (appCMSPresenter.getTemplateType()
                    .equals(AppCMSPresenter.TemplateType.SPORTS)) {
                updateSubscriptionStrip();
            } else {
                subscriptionTitle.setVisibility(View.GONE);
            }

            if (subscriptionTitle != null && appCMSPresenter.getTemplateType()
                    .equals(AppCMSPresenter.TemplateType.SPORTS) && appCMSPresenter.isAppSVOD()) {
                updateSubscriptionStrip();
            } else {
                subscriptionTitle.setVisibility(View.GONE);
            }

        /*if(!appCMSPresenter.isLeftNavigationEnabled())
        view.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBackgroundColor()));*/


            LinearLayout subNavHolder = (LinearLayout) view.findViewById(R.id.sub_navigation_placholder);
            subNavContaineer = (RelativeLayout) view.findViewById(R.id.sub_navigation_containeer);

            String backGroundColor = Utils.getBackGroundColor(getActivity(), appCMSPresenter);
            view.setBackgroundColor(Color.parseColor(backGroundColor));
            loginView = (TextView) view.findViewById(R.id.textView_login);
            signupView = (TextView) view.findViewById(R.id.textview_signup);
            loginIcon = (ImageView) view.findViewById(R.id.nav_item_login_image);
            signupIcon = (ImageView) view.findViewById(R.id.nav_item_logout_image);
            loginView.setTextColor(Color.parseColor(appCMSPresenter.getAppCtaTextColor()));
            signupView.setTextColor(Color.parseColor(appCMSPresenter.getAppCtaTextColor()));


            if(appCMSPresenter.isLeftNavigationEnabled()) {
                loginView.setTextSize(getResources().getDimension(R.dimen.appcms_tv_leftnavigation_textSize));
                signupView.setTextSize(getResources().getDimension(R.dimen.appcms_tv_leftnavigation_textSize));
            }
            loginContaineer = view.findViewById(R.id.nav_item_login_layout);
            signupContaineer = view.findViewById(R.id.nav_item_logout_layout);

            if (appCMSPresenter.isLeftNavigationEnabled()) {
                subNavHolder.setOrientation(LinearLayout.VERTICAL);
                subNavContaineer.getBackground().setTint(Color.parseColor(appCMSPresenter.getAppBackgroundColor()));

                List<NavigationUser> navigationUser = appCMSPresenter.getNavigation().getNavigationUser();

                for (NavigationUser navigation : navigationUser) {
                    String title = navigation.getTitle();
                    if (("Log In".equalsIgnoreCase(title) || "Sign In".equalsIgnoreCase(title)) && navigation.getIcon() != null) {
                        loginIcon.setImageResource(Utils.getIcon(navigation.getIcon(), mContext));
                        if (null != loginIcon.getDrawable()) {
                            loginIcon.getDrawable().setTint(Utils.getComplimentColor(appCMSPresenter.getGeneralBackgroundColor()));
                            loginIcon.getDrawable().setTintMode(PorterDuff.Mode.MULTIPLY);
                        }
                    }

                    if ("Sign Up".equalsIgnoreCase(title) && navigation.getIcon() != null) {
                        signupIcon.setImageResource(Utils.getIcon(navigation.getIcon(), mContext));
                        if (null != signupIcon.getDrawable()) {
                            signupIcon.getDrawable().setTint(Utils.getComplimentColor(appCMSPresenter.getGeneralBackgroundColor()));
                            signupIcon.getDrawable().setTintMode(PorterDuff.Mode.MULTIPLY);
                        }
                    }
                }
            }


            loginView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        subNavHolder.setAlpha(1f);
                    } else {
                        subNavHolder.setAlpha(0.52f);
                    }
                }
            });

            signupView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        subNavHolder.setAlpha(1f);
                    } else {
                        subNavHolder.setAlpha(0.52f);
                    }
                }
            });

            loginView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {

                    int keyCode = keyEvent.getKeyCode();
                    int action = keyEvent.getAction();
                    if (action == KeyEvent.ACTION_DOWN) {
                        if (appCMSPresenter.isLeftNavigationEnabled()) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_LEFT:
                                case KeyEvent.KEYCODE_DPAD_RIGHT:
                                    toogleLeftnavPanel(false);
                                    return true;
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    focusLoginView(signupView, loginView);
                                    return true;
                                case KeyEvent.KEYCODE_DPAD_DOWN:
                                    focusSignupView(signupView, loginView);
                                    return true;
                            }
                        } else {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_LEFT:
                                    return true;
                                case KeyEvent.KEYCODE_DPAD_RIGHT:
                                    focusSignupView(signupView, loginView);
                                    return true;
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    return true;
                                case KeyEvent.KEYCODE_DPAD_DOWN:
                                    focusLoginView(signupView, loginView);
                                    return false;
                            }
                        }
                    }
                    return false;
                }
            });

            signupView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    int keyCode = keyEvent.getKeyCode();
                    int action = keyEvent.getAction();
                    if (action == KeyEvent.ACTION_DOWN) {
                        if (appCMSPresenter.isLeftNavigationEnabled()) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_LEFT:
                                case KeyEvent.KEYCODE_DPAD_RIGHT:
                                    toogleLeftnavPanel(false);
                                    return true;
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    focusLoginView(signupView, loginView);
                                    return true;
                                case KeyEvent.KEYCODE_DPAD_DOWN:
                                    focusSignupView(signupView, loginView);
                                    return true;
                            }
                        } else {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_LEFT:
                                    focusLoginView(signupView, loginView);
                                    return true;
                                case KeyEvent.KEYCODE_DPAD_RIGHT:
                                    return true;
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    return true;
                                case KeyEvent.KEYCODE_DPAD_DOWN:
                                    focusLoginView(signupView, loginView);
                                    return false;
                            }
                        }
                    }
                    return false;
                }
            });

            focusLoginView(signupView, loginView);

            signupView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavigationUser navigationUser = appCMSPresenter.getSignUpNavigation();
                    appCMSPresenter.navigateToTVPage(
                            navigationUser.getPageId(),
                            navigationUser.getTitle(),
                            navigationUser.getUrl(),
                            false,
                            Uri.EMPTY,
                            false,
                            false,
                            true);
                }
            });


            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_BACK
                            && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (null != onBackKeyListener)
                            onBackKeyListener.call("");
                    }
                    return false;
                }
            });

            pageHolder = (FrameLayout) view.findViewById(R.id.profile_placeholder);
            pageHolder.addView(tvPageView);


            if (null != tvPageView && tvPageView.getChildrenContainer().getChildAt(0) instanceof TVModuleView) {
                TVModuleView tvModuleView = (TVModuleView) tvPageView.getChildrenContainer().getChildAt(0);
                EditText emailBox = ((EditText) tvModuleView.findViewById(R.id.email_edit_box));
                EditText passwordBox = ((EditText) tvModuleView.findViewById(R.id.password_edit_box));
                Button activateDevice = ((Button) tvModuleView.findViewById(R.id.btn_activate_device));
                Button loginButton = ((Button) tvModuleView.findViewById(R.id.btn_login));
                emailBox.setOnKeyListener(leftNavigationListener);
                passwordBox.setOnKeyListener(leftNavigationListener);
                if(null != activateDevice)
                activateDevice.setOnKeyListener(leftNavigationListener);
                loginButton.setOnKeyListener(leftNavigationListener);

            }
        }
        return view;
    }



    View.OnKeyListener leftNavigationListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                    && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (appCMSPresenter.isLeftNavigationEnabled())
                    toogleLeftnavPanel(true);
            }
            return false;
        }
    };

    private void updateSubscriptionStrip() {
        /*Check Subscription in case of SPORTS TEMPLATE*/
        if (appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.SPORTS) {
            if (appCMSPresenter.isAppSVOD()) {
                if (!appCMSPresenter.isUserLoggedIn()) {
                    setSubscriptionText(false);
                } else {
                    appCMSPresenter.getSubscriptionData(appCMSUserSubscriptionPlanResult -> {
                        try {
                            if (appCMSUserSubscriptionPlanResult != null) {
                                String subscriptionStatus = appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus();
                                if (subscriptionStatus.equalsIgnoreCase("COMPLETED") ||
                                        subscriptionStatus.equalsIgnoreCase("DEFERRED_CANCELLATION")) {
                                    setSubscriptionText(true);
                                } else {
                                    setSubscriptionText(false);
                                }
                            } else {
                                setSubscriptionText(false);
                            }
                        } catch (Exception e) {
                            setSubscriptionText(false);
                        }
                    });
                }
            } else {
                setSubscriptionText(true);
            }
        }
    }

    private void setSubscriptionText(boolean isSubscribe) {
        String message = getResources().getString(R.string.blank_string);
        if (!isSubscribe) {
            if (null != appCMSPresenter && null != appCMSPresenter.getNavigation()
                    && null != appCMSPresenter.getNavigation().getSettings()
                    && null != appCMSPresenter.getNavigation().getSettings().getPrimaryCta()
                    ) {
                message = appCMSPresenter.getNavigation().getSettings().getPrimaryCta().getBannerText() +
                        appCMSPresenter.getNavigation().getSettings().getPrimaryCta().getCtaText();
            } else {
                message = getResources().getString(R.string.watch_live_text);
            }
        }
        subscriptionTitle.setText(message);
        subscriptionTitle.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppCtaBackgroundColor()));
        subscriptionTitle.setTextColor(Color.parseColor(appCMSPresenter.getAppCtaTextColor()));
        LinearLayout.LayoutParams textLayoutParams = (LinearLayout.LayoutParams) subscriptionTitle.getLayoutParams();
        if (message.length() == 0) {
            textLayoutParams.height = 10;
        } else {
            textLayoutParams.height = 40;
        }
        subscriptionTitle.setLayoutParams(textLayoutParams);
    }


    private Action1<String> onBackKeyListener;

    public void setBackKeyListener(Action1<String> onBackKeyListener) {
        this.onBackKeyListener = onBackKeyListener;
    }


    private void focusSignupView(TextView signupView, TextView loginView) {
        if (appCMSPresenter.isLeftNavigationEnabled()) {
            signupContaineer.setBackground(
                    Utils.getNavigationSelectedState(mContext, appCMSPresenter, true, Color.parseColor(appCMSPresenter.getAppBackgroundColor())));
        } else {
            signupView.setBackground(Utils.getNavigationSelectedState(mContext, appCMSPresenter, true, Color.parseColor("#000000")));
        }

        if (appCMSPresenter.isLeftNavigationEnabled()) {
            signupContaineer.setAlpha(1.0f);
        } else {
            signupView.setTypeface(extraBoldTypeFace);
        }
        if (appCMSPresenter.isLeftNavigationEnabled()) {
            loginContaineer.setBackground(null);
        } else {
            loginView.setBackground(null);
        }

        if (appCMSPresenter.isLeftNavigationEnabled()) {
            loginContaineer.setAlpha(0.3f);
        } else {
            loginView.setTypeface(semiBoldTypeFace);
        }
        signupView.requestFocus();
    }

    private void focusLoginView(TextView signupView, TextView loginView) {
        if (appCMSPresenter.isLeftNavigationEnabled()) {
            loginContaineer.setBackground(
                    Utils.getNavigationSelectedState(mContext, appCMSPresenter, true, Color.parseColor(appCMSPresenter.getAppBackgroundColor())));
        } else {
            loginView.setBackground(Utils.getNavigationSelectedState(mContext, appCMSPresenter, true, Color.parseColor("#000000")));
        }
        if (appCMSPresenter.isLeftNavigationEnabled()) {
            loginContaineer.setAlpha(1.0f);
        } else {
            loginView.setTypeface(extraBoldTypeFace);
        }
        if (appCMSPresenter.isLeftNavigationEnabled()) {
            signupContaineer.setBackground(null);
        } else {
            signupView.setBackground(null);
        }

        if (appCMSPresenter.isLeftNavigationEnabled()) {
            signupContaineer.setAlpha(0.3f);
        } else {
            signupView.setTypeface(semiBoldTypeFace);
        }
        loginView.requestFocus();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != getActivity() && getActivity() instanceof AppCmsHomeActivity) {
            ((AppCmsHomeActivity) getActivity()).closeSignUpDialog();
        } else if (null != getActivity() && getActivity() instanceof AppCMSTVPlayVideoActivity) {
            ((AppCMSTVPlayVideoActivity) getActivity()).closeSignUpDialog();
        }
    }

    private void setTypeFaceValue(AppCMSPresenter appCMSPresenter) {
        if (null == extraBoldTypeFace) {
            extraBoldComp = new Component();
            extraBoldComp.setFontFamily(getResources().getString(R.string.app_cms_page_font_family_key));
            extraBoldComp.setFontWeight(getResources().getString(R.string.app_cms_page_font_extrabold_key));
            extraBoldTypeFace = Utils.getTypeFace(mContext, appCMSPresenter.getJsonValueKeyMap()
                    , extraBoldComp);
        }

        if (null == semiBoldTypeFace) {
            semiBoldComp = new Component();
            semiBoldComp.setFontFamily(getResources().getString(R.string.app_cms_page_font_family_key));
            semiBoldComp.setFontWeight(getResources().getString(R.string.app_cms_page_font_semibold_key));
            semiBoldTypeFace = Utils.getTypeFace(mContext, appCMSPresenter.getJsonValueKeyMap()
                    , semiBoldComp);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        super.onActivityCreated(bundle);
    }


    public AppCMSTVViewComponent buildAppCMSViewComponent() {
        return DaggerAppCMSTVViewComponent.builder()
                .appCMSTVPageViewModule(new AppCMSTVPageViewModule(mContext,
                        appCMSBinder.getAppCMSPageUI(),
                        appCMSBinder.getAppCMSPageAPI(),
                        appCMSPresenter.getJsonValueKeyMap(),
                        appCMSPresenter,
                        true
                ))
                .build();
    }


    private void toogleLeftnavPanel(boolean show) {
        if (null != subNavContaineer) {
            subNavContaineer.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        if (show) {
            focusLoginView(signupView, loginView);
        } else {
            loginView.clearFocus();
            subNavContaineer.clearFocus();
        }
    }

}
