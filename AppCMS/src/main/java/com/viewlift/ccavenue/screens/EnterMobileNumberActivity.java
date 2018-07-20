package com.viewlift.ccavenue.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.BaseView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EnterMobileNumberActivity extends AppCompatActivity {

    @BindView(R.id.id_et_mobile_number)
    EditText id_et_mobile_number;
    @BindView(R.id.elevated_button_card)
    CardView elevated_button_card;
    private AppCMSPresenter appCMSPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_mobile_number);
        ButterKnife.bind(this);
        appCMSPresenter = ((AppCMSApplication) getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();
        if (BaseView.isTablet(this)) {
            appCMSPresenter.unrestrictPortraitOnly();
        } else {
            appCMSPresenter.restrictPortraitOnly();
        }
        try {
            int colorCode = getIntent().getIntExtra("color_theme",R.color.colorNavBarText);
            elevated_button_card.setBackgroundColor(colorCode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @OnClick(R.id.id_btn_checkout)
    void openPaymentPage(View button) {
        String mobileNumber = id_et_mobile_number.getText().toString().trim();
        if (mobileNumber.length() == 10) {
            appCMSPresenter.closeSoftKeyboard();
            button.setEnabled(false);
            if (appCMSPresenter.useCCAvenue()) {
                Intent intent = new Intent(EnterMobileNumberActivity.this, WebViewActivity.class);
                intent.putExtras(getIntent());
                intent.putExtra("payment_option", "");
                intent.putExtra("orderId", "");
                intent.putExtra("accessCode", "");
                intent.putExtra("merchantID", "");
                intent.putExtra("cancelRedirectURL", "");
                intent.putExtra("rsa_key", "");
                intent.putExtra("billing_tel", mobileNumber);
                startActivity(intent);
                finish();
            }
            if (appCMSPresenter.useSSLCommerz()) {
                finish();
                appCMSPresenter.initiateSSLCommerzPurchase(mobileNumber,
                        getIntent().getStringExtra(getString(R.string.app_cms_plan_id)),
                        getIntent().getStringExtra("plan_to_purchase_name"));
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please provide valid 10 digits mobile number", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.app_cms_close_button)
    void closeActivity() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appCMSPresenter.closeSoftKeyboard();
    }
}
