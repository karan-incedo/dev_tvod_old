package com.viewlift.views.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
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
import com.viewlift.views.customviews.AsteriskPasswordTransformation;
import com.viewlift.views.customviews.ViewCreator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
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
    RelativeLayout appCMSEditProfileMainLayout;

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

        int bgColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
                .getBackgroundColor());
        int buttonColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
                .getBlockTitleColor());
        int textColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
                .getTextColor());

        Bundle args = getArguments();
        String username = args.getString(getContext().getString(R.string.app_cms_edit_profile_username_key));
        String email = args.getString(getContext().getString(R.string.app_cms_password_reset_email_key));

        titleTextView.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));

        if (!TextUtils.isEmpty(email)) {
            appCMSEditProfileNameInput.setText(username);
        }

        if (!TextUtils.isEmpty(email)) {
            appCMSEditProfileEmailInput.setText(email);
        }


        editProfileConfirmChangeButton.setOnClickListener((View v) -> {
            TextInputLayout textInputLayout = new TextInputLayout(view.getContext());
            TextInputEditText password = new TextInputEditText(view.getContext());

            textInputLayout.addView(password);
            textInputLayout.setPasswordVisibilityToggleEnabled(true);
            password.setTransformationMethod(new AsteriskPasswordTransformation());

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(textInputLayout);
            builder.setCancelable(false);
            builder.setTitle("Verify your password to Continue");
            builder.setPositiveButton(
                    "Proceed",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            appCMSPresenter.updateUserProfile(appCMSEditProfileNameInput.getText().toString(),
                                    appCMSEditProfileEmailInput.getText().toString(),
                                    password.getText().toString(),
                                    userIdentity -> {
                                    });
                        }
                    });

            builder.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            appCMSPresenter.sendCloseOthersAction(null, true);
                        }
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
}
