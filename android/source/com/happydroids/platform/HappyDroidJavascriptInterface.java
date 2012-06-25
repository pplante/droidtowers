/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import android.content.Context;
import com.badlogic.gdx.Gdx;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;

public class HappyDroidJavascriptInterface {
  private final Context context;
  private final TowerWebBrowser towerWebBrowser;

  public HappyDroidJavascriptInterface(Context context, TowerWebBrowser towerWebBrowser) {
    this.context = context;
    this.towerWebBrowser = towerWebBrowser;
  }

  public void storeSessionToken(final String sessionToken) {
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        TowerGameService.instance().setSessionToken(sessionToken);

        if (TowerGameService.instance().isAuthenticated()) {
          towerWebBrowser.dismiss();
        }
      }
    });
  }
}
