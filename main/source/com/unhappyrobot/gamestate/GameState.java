package com.unhappyrobot.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.unhappyrobot.achievements.AchievementEngine;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.gamestate.server.CloudGameSave;
import com.unhappyrobot.graphics.TowerMiniMap;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.grid.GridObjectState;
import com.unhappyrobot.gui.Dialog;
import com.unhappyrobot.gui.OnClickCallback;
import com.unhappyrobot.gui.ResponseType;
import com.unhappyrobot.input.CameraController;
import com.unhappyrobot.utils.PNG;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.OutputStream;

public class GameState {
  private static final String TAG = GameState.class.getSimpleName();

  private final OrthographicCamera camera;
  private final GameGrid gameGrid;
  private final FileHandle gameSaveLocation;
  private final String gameSaveFilename;
  private final FileHandle gameFile;
  private final TowerMiniMap towerMiniMap;
  private boolean shouldSaveGame;
  private String cloudGameSaveUri;
  private boolean loadedSavedGame;
  private FileHandle pngFile;

  public GameState(OrthographicCamera camera, final GameGrid gameGrid, FileHandle gameSaveLocation, String gameSaveFilename, TowerMiniMap towerMiniMap) {
    this.camera = camera;
    this.gameGrid = gameGrid;
    this.gameSaveLocation = gameSaveLocation;
    this.gameSaveFilename = gameSaveFilename;
    this.gameFile = gameSaveLocation.child(gameSaveFilename);
    this.pngFile = gameSaveLocation.child(gameSaveFilename + ".png");
    this.towerMiniMap = towerMiniMap;
  }

  public void loadSavedGame() {
    shouldSaveGame = true;
    Gdx.app.debug(TAG, "Loading: " + gameFile.path());
    if (!gameFile.exists()) {
      return;
    }

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      GameSave gameSave = objectMapper.readValue(gameFile.file(), GameSave.class);
      cloudGameSaveUri = gameSave.getCloudSaveUri();
      gameGrid.setGridSize(gameSave.getGridSize().x, gameSave.getGridSize().y);
      gameGrid.updateWorldSize();

      Player.setInstance(gameSave.getPlayer());

      camera.position.set(gameSave.getCameraPosition());
      camera.zoom = gameSave.getCameraZoom();
      CameraController.instance().panTo(gameSave.getCameraPosition(), false);
      CameraController.instance().checkBounds();

      AchievementEngine.instance().loadCompletedAchievements(gameSave.getCompletedAchievements());

      for (GridObjectState gridObjectState : gameSave.getGridObjects()) {
        gridObjectState.materialize(gameGrid);
      }

      loadedSavedGame = true;
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

  public void saveGame() {
    if (shouldSaveGame) {
      if (!gameSaveLocation.exists()) {
        gameSaveLocation.mkdirs();
      }

      if (!gameGrid.isEmpty()) {
        GameSave gameSave = new GameSave(gameGrid, camera, Player.instance(), cloudGameSaveUri);
        try {
          OutputStream stream = pngFile.write(false);
          stream.write(PNG.toPNG(towerMiniMap.redrawMiniMap(true, 2f)));
          stream.flush();
          stream.close();

          CloudGameSave cloudGameSave = new CloudGameSave(gameSave, pngFile);
          cloudGameSave.save();

          if (cloudGameSave.isSaved() && cloudGameSaveUri == null) {
            gameSave.setCloudSaveUri(cloudGameSave.getResourceUri());
          }

          GameSave.getObjectMapper().writeValue(gameFile.file(), gameSave);
        } catch (Exception e) {
          Gdx.app.log("GameSave", "Could not save game!", e);
        }
      }
    }
  }

  public boolean hasLoadedSavedGame() {
    return loadedSavedGame;
  }
}
