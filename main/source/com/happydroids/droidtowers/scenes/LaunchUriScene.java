/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.happydroids.droidtowers.gui.FontManager;
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

    Label fetchingLabel = FontManager.Roboto64.makeLabel("fetching game :D");
    getStage().addActor(fetchingLabel);

    URI launchUri = (URI) args[0];
    List<NameValuePair> queryParams = URLEncodedUtils.parse(launchUri, "utf8");
    if (launchUri.getHost().equals("launchgame")) {
      final NameValuePair gameId = getParam(queryParams, "id");
      if (gameId != null) {
        new LaunchGameUriAction().checkAuthAndLoadGame(gameId);
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
