package com.viewlift.views.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.AsteriskPasswordTransformation;
import com.viewlift.views.customviews.ViewCreator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppCMSChangePasswordFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.app_cms_change_password_main_layout)
    RelativeLayout appCMSChangePasswordMainLayout;

    @BindView(R.id.app_cms_change_password_page_title)
    TextView changePasswordPageTitle;

    @BindView(R.id.app_cms_old_password_container)
    TextInputLayout oldPasswordInputLayout;

    @BindView(R.id.app_cms_old_password_text_input)
    TextInputEditText oldPasswordInput;

    @BindView(R.id.app_cms_new_password_container)
    TextInputLayout newPasswordInputLayout;

    @BindView(R.id.app_cms_new_password_text_input)
    TextInputEditText newPasswordInput;

    @BindView(R.id.app_cms_confirm_password_container)
    TextInputLayout confirmPasswordInputLayout;

    @BindView(R.id.app_cms_confirm_password_text_input)
    TextInputEditText confirmPasswordInput;

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

        AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        int bgColor = Color.parseColor(appCMSPresenter.getAppBackgroundColor());
        int buttonColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
                .getBlockTitleColor());

        int textColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
                .getTextColor());

        changePasswordPageTitle.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));

//        oldPasswordInputLayout.addView(view);
//        newPasswordInputLayout.addView(view);
//        confirmPasswordInputLayout.addView(view);

        appCMSPresenter.noSpaceInEditTextFilter(oldPasswordInput, getActivity());
        appCMSPresenter.noSpaceInEditTextFilter(newPasswordInput, getActivity());
        appCMSPresenter.noSpaceInEditTextFilter(confirmPasswordInput, getActivity());
        appCMSPresenter.setCursorDrawableColor(oldPasswordInput);
        appCMSPresenter.setCursorDrawableColor(newPasswordInput);
        appCMSPresenter.setCursorDrawableColor(confirmPasswordInput);

        oldPasswordInputLayout.setPasswordVisibilityToggleEnabled(true);
        newPasswordInputLayout.setPasswordVisibilityToggleEnabled(true);
        confirmPasswordInputLayout.setPasswordVisibilityToggleEnabled(true);

//        oldPasswordInputLayout.listener

        oldPasswordInput.setTransformationMethod(new AsteriskPasswordTransformation());
        newPasswordInput.setTransformationMethod(new AsteriskPasswordTransformation());
        confirmPasswordInput.setTransformationMethod(new AsteriskPasswordTransformation());

        confirmPasswordButton.setOnClickListener(v -> {
            String oldPassword = oldPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();
            appCMSPresenter.closeSoftKeyboard();
            appCMSPresenter.updateUserPassword(oldPassword, newPassword, confirmPassword);
        });

        confirmPasswordButton.setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());
        confirmPasswordButton.setBackgroundColor(buttonColor);
        setBgColor(bgColor);

        return view;
    }

    private void setBgColor(int bgColor) {
        appCMSChangePasswordMainLayout.setBackgroundColor(bgColor);
    }
}
