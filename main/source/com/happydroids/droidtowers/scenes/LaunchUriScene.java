/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.badlogic.gdx.Gdx;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import org.apach3.http.NameValuePair;
import org.apach3.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.util.List;

public class LaunchUriScene extends SplashScene {
  private static final String TAG = LaunchUriScene.class.getSimpleName();

  @Override
  public void create(Object... args) {
    super.create(args);

    if (args == null) {
      throw new RuntimeException("args cannot be null!");
    }

    URI launchUri = (URI) args[0];
    List<NameValuePair> queryParams = URLEncodedUtils.parse(launchUri, "utf8");
    String launchUriHost = launchUri.getHost();

    Gdx.app.debug(TAG, "Launch task: " + launchUriHost);
    final NameValuePair sessionToken = getParam(queryParams, "session");
    if (sessionToken != null && !TowerGameService.instance().isAuthenticated()) {
      Gdx.app.debug(TAG, "Launch session: " + sessionToken.getValue());
      TowerGameService.instance().setSessionToken(sessionToken.getValue());
    }

    if (launchUriHost.equals("launchgame")) {
      final NameValuePair gameId = getParam(queryParams, "id");
      if (gameId != null) {
        new LaunchGameUriAction().checkAuthAndLoadGame(gameId);
      }
    } else if (launchUriHost.equals("register")) {
      final NameValuePair serial = getParam(queryParams, "serial");
      if (serial != null) {
        SceneManager.popScene();
        SceneManager.pushScene(VerifyPurchaseScene.class, serial.getValue());
      }
    } else {
      SceneManager.changeScene(MainMenuScene.class);
    }

  }

  private NameValuePair getParam(List<NameValuePair> queryParams, String keyName) {
    for (NameValuePair queryParam : queryParams) {
      if (queryParam.getName().equalsIgnoreCase(keyName)) {
        return queryParam;
      }
    }

    return null;
  }
}
