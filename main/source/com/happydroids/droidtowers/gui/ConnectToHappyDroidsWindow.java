/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.happydroids.droidtowers.DroidTowersGame;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.gamestate.server.TemporaryToken;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.scenes.MainMenuScene;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.utils.PeriodicBackgroundTask;
import com.happydroids.platform.LaunchBrowserAfterDelay;
import com.happydroids.server.ApiRunnable;
import com.happydroids.server.HappyDroidServiceObject;
import org.apach3.http.HttpResponse;

public class ConnectToHappyDroidsWindow extends TowerWindow {
  private static final String TAG = ConnectToHappyDroidsWindow.class.getSimpleName();
  private TemporaryToken token;
  private PeriodicBackgroundTask periodicBackgroundTask;
  private final TextButton openBrowserButton;
  private Runnable postConnectRunnable;
  private final Label sessionStatus;

  public ConnectToHappyDroidsWindow(Stage stage) {
    super("Connect to Happy Droids", stage);

    openBrowserButton = FontManager.RobotoBold18.makeTextButton("Open my web browser");
    openBrowserButton.setVisible(false);

    sessionStatus = FontManager.Roboto24.makeLabel("Waiting for You to login...");

    clear();
    defaults().center();
    add(FontManager.Roboto24.makeLabel("Connect to happydroids.com"));

    row();
    add(sessionStatus);

    row();
    add(openBrowserButton);

    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        token = new TemporaryToken();
        token.save(new TemporaryTokenApiRunnable());
      }
    });
  }

  public void setPostConnectRunnable(Runnable postConnectRunnable) {
    this.postConnectRunnable = postConnectRunnable;
  }

  private class TemporaryTokenApiRunnable extends ApiRunnable {
    @Override
    public void onError(HttpResponse response, int statusCode, HappyDroidServiceObject object) {
      sessionStatus.setText(String.format("Login failed: %d!", statusCode));

      new Dialog(DroidTowersGame.getRootUiStage())
              .setTitle("Connection Error")
              .setMessage("Could not contact happydroids.com, please your internet connection and try again.\n\nERROR:ETFAIL2FONHOME")
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
      openBrowserButton.setText("CODE: " + token.getValue());
      openBrowserButton.setVisible(true);
      sessionStatus.setVisible(true);

      openBrowserButton.addListener(new VibrateClickListener() {
        @Override
        public void onClick(InputEvent event, float x, float y) {
          Gdx.app.postRunnable(new LaunchBrowserAfterDelay(token.getClickableUri(), 1.5f));
        }
      });

      InputEvent event = new InputEvent();
      event.setType(InputEvent.Type.touchDown);
      openBrowserButton.fire(event);

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
        if (token == null) {
          return false;
        }
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
          TowerGameService.instance().setSessionToken(token.getSessionToken());

          new Dialog()
                  .setTitle("Connect to happydroids.com")
                  .setMessage("Congratulations, you are now connected to happydroids.com.\n\nYou can now share your Towers with friends\nby clicking the view neighbors button in game.")
                  .addButton("Dismiss", new OnClickCallback() {
                    @Override
                    public void onClick(Dialog dialog) {
                      dialog.dismiss();
                      dismiss();

                      if (SceneManager.activeScene() instanceof MainMenuScene) {
                        SceneManager.restartActiveScene();
                      }
                    }
                  })
                  .show();
        } else {
          new Dialog()
                  .setTitle("Connect to happydroids.com")
                  .setMessage("Sorry, but we were unable to connect to happydroids.com.\n\nPlease try again later.")
                  .addButton("Dismiss", new OnClickCallback() {
                    @Override
                    public void onClick(Dialog dialog) {
                      dialog.dismiss();
                      dismiss();
                    }
                  })
                  .show();
        }

        if (postConnectRunnable != null) {
          postConnectRunnable.run();
        }
      }
    }
  }
}
