package com.viewlift.tv.views.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.views.component.AppCMSTVViewComponent;
import com.viewlift.tv.views.component.DaggerAppCMSTVViewComponent;
import com.viewlift.tv.views.customviews.TVPageView;
import com.viewlift.tv.views.module.AppCMSTVPageViewModule;
import com.viewlift.views.binders.AppCMSBinder;

public class AppCmsGenericDialogFragment extends DialogFragment {

    private AppCMSPresenter appCMSPresenter;
    private AppCMSTVViewComponent appCmsViewComponent;
    private TVPageView tvPageView;
    private TextView subscriptionTitle;


    public AppCmsGenericDialogFragment() {
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
     }

    public static AppCmsGenericDialogFragment newInstance(AppCMSBinder appCMSBinder) {
        AppCmsGenericDialogFragment fragment = new AppCmsGenericDialogFragment();
        Bundle args = new Bundle();
        args.putBinder("app_cms_binder_key", appCMSBinder);
        fragment.setArguments(args);
        return fragment;
    }

    private AppCMSBinder appCMSBinder;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appCMSBinder = (AppCMSBinder) getArguments().getBinder("app_cms_binder_key");
        }

        appCMSPresenter =
                ((AppCMSApplication) getActivity().getApplication()).getAppCMSPresenterComponent().appCMSPresenter();

        if(null != appCMSBinder)
            appCMSPresenter.sendGaScreen(appCMSBinder.getScreenName());

        appCmsViewComponent = buildAppCMSViewComponent();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (appCmsViewComponent == null ) {
            appCmsViewComponent = buildAppCMSViewComponent();
        }

        if (appCmsViewComponent != null) {
            tvPageView = appCmsViewComponent.appCMSTVPageView();
        } else {
            tvPageView = null;
        }

        subscriptionTitle = new TextView(getActivity());
        subscriptionTitle.setId(R.id.subscription_text);
        subscriptionTitle.setText(getResources().getString(R.string.blank_string));
        subscriptionTitle.setGravity(Gravity.CENTER);
        subscriptionTitle.setFocusable(false);
        subscriptionTitle.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppCtaBackgroundColor()));
        subscriptionTitle.setTextColor(Color.parseColor(appCMSPresenter.getAppCtaTextColor()));


        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
               10
        );
        subscriptionTitle.setLayoutParams(layoutParams);

        if(subscriptionTitle.getParent() != null){
            ((FrameLayout)subscriptionTitle.getParent()).removeView(subscriptionTitle);
        }

        if (tvPageView.getChildAt(0).getId() == R.id.subscription_text){
            tvPageView.removeViewAt(0);
        }
        tvPageView.addView(subscriptionTitle,0);
        updateSubscriptionStrip();

        if (tvPageView != null) {
            if (tvPageView.getParent() != null) {
                ((ViewGroup) tvPageView.getParent()).removeAllViews();
            }
        }
        if (container != null) {
            container.removeAllViews();
        }
        tvPageView.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBackgroundColor()));
        return tvPageView;
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
        }else{
            subscriptionTitle.setVisibility(View.GONE);
        }
    }

    private void setSubscriptionText(boolean isSubscribe) {
        try {
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

            FrameLayout.LayoutParams textLayoutParams = (FrameLayout.LayoutParams) subscriptionTitle.getLayoutParams();
            if (message.length() == 0) {
                textLayoutParams.height = 10;
            } else {
                textLayoutParams.height = 40;
            }
            subscriptionTitle.setLayoutParams(textLayoutParams);
        }catch (Exception e){

        }
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        super.onActivityCreated(bundle);
    }

    public AppCMSTVViewComponent buildAppCMSViewComponent() {
        return DaggerAppCMSTVViewComponent.builder()
                .appCMSTVPageViewModule(new AppCMSTVPageViewModule(getActivity(),
                        appCMSBinder.getAppCMSPageUI(),
                        appCMSBinder.getAppCMSPageAPI(),
                        appCMSPresenter.getJsonValueKeyMap(),
                        appCMSPresenter
                ))
                .build();
    }

}
