/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.happydroids.droidtowers.SplashSceneStates;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.gamestate.server.CloudGameSave;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.util.List;

public class LaunchUriScene extends Scene {
  @Override
  public void create(Object... args) {
    if (args == null) {
      throw new RuntimeException("args cannot be null!");
    }

    URI launchUri = (URI) args[0];
    List<NameValuePair> queryParams = URLEncodedUtils.parse(launchUri, "utf8");
    if (launchUri.getHost().equals("launchgame")) {
      NameValuePair gameId = getParam(queryParams, "id");
      if (gameId != null) {
        CloudGameSave save = new CloudGameSave();
        save.findById(Integer.parseInt(gameId.getValue()));

        TowerGame.changeScene(SplashScene.class, SplashSceneStates.FULL_LOAD, save.getGameSave());
      }
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

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void render(float deltaTime) {
  }

  @Override
  public void dispose() {
  }
}
