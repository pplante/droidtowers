/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.gamestate.server.TemporaryToken;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.utils.PeriodicBackgroundTask;
import com.happydroids.server.ApiRunnable;
import com.happydroids.server.HappyDroidServiceObject;
import org.apache.http.HttpResponse;

public class ConnectToHappyDroidsWindow extends TowerWindow {
  private static final String TAG = ConnectToHappyDroidsWindow.class.getSimpleName();
  private TemporaryToken token;
  private PeriodicBackgroundTask periodicBackgroundTask;
  private final TextButton accessTokenButton;
  private Runnable postConnectRunnable;

  public ConnectToHappyDroidsWindow(Stage stage) {
    super("Connect to Happy Droids", stage);

    clear();
    row().pad(10);
    add(FontManager.Roboto24.makeLabel("Connecting your Facebook account to Happy Droids will enable:\n\n* Towers to be stored in the cloud\n* Sharing towers with friends\n* Other stuff!")).expandX();
    row().pad(10);
    add(FontManager.Roboto24.makeLabel("To get started, goto happydroids.com\n then click the \"Connect to Facebook\" button."));
    row().pad(10);
    add(FontManager.Roboto24.makeLabel("After logging in, type the code below to connect your game."));
    row().expand();

    accessTokenButton = FontManager.RobotoBold18.makeTextButton("CODE: Reticulating splines...");
    accessTokenButton.visible = false;

    final Label sessionStatus = FontManager.Roboto24.makeLabel("Waiting for You to login...");

    final Table bottom = new Table();
    bottom.defaults();
    bottom.row().space(20);
    bottom.add(accessTokenButton);
    bottom.add(sessionStatus).expand();
    add(bottom).fill();

    token = new TemporaryToken();
    token.save(new TemporaryTokenApiRunnable(sessionStatus, bottom));
  }

  public void setPostConnectRunnable(Runnable postConnectRunnable) {
    this.postConnectRunnable = postConnectRunnable;
  }

  private class TemporaryTokenApiRunnable extends ApiRunnable {
    private final Label sessionStatus;
    private final Table bottom;

    public TemporaryTokenApiRunnable(Label sessionStatus, Table bottom) {
      this.sessionStatus = sessionStatus;
      this.bottom = bottom;
    }

    @Override
    public void onError(HttpResponse response, int statusCode, HappyDroidServiceObject object) {
      sessionStatus.setText(String.format("Login failed: %d!", statusCode));
      bottom.pack();

      new Dialog(TowerGame.getRootUiStage())
              .setTitle("Connection Error")
              .setMessage("Could not contact happydroids.com, please check that you have internet access and try again.\n\nERROR:ETFAIL2FONHOME")
              .addButton("Dismiss", new OnClickCallback() {
                @Override
                public void onClick(Dialog dialog) {
                  dialog.dismiss();
                  ConnectToHappyDroidsWindow.this.dismiss();
                }
              }).show();
    }

    @Override
    public void onSuccess(HttpResponse response, HappyDroidServiceObject object) {
      accessTokenButton.setText("CODE: " + token.getValue());
      accessTokenButton.visible = true;
      sessionStatus.visible = true;

      accessTokenButton.setClickListener(new ClickListener() {
        public void click(Actor actor, float x, float y) {
          String uri = token.getClickableUri();

          TowerGame.getPlatformBrowserUtil().launchWebBrowser(uri);
        }
      });

      TowerGame.getPlatformBrowserUtil().launchWebBrowser(token.getClickableUri());

      periodicBackgroundTask = new AccessTokenCheckStateTask();
      periodicBackgroundTask.run();

      setDismissCallback(new Runnable() {
        public void run() {
          periodicBackgroundTask.cancel();
        }
      });
    }

    private class AccessTokenCheckStateTask extends PeriodicBackgroundTask {
      public AccessTokenCheckStateTask() {
        super(TowerConsts.FACEBOOK_CONNECT_DELAY_BETWEEN_TOKEN_CHECK);
      }

      @Override
      public boolean update() {
        if (token == null) return false;
        try {
          token.validate();
          Gdx.app.debug(TAG, "Checking token: " + token);
          return !token.hasSessionToken();
        } catch (RuntimeException e) {
          Gdx.app.error(TAG, "Error validating the temporary token.", e);
        }

        return false;
      }

      @Override
      public synchronized void beforeExecute() {
        TowerGameService.instance().resetAuthentication();
      }

      @Override
      public synchronized void afterExecute() {
        if (token != null && token.hasSessionToken()) {
          sessionStatus.setText("Login successful!");
          TowerGameService.instance().setSessionToken(token.getSessionToken());
        } else {
          sessionStatus.setText("Login failed!");
        }

        if (postConnectRunnable != null) {
          postConnectRunnable.run();
        }
      }
    }
  }
}
