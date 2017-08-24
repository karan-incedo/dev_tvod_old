package com.viewlift.views.fragments;

import android.graphics.Color;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppCMSChangePasswordFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.app_cms_change_password_page_title)
    TextView changePasswordPageTitle;

    @BindView(R.id.app_cms_change_old_password_text_input)
    EditText oldPasswordInput;

    @BindView(R.id.app_cms_change_new_password_text_input)
    EditText newPasswordInput;

    @BindView(R.id.app_cms_confirm_password_text_input)
    EditText confirmPasswordInput;

    @BindView(R.id.app_cms_change_password_button)
    Button confirmPasswordButton;

    public static AppCMSChangePasswordFragment newInstance() {
        return new AppCMSChangePasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        ButterKnife.bind(this, view);

        final AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        int bgColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBackgroundColor());
        int buttonColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor());
        int textColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor());

        String oldPassword = oldPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        changePasswordPageTitle.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));

        confirmPasswordButton.setOnClickListener(v ->
                appCMSPresenter.updateUserPassword(oldPassword, newPassword, confirmPassword));

        confirmPasswordButton.setTextColor(0xff000000 + (int) ViewCreator.adjustColor1(textColor, buttonColor));
        confirmPasswordButton.setBackgroundColor(buttonColor);
        setBgColor(bgColor, view);

        return view;
    }

    private void setBgColor(int bgColor, View view) {
        RelativeLayout appCMSEditProfileMainLayout =
                (RelativeLayout) view.findViewById(R.id.app_cms_edit_profile_main_layout);
        appCMSEditProfileMainLayout.setBackgroundColor(bgColor);
    }
}
