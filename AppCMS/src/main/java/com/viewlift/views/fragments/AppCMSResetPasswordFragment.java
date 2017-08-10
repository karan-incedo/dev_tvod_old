package com.viewlift.views.fragments;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.ViewCreator;

import com.viewlift.R;

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

        final AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        int bgColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBackgroundColor());
        int buttonColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor());
        int textColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor());

        Bundle args = getArguments();
        String email = args.getString(getContext().getString(R.string.app_cms_password_reset_email_key));

        TextView titleTextView = (TextView) view.findViewById(R.id.app_cms_reset_password_page_title);
        titleTextView.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));

        final EditText appCMSResetPasswordEmailInput = (EditText) view.findViewById(R.id.app_cms_reset_password_email_input);
        if (!TextUtils.isEmpty(email)) {
            appCMSResetPasswordEmailInput.setText(email);
        }

        TextView appCMSResetPasswordTextInputDescription =
                (TextView) view.findViewById(R.id.app_cms_reset_password_text_input_description);
        appCMSResetPasswordTextInputDescription.setTextColor(textColor);

        Button appCMSSubmitResetPasswordButton = (Button) view.findViewById(R.id.app_cms_submit_reset_password_button);
        appCMSSubmitResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCMSPresenter.resetPassword(appCMSResetPasswordEmailInput.getText().toString());
                appCMSPresenter.sendCloseOthersAction(null, true);
            }
        });
        appCMSSubmitResetPasswordButton.setTextColor(0xff000000 + (int) ViewCreator.adjustColor1(textColor, buttonColor));
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
