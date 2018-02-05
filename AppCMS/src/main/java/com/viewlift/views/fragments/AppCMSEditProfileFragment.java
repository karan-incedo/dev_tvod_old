package com.viewlift.views.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.ViewCreator;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by viewlift on 7/27/17.
 */

public class AppCMSEditProfileFragment extends DialogFragment {

    @BindView(R.id.app_cms_edit_profile_page_title)
    TextView titleTextView;

    @BindView(R.id.app_cms_edit_profile_name_input)
    EditText appCMSEditProfileNameInput;

    @BindView(R.id.app_cms_edit_profile_email_input)
    EditText appCMSEditProfileEmailInput;

    @BindView(R.id.edit_profile_confirm_change_button)
    Button editProfileConfirmChangeButton;

    @BindView(R.id.app_cms_edit_profile_main_layout)
    ConstraintLayout appCMSEditProfileMainLayout;

    @BindView(R.id.app_cms_edit_profile_name_text)
    TextView appCMSEditProfileNameText;

    @BindView(R.id.app_cms_edit_profile_email_text)
    TextView appCMSEditProfileEmailText;

    public static AppCMSEditProfileFragment newInstance(Context context,
                                                        String username,
                                                        String email) {
        AppCMSEditProfileFragment fragment = new AppCMSEditProfileFragment();
        Bundle args = new Bundle();
        args.putString(context.getString(R.string.app_cms_edit_profile_username_key), username);
        args.putString(context.getString(R.string.app_cms_password_reset_email_key), email);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        ButterKnife.bind(this, view);

        final AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        //appCMSPresenter.scrollUpWhenSoftKeyboardIsVisible();

        int bgColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
                .getBackgroundColor());

        int buttonColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta()
                .getPrimary().getBackgroundColor());
        int textColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
                .getTextColor());
        int transparentColor = getResources().getColor(R.color.transparentColor);

        Bundle args = getArguments();
        String username = args.getString(getContext().getString(R.string.app_cms_edit_profile_username_key));
        String email = args.getString(getContext().getString(R.string.app_cms_password_reset_email_key));

        titleTextView.setTextColor(textColor);
        titleTextView.setTypeface(appCMSPresenter.getBoldTypeFace());

        appCMSEditProfileNameText.setTextColor(textColor);
        appCMSEditProfileNameText.setTypeface(appCMSPresenter.getBoldTypeFace());
        appCMSEditProfileNameText.setBackgroundColor(transparentColor);

        appCMSEditProfileEmailText.setTextColor(textColor);
        appCMSEditProfileEmailText.setTypeface(appCMSPresenter.getBoldTypeFace());
        appCMSEditProfileEmailText.setBackgroundColor(transparentColor);

        appCMSEditProfileNameInput.setTextColor(textColor);
        if (!TextUtils.isEmpty(username)) {
            appCMSEditProfileNameInput.setText(username);
        }

        if (!TextUtils.isEmpty(email)) {
            appCMSEditProfileEmailInput.setText(email);
            appCMSEditProfileEmailInput.setTextColor(textColor);
        }

        editProfileConfirmChangeButton.setOnClickListener((View v) -> {
            String userName = appCMSEditProfileNameInput.getText().toString().trim();
            if (!doesValidNameExist(userName)) {
                Toast.makeText(getContext(), getResources().getString(R.string.edit_profile_name_message), Toast.LENGTH_LONG).show();
                return;
            }

            TextInputLayout textInputLayout = new TextInputLayout(view.getContext());
            TextInputEditText password = new TextInputEditText(view.getContext());
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password.setImeOptions(EditorInfo.IME_ACTION_DONE);

            textInputLayout.addView(password);
            textInputLayout.setPasswordVisibilityToggleEnabled(true);
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(textInputLayout);
            builder.setCancelable(false);
            builder.setTitle("Verify your password to Continue");
            builder.setPositiveButton(
                    "Proceed",
                    (dialog, id) -> {
                        appCMSPresenter.closeSoftKeyboardNoView();

                        appCMSPresenter.updateUserProfile(userName,
                                appCMSEditProfileEmailInput.getText().toString(),
                                password.getText().toString(),
                                userIdentity -> {
                                    //
                                });
                    });

            builder.setNegativeButton(
                    "Cancel",
                    (dialog, id) -> {
                        dialog.cancel();
                        appCMSPresenter.sendCloseOthersAction(null,
                                true,
                                false);
                        appCMSPresenter.closeSoftKeyboardNoView();
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        editProfileConfirmChangeButton.setTextColor(0xff000000 + (int) ViewCreator.adjustColor1(textColor,
                buttonColor));
        editProfileConfirmChangeButton.setBackgroundColor(buttonColor);
        setBgColor(bgColor);

        return view;
    }

    private void setBgColor(int bgColor) {
        appCMSEditProfileMainLayout.setBackgroundColor(bgColor);
    }

    private boolean doesValidNameExist(String input) {
        String regex = "[a-zA-Z\\s]+";
        return !TextUtils.isEmpty(input) && Pattern.matches(regex, input);
    }
}
