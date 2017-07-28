package com.viewlift.tv.views.fragment;

import android.content.Context;

import android.graphics.Color;
import android.os.Build;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;


/**
 * Created by anup.gupta on 7/17/2017.
 */

public class TextOverlayDialogFragment extends AbsDialogFragment {

    private String desc_text;
    private static Context mContext;
    private AppCMSPresenter appCMSPresenter;

    public TextOverlayDialogFragment() {
        super();
    }

    public static TextOverlayDialogFragment newInstance(Context context , Bundle bundle) {
        TextOverlayDialogFragment fragment = new TextOverlayDialogFragment();
        mContext = context;
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_text_overlay, container, false);

        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();
        
        /*Bind Views*/
        Button btnClose = (Button) mView.findViewById(R.id.btn_close);
        TextView tvTitle = (TextView) mView.findViewById(R.id.text_overlay_title);
        TextView tvDescription = (TextView) mView.findViewById(R.id.text_overlay_description);


        /*Request focus on the description */
        //tvDescription.requestFocus();
        Bundle arguments = getArguments();
        String title = arguments.getString(mContext.getString(R.string.dialog_item_title_key), null);
        String description = arguments.getString(mContext.getString(R.string.dialog_item_description_key), null);
        String textColor = appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor();

        if (title == null || description == null) {
            throw new RuntimeException("Either title or description is null");
        }

        desc_text = getString(R.string.text_with_color,
                Integer.toHexString(Color.parseColor(textColor)).substring(2),
                description);
        tvTitle.setText(title);
        tvDescription.setText(Html.fromHtml(desc_text));

        //sendAnalytics(title);
        btnClose.requestFocus();
        /*Set click listener*/
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return mView;
    }

}
