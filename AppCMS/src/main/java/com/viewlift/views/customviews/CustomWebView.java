package com.viewlift.views.customviews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.viewlift.presenters.AppCMSPresenter;

/**
 * Created by karan.kaushik on 11/22/2017.
 */

public class CustomWebView extends WebView {

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
                super.shouldOverrideUrlLoading(view, url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");
                view.requestLayout();
                //appCMSPresenter.setWebViewCache(cacheKey, (CustomWebView) view);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                //appCMSPresenter.clearWebViewCache();
            }
        });

        this.loadData(loadingURL, "text/html", "UTF-8");
    }

    public void loadURL(Context mContext, AppCMSPresenter appCMSPresenter, String loadingURL, String cacheKey) {
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setDefaultFontSize(30);
        this.addJavascriptInterface(this, "MyApp");
        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                super.shouldOverrideUrlLoading(view, url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");
                view.requestLayout();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                //appCMSPresenter.clearWebViewCache();
            }

        });

        this.loadUrl(loadingURL);
    }

    @JavascriptInterface
    public void resize(final float height) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.setLayoutParams(new FrameLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, (int) (height * getResources().getDisplayMetrics().density)));
            }
        });
    }
}