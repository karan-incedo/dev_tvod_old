package com.viewlift.tv.views.fragment;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

public class AppCmsSignUpDialogFragment extends DialogFragment {

    private AppCMSPresenter appCMSPresenter;
    private AppCMSTVViewComponent appCmsViewComponent;
    private TVPageView tvPageView;
    private AppCmsSubNavigationFragment appCmsSubNavigationFragment;
    private Typeface extraBoldTypeFace;
    private Component extraBoldComp;
    private Typeface semiBoldTypeFace;
    private Component semiBoldComp;
    private Context mContext;
    private FrameLayout pageHolder;


    public AppCmsSignUpDialogFragment() {
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public static AppCmsSignUpDialogFragment newInstance(AppCMSBinder appCMSBinder) {
        AppCmsSignUpDialogFragment fragment = new AppCmsSignUpDialogFragment();
        Bundle args = new Bundle();
        args.putBinder("app_cms_binder_key", appCMSBinder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null != getActivity() && getActivity() instanceof AppCmsHomeActivity){
            ((AppCmsHomeActivity) getActivity()).closeSignInDialog();
        }else if(null != getActivity() && getActivity() instanceof AppCMSTVPlayVideoActivity){
            ((AppCMSTVPlayVideoActivity) getActivity()).closeSignInDialog();
        }
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

    private AppCMSBinder appCMSBinder;
    private boolean isLoginPage;

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
        TextView loginView = (TextView) view.findViewById(R.id.textView_login);
        TextView signupView = (TextView) view.findViewById(R.id.textview_signup);



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
                            loginView.setFocusable(true);
                            focusSignupView(signupView,loginView);
                            return true;
                        case KeyEvent.KEYCODE_DPAD_UP:
                            return true;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            focusSignupView(signupView,loginView);
                            loginView.setFocusable(false);
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
                            loginView.setFocusable(true);
                            focusLoginView(signupView,loginView);
                            return true;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            return true;
                        case KeyEvent.KEYCODE_DPAD_UP:
                            return true;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            focusSignupView(signupView,loginView);
                            loginView.setFocusable(false);
                            return false;
                    }
                }
                return false;
            }
        });

        focusSignupView(signupView,loginView);

        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationUser navigationUser = appCMSPresenter.getLoginNavigation();
                appCMSPresenter.navigateToTVPage(
                        navigationUser.getPageId(),
                        navigationUser.getTitle(),
                        navigationUser.getUrl(),
                        false,
                        Uri.EMPTY,
                        false,
                        false,
                        true
                );

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
        signupView.requestFocus();

        return view;
    }




    private Action1<String> onBackKeyListener;
    public void setBackKeyListener(Action1<String> onBackKeyListener){
        this.onBackKeyListener = onBackKeyListener;
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



    private void focusSignupView(TextView signupView , TextView loginView) {
        signupView.setBackground(Utils.getNavigationSelectedState(mContext, appCMSPresenter, true));
        signupView.setTypeface(extraBoldTypeFace);
        loginView.setBackground(null);
        loginView.setTypeface(semiBoldTypeFace);
        signupView.requestFocus();
    }

    private void focusLoginView(TextView signupView , TextView loginView) {
        loginView.setBackground(Utils.getNavigationSelectedState(mContext, appCMSPresenter, true));
        loginView.setTypeface(extraBoldTypeFace);
        signupView.setBackground(null);
        signupView.setTypeface(semiBoldTypeFace);
        loginView.requestFocus();
    }


}
