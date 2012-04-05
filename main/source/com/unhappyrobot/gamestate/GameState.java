/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.happydroids.utils.BackgroundTask;
import com.unhappyrobot.gamestate.server.CloudGameSave;
import com.unhappyrobot.graphics.TowerMiniMap;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.gui.Dialog;
import com.unhappyrobot.gui.OnClickCallback;
import com.unhappyrobot.gui.ResponseType;
import com.unhappyrobot.utils.PNG;

import java.io.OutputStream;

public class GameState {
  private static final String TAG = GameState.class.getSimpleName();

  private final OrthographicCamera camera;
  private final GameGrid gameGrid;
  private final FileHandle gameSaveLocation;
  private final GameSave currentGameSave;
  private final FileHandle gameFile;
  private boolean shouldSaveGame;
  private FileHandle pngFile;
  private int fileGeneration;

  public GameState(OrthographicCamera camera, FileHandle gameSaveLocation, GameSave currentGameSave, final GameGrid gameGrid) {
    this.camera = camera;
    this.gameGrid = gameGrid;
    this.gameSaveLocation = gameSaveLocation;
    this.currentGameSave = currentGameSave;
    this.gameFile = gameSaveLocation.child(currentGameSave.getBaseFilename());
    this.pngFile = gameSaveLocation.child(currentGameSave.getBaseFilename() + ".png");
  }

  public void loadSavedGame() {
    shouldSaveGame = true;
    if (currentGameSave == null) {
      throw new RuntimeException("Unknown GameSave to load.");
    }

    try {
      Gdx.app.debug(TAG, "Loading: " + currentGameSave.getBaseFilename());
      currentGameSave.attachToGame(gameGrid, camera);

    } catch (Exception e) {
      shouldSaveGame = false;

      Gdx.app.log("GameSave", "Could not load saved game!", e);
      new Dialog().setMessage("Saved game could not be loaded, want to reset?").addButton(ResponseType.POSITIVE, "Yes", new OnClickCallback() {
        @Override
        public void onClick(Dialog dialog) {
          if (gameFile.exists()) {
            gameFile.delete();
          }
          if (pngFile.exists()) {
            pngFile.delete();
          }
          dialog.dismiss();
          shouldSaveGame = true;
        }
      }).addButton(ResponseType.NEGATIVE, "No, exit game", new OnClickCallback() {
        @Override
        public void onClick(Dialog dialog) {
          dialog.dismiss();
          Gdx.app.exit();
        }
      }).show();
    } finally {
      Gdx.app.debug(TAG, "loadGameSave - finished");
    }
  }

  public void saveGame(final boolean shouldForceCloudSave) {
    if (shouldSaveGame) {
      if (!gameGrid.isEmpty()) {
        currentGameSave.update();

        new BackgroundTask() {
          @Override
          public synchronized void afterExecute() {
            Gdx.app.debug(TAG, "After save.");
          }

          @Override
          public synchronized void beforeExecute() {
            Gdx.app.debug(TAG, "Before save.");
            if (!gameSaveLocation.exists()) {
              gameSaveLocation.mkdirs();
            }

          }

          @Override
          public void execute() {
            try {
              OutputStream stream = pngFile.write(false);
              stream.write(PNG.toPNG(TowerMiniMap.redrawMiniMap(gameGrid, true, 2f)));
              stream.flush();
              stream.close();

              if (shouldForceCloudSave || currentGameSave.getCloudSaveUri() == null || currentGameSave.getFileGeneration() % 4 == 0) {
                CloudGameSave cloudGameSave = new CloudGameSave(currentGameSave, pngFile);
                cloudGameSave.save();
                if (cloudGameSave.isSaved()) {
                  currentGameSave.setCloudSaveUri(cloudGameSave.getResourceUri());
                }
              }

              currentGameSave.save(gameFile);
            } catch (Exception e) {
              Gdx.app.log("GameSave", "Could not save game!", e);
            }
          }
        }.run();
      }
    }
  }
}
