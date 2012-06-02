/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.happydroids.droidtowers.SplashSceneStates;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.gamestate.server.CloudGameSave;
import com.happydroids.droidtowers.gamestate.server.CloudGameSaveCollection;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.OnClickCallback;
import com.happydroids.server.ApiCollectionRunnable;
import com.happydroids.server.HappyDroidServiceCollection;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import static com.happydroids.droidtowers.SplashSceneStates.FULL_LOAD;

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

  void fetchAndLoadGameFromCloud(final NameValuePair nameValuePair) {
    final int gameId = Integer.parseInt(nameValuePair.getValue());
    new CloudGameSaveCollection()
            .filterBy("id", gameId)
            .fetch(new ApiCollectionRunnable<HappyDroidServiceCollection<CloudGameSave>>() {
              @Override
              public void onSuccess(HttpResponse response, HappyDroidServiceCollection<CloudGameSave> collection) {
                TowerGame.changeScene(SplashScene.class, FULL_LOAD, collection.getObjects().get(0).getGameSave());
              }

              @Override
              public void onError(HttpResponse response, int statusCode, HappyDroidServiceCollection<CloudGameSave> collection) {
                new Dialog()
                        .setTitle("Could not find game: " + gameId)
                        .setMessage("Not able to load game: " + gameId + "\n\nReason: " + response.getStatusLine())
                        .addButton("Dismiss", new OnClickCallback() {
                          @Override
                          public void onClick(Dialog dialog) {
                            dialog.dismiss();
                          }
                        })
                        .show();
              }
            });
  }
}
