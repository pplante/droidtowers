/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.happydroids.droidtowers.SplashSceneStates;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.gamestate.server.CloudGameSave;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.server.ApiRunnable;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

public class LaunchGameUriAction {
  void checkAuthAndLoadGame(final NameValuePair gameId) {
    TowerGameService.instance().afterAuthentication(new Runnable() {
      public void run() {
        if (TowerGameService.instance().isAuthenticated()) {
          fetchAndLoadGameFromCloud(gameId);
        } else {
          forceAuthentication(gameId);
        }
      }
    });
  }

  void forceAuthentication(final NameValuePair gameId) {
    TowerGame.changeScene(SplashScene.class, SplashSceneStates.PRELOAD_ONLY, new Runnable() {
      public void run() {
        TowerGame.changeScene(HappyDroidConnect.class, new Runnable() {
          public void run() {
            checkAuthAndLoadGame(gameId);
          }
        });
      }
    });
  }

  void fetchAndLoadGameFromCloud(NameValuePair gameId) {
    CloudGameSave cloudSave = new CloudGameSave();
    cloudSave.findById(Integer.parseInt(gameId.getValue()), new ApiRunnable<CloudGameSave>() {
      @Override
      public void onSuccess(HttpResponse response, CloudGameSave object) {
        TowerGame.changeScene(SplashScene.class, SplashSceneStates.FULL_LOAD, object.getGameSave());
      }
    });
  }
}
