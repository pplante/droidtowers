/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.gamestate.GameSaveFactory;
import com.happydroids.droidtowers.gamestate.server.CloudGameSave;
import com.happydroids.droidtowers.gamestate.server.CloudGameSaveCollection;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.OnClickCallback;
import com.happydroids.droidtowers.scenes.SplashScene;
import com.happydroids.server.ApiCollectionRunnable;
import com.happydroids.server.HappyDroidServiceCollection;
import org.apache.http.HttpResponse;

import static com.happydroids.HappyDroidConsts.DEBUG;
import static com.happydroids.droidtowers.SplashSceneStates.FULL_LOAD;

public class DebugUtils {
  public static void loadFirstGameFound(VarArgRunnable loadGameRunnable) {
    verifyEnvironment();

    try {
      FileHandle storage = Gdx.files.external(TowerConsts.GAME_SAVE_DIRECTORY);
      FileHandle[] files = storage.list(".json");
      if (files.length > 0) {
        for (FileHandle file : files) {
          if (!file.path().endsWith("png")) {
            loadGameRunnable.run(file);
            break;
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void createNonSavableGame(boolean newGame) {
    verifyEnvironment();

    GameSave gameSave = new GameSave("DO NOT SAVE!", DifficultyLevel.EASY);
    gameSave.setNewGame(newGame);
    gameSave.disableSaving();
    TowerGame.changeScene(SplashScene.class, FULL_LOAD, gameSave);
  }

  private static void verifyEnvironment() {
    if (DEBUG) {
      return;
    }

    throw new RuntimeException("CANNOT BE USED IN PRODUCTION.");
  }

  public static void loadFirstGameFound() {
    loadFirstGameFound(new VarArgRunnable() {
      public void run(Object... args) {
        try {
          TowerGame.changeScene(SplashScene.class, FULL_LOAD, GameSaveFactory.readFile((FileHandle) args[0]));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public static void loadGameFromCloud(final int gameId) {
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
