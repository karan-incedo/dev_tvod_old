package com.viewlift.views.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.models.data.appcms.ui.authentication.UserIdentity;
import com.viewlift.presenters.AppCMSPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;
import com.viewlift.R;

/**
 * Created by viewlift on 7/6/17.
 */

public class AppCMSSettingsFragment extends DialogFragment {
    public static AppCMSSettingsFragment newInstance() {
        AppCMSSettingsFragment appCMSSettingsFragment = new AppCMSSettingsFragment();
        return appCMSSettingsFragment;
    }

    private enum InputField {
        USERNAME, EMAIL
    }

    @BindView(R.id.app_cms_settings_page_title)
    TextView appCMSSettingsPageTitle;

    @BindView(R.id.app_cms_close_button)
    ImageButton appCMSCloseButton;

    @BindView(R.id.app_cms_settings_separator_view)
    View appCMSettingsSeparatorView;

    @BindView(R.id.app_cms_account_title)
    TextView appCMSAccountTitle;

    @BindView(R.id.app_cms_account_title_separator_view)
    View appCMSAccountTitleSeparatorView;

    @BindView(R.id.app_cms_account_name_title)
    TextView appCMSAccountNameTitle;

    @BindView(R.id.app_cms_account_name_info)
    TextView appCMSAccountNameInfo;

    @BindView(R.id.app_cms_edit_account_name_button)
    Button appCMSEditAccountNameButton;

    @BindView(R.id.app_cms_account_email_title)
    TextView appCMSAccountEmailTitle;

    @BindView(R.id.app_cms_account_email_info)
    TextView appCMSAccountEmailInfo;

    @BindView(R.id.app_cms_edit_account_email_button)
    Button appCMSEditAccountEmailButton;

    private AppCMSPresenter appCMSPresenter;

    public static AppCMSSettingsFragment newInstance(Context context) {
        AppCMSSettingsFragment appCMSSettingsFragment = new AppCMSSettingsFragment();
        return appCMSSettingsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ButterKnife.bind(this, view);

        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        appCMSSettingsPageTitle.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));
        appCMSCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (appCMSPresenter != null) {
                    appCMSPresenter.popActionInternalEvents();
                    appCMSPresenter.setNavItemToCurrentAction(getActivity());
                    appCMSPresenter.showMainFragmentView(true);
                }
            }
        });
        appCMSettingsSeparatorView.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));
        appCMSAccountTitle.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));
        appCMSAccountTitleSeparatorView.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));
        appCMSAccountNameTitle.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));
        appCMSAccountNameInfo.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));
        appCMSEditAccountNameButton.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getPageTitleColor()));
        appCMSAccountEmailTitle.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));
        appCMSAccountEmailInfo.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));
        appCMSEditAccountEmailButton.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getPageTitleColor()));

        appCMSPresenter.getUserData(new Action1<UserIdentity>() {
            @Override
            public void call(UserIdentity userIdentity) {
                if (userIdentity != null) {
                    updateUserIdentity(userIdentity);
                }
            }
        });

        appCMSEditAccountNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(InputField.USERNAME);
            }
        });

        appCMSEditAccountEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(InputField.EMAIL);
            }
        });

        setBgColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBackgroundColor()));

        appCMSPresenter.dismissOpenDialogs();

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

    private void setBgColor(int bgColor) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(bgColor));
        }
    }

    private void setWindow() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = dialog.getWindow();
            window.setLayout(width, height);
            window.setGravity(Gravity.START);
        }
    }

    private void showDialog(InputField inputType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_update_info, null);
        final EditText inputField = (EditText) dialogView.findViewById(R.id.app_cms_user_update_input);

        builder.setView(dialogView);

        final String username = appCMSAccountNameInfo.getText().toString();
        final String email = appCMSAccountEmailInfo.getText().toString();

        switch (inputType) {
            case USERNAME:
                inputField.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                builder.setPositiveButton(R.string.app_cms_update_username_dialog_title,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                appCMSPresenter.updateUserData(inputField.getText().toString(),
                                        email,
                                        new Action1<UserIdentity>() {
                                            @Override
                                            public void call(UserIdentity userIdentity) {
                                                updateUserIdentity(userIdentity);
                                            }
                                        });
                            }
                        });
                break;
            case EMAIL:
                inputField.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                builder.setPositiveButton(R.string.app_cms_update_email_dialog_title,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                appCMSPresenter.updateUserData(username,
                                        inputField.getText().toString(),
                                        new Action1<UserIdentity>() {
                                            @Override
                                            public void call(UserIdentity userIdentity) {
                                                updateUserIdentity(userIdentity);
                                            }
                                        });
                            }
                        });
                break;
            default:
        }
        builder.create().show();
    }

    private void updateUserIdentity(UserIdentity userIdentity) {
        if (userIdentity != null) {
            if (!TextUtils.isEmpty(userIdentity.getName())) {
                appCMSAccountNameInfo.setText(userIdentity.getName());
            }
            if (!TextUtils.isEmpty(userIdentity.getEmail())) {
                appCMSAccountEmailInfo.setText(userIdentity.getEmail());
            }
        }
    }
}
