package com.viewlift.tv.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewlift.R;

/**
 * Created by nitin.tyagi on 7/6/2017.
 */

public class CustomProgressBar {

    private static CustomProgressBar customProgressBar;
    private static Context context;
    private Dialog dialog;
    private AnimationDrawable animDrawable;
    private final String TAG = CustomProgressBar.class.getName();
    private CustomProgressBar() {

    }

    public static final CustomProgressBar getInstance(Context context) {
        if (null == customProgressBar) {
            customProgressBar = new CustomProgressBar();
        }
        CustomProgressBar.context = context;
        return customProgressBar;
    }

    public void showProgressDialog() {

        try {
            if (dialog == null) {
                dialog = new Dialog(context, R.style.ProgressDialog);
                dialog.setContentView(R.layout.progress_dialog);
                dialog.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setCancelable(false);
                ImageView image = (ImageView) dialog.findViewById(R.id.spinnerImageView);
                animDrawable = (AnimationDrawable) image.getDrawable();
                animDrawable.start();
                dialog.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * In case we need to show some message over progress
     *
     * @param activity
     * @param message
     */
    public void showProgressDialog(Activity activity, String message ) {
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            if(dialog==null){
                dialog = new Dialog(context, R.style.ProgressDialog);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                View view = inflater.inflate(R.layout.progress_dialog, null);
                TextView txtViewMessage = (TextView)view.findViewById(R.id.text_loading);
                txtViewMessage.setText("");
                dialog.setContentView(view);
                dialog.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setCancelable(false);

                ImageView image = (ImageView)dialog.findViewById(R.id.spinnerImageView);
                animDrawable = (AnimationDrawable)image.getDrawable();
                animDrawable.start();
                dialog.show();
            }
        }
    }

    public void dismissProgressDialog() {
        //if (null != dialog && null != animDrawable && dialog.isShowing()) {

        try {
            if (null != dialog && null != animDrawable && dialog.isShowing()) {
                animDrawable.stop();
                dialog.dismiss();

                animDrawable = null;
                dialog = null;
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
            //Log.e("ASEEM...","dismissProgressDialog: "+e.getMessage());
        }
        //}
    }
}

