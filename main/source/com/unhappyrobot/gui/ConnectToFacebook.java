package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.gamestate.server.TemporaryToken;

public class ConnectToFacebook extends TowerWindow {
  public ConnectToFacebook() {
    super("Connect to Facebook");

    row().pad(10);
    add(makeLabel("Connecting to Facebook will enable:\n\n* Towers to be stored in the cloud\n* Sharing towers with friends\n* Other stuff!"));
    row().pad(10);
    add(makeLabel("To get started, goto happydroids.com\n then click the \"Connect to Facebook\" button."));
    row().pad(10);
    add(makeLabel("After logging in, type the code below to connect your game."));
    row().pad(10);
    final Label codeLabel = makeLabel("CODE: Reticulating splines...");
    add(codeLabel);

    final Label sessionStatus = makeLabel("Waiting for You to login...");
    sessionStatus.visible = false;
    add(sessionStatus);

    final Preferences connectPrefs = Gdx.app.getPreferences("CONNECT");
    if (!connectPrefs.contains("SESSION_TOKEN")) {
      new Thread() {
        @Override
        public void run() {
          TemporaryToken token = TemporaryToken.create();
          codeLabel.setText("CODE: " + token.getValue());
          sessionStatus.visible = true;

          long timeSinceCheck = System.currentTimeMillis();
          do {
            try {
              Thread.sleep(TowerConsts.FACEBOOK_CONNECT_DELAY_BETWEEN_TOKEN_CHECK);
              token.validate();
            } catch (InterruptedException ignored) {

            }
          } while (!token.hasSessionToken());

          sessionStatus.setText("Login successful!");

          connectPrefs.putString("SESSION_TOKEN", token.getSessionToken());
          connectPrefs.flush();
        }
      }.start();
    }
  }

  private Label makeLabel(String text) {
    return new Label(text, skin);
  }
}
