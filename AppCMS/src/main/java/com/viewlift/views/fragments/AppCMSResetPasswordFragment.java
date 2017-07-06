package com.viewlift.views.fragments;

import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.viewlift.AppCMSApplication;
import com.viewlift.presenters.AppCMSPresenter;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 7/6/17.
 */

public class AppCMSResetPasswordFragment extends DialogFragment {
    public static AppCMSResetPasswordFragment newInstance(Context context, String email) {
        AppCMSResetPasswordFragment fragment = new AppCMSResetPasswordFragment();
        Bundle args = new Bundle();
        args.putString(context.getString(R.string.app_cms_password_reset_email_key), email);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        Bundle args = getArguments();
        String email = args.getString(getContext().getString(R.string.app_cms_password_reset_email_key));
        final EditText appCMSResetPasswordEmailInput = (EditText) view.findViewById(R.id.app_cms_reset_password_email_input);
        if (!TextUtils.isEmpty(email)) {
            appCMSResetPasswordEmailInput.setText(email);
        }

        final AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();
        Button appCMSSubmitResetPasswordButton = (Button) view.findViewById(R.id.app_cms_submit_reset_password_button);
        appCMSSubmitResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCMSPresenter.resetPassword(appCMSResetPasswordEmailInput.getText().toString());
                dismiss();
            }
        });
        return view;
    }
}
