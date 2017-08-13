package com.viewlift.tv.views.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.WindowManager;

import com.viewlift.R;


/**
 * Created by anup.gupta on 7/17/2017.
 */

public class AbsDialogFragment extends DialogFragment {

    public AbsDialogFragment() {
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getDialog().getWindow() != null) {
            int width = getResources().getDimensionPixelSize(R.dimen.text_overlay_dialog_width);
            int height = getResources().getDimensionPixelSize(R.dimen.text_overlay_dialog_height);
            getDialog().getWindow().setLayout(width, height);
            getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            getDialog().getWindow().setDimAmount(0.6f);
            getDialog().getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        }
    }
}
