package com.viewlift.views.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.presenters.AppCMSPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.viewlift.R;

/**
 * Created by viewlift on 7/17/17.
 */

public class AppCMSMoreFragment extends DialogFragment {
    private static final String TAG = "MoreFragment";

    public static AppCMSMoreFragment newInstance(Context context, String title, String moreText) {
        AppCMSMoreFragment fragment = new AppCMSMoreFragment();
        Bundle args = new Bundle();
        args.putString(context.getString(R.string.app_cms_more_title_key), title);
        args.putString(context.getString(R.string.app_cms_more_text_key), moreText);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.app_cms_close_button)
    ImageButton appCMSCloseButton;

    @BindView(R.id.app_cms_more_text)
    TextView appCMSMoreText;


    @BindView(R.id.app_cms_more_title_text)
    TextView appCMSMoreTitleText;

    private AppCMSPresenter appCMSPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        ButterKnife.bind(this, view);

        Bundle args = getArguments();

        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        String textColor = "#ffffffff";
        try {
            textColor = appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor();
        } catch (Exception e) {
            Log.e(TAG, "Could not retrieve text color from AppCMS Brand: " + e.getMessage());
        }

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

        appCMSMoreText.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));
        appCMSMoreText.setText(Html.fromHtml(getContext().getString(R.string.text_with_color,
                Integer.toHexString(Color.parseColor(textColor)).substring(2),
                args.getString(getContext().getString(R.string.app_cms_more_text_key)))));



        appCMSMoreText.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                .getBrand().getGeneral().getTextColor()));
        appCMSMoreTitleText.setText(Html.fromHtml(getContext().getString(R.string.text_with_color,
                Integer.toHexString(Color.parseColor(textColor)).substring(2),
                args.getString(getContext().getString(R.string.app_cms_more_title_key)))));
        appCMSPresenter.dismissOpenDialogs(null);

        setBgColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBackgroundColor()));

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
}
