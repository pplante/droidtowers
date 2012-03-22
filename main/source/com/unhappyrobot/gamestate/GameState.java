package com.unhappyrobot.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.achievements.AchievementEngine;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.events.EventListener;
import com.unhappyrobot.gamestate.actions.*;
import com.unhappyrobot.gamestate.server.HappyDroidService;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.grid.GridObjectState;
import com.unhappyrobot.gui.Dialog;
import com.unhappyrobot.gui.OnClickCallback;
import com.unhappyrobot.gui.ResponseType;
import com.unhappyrobot.input.CameraController;
import org.codehaus.jackson.map.ObjectMapper;

public class GameState extends EventListener {
  private final OrthographicCamera camera;
  private final GameGrid gameGrid;
  private final FileHandle gameSaveLocation;
  private final GameStateAction calculatePopulation;
  private final GameStateAction calculateJobs;
  private final GameStateAction calculateEarnout;
  private final GameStateAction calculateDesirability;
  private boolean shouldSaveGame;
  private final TransportCalculator transportCalculator;
  private long nextTimeToSave;

  public GameState(OrthographicCamera camera, final GameGrid gameGrid, FileHandle gameSaveLocation) {
    this.camera = camera;
    this.gameGrid = gameGrid;
    this.gameSaveLocation = gameSaveLocation;

    nextTimeToSave = System.currentTimeMillis() + TowerConsts.GAME_SAVE_FREQUENCY;
    calculatePopulation = new PopulationCalculator(gameGrid, TowerConsts.ROOM_UPDATE_FREQUENCY);
    calculateEarnout = new EarnoutCalculator(gameGrid, TowerConsts.PLAYER_EARNOUT_FREQUENCY);
    calculateJobs = new EmploymentCalculator(gameGrid, TowerConsts.JOB_UPDATE_FREQUENCY);
    calculateDesirability = new DesirabilityCalculator(gameGrid, TowerConsts.ROOM_UPDATE_FREQUENCY);
    transportCalculator = new TransportCalculator(gameGrid, TowerConsts.TRANSPORT_CALCULATOR_FREQUENCY);

    gameGrid.events().register(this);
  }

  public void update(float deltaTime) {
    transportCalculator.act(deltaTime);
    calculatePopulation.act(deltaTime);
    calculateJobs.act(deltaTime);
    calculateEarnout.act(deltaTime);
    calculateDesirability.act(deltaTime);

    if (nextTimeToSave <= System.currentTimeMillis()) {
      nextTimeToSave = System.currentTimeMillis() + TowerConsts.GAME_SAVE_FREQUENCY;
      saveGame();
    }
  }

  public void loadSavedGame() {
    shouldSaveGame = true;

    if (!gameSaveLocation.exists()) {
      return;
    }

    try {
      transportCalculator.pause();

      ObjectMapper objectMapper = new ObjectMapper();
      GameSave gameSave = objectMapper.readValue(gameSaveLocation.file(), GameSave.class);

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
    } catch (Exception e) {
      shouldSaveGame = false;

      Gdx.app.log("GameSave", "Could not load saved game!", e);
      new Dialog().setMessage("Saved game could not be loaded, want to reset?").addButton(ResponseType.POSITIVE, "Yes", new OnClickCallback() {
        @Override
        public void onClick(Dialog dialog) {
          gameSaveLocation.delete();
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
      transportCalculator.unpause();
    }
  }

  public void saveGame() {
    if (shouldSaveGame) {
      if (!gameSaveLocation.exists()) {
        gameSaveLocation.parent().mkdirs();
      }

      GameSave gameSave = new GameSave(gameGrid, camera, Player.instance());
      try {
        String cloudSaveUri = HappyDroidService.instance().uploadGameSave(gameSave);
        System.out.println("cloudSaveUri = " + cloudSaveUri);
        if (cloudSaveUri != null) {
          gameSave.setCloudSaveUri(cloudSaveUri);
        }

        GameSave.getObjectMapper().writeValue(gameSaveLocation.file(), gameSave);
      } catch (Exception e) {
        Gdx.app.log("GameSave", "Could not save game!", e);
      }
    }
  }
}
