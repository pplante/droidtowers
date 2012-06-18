/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.platform;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import com.happydroids.droidtowers.R;

public class TowerWebBrowser extends AlertDialog {
  private WebView webView;
  private String urlToLoad;
  private Activity activity;

  public TowerWebBrowser(Context context) {
    super(context);
    activity = (Activity) context;

    getWindow().requestFeature(Window.FEATURE_PROGRESS);
    setContentView(R.layout.web_browser);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    webView = (WebView) findViewById(R.id.web_view);
    webView.getSettings().setJavaScriptEnabled(true);

    webView.setWebChromeClient(new WebChromeClient() {
      @Override
      public void onProgressChanged(WebView view, int newProgress) {
        activity.setProgress(newProgress);
      }
    });

    if (urlToLoad != null) {
      webView.loadUrl(urlToLoad);
    }
  }

  public Dialog openUrl(String uri) {
    if (webView == null) {
      urlToLoad = uri;
    } else {
      webView.loadUrl(uri);
    }
    return this;
  }
}
