package com.viewlift.views.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.activity.AutoplayActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AppCMSNoPurchaseFragment extends DialogFragment {
    private static final String TAG = "MoreFragment";
    public static boolean isVisible=false;

    public static AppCMSNoPurchaseFragment newInstance(Context context, String title, String moreText) {
        AppCMSNoPurchaseFragment fragment = new AppCMSNoPurchaseFragment();
        Bundle args = new Bundle();
        args.putString(context.getString(R.string.app_cms_more_title_key), title);
        args.putString(context.getString(R.string.app_cms_more_text_key), moreText);
        fragment.setArguments(args);
        return fragment;
    }

//    @BindView(R.id.app_cms_close_button)
//    ImageButton appCMSCloseButton;



//    @BindView(R.id.app_cms_container_layout_parent)
//    ImageView app_cms_container_layout_parent;

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


        ButterKnife.bind(this, view);

        Bundle args = getArguments();

        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        app_cms_back_to_desc.setBackgroundColor(appCMSPresenter.getBrandPrimaryCtaColor());
        app_cms_back_to_desc.setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());
        isVisible=true;
        String textColor = "#ffffffff";
        try {
            textColor = appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor();
        } catch (Exception e) {
            //Log.e(TAG, "Could not retrieve text color from AppCMS Brand: " + e.getMessage());
        }



        app_cms_back_to_desc.setOnClickListener((v) -> {
            dismiss();
            if (appCMSPresenter != null) {
                appCMSPresenter.popActionInternalEvents();
                appCMSPresenter.setNavItemToCurrentAction(getActivity());
                appCMSPresenter.showMainFragmentView(true);
                isVisible=false;
            }
            if(getActivity() instanceof AutoplayActivity){
                getActivity().finish();
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
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);

//        app_cms_container_layout_parent.setAlpha(0.96f);
//        final Activity activity = getActivity();
//        final View content = activity.findViewById(android.R.id.content).getRootView();
//        if (app_cms_container_layout_parent.getWidth() > 0 && app_cms_container_layout_parent.getHeight()>0) {
//            Bitmap image = new BlurBuilder().blur(app_cms_container_layout_parent);
//            app_cms_container_layout_parent.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), image));
//        }
        /*else {
            content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Bitmap image = new BlurBuilder().blur(app_cms_container_layout_parent);
                    app_cms_container_layout_parent.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), image));
                }
            });
        }*/

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setWindow();
    }

    @Override
    public void onPause() {
        super.onPause();
//        dismiss();
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
        if(getActivity() instanceof AutoplayActivity){
            getActivity().finish();
        }
    }

    private void setBgColor(int bgColor) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(bgColor));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        isVisible=false;
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

    @NonNull
    public static Point getDisplayDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // find out if status bar has already been subtracted from screenHeight
        display.getRealMetrics(metrics);
        int physicalHeight = metrics.heightPixels;
        int statusBarHeight = getStatusBarHeight(context);
        int navigationBarHeight = 100;//dpToPx(100);//getNavigationBarHeight( context );
        int heightDelta = physicalHeight - screenHeight;
        if (heightDelta == 0 || heightDelta == navigationBarHeight) {
            screenHeight -= statusBarHeight;
        }

        return new Point(screenWidth, screenHeight);
    }

    public class BlurBuilder {
        private static final float BITMAP_SCALE = 0.4f;
        private static final float BLUR_RADIUS = 7.5f;

        public  Bitmap blur(View v) {
            return blur(v.getContext(), getScreenshot(v));
        }

        public Bitmap blur(Context ctx, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(ctx);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }

        private Bitmap getScreenshot(View v) {
            Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.draw(c);
            return b;
        }
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return (resourceId > 0) ? resources.getDimensionPixelSize(resourceId) : 0;
    }
//    public static int dpToPx(int dp) {
//        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
//    }
}
