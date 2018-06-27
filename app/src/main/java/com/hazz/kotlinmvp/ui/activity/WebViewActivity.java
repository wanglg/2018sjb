package com.hazz.kotlinmvp.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.hazz.kotlinmvp.R;
import com.orhanobut.logger.Logger;

/**
 * 内置浏览器
 * Created by liyingfeng on 2015/5/18.
 */
public class WebViewActivity extends AppCompatActivity {

    public static final String WEBVIEW_URL = "WEBVIEW_URL";
    private WebView webView;
    private ProgressBar progressBar;
    private String url;
    FrameLayout wrapWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        bindViews(savedInstanceState);
        init();
    }

    protected void bindViews(Bundle savedInstanceState) {
        wrapWebView = (FrameLayout) findViewById(R.id.web_view_wrap);
        webView = new WebView(getApplicationContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        wrapWebView.addView(webView, layoutParams);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    private void init() {
        webView.setVerticalScrollBarEnabled(false);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                progressBar.setVisibility(View.GONE);
            }
        });
        url = getIntent().getStringExtra(WEBVIEW_URL);
        Logger.d("load--doBindService" + url);
        if (url.contains("?")) {
            url = url + "&from=app";
        } else {
            url = url + "?from=app";
        }
        Logger.d("load-->" + url);
        webView.loadUrl(url);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
    }
}