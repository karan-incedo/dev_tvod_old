package com.viewlift.views.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.AsteriskPasswordTransformation;
import com.viewlift.views.customviews.ViewCreator;

import java.lang.reflect.Field;
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

    private String regex = "[a-zA-Z\\s]+";

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

        Bundle args = getArguments();
        String username = args.getString(getContext().getString(R.string.app_cms_edit_profile_username_key));
        String email = args.getString(getContext().getString(R.string.app_cms_password_reset_email_key));

        titleTextView.setTextColor(textColor);
        appCMSEditProfileNameText.setTextColor(textColor);
        appCMSEditProfileEmailText.setTextColor(textColor);

        titleTextView.setTypeface(appCMSPresenter.getBoldTypeFace());
        appCMSEditProfileNameText.setTypeface(appCMSPresenter.getBoldTypeFace());
        appCMSEditProfileEmailText.setTypeface(appCMSPresenter.getBoldTypeFace());

        if (!TextUtils.isEmpty(username)) {
            appCMSEditProfileNameInput.setText(username);
            appCMSEditProfileNameInput.setTextColor(textColor);
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

            setCursorColor(password,textColor);
            textInputLayout.addView(password);
            textInputLayout.setPasswordVisibilityToggleEnabled(true);
            password.setTransformationMethod(new AsteriskPasswordTransformation());

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
            Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            buttonPositive.setTextColor(textColor);
            Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            buttonNegative.setTextColor(textColor);

        });

        editProfileConfirmChangeButton.setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());
        editProfileConfirmChangeButton.setBackgroundColor(buttonColor);
        setBgColor(bgColor);

        return view;
    }

    public static void setCursorColor(EditText view, @ColorInt int color) {
        try {
            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(view);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(view);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception ignored) {
        }
    }

    private void setBgColor(int bgColor) {
        appCMSEditProfileMainLayout.setBackgroundColor(bgColor);
    }

    private boolean doesValidNameExist(String input) {
        return !TextUtils.isEmpty(input) && Pattern.matches(regex, input);
    }
}
