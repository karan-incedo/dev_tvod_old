package com.viewlift.views.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.viewlift.views.customviews.ViewCreator;

/**
 * Created by viewlift on 7/27/17.
 */

public class AppCMSEditProfileFragment extends DialogFragment {
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        final AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        int bgColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBackgroundColor());
        int buttonColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor());
        int textColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor());

        Bundle args = getArguments();
        String username = args.getString(getContext().getString(R.string.app_cms_edit_profile_username_key));
        String email = args.getString(getContext().getString(R.string.app_cms_password_reset_email_key));

        TextView titleTextView = (TextView) view.findViewById(R.id.app_cms_edit_profile_page_title);
        titleTextView.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));

        final EditText appCMSEditProfileNameInput = (EditText) view.findViewById(R.id.app_cms_edit_profile_name_input);
        if (!TextUtils.isEmpty(email)) {
            appCMSEditProfileNameInput.setText(username);
        }

        final EditText appCMSEditProfileEmailInput = (EditText) view.findViewById(R.id.app_cms_edit_profile_email_input);
        if (!TextUtils.isEmpty(email)) {
            appCMSEditProfileEmailInput.setText(email);
        }
        final EditText password = new EditText(view.getContext());
        Button editProfileConfirmChangeButton = (Button) view.findViewById(R.id.edit_profile_confirm_change_button);
        editProfileConfirmChangeButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setCancelable(false);
            builder.setView(password)
                    .setTitle("Enter your password to continue")
                    .setPositiveButton("Proceed", (dialog, position) -> {
                        if (!TextUtils.isEmpty(password.getText().toString())) {
                            appCMSPresenter.updateUserData(appCMSEditProfileNameInput.getText().toString(),
                                    appCMSEditProfileEmailInput.getText().toString(),
                                    password.getText().toString(),
                                    userIdentity -> {
                                        //
                                    }
                            );
                        }
                        appCMSPresenter.sendCloseOthersAction(null, true);
                    })
                    .setNegativeButton("Cancel", (dialog, position) ->
                            appCMSPresenter.sendCloseOthersAction(null, true))
                    .create().show();
        });

        editProfileConfirmChangeButton.setTextColor(0xff000000 + (int) ViewCreator.adjustColor1(textColor, buttonColor));
        editProfileConfirmChangeButton.setBackgroundColor(buttonColor);
        setBgColor(bgColor, view);

        return view;
    }

    private void setBgColor(int bgColor, View view) {
        RelativeLayout appCMSEditProfileMainLayout =
                (RelativeLayout) view.findViewById(R.id.app_cms_edit_profile_main_layout);
        appCMSEditProfileMainLayout.setBackgroundColor(bgColor);
    }
}
