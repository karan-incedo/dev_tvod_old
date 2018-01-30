package com.viewlift.tv.views.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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


/**
 * Created by anup.gupta on 7/17/2017.
 */

public class TextOverlayDialogFragment extends AbsDialogFragment {

    private String desc_text;
    private static Context mContext;
    private AppCMSPresenter appCMSPresenter;
    private Button btnClose;

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

        String backGroundColor = Utils.getBackGroundColor(getActivity(),appCMSPresenter);
        mView.findViewById(R.id.fragment_text_overlay).setBackgroundColor(Color.parseColor(backGroundColor));

        /*Bind Views*/
        btnClose = (Button) mView.findViewById(R.id.btn_close);
        TextView tvTitle = (TextView) mView.findViewById(R.id.text_overlay_title);
        TextView tvDescription = (TextView) mView.findViewById(R.id.text_overlay_description);
        ScrollView scrollView = (ScrollView)mView.findViewById(R.id.scrollview);

        /*Request focus on the description */
        //tvDescription.requestFocus();
        Bundle arguments = getArguments();
        String title = arguments.getString(mContext.getString(R.string.dialog_item_title_key), null);
        String description = arguments.getString(mContext.getString(R.string.dialog_item_description_key), null);
        String textColor = Utils.getTextColor(mContext,appCMSPresenter);

        if (title == null || description == null) {
            throw new RuntimeException("Either title or description is null");
        }

        desc_text = getString(R.string.text_with_color,
                Integer.toHexString(Color.parseColor(textColor)).substring(2),
                description);

        tvTitle.setText(title);
        tvDescription.setText(Html.fromHtml(desc_text));

        Component component = new Component();
        component.setFontFamily(mContext.getString(R.string.app_cms_page_font_family_key));
        tvDescription.setTypeface(Utils.getTypeFace(mContext ,appCMSPresenter.getJsonValueKeyMap(), component));
        //tvDescription.setTextSize(R.dimen.text_ovelay_dialog_desc_font_size);

        Component btnComponent1 = new Component();
        btnComponent1.setFontFamily(mContext.getString(R.string.app_cms_page_font_family_key));
        btnComponent1.setFontWeight(mContext.getString(R.string.app_cms_page_font_semibold_key));
        btnComponent1.setBorderColor(Utils.getColor(getActivity(),Integer.toHexString(ContextCompat.getColor(getActivity() ,
                R.color.btn_color_with_opacity))));
        btnComponent1.setBorderWidth(4);


        btnClose.setBackground(Utils.setButtonBackgroundSelector(getActivity() ,
                Color.parseColor(Utils.getFocusColor(mContext,appCMSPresenter)),
                btnComponent1));

        btnClose.setTextColor(Utils.getButtonTextColorDrawable(
                Utils.getColor(getActivity(),Integer.toHexString(ContextCompat.getColor(getActivity() ,
                        R.color.btn_color_with_opacity)))
                ,
                Utils.getColor(getActivity() , Integer.toHexString(ContextCompat.getColor(getActivity() ,
                        android.R.color.white)))
        ));


        btnClose.setTypeface(Utils.getTypeFace(mContext ,appCMSPresenter.getJsonValueKeyMap(), btnComponent1));

        btnClose.requestFocus();

        /*Set click listener*/
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnClose.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                int keyCode = keyEvent.getKeyCode();
                switch(keyCode){
                    case KeyEvent.KEYCODE_DPAD_UP:
                        if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                            if(scrollView.canScrollVertically(View.SCROLL_AXIS_VERTICAL)
                                    || scrollView.canScrollVertically(View.NO_ID)){
                                tvDescription.requestFocus();
                            }else{
                                btnClose.requestFocus();
                            }
                            return true;
                        }
                }
                return false;
            }
        });
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        int width =  getResources().getDimensionPixelSize(R.dimen.text_overlay_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.text_overlay_dialog_height);
        bundle.putInt( getString(R.string.tv_dialog_width_key) , width);
        bundle.putInt( getString(R.string.tv_dialog_height_key) , height);
        super.onActivityCreated(bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> {
            if (isVisible() && isAdded()) {
                btnClose.requestFocus();
            }
        }, 500);
    }
}
