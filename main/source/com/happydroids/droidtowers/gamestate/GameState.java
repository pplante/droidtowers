/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.happydroids.droidtowers.gamestate.server.CloudGameSave;
import com.happydroids.droidtowers.graphics.TowerMiniMap;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.OnClickCallback;
import com.happydroids.droidtowers.input.CameraController;
import com.happydroids.droidtowers.scenes.MainMenuScene;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.utils.PNG;
import com.happydroids.server.ApiRunnable;
import com.happydroids.server.HappyDroidServiceObject;
import com.happydroids.utils.BackgroundTask;
import org.apach3.http.HttpResponse;

import java.io.OutputStream;

public class GameState {
  private static final String TAG = GameState.class.getSimpleName();

  private final OrthographicCamera camera;
  private final CameraController cameraController;
  private final GameGrid gameGrid;
  private final FileHandle gameSaveLocation;
  private final GameSave currentGameSave;
  private final FileHandle gameFile;
  private boolean shouldSaveGame;
  private FileHandle pngFile;
  private int fileGeneration;
  private CloudGameSave cloudGameSave;

  public GameState(OrthographicCamera camera, CameraController cameraController, FileHandle gameSaveLocation, GameSave currentGameSave, final GameGrid gameGrid) {
    this.camera = camera;
    this.cameraController = cameraController;
    this.gameGrid = gameGrid;
    this.gameSaveLocation = gameSaveLocation;
    this.currentGameSave = currentGameSave;
    gameFile = gameSaveLocation.child(currentGameSave.getBaseFilename());
    pngFile = gameSaveLocation.child(currentGameSave.getBaseFilename() + ".png");
    cloudGameSave = new CloudGameSave(currentGameSave, pngFile);
  }

  public void loadSavedGame() {
    shouldSaveGame = true;
    if (currentGameSave == null) {
      throw new RuntimeException("Unknown GameSave to load.");
    }

    try {
      Gdx.app.debug(TAG, "Loading: " + currentGameSave.getBaseFilename());
      currentGameSave.attachToGame(gameGrid, camera, cameraController);
    } catch (Exception e) {
      shouldSaveGame = false;

      Gdx.app.log("GameSave", "Could not load saved game!", e);
      new Dialog().setMessage("Saved game could not be loaded, want to reset?").addButton("Yes", new OnClickCallback() {
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
      }).addButton("No, exit game", new OnClickCallback() {
        @Override
        public void onClick(Dialog dialog) {
          dialog.dismiss();
          SceneManager.changeScene(MainMenuScene.class);
        }
      }).show();
    } finally {
      Gdx.app.debug(TAG, "loadGameSave - finished");
    }
  }

  public void saveGame(final boolean shouldForceCloudSave) {
    if (shouldSaveGame && !currentGameSave.isSaveToDiskDisabled()) {
      if (!gameGrid.isEmpty()) {
        currentGameSave.update(camera, gameGrid, cloudGameSave.getNeighbors());

        new BackgroundTask() {
          @Override
          protected void execute() throws Exception {
            try {
              if (!gameSaveLocation.exists()) {
                gameSaveLocation.mkdirs();
              }
              OutputStream stream = pngFile.write(false);
              stream.write(PNG.toPNG(TowerMiniMap.redrawMiniMap(gameGrid, true, 2f)));
              stream.flush();
              stream.close();

              if (shouldForceCloudSave || currentGameSave.getCloudSaveUri() == null || currentGameSave.getFileGeneration() % 4 == 0) {
                cloudGameSave.updateImage(pngFile);
                cloudGameSave.save(new ApiRunnable() {
                  @Override
                  public void onSuccess(HttpResponse response, HappyDroidServiceObject object) {
                    if (cloudGameSave.isSaved()) {
                      currentGameSave.setCloudSaveUri(cloudGameSave.getResourceUri());
                    }
                  }
                });
              }

              GameSaveFactory.save(currentGameSave, gameFile);
            } catch (Exception e) {
              Gdx.app.log("GameSave", "Could not save game!", e);
            }
          }
        }.run();
      }
    }
  }

  public CloudGameSave getCloudGameSave() {
    return cloudGameSave;
  }
}
