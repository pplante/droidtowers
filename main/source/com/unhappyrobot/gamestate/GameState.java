package com.unhappyrobot.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.achievements.AchievementEngine;
import com.unhappyrobot.controllers.GameTips;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.events.EventListener;
import com.unhappyrobot.gamestate.actions.*;
import com.unhappyrobot.gamestate.server.HappyDroidService;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.grid.GridObjectState;
import com.unhappyrobot.gui.Dialog;
import com.unhappyrobot.gui.OnClickCallback;
import com.unhappyrobot.gui.ResponseType;
import org.codehaus.jackson.map.ObjectMapper;

public class GameState extends EventListener {
  private final GameGrid gameGrid;
  private final GameStateAction calculatePopulation;
  private final GameStateAction calculateJobs;
  private final GameStateAction calculateEarnout;
  private final GameStateAction calculateDesirability;
  private boolean shouldSaveGame;
  private final TransportCalculator transportCalculator;

  public GameState(final GameGrid gameGrid) {
    this.gameGrid = gameGrid;

    calculatePopulation = new PopulationCalculator(this.gameGrid, TowerConsts.ROOM_UPDATE_FREQUENCY);
    calculateEarnout = new EarnoutCalculator(this.gameGrid, TowerConsts.PLAYER_EARNOUT_FREQUENCY);
    calculateJobs = new EmploymentCalculator(this.gameGrid, TowerConsts.JOB_UPDATE_FREQUENCY);
    calculateDesirability = new DesirabilityCalculator(gameGrid, TowerConsts.ROOM_UPDATE_FREQUENCY);
    transportCalculator = new TransportCalculator(gameGrid, TowerConsts.TRANSPORT_CALCULATOR_FREQUENCY);

    GameGrid.events().register(this);
  }

  public void update(float deltaTime, GameGrid gameGrid) {
    gameGrid.update(deltaTime);

    transportCalculator.act(deltaTime);
    calculatePopulation.act(deltaTime);
    calculateJobs.act(deltaTime);
    calculateEarnout.act(deltaTime);
    calculateDesirability.act(deltaTime);
  }

  public void loadSavedGame(final FileHandle fileHandle, OrthographicCamera camera) {
    shouldSaveGame = true;

    if (!fileHandle.exists()) {
      return;
    }

    try {
      transportCalculator.pause();

      ObjectMapper objectMapper = new ObjectMapper();
      GameSave gameSave = objectMapper.readValue(fileHandle.file(), GameSave.class);

      Player.setInstance(gameSave.getPlayer());

      camera.position.set(gameSave.getCameraPosition());
      camera.zoom = gameSave.getCameraZoom();

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
          fileHandle.delete();
          dialog.dismiss();
          shouldSaveGame = true;
        }
      }).addButton(ResponseType.NEGATIVE, "No, exit game", new OnClickCallback() {
        @Override
        public void onClick(Dialog dialog) {
          dialog.dismiss();
          Gdx.app.exit();
        }
      }).centerOnScreen().show();
    } finally {
      transportCalculator.unpause();
      GameTips.instance().enable();
    }
  }

  public void saveGame(FileHandle fileHandle, OrthographicCamera camera) {
    if (shouldSaveGame) {
      if (!fileHandle.exists()) {
        fileHandle.parent().mkdirs();
      }

      GameSave gameSave = new GameSave(gameGrid, camera, Player.instance());
      try {
        String cloudSaveUri = HappyDroidService.instance().uploadGameSave(gameSave);
        System.out.println("cloudSaveUri = " + cloudSaveUri);
        if (cloudSaveUri != null) {
          gameSave.setCloudSaveUri(cloudSaveUri);
        }

        GameSave.getObjectMapper().writeValue(fileHandle.file(), gameSave);
      } catch (Exception e) {
        Gdx.app.log("GameSave", "Could not save game!", e);
      }
    }
  }
}
