package com.unhappyrobot.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.unhappyrobot.GameSave;
import com.unhappyrobot.GridObjectState;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.achievements.AchievementEngine;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.events.EventListener;
import com.unhappyrobot.events.GameEvents;
import com.unhappyrobot.gamestate.actions.*;
import com.unhappyrobot.gui.Dialog;
import com.unhappyrobot.gui.OnClickCallback;
import com.unhappyrobot.gui.ResponseType;
import com.unhappyrobot.jackson.Vector3Serializer;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

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

    GameEvents.register(this);

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
    }
  }

  public void saveGame(FileHandle fileHandle, OrthographicCamera camera) {
    if (shouldSaveGame) {
      GameSave gameSave = new GameSave(gameGrid, camera, Player.instance());
      ObjectMapper objectMapper = new ObjectMapper();
      SimpleModule simpleModule = new SimpleModule("Specials", new Version(1, 0, 0, null));
      simpleModule.addSerializer(new Vector3Serializer());
      objectMapper.registerModule(simpleModule);
      try {
        objectMapper.writeValue(fileHandle.file(), gameSave);
      } catch (Exception e) {
        Gdx.app.log("GameSave", "Could not save game!", e);
      }
    }
  }
}
