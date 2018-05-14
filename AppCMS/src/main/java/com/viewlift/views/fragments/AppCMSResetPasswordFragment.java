package com.viewlift.views.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.ViewCreator;

/*
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

        final AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        int bgColor = appCMSPresenter.getGeneralBackgroundColor();
        int buttonColor = appCMSPresenter.getBrandPrimaryCtaColor();
        int buttonTextColor = appCMSPresenter.getBrandPrimaryCtaTextColor();
        int textColor = appCMSPresenter.getGeneralTextColor();

        Bundle args = getArguments();
        String email = args.getString(getContext().getString(R.string.app_cms_password_reset_email_key));

        TextView titleTextView = (TextView) view.findViewById(R.id.app_cms_reset_password_page_title);
        titleTextView.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));

        final EditText appCMSResetPasswordEmailInput = (EditText) view.findViewById(R.id.app_cms_reset_password_email_input);
        appCMSPresenter.setCursorDrawableColor(appCMSResetPasswordEmailInput);
        if (!TextUtils.isEmpty(email)) {
            appCMSResetPasswordEmailInput.setText(email);
        }

        TextView appCMSResetPasswordTextInputDescription =
                (TextView) view.findViewById(R.id.app_cms_reset_password_text_input_description);
        appCMSResetPasswordTextInputDescription.setTextColor(textColor);

        Button appCMSSubmitResetPasswordButton = (Button) view.findViewById(R.id.app_cms_submit_reset_password_button);
        appCMSSubmitResetPasswordButton.setOnClickListener(v -> {
            if (appCMSResetPasswordEmailInput.getText().toString().length() == 0) {
                appCMSPresenter.showDialog(AppCMSPresenter.DialogType.RESET_PASSWORD,
                        getActivity().getResources().getString(R.string.email_blank_toast_msg_reset_password),
                        false,
                        null,
                        null);
            } else {
                appCMSPresenter.resetPassword(appCMSResetPasswordEmailInput.getText().toString());
                appCMSPresenter.sendCloseOthersAction(null,
                        true,
                        false);
            }
        });
        appCMSSubmitResetPasswordButton.setTextColor(buttonTextColor);
        appCMSSubmitResetPasswordButton.setBackgroundColor(buttonColor);

        setBgColor(bgColor, view);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    private void setBgColor(int bgColor, View view) {
        RelativeLayout appCMSResetPasswordMainLayout =
                (RelativeLayout) view.findViewById(R.id.app_cms_reset_password_main_layout);
        appCMSResetPasswordMainLayout.setBackgroundColor(bgColor);
    }
}
