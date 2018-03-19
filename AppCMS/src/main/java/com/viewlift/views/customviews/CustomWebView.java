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

import com.viewlift.presenters.AppCMSPresenter;

/**
 * Created by karan.kaushik on 11/22/2017.
 */

public class CustomWebView extends AppCMSAdvancedWebView {

    private Activity context;
    private WebView webView;
    public CustomWebView(Context context) {
        super(context);
        this.context = (Activity) context;
        webView = this;
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

    public void loadURL(Context mContext, AppCMSPresenter appCMSPresenter, String loadingURL, String cacheKey) {
        context.sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setLoadWithOverviewMode(true);

        this.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        this.getSettings().setBuiltInZoomControls(true);
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            this.getSettings().setDisplayZoomControls(false);
        }

       // this.getSettings().setDefaultFontSize(30);
        this.addJavascriptInterface(this, "MyApp");
        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                //if(!loadingURL.equalsIgnoreCase(url.replace("https","http"))) {
                    appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.OPEN_URL_IN_BROWSER,
                    () -> {
                       Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                       context.startActivity(browserIntent);
                    });
                /*}else {
                    Log.e("CustomWebView","Redirected URL :"+url);
                    view.loadUrl(url);
                }*/
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");
                view.requestLayout();
                context.sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION));
            }

        });

        loadingURL = loadingURL.replace("http","https");
        this.loadUrl(loadingURL);
        Log.e("CustomWebView","URL :"+loadingURL);
    }

    public void showAlert(Context context, String url) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle( "Open Link");

        // set dialog message
        AlertDialog dialog =alertDialogBuilder
                .setMessage( "Open Link outside?" )
                .setCancelable(false)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("YES",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
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