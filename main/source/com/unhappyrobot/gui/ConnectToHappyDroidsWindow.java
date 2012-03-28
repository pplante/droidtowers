package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.gamestate.server.HappyDroidService;
import com.unhappyrobot.gamestate.server.TemporaryToken;
import com.unhappyrobot.utils.PeriodicBackgroundTask;

public class ConnectToHappyDroidsWindow extends TowerWindow {
  private static final String TAG = ConnectToHappyDroidsWindow.class.getSimpleName();

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
    final TextButton codeLabel = new TextButton("CODE: Reticulating splines...", skin);
    add(codeLabel);

    final Label sessionStatus = LabelStyle.Default.makeLabel("Waiting for You to login...");
    sessionStatus.visible = false;
    add(sessionStatus);

    if (HappyDroidService.instance().getSessionToken() == null) {
      new PeriodicBackgroundTask(TowerConsts.FACEBOOK_CONNECT_DELAY_BETWEEN_TOKEN_CHECK) {
        private TemporaryToken token;

        @Override
        public synchronized void beforeExecute() {
          token = new TemporaryToken();
          token.save();
          if (!token.isSaved()) {
            new Dialog().setMessage("Could not contact happydroids.com, please check that you have internet access and try again.").addButton("Dismiss", new OnClickCallback() {
              @Override
              public void onClick(Dialog dialog) {
                dialog.dismiss();
                ConnectToHappyDroidsWindow.this.dismiss();
              }
            });

            cancel();
          }
          codeLabel.setText("CODE: " + token.getValue());
          sessionStatus.visible = true;

          codeLabel.setClickListener(new ClickListener() {
            public void click(Actor actor, float x, float y) {
              String uri = token.getClickableUri();

              TowerGame.getPlatformBrowserUtil().launchWebBrowser(uri);
            }
          });
        }

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
      }.run();
    }
  }
}
