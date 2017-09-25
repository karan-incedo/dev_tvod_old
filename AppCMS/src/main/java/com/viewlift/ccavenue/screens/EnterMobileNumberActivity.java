package com.viewlift.ccavenue.screens;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;

public class EnterMobileNumberActivity extends AppCompatActivity {

    EditText id_et_mobile_number ;
    Button id_btn_checkout ;
    CardView elevated_button_card ;
    private AppCMSPresenter appCMSPresenter;
    ImageButton app_cms_close_button ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_mobile_number);
        if (!getResources().getBoolean(R.bool.isTablet)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        appCMSPresenter = ((AppCMSApplication) getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        id_et_mobile_number = (EditText) findViewById(R.id.id_et_mobile_number) ;
        id_btn_checkout = (Button) findViewById(R.id.id_btn_checkout) ;
        elevated_button_card = (CardView) findViewById(R.id.elevated_button_card) ;
        try {
            String colorCode = getIntent().getStringExtra("color_theme");
            elevated_button_card.setBackgroundColor(Color.parseColor(colorCode));
        } catch (Exception ex) {
           ex.printStackTrace();
        }
        id_btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobileNumber = id_et_mobile_number.getText().toString().trim() ;
                if (mobileNumber.length()==10) {
                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    id_btn_checkout.setEnabled(false);
                    Intent intent = new Intent(EnterMobileNumberActivity.this, WebViewActivity.class);
                    intent.putExtras(getIntent()) ;
                    intent.putExtra("payment_option","") ;
                    intent.putExtra("orderId","") ;
                    intent.putExtra("accessCode","") ;
                    intent.putExtra("merchantID","") ;
                    intent.putExtra("cancelRedirectURL","") ;
                    intent.putExtra("rsa_key","") ;
                    intent.putExtra("billing_tel",mobileNumber) ;
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide valid 10 digits mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        app_cms_close_button = (ImageButton) findViewById(R.id.app_cms_close_button) ;
        app_cms_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
