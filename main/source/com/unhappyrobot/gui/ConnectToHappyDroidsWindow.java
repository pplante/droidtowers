/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.gamestate.server.ApiRunnable;
import com.unhappyrobot.gamestate.server.HappyDroidService;
import com.unhappyrobot.gamestate.server.HappyDroidServiceObject;
import com.unhappyrobot.gamestate.server.TemporaryToken;
import com.unhappyrobot.utils.PeriodicBackgroundTask;
import org.apache.http.HttpResponse;

public class ConnectToHappyDroidsWindow extends TowerWindow {
  private static final String TAG = ConnectToHappyDroidsWindow.class.getSimpleName();
  private TemporaryToken token;
  private PeriodicBackgroundTask periodicBackgroundTask;
  private final TextButton accessTokenButton;

  public ConnectToHappyDroidsWindow(Stage stage, Skin skin) {
    super("Connect to Facebook", stage, skin);
    defaults().top().left().pad(5);

    row().pad(10);
    add(LabelStyle.Default.makeLabel("Connecting to Facebook will enable:\n\n* Towers to be stored in the cloud\n* Sharing towers with friends\n* Other stuff!"));
    row().pad(10);
    add(LabelStyle.Default.makeLabel("To get started, goto happydroids.com\n then click the \"Connect to Facebook\" button."));
    row().pad(10);
    add(LabelStyle.Default.makeLabel("After logging in, type the code below to connect your game."));
    row().pad(10);

    accessTokenButton = new TextButton("CODE: Reticulating splines...", skin);
    TextButton close = new TextButton("close", skin);
    close.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        dismiss();
      }
    });

    final Label sessionStatus = LabelStyle.Default.makeLabel("Waiting for You to login...");

    final Table bottom = new Table();
    bottom.defaults().top().left().expand();
    bottom.add(accessTokenButton);
    bottom.add(sessionStatus).fill().center();
    bottom.add(close).right();
    add(bottom).width(500);

    token = new TemporaryToken();
    token.save(new ApiRunnable() {
      @Override
      public void onError(HttpResponse response, int statusCode, HappyDroidServiceObject object) {
        sessionStatus.setText("Login failed!");
        bottom.pack();

        new Dialog(TowerGame.getRootUiStage()).setMessage("Could not contact happydroids.com, please check that you have internet access and try again.\n\nERROR:ETFAIL2FONHOME").addButton("Dismiss", new OnClickCallback() {
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
        sessionStatus.visible = true;

        accessTokenButton.setClickListener(new ClickListener() {
          public void click(Actor actor, float x, float y) {
            String uri = token.getClickableUri();

            TowerGame.getPlatformBrowserUtil().launchWebBrowser(uri);
          }
        });

        periodicBackgroundTask = new PeriodicBackgroundTask(TowerConsts.FACEBOOK_CONNECT_DELAY_BETWEEN_TOKEN_CHECK) {
          @Override
          public boolean update() {
            if (token == null) return false;
            try {
              token.validate();
              System.out.println("token = " + token);
              return !token.hasSessionToken();
            } catch (RuntimeException e) {
              Gdx.app.error(TAG, "Error validating the temporary token.", e);
            }

            return false;
          }

          @Override
          public synchronized void afterExecute() {
            if (token != null && token.hasSessionToken()) {
              sessionStatus.setText("Login successful!");
              HappyDroidService.instance().setSessionToken(token.getSessionToken());
            } else {
              sessionStatus.setText("Login failed!");
            }
          }
        };
        periodicBackgroundTask.run();

        setDismissCallback(new Runnable() {
          public void run() {
            periodicBackgroundTask.cancel();
          }
        });
      }
    });
  }
}
