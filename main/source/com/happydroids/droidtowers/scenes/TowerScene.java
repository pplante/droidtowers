/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.input.GestureDetector;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.WeatherService;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.actions.ActionManager;
import com.happydroids.droidtowers.actions.GameSaveAction;
import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.entities.CloudLayer;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.events.GameGridResizeEvent;
import com.happydroids.droidtowers.events.RespondsToWorldSizeChange;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.gamestate.GameState;
import com.happydroids.droidtowers.gamestate.actions.*;
import com.happydroids.droidtowers.graphics.*;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GameGridRenderer;
import com.happydroids.droidtowers.gui.HeadsUpDisplay;
import com.happydroids.droidtowers.input.DefaultKeybindings;
import com.happydroids.droidtowers.input.GestureDelegater;
import com.happydroids.droidtowers.input.GestureTool;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;

import java.util.List;

public class TowerScene extends Scene {
  private List<GameLayer> gameLayers;
  private GameGrid gameGrid;
  private GameGridRenderer gameGridRenderer;
  private GameState gameState;
  private FileHandle gameSaveLocation;
  private GameSave gameSave;
  private WeatherService weatherService;
  private HeadsUpDisplay headsUpDisplay;
  private GestureDetector gestureDetector;
  private GestureDelegater gestureDelegater;
  private TowerMiniMap towerMiniMap;
  private TransportCalculator transportCalculator;
  private PopulationCalculator populationCalculator;
  private BudgetCalculator budgetCalculator;
  private EmploymentCalculator employmentCalculator;
  private DesirabilityCalculator desirabilityCalculator;
  private GameSaveAction saveAction;
  private DefaultKeybindings keybindings;
  private AchievementEngineCheck achievementEngineCheck;
  private AvatarLayer avatarLayer;
  private StarRatingCalculator starRatingCalculator;

  public TowerScene() {
    gameSaveLocation = Gdx.files.external(TowerConsts.GAME_SAVE_DIRECTORY);
  }

  @Override
  public void create(Object... args) {
    if (args != null && args.length > 0) {
      if (args[0] instanceof GameSave) {
        gameSave = (GameSave) args[0];
      }
    }

    if (gameSave == null) {
      throw new RuntimeException("Cannot load game with no GameSave passed.");
    }

    gameGrid = new GameGrid(camera);
    gameGridRenderer = gameGrid.getRenderer();
    gameState = new GameState(camera, cameraController, gameSaveLocation, gameSave, gameGrid);
    avatarLayer = new AvatarLayer(gameGrid);

    gameGrid.events().register(this);

    gameGrid.events().register(TowerGame.getSoundController());

    headsUpDisplay = new HeadsUpDisplay(getStage(), getCamera(), getCameraController(), gameGrid, avatarLayer, AchievementEngine.instance(), TutorialEngine.instance(), gameState);
    weatherService = new WeatherService();

    gameLayers = Lists.newArrayList();
    gameLayers.add(new SkyLayer(weatherService));
    gameLayers.add(new CityScapeLayer());
    gameLayers.add(new CloudLayer(weatherService));
    gameLayers.add(new RainLayer(weatherService));
    gameLayers.add(new GroundLayer());
    gameLayers.add(gameGridRenderer);
    gameLayers.add(gameGrid);
    gameLayers.add(avatarLayer);

    gestureDelegater = new GestureDelegater(camera, gameLayers, gameGrid, getCameraController());
    gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, gestureDelegater);

    InputSystem.instance().addInputProcessor(gestureDetector, 100);
    InputSystem.instance().setGestureDelegator(gestureDelegater);
    InputSystem.instance().switchTool(GestureTool.PICKER, null);
    keybindings = new DefaultKeybindings(this);
    keybindings.bindKeys();

    gameState.loadSavedGame();

    populationCalculator = new PopulationCalculator(gameGrid, TowerConsts.ROOM_UPDATE_FREQUENCY);
    budgetCalculator = new BudgetCalculator(gameGrid, TowerConsts.PLAYER_EARNOUT_FREQUENCY);
    employmentCalculator = new EmploymentCalculator(gameGrid, TowerConsts.JOB_UPDATE_FREQUENCY);
    desirabilityCalculator = new DesirabilityCalculator(gameGrid, TowerConsts.ROOM_UPDATE_FREQUENCY);
    starRatingCalculator = new StarRatingCalculator(gameGrid, TowerConsts.ROOM_UPDATE_FREQUENCY);
    achievementEngineCheck = new AchievementEngineCheck(gameGrid, TowerConsts.ACHIEVEMENT_ENGINE_FREQUENCY);
    saveAction = new GameSaveAction(gameState);
    transportCalculator = new TransportCalculator(gameGrid, TowerConsts.TRANSPORT_CALCULATOR_FREQUENCY);
    transportCalculator.run();

    attachActions();
  }

  private void attachActions() {
    ActionManager.instance().addAction(transportCalculator);
    ActionManager.instance().addAction(populationCalculator);
    ActionManager.instance().addAction(budgetCalculator);
    ActionManager.instance().addAction(employmentCalculator);
    ActionManager.instance().addAction(desirabilityCalculator);
    ActionManager.instance().addAction(starRatingCalculator);
    ActionManager.instance().addAction(achievementEngineCheck);
    transportCalculator.run();
    desirabilityCalculator.run();
    populationCalculator.run();
    employmentCalculator.run();
    starRatingCalculator.run();
    achievementEngineCheck.run();

    // SHOULD ALWAYS BE LAST.
    ActionManager.instance().addAction(saveAction);
  }

  private void detachActions() {
    ActionManager.instance().removeAction(achievementEngineCheck);
    ActionManager.instance().removeAction(transportCalculator);
    ActionManager.instance().removeAction(populationCalculator);
    ActionManager.instance().removeAction(budgetCalculator);
    ActionManager.instance().removeAction(employmentCalculator);
    ActionManager.instance().removeAction(desirabilityCalculator);
    ActionManager.instance().removeAction(starRatingCalculator);

    // SHOULD ALWAYS BE LAST.
    ActionManager.instance().removeAction(saveAction);
  }

  @Override
  public void pause() {
    gameState.saveGame(true);

    detachActions();

    InputSystem.instance().removeInputProcessor(gestureDetector);
    InputSystem.instance().setGestureDelegator(null);
    keybindings.unbindKeys();
  }

  @Override
  public void resume() {
    attachActions();

    InputSystem.instance().addInputProcessor(gestureDetector, 100);
    InputSystem.instance().setGestureDelegator(gestureDelegater);
    InputSystem.instance().switchTool(GestureTool.PICKER, null);
    keybindings.bindKeys();
  }

  @Override
  public void render(float deltaTime) {
    updateGameObjects(deltaTime);

    for (GameLayer layer : gameLayers) {
      layer.render(getSpriteBatch());
    }
  }

  @Override
  public void dispose() {
    TutorialEngine.instance().resetState();
    AchievementEngine.instance().resetState();

    for (GridObjectTypeFactory typeFactory : GridObjectTypeFactory.allFactories()) {
      for (Object o : typeFactory.all()) {
        ((GridObjectType) o).removeLock();
      }
    }

    gameGrid.events().unregister(TowerGame.getSoundController());
  }


  private void updateGameObjects(float deltaTime) {
    deltaTime *= getTimeMultiplier();

    for (GameLayer layer : gameLayers) {
      layer.update(deltaTime);
    }

    headsUpDisplay.act(deltaTime);
    weatherService.update(deltaTime);
  }

  public GameGridRenderer getGameGridRenderer() {
    return gameGridRenderer;
  }

  public List<GameLayer> getGameLayers() {
    return gameLayers;
  }

  public void setGameSaveLocation(FileHandle gameSaveLocation) {
    this.gameSaveLocation = gameSaveLocation;
  }

  public GameSave getCurrentGameSave() {
    return gameSave;
  }

  @Subscribe
  public void GameGrid_onGridResize(GameGridResizeEvent event) {
    cameraController.updateCameraConstraints(gameGrid.getWorldSize());

    for (GameLayer gameLayer : gameLayers) {
      if (gameLayer instanceof RespondsToWorldSizeChange) {
        ((RespondsToWorldSizeChange) gameLayer).updateWorldSize(gameGrid.getWorldSize());
      }
    }
  }

  public GameState getGameState() {
    return gameState;
  }
}
