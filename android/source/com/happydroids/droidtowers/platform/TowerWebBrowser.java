/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.platform;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

public class TowerWebBrowser extends Dialog {
  static final FrameLayout.LayoutParams FILL =
          new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                              ViewGroup.LayoutParams.FILL_PARENT);

  private WebView webView;
  private Activity activity;
  private ProgressDialog mSpinner;
  private FrameLayout container;
  private final String uriToLoad;

  public TowerWebBrowser(Context context, String uriToLoad) {
    super(context, android.R.style.Theme_Translucent_NoTitleBar);
    this.uriToLoad = uriToLoad;
    activity = (Activity) context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mSpinner = new ProgressDialog(getContext());
    mSpinner.requestWindowFeature(FEATURE_NO_TITLE);
    mSpinner.setCancelable(false);
    mSpinner.setMessage("Loading...");

    requestWindowFeature(FEATURE_NO_TITLE);

    buildWebView();

    container = new FrameLayout(getContext());
    LinearLayout webViewContainer = new LinearLayout(getContext());

    webViewContainer.addView(webView);
    webViewContainer.setPadding(20, 20, 20, 20);
    container.addView(webViewContainer);

    addContentView(container, FILL);
  }

  private void buildWebView() {
    webView = new WebView(getContext());
    webView.setLayoutParams(FILL);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setSavePassword(false);

    webView.setWebChromeClient(new WebChromeClient() {
      @Override
      public void onProgressChanged(WebView view, int newProgress) {
        activity.setProgress(newProgress);
      }
    });

    webView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
      }

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        mSpinner.show();
        mSpinner.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        mSpinner.dismiss();

        setTitle(view.getTitle());
      }
    });

    if (uriToLoad != null) {
      webView.loadUrl(uriToLoad);
    }
  }
}
