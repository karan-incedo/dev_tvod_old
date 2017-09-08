package com.viewlift.tv.views.fragment;


import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.views.activity.AppCmsHomeActivity;
import com.viewlift.tv.views.component.AppCMSTVViewComponent;
import com.viewlift.tv.views.component.DaggerAppCMSTVViewComponent;
import com.viewlift.tv.views.customviews.TVModuleView;
import com.viewlift.tv.views.customviews.TVPageView;
import com.viewlift.tv.views.module.AppCMSTVPageViewModule;
import com.viewlift.views.binders.AppCMSBinder;

public class AppCmsResetPasswordFragment extends DialogFragment {

    private AppCMSPresenter appCMSPresenter;
    private AppCMSTVViewComponent appCmsViewComponent;
    private TVPageView tvPageView;


    public AppCmsResetPasswordFragment() {
     }

    public static AppCmsResetPasswordFragment newInstance(AppCMSBinder appCMSBinder) {
        AppCmsResetPasswordFragment fragment = new AppCmsResetPasswordFragment();
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

        if (tvPageView != null) {
            if (tvPageView.getParent() != null) {
                ((ViewGroup) tvPageView.getParent()).removeAllViews();
            }
        }
        if (container != null) {
            container.removeAllViews();
        }

        if(null != tvPageView && tvPageView.getChildrenContainer().getChildAt(0) instanceof TVModuleView){
            TVModuleView tvModuleView = (TVModuleView)tvPageView.getChildrenContainer().getChildAt(0);
            EditText emailId = ((EditText)tvModuleView.findViewById(R.id.email_edit_box)) ;
            Button cancelButton = ((Button)tvModuleView.findViewById(R.id.reset_password_cancel_button)) ;
            Button continueButton = ((Button)tvModuleView.findViewById(R.id.reset_password_continue_button)) ;

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AppCmsHomeActivity)getActivity()).pageLoading(true);
                    appCMSPresenter.resetPassword(emailId.getEditableText().toString());
                }
            });

        }




        return tvPageView;
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
