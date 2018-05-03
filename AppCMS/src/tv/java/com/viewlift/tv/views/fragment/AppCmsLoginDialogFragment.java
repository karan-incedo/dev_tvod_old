package com.viewlift.tv.views.fragment;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.viewlift.tv.views.customviews.TVPageView;
import com.viewlift.tv.views.module.AppCMSTVPageViewModule;
import com.viewlift.views.binders.AppCMSBinder;

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

        View view = inflater.inflate(R.layout.app_cms_login_dialog_fragment, null);

        subscriptionTitle = (TextView)view.findViewById(R.id.nav_top_line);


        if (subscriptionTitle != null && appCMSPresenter.getTemplateType()
                .equals(AppCMSPresenter.TemplateType.SPORTS) && appCMSPresenter.getAppCMSMain().getServiceType().equalsIgnoreCase("SVOD")) {
            updateSubscriptionStrip();
        }else{
            subscriptionTitle.setVisibility(View.GONE);
        }

        view.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBackgroundColor()));


        LinearLayout navHolder = (LinearLayout) view.findViewById(R.id.sub_navigation_placholder);
        LinearLayout subNavHolder = (LinearLayout) view.findViewById(R.id.sub_navigation_placholder);

        String backGroundColor = Utils.getBackGroundColor(getActivity(), appCMSPresenter);
        view.setBackgroundColor(Color.parseColor(backGroundColor));


        TextView loginView = (TextView) view.findViewById(R.id.textView_login);
        TextView signupView = (TextView) view.findViewById(R.id.textview_signup);
        loginView.setTextColor(Color.parseColor(appCMSPresenter.getAppCtaTextColor()));
        signupView.setTextColor(Color.parseColor(appCMSPresenter.getAppCtaTextColor()));

        loginView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    subNavHolder.setAlpha(1f);
                }else{
                    subNavHolder.setAlpha(0.52f);
                }
            }
        });

       signupView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View v, boolean hasFocus) {
               if(hasFocus){
                   subNavHolder.setAlpha(1f);
               }else{
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
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            return true;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            focusSignupView(signupView,loginView);
                            return true;
                        case KeyEvent.KEYCODE_DPAD_UP:
                            return true;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            focusLoginView(signupView,loginView);
                            return false;
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
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            focusLoginView(signupView,loginView);
                            return true;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            return true;
                        case KeyEvent.KEYCODE_DPAD_UP:
                            return true;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            focusLoginView(signupView,loginView);
                            return false;
                    }
                }
                return false;
            }
        });

        focusLoginView(signupView,loginView);

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
                if(keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_DOWN){
                    if(null != onBackKeyListener)
                        onBackKeyListener.call("");
                }
                return false;
            }
        });

        pageHolder = (FrameLayout) view.findViewById(R.id.profile_placeholder);
        pageHolder.addView(tvPageView);

        return view;
    }


    private void updateSubscriptionStrip() {
        /*Check Subscription in case of SPORTS TEMPLATE*/
        if (appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.SPORTS) {
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
    public void setBackKeyListener(Action1<String> onBackKeyListener){
        this.onBackKeyListener = onBackKeyListener;
    }


    private void focusSignupView(TextView signupView , TextView loginView) {
        signupView.setBackground(Utils.getNavigationSelectedState(mContext, appCMSPresenter, true , Color.parseColor("#000000")));
        signupView.setTypeface(extraBoldTypeFace);
        loginView.setBackground(null);
        loginView.setTypeface(semiBoldTypeFace);
        signupView.requestFocus();
    }

    private void focusLoginView(TextView signupView , TextView loginView) {
        loginView.setBackground(Utils.getNavigationSelectedState(mContext, appCMSPresenter, true , Color.parseColor("#000000")) );
        loginView.setTypeface(extraBoldTypeFace);
        signupView.setBackground(null);
        signupView.setTypeface(semiBoldTypeFace);
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
        if(null != getActivity() && getActivity() instanceof AppCmsHomeActivity){
            ((AppCmsHomeActivity) getActivity()).closeSignUpDialog();
        }else if(null != getActivity() && getActivity() instanceof AppCMSTVPlayVideoActivity){
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

}
