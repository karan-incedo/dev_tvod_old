package com.viewlift.tv.views.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;

import rx.functions.Action1;

/**
 * Created by anas.azeem on 9/13/2017.
 * Owned by ViewLift, NYC
 */

public class ClearDialogFragment extends AbsDialogFragment {

    public static final String DIALOG_HEIGHT_KEY = "dialog_height_key";
    public static final String DIALOG_WIDTH_KEY = "dialog_width_key";
    public static final String DIALOG_TITLE_KEY = "dialog_title_key";
    public static final String DIALOG_MESSAGE_KEY = "dialog_message_key";
    public static final String DIALOG_TITLE_TEXT_COLOR_KEY = "dialog_title_text_color_key";
    public static final String DIALOG_MESSAGE_TEXT_COLOR_KEY = "dialog_message_text_color_key";
    public static final String DIALOG_POSITIVE_BUTTON_TEXT_KEY = "dialog_positive_button_text_key";
    public static final String DIALOG_NEGATIVE_BUTTON_TEXT_KEY = "dialog_negative_button_text_key";
    public static final String DIALOG_TITLE_VISIBILITY_KEY = "dialog_title_visibility_key";
    public static final String DIALOG_MESSAGE_VISIBILITY_KEY = "dialog_message_visibility_key";
    public static final String DIALOG_POSITIVE_BUTTON_VISIBILITY_KEY
            = "dialog_positive_button_visibility_key";
    public static final String DIALOG_NEGATIVE_BUTTON_VISIBILITY_KEY
            = "dialog_negative_button_visibility_key";
    private Action1<String> onPositiveButtonClicked;

    public ClearDialogFragment() {
        super();
    }

    public static ClearDialogFragment newInstance(Bundle bundle) {
        ClearDialogFragment fragment = new ClearDialogFragment();
        fragment.setArguments(bundle);

        return fragment;
    }


    public void setOnPositiveButtonClicked (Action1<String> onPositiveButtonClicked){
        this.onPositiveButtonClicked = onPositiveButtonClicked;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_clear_overlay, container, false);

        AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        String backGroundColor = Utils.getBackGroundColor(getActivity(), appCMSPresenter);
        mView.findViewById(R.id.fragment_clear_overlay).setBackgroundColor(Color.parseColor(backGroundColor));

        /*Bind Views*/
        Button negativeButton = (Button) mView.findViewById(R.id.btn_cancel);
        Button positiveButton = (Button) mView.findViewById(R.id.btn_yes);
//        TextView tvTitle = (TextView) mView.findViewById(R.id.text_overlay_title);
        TextView tvDescription = (TextView) mView.findViewById(R.id.text_overlay_description);
        ScrollView scrollView = (ScrollView) mView.findViewById(R.id.scrollview);

        /*Request focus on the description */
        //tvDescription.requestFocus();
        Bundle arguments = getArguments();
        String title = arguments.getString(DIALOG_TITLE_KEY, null);
        String description = arguments.getString(DIALOG_MESSAGE_KEY, null);
        String textColor = arguments.getString(DIALOG_MESSAGE_TEXT_COLOR_KEY, null);

        if (title == null || description == null) {
            throw new RuntimeException("Either title or description is null");
        }

        String desc_text = getString(R.string.text_with_color,
                Integer.toHexString(Color.parseColor(textColor)).substring(2),
                description);

        /*if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }*/
        tvDescription.setText(Html.fromHtml(desc_text));

        Component component = new Component();
        component.setFontFamily(getString(R.string.app_cms_page_font_family_key));
        tvDescription.setTypeface(Utils.getTypeFace(getActivity(), appCMSPresenter.getJsonValueKeyMap(), component));
        //tvDescription.setTextSize(R.dimen.text_ovelay_dialog_desc_font_size);

        Component btnComponent1 = new Component();
        btnComponent1.setFontFamily(getString(R.string.app_cms_page_font_family_key));
        btnComponent1.setFontWeight(getString(R.string.app_cms_page_font_semibold_key));
        btnComponent1.setBorderColor(Utils.getColor(getActivity(), Integer.toHexString(ContextCompat.getColor(getActivity(),
                R.color.btn_color_with_opacity))));
        btnComponent1.setBorderWidth(4);

        negativeButton.setBackground(Utils.setButtonBackgroundSelector(getActivity(),
                Color.parseColor(Utils.getFocusColor(getActivity(), appCMSPresenter)),
                btnComponent1));

        negativeButton.setTextColor(Utils.getButtonTextColorDrawable(
                Utils.getColor(getActivity(), Integer.toHexString(ContextCompat.getColor(getActivity(),
                        R.color.btn_color_with_opacity))),
                Utils.getColor(getActivity(), Integer.toHexString(ContextCompat.getColor(getActivity(),
                        android.R.color.white)))
        ));


        negativeButton.setTypeface(Utils.getTypeFace(getActivity(), appCMSPresenter.getJsonValueKeyMap(), btnComponent1));

        positiveButton.setBackground(Utils.setButtonBackgroundSelector(getActivity(),
                Color.parseColor(Utils.getFocusColor(getActivity(), appCMSPresenter)),
                btnComponent1));

        positiveButton.setTextColor(Utils.getButtonTextColorDrawable(
                Utils.getColor(getActivity(), Integer.toHexString(ContextCompat.getColor(getActivity(),
                        R.color.btn_color_with_opacity))),
                Utils.getColor(getActivity(), Integer.toHexString(ContextCompat.getColor(getActivity(),
                        android.R.color.white)))
        ));


        positiveButton.setTypeface(Utils.getTypeFace(getActivity(), appCMSPresenter.getJsonValueKeyMap(), btnComponent1));

        positiveButton.requestFocus();

        /*Set click listener*/
        negativeButton.setOnClickListener(v -> dismiss());

        negativeButton.setOnKeyListener((view, i, keyEvent) -> {
            int keyCode = keyEvent.getKeyCode();
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        if (scrollView.canScrollVertically(View.SCROLL_AXIS_VERTICAL)
                                || scrollView.canScrollVertically(View.NO_ID)) {
                            tvDescription.requestFocus();
                        } else {
                            negativeButton.requestFocus();
                        }
                        return true;
                    }
            }
            return false;
        });

        positiveButton.setOnClickListener(v -> {
            onPositiveButtonClicked.call("");
        });
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        int width = getArguments().getInt(DIALOG_WIDTH_KEY);
        int height = getArguments().getInt(DIALOG_HEIGHT_KEY);
        bundle.putInt(getString(R.string.tv_dialog_width_key), width);
        bundle.putInt(getString(R.string.tv_dialog_height_key), height);
        super.onActivityCreated(bundle);
    }
}
