/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.scenes.SplashScene;

import static com.happydroids.HappyDroidConsts.DEBUG;

public class DebugUtils {
  public static void loadFirstGameFound(VarArgRunnable loadGameRunnable) {
    verifyEnvironment();

    try {
      FileHandle storage = Gdx.files.external(TowerConsts.GAME_SAVE_DIRECTORY);
      FileHandle[] files = storage.list(".json");
      if (files.length > 0) {
        while (!TowerAssetManager.assetManager().update()) {
          Thread.yield();
        }

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
    TowerGame.changeScene(SplashScene.class, SplashSceneStates.FULL_LOAD, gameSave);
  }

  private static void verifyEnvironment() {
    if (DEBUG) {
      return;
    }

    throw new RuntimeException("CANNOT BE USED IN PRODUCTION.");
  }
}
