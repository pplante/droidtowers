/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.input.GestureDetector;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.DroidTowersGame;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.WeatherService;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.actions.ActionManager;
import com.happydroids.droidtowers.actions.GameSaveAction;
import com.happydroids.droidtowers.audio.GameSoundController;
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
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
  private CrimeCalculator crimeCalculator;
  private ParticleEffectPool effectPool;
  private Set<ParticleEffect> activeEffects;
  private Set<ParticleEffect> reactivatedEffects;
  private Iterator<float[]> colorsIterator;

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


    for (GridObjectTypeFactory typeFactory : GridObjectTypeFactory.allFactories()) {
      for (Object gridObjectType : typeFactory.all()) {
        ((GridObjectType) gridObjectType).removeLock();
      }
    }

    AchievementEngine.instance().resetState();
    TutorialEngine.instance().resetState();

    gameGrid = new GameGrid(camera);
    gameGridRenderer = gameGrid.getRenderer();
    avatarLayer = new AvatarLayer(gameGrid);
    gameState = new GameState(camera, cameraController, gameSaveLocation, gameSave, gameGrid);

    gameGrid.events().register(this);

    headsUpDisplay = new HeadsUpDisplay(getStage(), getCamera(), getCameraController(), gameGrid, avatarLayer, AchievementEngine
                                                                                                                       .instance(), TutorialEngine
                                                                                                                                            .instance(), gameState);
    weatherService = new WeatherService();

    gameLayers = Lists.newArrayList();
    gameLayers.add(new SkyLayer(weatherService));
    gameLayers.add(new CityScapeLayer());
    gameLayers.add(new CloudLayer(weatherService));
    gameLayers.add(new RainLayer(weatherService));
    gameLayers.add(new FireWorksLayer(gameGrid));
    gameLayers.add(new GroundLayer());
    gameLayers.add(gameGridRenderer);
    gameLayers.add(gameGrid);
    gameLayers.add(avatarLayer);

    gestureDelegater = new GestureDelegater(camera, gameLayers, gameGrid, getCameraController());
    gestureDetector = new GestureDetector(20 * Display.getScaledDensity(), 0.5f, 1, 0.15f, gestureDelegater);
    keybindings = new DefaultKeybindings(this);

    attachToInputSystem();

    gameState.loadSavedGame();

    populationCalculator = new PopulationCalculator(gameGrid, avatarLayer, TowerConsts.ROOM_UPDATE_FREQUENCY);
    budgetCalculator = new BudgetCalculator(gameGrid, TowerConsts.PLAYER_EARNOUT_FREQUENCY);
    employmentCalculator = new EmploymentCalculator(gameGrid, TowerConsts.JOB_UPDATE_FREQUENCY);
    desirabilityCalculator = new DesirabilityCalculator(gameGrid, TowerConsts.ROOM_UPDATE_FREQUENCY);
    starRatingCalculator = new StarRatingCalculator(gameGrid, TowerConsts.ROOM_UPDATE_FREQUENCY);
    achievementEngineCheck = new AchievementEngineCheck(gameGrid, TowerConsts.ACHIEVEMENT_ENGINE_FREQUENCY);
    saveAction = new GameSaveAction(gameState);
    transportCalculator = new TransportCalculator(gameGrid, TowerConsts.TRANSPORT_CALCULATOR_FREQUENCY);
    crimeCalculator = new CrimeCalculator(gameGrid, TowerConsts.CRIME_CALCULATOR_FREQUENCY);

    attachActions();

    if (avatarLayer != null) {
      avatarLayer.setupInitialAvatars();
    }

    GameSoundController.runAfterInit(new Runnable() {
      @Override
      public void run() {
        gameGrid.events().register(DroidTowersGame.getSoundController());
      }
    });
  }

  private void attachActions() {
    ActionManager.instance().addAction(transportCalculator);
    ActionManager.instance().addAction(crimeCalculator);
    ActionManager.instance().addAction(populationCalculator);
    ActionManager.instance().addAction(budgetCalculator);
    ActionManager.instance().addAction(employmentCalculator);
    ActionManager.instance().addAction(desirabilityCalculator);
    ActionManager.instance().addAction(starRatingCalculator);
    ActionManager.instance().addAction(achievementEngineCheck);
    transportCalculator.run();
    crimeCalculator.run();
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
    ActionManager.instance().removeAction(crimeCalculator);
    ActionManager.instance().removeAction(populationCalculator);
    ActionManager.instance().removeAction(budgetCalculator);
    ActionManager.instance().removeAction(employmentCalculator);
    ActionManager.instance().removeAction(desirabilityCalculator);
    ActionManager.instance().removeAction(starRatingCalculator);

    // SHOULD ALWAYS BE LAST.
    ActionManager.instance().removeAction(saveAction);
  }

  private void attachToInputSystem() {
    InputSystem.instance().events().register(gameGridRenderer);

    InputSystem.instance().addInputProcessor(gestureDetector, 100);
    InputSystem.instance().setGestureDelegate(gestureDelegater);
    InputSystem.instance().switchTool(GestureTool.PICKER, null);
    keybindings.bindKeys();
  }

  private void detachFromInputSystem() {
    InputSystem.instance().removeInputProcessor(gestureDetector);
    InputSystem.instance().setGestureDelegate(null);
    keybindings.unbindKeys();

    InputSystem.instance().events().unregister(gameGridRenderer);
  }

  @Override
  public void pause() {
    gameState.saveGame(false);

    detachActions();

    detachFromInputSystem();
  }

  @Override
  public void resume() {
    attachActions();

    attachToInputSystem();
  }

  @Override
  public void render(float deltaTime) {
    TowerAssetManager.assetManager().update();

    updateGameObjects(deltaTime);

    for (GameLayer layer : gameLayers) {
      layer.render(getSpriteBatch(), getCamera());
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

    detachActions();

    gameGrid.events().unregister(DroidTowersGame.getSoundController());
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
