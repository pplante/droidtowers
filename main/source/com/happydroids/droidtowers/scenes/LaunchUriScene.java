/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.util.List;

public class LaunchUriScene extends DroidSplashScene {

  @Override
  public void create(Object... args) {
    if (args == null) {
      throw new RuntimeException("args cannot be null!");
    }

    setStatusText("fetching tower :D");

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
  protected void handleBackButton() {
  }
}
