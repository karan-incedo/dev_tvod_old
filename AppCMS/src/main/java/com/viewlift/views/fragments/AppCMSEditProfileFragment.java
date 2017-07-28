package com.viewlift.views.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.authentication.UserIdentity;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.ViewCreator;

import rx.functions.Action1;

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

        ImageButton closeButton = (ImageButton) view.findViewById(R.id.app_cms_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final EditText appCMSEditProfileNameInput = (EditText) view.findViewById(R.id.app_cms_edit_profile_name_input);
        if (!TextUtils.isEmpty(email)) {
            appCMSEditProfileNameInput.setText(username);
        }

        final EditText appCMSEditProfileEmailInput = (EditText) view.findViewById(R.id.app_cms_edit_profile_email_input);
        if (!TextUtils.isEmpty(email)) {
            appCMSEditProfileEmailInput.setText(email);
        }

        Button editProfileConfirmChangeButton = (Button) view.findViewById(R.id.edit_profile_confirm_change_button);
        editProfileConfirmChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCMSPresenter.updateUserData(appCMSEditProfileNameInput.getText().toString(),
                        appCMSEditProfileEmailInput.getText().toString(),
                        new Action1<UserIdentity>() {
                            @Override
                            public void call(UserIdentity userIdentity) {
                                // NO-OP - just close window
                            }
                        });
                dismiss();
            }
        });
        editProfileConfirmChangeButton.setTextColor(0xff000000 + (int) ViewCreator.adjustColor1(textColor, buttonColor));
        editProfileConfirmChangeButton.setBackgroundColor(buttonColor);

        setBgColor(bgColor);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setWindow();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setWindow();
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    private void setBgColor(int bgColor) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(bgColor));
            }
        }
    }

    private void setWindow() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(width, height);
                window.setGravity(Gravity.START);
            }
        }
    }
}
