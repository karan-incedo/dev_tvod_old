package com.viewlift.views.customviews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.viewlift.presenters.AppCMSPresenter;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by karan.kaushik on 11/22/2017.
 */

public class CustomWebView extends AppCMSAdvancedWebView {

    private Activity context;
    private WebView webView;
    AppCMSPresenter appcmsPresenter;

    public CustomWebView(Context context, AppCMSPresenter appcmsPresenter) {
        super(context);
        this.context = (Activity) context;
        webView = this;
        this.appcmsPresenter = appcmsPresenter;
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setBuiltInZoomControls(false);
        this.getSettings().setDisplayZoomControls(false);
        this.setBackgroundColor(Color.TRANSPARENT);
        this.getSettings().setAppCacheEnabled(true);
        this.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        this.getSettings().setLoadWithOverviewMode(true);
        this.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        this.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (MotionEventCompat.findPointerIndex(event, 0) == -1) {
            return super.onTouchEvent(event);
        }

        if (event.getPointerCount() >= 2) {
            requestDisallowInterceptTouchEvent(true);
        } else {
            requestDisallowInterceptTouchEvent(false);
        }
        return super.onTouchEvent(event);
    }


    public void loadURLData(Context mContext, AppCMSPresenter appCMSPresenter, String loadingURL, String cacheKey) {
        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                context.startActivity(browserIntent);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                appCMSPresenter.setWebViewCache(cacheKey, (CustomWebView) view);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                appCMSPresenter.clearWebViewCache();
            }
        });

        this.loadData(loadingURL, "text/html", "UTF-8");
    }

    public class checkURLAysyncTask extends AsyncTask<String, String, Integer> {

        private String loadwebUrl = "";
        private AppCMSPresenter appCMSPresenter;

        public checkURLAysyncTask(String loadingUrl, AppCMSPresenter appCMSPresenter) {
            this.loadwebUrl = loadingUrl;
            this.appCMSPresenter = appCMSPresenter;
        }

        @Override
        protected void onPreExecute() {
            appcmsPresenter.showLoadingDialog(true);

        }

        @Override
        protected Integer doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            int iHTTPStatus = 0;

            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpRequest = new HttpGet(arg0[0]);

                HttpResponse httpResponse = httpClient.execute(httpRequest);
                iHTTPStatus = httpResponse.getStatusLine().getStatusCode();
//                if (iHTTPStatus != 200) {
//                    // Serve a local page instead...
//                    CustomWebView.this.loadUrl(arg0[0]);
//                } else {
//                    Toast.makeText(context, "Status code " + iHTTPStatus, Toast.LENGTH_LONG).show();
//                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();

            }

            return iHTTPStatus;
        }

        @Override
        protected void onPostExecute(Integer httpStatusCode) {
            super.onPostExecute(httpStatusCode);
            if (httpStatusCode == 200) {
                loadUrlWithWebViewClient(appcmsPresenter, loadwebUrl);
            } else {
                Toast.makeText(context, "Error while loading page..", Toast.LENGTH_LONG).show();
                CustomWebView.this.loadUrl(loadwebUrl);
                appCMSPresenter.showLoadingDialog(false);
                context.sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION));

            }
        }
    }

    ;

    public void loadURL(Context mContext, AppCMSPresenter appCMSPresenter, String loadingURL, String cacheKey) {


        loadingURL = loadingURL.replace("http", "https");
//        this.loadUrl(loadingURL);
        new checkURLAysyncTask(loadingURL, appCMSPresenter).execute(loadingURL);
    }

    private void loadUrlWithWebViewClient(AppCMSPresenter appCMSPresenter, String loadingURL) {
        context.sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setLoadWithOverviewMode(true);
        appCMSPresenter.showLoadingDialog(true);

        this.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        this.getSettings().setBuiltInZoomControls(true);
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            this.getSettings().setDisplayZoomControls(false);
        }

        // this.getSettings().setDefaultFontSize(30);
        this.addJavascriptInterface(this, "MyApp");
        this.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                appCMSPresenter.clearWebViewCache();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                appCMSPresenter.showLoadingDialog(true);

                if (!loadingURL.equalsIgnoreCase(url.replace("https", "http"))) {
                    appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.OPEN_URL_IN_BROWSER,
                            () -> {
                                Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                                context.startActivity(browserIntent);
                            });
                } else {
                    Log.e("CustomWebView", "Redirected URL :" + url);
                    view.loadUrl(url);
                }

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                appCMSPresenter.showLoadingDialog(false);

                view.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");
                view.requestLayout();
                context.sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION));
            }

        });
        this.loadUrl(loadingURL);
    }

    public void showAlert(Context context, String url) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("Open Link");

        // set dialog message
        AlertDialog dialog = alertDialogBuilder
                .setMessage("Open Link outside?")
                .setCancelable(false)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                        context.startActivity(browserIntent);
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        dialog.show();

    }

    @JavascriptInterface
    public void resize(final float height) {
        context.runOnUiThread(() -> {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    getResources().getDisplayMetrics().widthPixels,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            params.bottomMargin = (int) (55 * (metrics.densityDpi / 160f));
            webView.setLayoutParams(params);
        });
    }
}