package com.viewlift.views.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by viewlift on 7/17/17.
 */

public class AppCMSNoPurchaseFragment extends DialogFragment {
    private static final String TAG = "MoreFragment";

    public static AppCMSNoPurchaseFragment newInstance(Context context, String title, String moreText) {
        AppCMSNoPurchaseFragment fragment = new AppCMSNoPurchaseFragment();
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

    @BindView(R.id.app_cms_back_to_desc)
    Button app_cms_back_to_desc;

    private AppCMSPresenter appCMSPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_purchase, container, false);


//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        params.setMargins(3, 100, 3, 0);
//        view.setLayoutParams(params);

        ButterKnife.bind(this, view);

        Bundle args = getArguments();

        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        app_cms_back_to_desc.setBackgroundColor(appCMSPresenter.getBrandPrimaryCtaColor());

        String textColor = "#ffffffff";
        try {
            textColor = appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor();
        } catch (Exception e) {
            //Log.e(TAG, "Could not retrieve text color from AppCMS Brand: " + e.getMessage());
        }

        appCMSCloseButton.setOnClickListener((v) -> {
            dismiss();
            if (appCMSPresenter != null) {
                appCMSPresenter.popActionInternalEvents();
                appCMSPresenter.setNavItemToCurrentAction(getActivity());
                appCMSPresenter.showMainFragmentView(true);
            }
        });

        try {
            appCMSMoreText.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                    .getBrand().getGeneral().getTextColor()));
        } catch (Exception e) {
            appCMSMoreText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        }
        appCMSMoreText.setText(Html.fromHtml(getContext().getString(R.string.text_with_color,
                Integer.toHexString(Color.parseColor(textColor)).substring(2),
                args.getString(getContext().getString(R.string.app_cms_more_text_key)))));

        try {
            appCMSMoreText.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                    .getBrand().getGeneral().getTextColor()));
        } catch (Exception e) {
            appCMSMoreText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        }
        appCMSMoreTitleText.setText(Html.fromHtml(getContext().getString(R.string.text_with_color,
                Integer.toHexString(Color.parseColor(textColor)).substring(2),
                args.getString(getContext().getString(R.string.app_cms_more_title_key)))));
        appCMSPresenter.dismissOpenDialogs(null);

        try {
            setBgColor(Color.parseColor(appCMSPresenter.getAppBackgroundColor()));
        } catch (Exception e) {
            setBgColor(ContextCompat.getColor(getContext(), android.R.color.black));
        }

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

    public void sendDismissAction() {
        dismiss();
        if (appCMSPresenter != null) {
            appCMSPresenter.showMainFragmentView(true);
        }
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
//            int width = ViewGroup.LayoutParams.MATCH_PARENT;
//            int height = dpToPx(400);//ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = dialog.getWindow();
//            window.setLayout(width, height);
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.gravity=Gravity.CENTER;
            params.y = 150;
//            params.verticalMargin=200;
            dialog.getWindow().setAttributes(params);
            Context context = dialog.getContext();

            Point displaySize = getDisplayDimensions( context );
            int width = displaySize.x - 0 - 0;
            int height = displaySize.y - 150 - 0;
            window.setLayout( width, height );
        }



    }

    @NonNull
    public static Point getDisplayDimensions(Context context )
    {
        WindowManager wm = ( WindowManager ) context.getSystemService( Context.WINDOW_SERVICE );
        Display display = wm.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics( metrics );
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // find out if status bar has already been subtracted from screenHeight
        display.getRealMetrics( metrics );
        int physicalHeight = metrics.heightPixels;
        int statusBarHeight = getStatusBarHeight( context );
        int navigationBarHeight = 100;//dpToPx(100);//getNavigationBarHeight( context );
        int heightDelta = physicalHeight - screenHeight;
        if ( heightDelta == 0 || heightDelta == navigationBarHeight )
        {
            screenHeight -= statusBarHeight;
        }

        return new Point( screenWidth, screenHeight );
    }
    public static int getStatusBarHeight( Context context )
    {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier( "status_bar_height", "dimen", "android" );
        return ( resourceId > 0 ) ? resources.getDimensionPixelSize( resourceId ) : 0;
    }
//    public static int dpToPx(int dp) {
//        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
//    }
}
