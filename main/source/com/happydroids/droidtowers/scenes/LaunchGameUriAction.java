/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.happydroids.droidtowers.gamestate.server.CloudGameSave;
import com.happydroids.droidtowers.gamestate.server.CloudGameSaveCollection;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.OnClickCallback;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.server.ApiCollectionRunnable;
import com.happydroids.server.HappyDroidServiceCollection;
import org.apach3.http.HttpResponse;
import org.apach3.http.NameValuePair;


public class LaunchGameUriAction {
  void checkAuthAndLoadGame(final NameValuePair gameId) {
    TowerGameService.instance().afterDeviceIdentification(new Runnable() {
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
    SceneManager.changeScene(PreloadAsssetsSplashScene.class, new Runnable() {
      public void run() {
        SceneManager.changeScene(HappyDroidConnect.class, new Runnable() {
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
                SceneManager.changeScene(LoadTowerSplashScene.class, collection.getObjects().get(0).getGameSave());
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
