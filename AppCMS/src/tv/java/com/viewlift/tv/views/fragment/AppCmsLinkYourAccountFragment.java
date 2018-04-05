package com.viewlift.tv.views.fragment;


import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.views.component.AppCMSTVViewComponent;
import com.viewlift.tv.views.component.DaggerAppCMSTVViewComponent;
import com.viewlift.tv.views.customviews.TVModuleView;
import com.viewlift.tv.views.customviews.TVPageView;
import com.viewlift.tv.views.module.AppCMSTVPageViewModule;
import com.viewlift.views.binders.AppCMSBinder;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class AppCmsLinkYourAccountFragment extends AbsDialogFragment {

    private AppCMSPresenter appCMSPresenter;
    private AppCMSTVViewComponent appCmsViewComponent;
    private TVPageView tvPageView;


    public AppCmsLinkYourAccountFragment() {
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public static AppCmsLinkYourAccountFragment newInstance(AppCMSBinder appCMSBinder) {
        AppCmsLinkYourAccountFragment fragment = new AppCmsLinkYourAccountFragment();
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

            TextView textLine2 = (TextView)tvModuleView.findViewById(R.id.code_sync_text_line_2);
            TextView headerTextLine = (TextView)tvModuleView.findViewById(R.id.code_sync_text_line_header);

            final String[] text2 = {null};
            String headerText = null;
            String codeText = null;
            if(null != textLine2.getText()){
                text2[0] = textLine2.getText().toString();
                codeText= textLine2.getText().toString();
            }
            if(null != headerTextLine.getText()) {
                headerText = headerTextLine.getText().toString()
                        .replace("$App$", getResources().getString(R.string.app_name))
                        .replace("$app_web_url$", appCMSPresenter.getAppCMSMain().getDomainName());
            }

            if(null != text2[0])
                textLine2.setText(text2[0]);
            if(null != headerText) {
                headerTextLine.setText(headerText);
                setSpan(headerTextLine, headerText, appCMSPresenter.getAppCMSMain().getDomainName());
            }
            String finalCodeText = codeText;
            appCMSPresenter.getDeviceLinkCode(getSyncCode -> {
                if(null != getSyncCode) {
                    appCMSPresenter.stopLoader();

                    text2[0] = finalCodeText.replace("XXXXXX",getSyncCode.getActivationCode());
                    textLine2.setText(text2[0]);
                    appCMSPresenter.syncCode(syncDeviceCode -> {
                        if(null != syncDeviceCode) {
                            //   Log.d("TAG", "syncDeviceCode Name = " + syncDeviceCode.getName());
                            dismiss();
                        }
                    });
                }else{
                    appCMSPresenter.stopLoader();
                                /*String text = getResources().getString(R.string.code_sync_dialog_text_line_2,
                                        "Some Error Occured.");*/
                    textLine2.setText(text2[0].replace("XXXXXX","Some Error Occured."));
                }
            });
        }
        tvPageView.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBackgroundColor()));
        return tvPageView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog().setOnKeyListener((dialogInterface, i, keyEvent) -> {
            switch(keyEvent.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                        appCMSPresenter.stopSyncCodeAPI();
                        dismiss();
                        return true;
                    }
                    break;
            }
            return false;
        });
    }

    public void setSpan(TextView textView , String text, String replacementString) {
        Spannable wordToSpan = new SpannableString(text);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(
                Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor())
        );

        int startIndex = text.indexOf(replacementString);
        int indexOfFirstSpaceAfterURL = text.indexOf(" ", text.indexOf(appCMSPresenter.getAppCMSMain().getDomainName()));
//        wordToSpan.setSpan(new StyleSpan(Typeface.BOLD), startIndex, startIndex + lengthOfReplacementString, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordToSpan.setSpan(foregroundColorSpan, startIndex, indexOfFirstSpaceAfterURL, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(wordToSpan);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle.putInt( getString(R.string.tv_dialog_width_key) , MATCH_PARENT);
        bundle.putInt( getString(R.string.tv_dialog_height_key) , MATCH_PARENT);
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