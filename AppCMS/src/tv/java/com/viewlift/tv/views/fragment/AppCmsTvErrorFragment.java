package com.viewlift.tv.views.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;


/**
 * A placeholder fragment containing a simple view.
 */
public class AppCmsTvErrorFragment extends AbsDialogFragment {

    AppCMSPresenter appCMSPresenter;
    private ErrorFragmentListener mErrorFragmentListener;
    Button btnRetry;
    boolean shouldRegisterInternetReciever = true;
    private boolean shouldNavigateToLogin;

    public AppCmsTvErrorFragment(){

    }

    public static AppCmsTvErrorFragment newInstance(Bundle bundle) {
        AppCmsTvErrorFragment appCmsTvErrorActivityFragment = new AppCmsTvErrorFragment();
        appCmsTvErrorActivityFragment.setArguments(bundle);
        return appCmsTvErrorActivityFragment;
    }

    public void setErrorListener(ErrorFragmentListener errorFragmentListener){
        mErrorFragmentListener = errorFragmentListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_cms_tv_error, container, false);
        TextView errorTextView = (TextView) view.findViewById(R.id.app_cms_error_textview);
        TextView headerView = (TextView)view.findViewById(R.id.title);
        errorTextView.setText(getString(R.string.no_inernet_error , getString(R.string.app_name)));
        RelativeLayout parentLayout = (RelativeLayout)view.findViewById(R.id.dialog_parent);

        final Bundle bundle = getArguments();
        final boolean shouldRetry = bundle.getBoolean(getString(R.string.retry_key));
        shouldRegisterInternetReciever = bundle.getBoolean(getString(R.string.register_internet_receiver_key));
        String errorMsg = bundle.getString(getString(R.string.tv_dialog_msg_key));
        String headerTitle = bundle.getString(getString(R.string.tv_dialog_header_key));
        shouldNavigateToLogin = bundle.getBoolean(getString(R.string.shouldNavigateToLogin));

        if(null != errorMsg){
            errorTextView.setText(errorMsg);
        }

        if(null != headerTitle){
            headerView.setText(headerTitle);
        }


        appCMSPresenter =
                ((AppCMSApplication) getActivity().getApplication()).getAppCMSPresenterComponent().appCMSPresenter();

        String textColor = Utils.getTextColor(getActivity(),appCMSPresenter);
        String backGroundColor = appCMSPresenter.getAppBackgroundColor();
        String focusColor = Utils.getFocusColor(getActivity(),appCMSPresenter);

        errorTextView.setTextColor(Color.parseColor(textColor));

        Button btnClose = (Button) view.findViewById(R.id.btn_close);
        Component btnComponent1 = new Component();
        btnComponent1.setFontFamily(getString(R.string.app_cms_page_font_family_key));
        btnComponent1.setFontWeight(getString(R.string.app_cms_page_font_semibold_key));
        btnComponent1.setBorderColor("#ffffff");
        btnComponent1.setBorderWidth(4);
        btnClose.setTextColor(Color.parseColor(textColor));

        btnClose.setBackground(Utils.setButtonBackgroundSelector(getActivity() ,
                Color.parseColor(focusColor),
                btnComponent1 , appCMSPresenter));

        btnClose.setTypeface(Utils.getTypeFace(getActivity() ,appCMSPresenter.getJsonValueKeyMap(), btnComponent1));

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // if(shouldNavigateToLogin) {
                    mErrorFragmentListener.onErrorScreenClose();
                //}
                dismiss();
            }
        });


        btnRetry = (Button) view.findViewById(R.id.btn_retry);
        Component btnRetryComp = new Component();
        btnRetryComp.setFontFamily(getString(R.string.app_cms_page_font_family_key));
        btnRetryComp.setFontWeight(getString(R.string.app_cms_page_font_semibold_key));
        btnRetryComp.setBorderColor("#ffffff");
        btnRetryComp.setBorderWidth(4);
        btnRetry.setTextColor(Color.parseColor(textColor));

        btnRetry.setBackground(Utils.setButtonBackgroundSelector(getActivity() ,
                Color.parseColor(focusColor),
                btnRetryComp,
                appCMSPresenter));

        btnRetry.setTypeface(Utils.getTypeFace(getActivity() ,appCMSPresenter.getJsonValueKeyMap(), btnRetryComp));

        btnRetry.requestFocus();
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mErrorFragmentListener.onRetry(bundle);
                dismiss();
            }
        });

        if(!shouldRetry) {
            btnRetry.setVisibility(View.INVISIBLE);
            btnClose.setText(getResources().getString(R.string.app_cms_ok_alert_dialog_button_text));
        } else {
            btnClose.setText(getResources().getString(R.string.app_cms_close_alert_dialog_button_text));
        }
        if(null != backGroundColor)
        parentLayout.setBackgroundColor(Color.parseColor(backGroundColor));
        return view;
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

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                switch(keyEvent.getKeyCode()){
                    case KeyEvent.KEYCODE_BACK:
                        if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                            mErrorFragmentListener.onErrorScreenClose();
                            dismiss();
                            return true;
                        }
                        break;
                }
                return false;
            }
        });

        if(shouldRegisterInternetReciever)
        getActivity().registerReceiver(networkReciever ,
                new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

    }

    @Override
    public void onPause() {
        if(shouldRegisterInternetReciever)
        getActivity().unregisterReceiver(networkReciever);
        super.onPause();
    }

    public interface ErrorFragmentListener{
        public void onErrorScreenClose();
        public void onRetry(Bundle bundle);
    }

    BroadcastReceiver networkReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE")){
                if(appCMSPresenter.isNetworkConnected()){
                    //TODO:resume the video.
                    btnRetry.callOnClick();
                }
            }
        }
    };
}
