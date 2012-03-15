package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.gamestate.server.HappyDroidService;
import com.unhappyrobot.gamestate.server.TemporaryToken;
import com.unhappyrobot.utils.PeriodicAsyncTask;

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

    if (HappyDroidService.instance().getSessionToken() == null) {
      new PeriodicAsyncTask(TowerConsts.FACEBOOK_CONNECT_DELAY_BETWEEN_TOKEN_CHECK) {
        private TemporaryToken token;

        @Override
        public synchronized void beforeExecute() {
          token = TemporaryToken.create();
          codeLabel.setText("CODE: " + token.getValue());
          sessionStatus.visible = true;
        }

        @Override
        public boolean update() {
          if (token == null) return false;

          token.validate();
          System.out.println("token = " + token);
          return !token.hasSessionToken();
        }

        @Override
        public synchronized void afterExecute() {
          if (token != null && token.hasSessionToken()) {
            sessionStatus.setText("Login successful!");
          } else {
            sessionStatus.setText("Login failed!");
          }
        }
      }.run();
    }
  }

  private Label makeLabel(String text) {
    return new Label(text, skin);
  }
}
