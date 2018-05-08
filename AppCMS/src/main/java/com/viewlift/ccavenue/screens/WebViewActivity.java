package com.viewlift.ccavenue.screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.analytics.AppsFlyerUtils;
import com.viewlift.ccavenue.utility.AvenuesParams;
import com.viewlift.ccavenue.utility.RSAUtility;
import com.viewlift.ccavenue.utility.ServiceUtility;
import com.viewlift.models.data.appcms.ccavenue.RSAKeyResponse;
import com.viewlift.presenters.AppCMSPresenter;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import static com.viewlift.ccavenue.utility.Constants.TRANS_URL;

public class WebViewActivity extends Activity {
    private static final String TAG = "CCAvenueWebView";

    Intent mainIntent;
    String html, encVal;
    String orderID = "";
    String accessCode = "";
    String cancelRedirectURL = "";
    String merchantID = "";
    private AppCMSPresenter appCMSPresenter;
    RenderView readerViewAyncTask = null;
    updateSubscriptionPlanAsyncTask updateStatus = null;
    ProgressDialog progressDialog = null;
    boolean backPressFlag;
    private final String FIREBASE_PLAN_ID = "item_id";
    private final String FIREBASE_PLAN_NAME = "item_name";
    private final String FIREBASE_CURRENCY_NAME = "currency";
    private final String FIREBASE_VALUE = "value";
    private final String FIREBASE_TRANSACTION_ID = "transaction_id";

    private final String FIREBASE_ECOMMERCE_PURCHASE = "ecommerce_purchase";
    String vResponse = "";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        backPressFlag = false;
        setContentView(R.layout.activity_webview);
        mainIntent = getIntent();
        orderID = mainIntent.getStringExtra("orderId");
        accessCode = mainIntent.getStringExtra("accessCode");
        merchantID = mainIntent.getStringExtra("merchantID");
        cancelRedirectURL = mainIntent.getStringExtra("cancelRedirectURL");

        deleteDatabase("webview.db");
        deleteDatabase("webviewCache.db");

        final WebView webview = (WebView) findViewById(R.id.webview);
        webview.clearFormData();
        webview.clearHistory();
        webview.clearCache(true);

        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        clearCookies();

        appCMSPresenter = ((AppCMSApplication) getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        // Calling async task to get display content
        ProgressDialog dialog;
        dialog = new ProgressDialog(WebViewActivity.this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        appCMSPresenter.getCCAvenueRSAKey(
                new AppCMSPresenter.AppCMSCCAvenueRSAKeyAPIAction("RSA") {
                    @Override
                    public void call(RSAKeyResponse rsaKeyResponse) {
                        dialog.dismiss();
                        vResponse = rsaKeyResponse.getRsaToken();
                        orderID = rsaKeyResponse.getOrderId();
                        accessCode = rsaKeyResponse.getAccessCode();
                        cancelRedirectURL = rsaKeyResponse.getRedirectUrl();
                        merchantID = rsaKeyResponse.getMerchantId();
                        readerViewAyncTask = new RenderView();
                        readerViewAyncTask.execute();
                    }
                });
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class RenderView extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            dialog = new ProgressDialog(WebViewActivity.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            String vResponse = getRSAKey();
            if (!ServiceUtility.chkNull(vResponse).equals("")
                    && ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR") == -1) {
                StringBuffer vEncVal = new StringBuffer("");
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, mainIntent.getStringExtra(AvenuesParams.AMOUNT)));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, mainIntent.getStringExtra(AvenuesParams.CURRENCY)));
                encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length() - 1), vResponse);
            }
            return null;
        }

        @SuppressWarnings("unused")
        class MyJavaScriptInterface {
            @JavascriptInterface
            public void processHTML(String html) {
                // process the html as needed by the app
                String status = null;
                if (html.indexOf("F") != -1) {
                    backPressFlag = false;
                    status = "Transaction Declined!";
                    displaySuccessPaymentDialog("Transaction Failed!", "Retry Later");
                } else if (html.contains("O")) {
                    backPressFlag = false;
                    displaySuccessPaymentDialog("Thank you for signing up on Hoichoi! We are processing your subscription and shall notify you via email.#hoyejak",
                            "OK");
                } else if (html.indexOf("S") != -1) {
                    try {
                        backPressFlag = true;

                        if (!TextUtils.isEmpty(appCMSPresenter.getAppsFlyerKey())) {
                            AppsFlyerUtils.subscriptionEvent(WebViewActivity.this,
                                    true,
                                    appCMSPresenter.getAppsFlyerKey(),
                                    String.valueOf(mainIntent.getStringExtra(AvenuesParams.AMOUNT)),
                                    getIntent().getStringExtra(getString(R.string.app_cms_plan_id)),
                                    mainIntent.getStringExtra(AvenuesParams.CURRENCY));
                        }

                        Bundle bundle = new Bundle();
                        bundle.putString(FIREBASE_PLAN_ID, mainIntent.getStringExtra(getString(R.string.app_cms_plan_id)));
                        bundle.putString(FIREBASE_PLAN_NAME, mainIntent.getStringExtra("plan_to_purchase_name"));
                        bundle.putString(FIREBASE_CURRENCY_NAME, mainIntent.getStringExtra(AvenuesParams.CURRENCY));
                        bundle.putDouble(FIREBASE_VALUE, Double.valueOf(mainIntent.getStringExtra(AvenuesParams.AMOUNT)));
                        bundle.putString(FIREBASE_TRANSACTION_ID, orderID);

                        if (appCMSPresenter.getmFireBaseAnalytics() != null)
                            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_ECOMMERCE_PURCHASE, bundle);


                        appCMSPresenter.finalizeSignupAfterCCAvenueSubscription(null);
                        updateStatus = new updateSubscriptionPlanAsyncTask();
                        updateStatus.execute();
                        Intent stopPageLoadingActionIntent = new Intent(
                                AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
                        stopPageLoadingActionIntent.putExtra(getString(R.string.app_cms_package_name_key), getPackageName());
                        sendBroadcast(stopPageLoadingActionIntent);
                        finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (html.indexOf("Aborted") != -1) {
                    status = "Transaction Cancelled!";
                    backPressFlag = false;
                    displaySuccessPaymentDialog("Transaction Failed!", "Retry Later");
                } else {
                    status = "Status Not Known!";
                    backPressFlag = false;
                    displaySuccessPaymentDialog("Transaction Failed!", "Retry Later");
                }
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            try {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
            } catch (Exception ex) {
                dialog = null;
            }

            final WebView webview = (WebView) findViewById(R.id.webview);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
            webview.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    backPressFlag = url.equalsIgnoreCase(TRANS_URL);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(webview, url);

                    backPressFlag = false;

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            webview.scrollTo(0, 0);
                        }
                    }, 600);
                    if (url.indexOf("/ccavResponseHandler.jsp") != -1) {
                        webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                        //webview.loadUrl("https://test.ccavenue.com/transaction/transaction.do?command=initiateTransaction");
                    }
                    //https://stgsecure.ccavenue.com/servlet/processTxn
                    ///cancelRedirectURL = "https://stgsecure.ccavenue.com/servlet/processTxn" ;
                    if (url.equalsIgnoreCase(cancelRedirectURL)) {
                        webview.setVisibility(View.GONE);
                        /* This call inject JavaScript into the page which just finished loading. */
                        webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                        //webview.stopLoading();
                    }
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
                    backPressFlag = false;
                }
            });

            /* An instance of this class will be registered as a JavaScript interface */
            StringBuffer params = new StringBuffer();
            params.append(ServiceUtility.addToPostParams(AvenuesParams.ACCESS_CODE, accessCode));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_ID, merchantID));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.ORDER_ID, orderID));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.REDIRECT_URL, cancelRedirectURL));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.CANCEL_URL, cancelRedirectURL));
            //params.append(ServiceUtility.addToPostParams("billing_name",getIntent().getStringExtra("authorizedUserName")));
            params.append(ServiceUtility.addToPostParams("billing_email", getIntent().getStringExtra("email")));
            params.append(ServiceUtility.addToPostParams("billing_country", "India"));
            params.append(ServiceUtility.addToPostParams("billing_tel", getIntent().getStringExtra("billing_tel")));

            Log.v("payment_option", mainIntent.getStringExtra("payment_option"));
            params.append(ServiceUtility.addToPostParams("payment_option", mainIntent.getStringExtra("payment_option")));
            //if (getIntent().getBooleanExtra("renewable",false)) {
            //if (getIntent().getStringExtra("payment_option").equalsIgnoreCase("OPTCRDC")) {
            //params.append(ServiceUtility.addToPostParams("payment_option","OPTCRDC")) ;
            params.append(ServiceUtility.addToPostParams("si_type", "ONDEMAND"));
            params.append(ServiceUtility.addToPostParams("si_mer_ref_no", merchantID));
            params.append(ServiceUtility.addToPostParams("si_is_setup_amt", "Y"));
            params.append(ServiceUtility.addToPostParams("si_amount", mainIntent.getStringExtra(AvenuesParams.AMOUNT)));
            params.append(ServiceUtility.addToPostParams("si_setup_amount", mainIntent.getStringExtra(AvenuesParams.AMOUNT)));
//				params.append(ServiceUtility.addToPostParams("si_frequency",mainIntent.getStringExtra("si_frequency"))) ;
//				params.append(ServiceUtility.addToPostParams("si_bill_cycle",mainIntent.getStringExtra("si_frequency"))) ;
//				params.append(ServiceUtility.addToPostParams("si_frequency_type",mainIntent.getStringExtra("si_frequency_type"))) ;
            //}

            params.append(ServiceUtility.addToPostParams("merchant_param1", getIntent().getStringExtra(getString(R.string.app_cms_site_name))));
            params.append(ServiceUtility.addToPostParams("merchant_param2", getIntent().getStringExtra(getString(R.string.app_cms_user_id))));
            params.append(ServiceUtility.addToPostParams("merchant_param3", getIntent().getStringExtra(getString(R.string.app_cms_plan_id))));
            params.append(ServiceUtility.addToPostParams("merchant_param4", getString(R.string.app_cms_subscription_platform_key)));
            params.append(ServiceUtility.addToPostParams("merchant_param5", ""));
            //params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_EMAIL,"email")) ;
            try {
                params.append(ServiceUtility.addToPostParams(AvenuesParams.ENC_VAL, URLEncoder.encode(encVal, "UTF-8")));
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }


            String vPostParams = params.substring(0, params.length() - 1);
            try {
                webview.postUrl(TRANS_URL, EncodingUtils.getBytes(vPostParams, "UTF-8"));
            } catch (Exception e) {
                showToast("Exception occured while opening webview.");
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("deprecation")
    public void clearCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d(TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }


    private class updateSubscriptionPlanAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            //      progressDialog = new ProgressDialog(WebViewActivity.this);
            //      progressDialog.setMessage("Updating Subscription...");
            //      progressDialog.setCancelable(false);
            //      progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            // Creating service handler class instance
            String JsonResponse = null;
            String JsonDATA = "";
            JSONObject post_dict = new JSONObject();

            try {
                post_dict.put("vlTransactionId", orderID);
                post_dict.put("email", getIntent().getStringExtra("email"));
                post_dict.put("currencyCode", "INR");
                post_dict.put("siteId", getIntent().getStringExtra("siteId"));
                post_dict.put("planId", getIntent().getStringExtra(getString(R.string.app_cms_plan_id)));
                post_dict.put("platform", "android");
                post_dict.put("zip", "");
                post_dict.put("description", "CCAvenue Subscripton");
                post_dict.put("subscription", "ccavenue"); //very important to say this is a ccavenue request
                post_dict.put("authorizedUserName", getIntent().getStringExtra("authorizedUserName"));
                JsonDATA = String.valueOf(post_dict);
                try {
                    Log.v("userid", getIntent().getStringExtra("authorizedUserName"));
                    Log.v("JsonDATA", JsonDATA);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(mainIntent.getStringExtra("api_base_url") + "/subscription/subscribe?site=hoichoi-tv");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Authorization", getIntent().getStringExtra("auth_token"));
                urlConnection.setRequestProperty("x-api-token", getIntent().getStringExtra("x-api-token"));
                //set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                // json data
                writer.close();
                InputStream inputStream = null;
                try {
                    inputStream = urlConnection.getInputStream();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                    closeConnection(urlConnection, reader);
                    return "";
                }
                //input stream
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    closeConnection(urlConnection, reader);
                    return "";
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                JsonResponse = buffer.toString();
                Log.v("subscriberesponse", JsonResponse);
                //response data
                Log.i("TAG", JsonResponse);
            } catch (IOException e) {
                closeConnection(urlConnection, reader);
                Log.e(TAG, e.getMessage());
            }
            //      } finally {
            //        if (urlConnection != null) {
            //          try {
            //            urlConnection.disconnect();
            //          } catch (Exception ex) {
            //            Log.e(TAG, ex.getMessage());
            //          }
            //        }
            //        try {
            //          if (reader != null) {
            //            try {
            //              reader.close();
            //            } catch (final IOException e) {
            //              Log.e("TAG", "Error closing stream", e);
            //            }
            //          }
            //        } catch (Exception ex) {
            //          Log.e(TAG, ex.getMessage());
            //        }
            //      }
            return JsonResponse;
        }

        private void closeConnection(HttpURLConnection urlConnection, BufferedReader reader) {
            if (urlConnection != null) {
                try {
                    urlConnection.disconnect();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
            try {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("TAG", "Error closing stream", e);
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private void displaySuccessPaymentDialog(String message, String buttonTitle) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(WebViewActivity.this);
        builder1.setMessage(message);
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                buttonTitle,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    protected void onDestroy() {
        if (readerViewAyncTask != null) {
            if (readerViewAyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                readerViewAyncTask.cancel(true);
                readerViewAyncTask = null;
            }
        }
        if (progressDialog != null) {
            try {
                if (progressDialog.isShowing()) {
                    progressDialog.hide();
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (backPressFlag) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //Do something here
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}