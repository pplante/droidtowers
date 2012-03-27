package com.unhappyrobot.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.input.GestureDetector;
import com.google.common.collect.Lists;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.WeatherService;
import com.unhappyrobot.achievements.AchievementEngine;
import com.unhappyrobot.actions.ActionManager;
import com.unhappyrobot.actions.GameSaveAction;
import com.unhappyrobot.audio.GameGridSoundDispatcher;
import com.unhappyrobot.controllers.AvatarLayer;
import com.unhappyrobot.controllers.GameTips;
import com.unhappyrobot.entities.CloudLayer;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.gamestate.GameSave;
import com.unhappyrobot.gamestate.GameState;
import com.unhappyrobot.gamestate.actions.*;
import com.unhappyrobot.graphics.*;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.grid.GameGridRenderer;
import com.unhappyrobot.grid.GridPositionCache;
import com.unhappyrobot.gui.HeadsUpDisplay;
import com.unhappyrobot.input.DefaultKeybindings;
import com.unhappyrobot.input.GestureDelegater;
import com.unhappyrobot.input.GestureTool;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.types.CommercialTypeFactory;
import com.unhappyrobot.types.ElevatorTypeFactory;
import com.unhappyrobot.types.RoomTypeFactory;
import com.unhappyrobot.types.StairTypeFactory;

import java.util.List;

public class TowerScene extends Scene {
  private List<GameLayer> gameLayers;
  private GameGrid gameGrid;
  private GameGridRenderer gameGridRenderer;
  private GameState gameState;
  private float timeMultiplier;
  private FileHandle gameSaveLocation;
  private GameSave gameSave;
  private WeatherService weatherService;
  private HeadsUpDisplay headsUpDisplay;
  private GameTips gameTips;
  private GestureDetector gestureDetector;
  private GestureDelegater gestureDelegater;
  private GameGridSoundDispatcher gridSoundDispatcher;
  private TowerMiniMap towerMiniMap;
  private TransportCalculator transportCalculator;
  private PopulationCalculator populationCalculator;
  private EarnoutCalculator earnoutCalculator;
  private EmploymentCalculator employmentCalculator;
  private DesirabilityCalculator desirabilityCalculator;
  private GameSaveAction saveAction;
  private DefaultKeybindings keybindings;

  public TowerScene() {
    gameSaveLocation = Gdx.files.external(TowerConsts.GAME_SAVE_DIRECTORY);
    timeMultiplier = 1f;
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

    RoomTypeFactory.instance();
    CommercialTypeFactory.instance();
    ElevatorTypeFactory.instance();
    StairTypeFactory.instance();

    gameGrid = new GameGrid(camera);
    gameGridRenderer = gameGrid.getRenderer();
    gameState = new GameState(camera, gameSaveLocation, gameSave, gameGrid);

    GridPositionCache.reset(gameGrid);

    headsUpDisplay = new HeadsUpDisplay(this);
    weatherService = new WeatherService();

//    towerMiniMap.x = 100;
//    towerMiniMap.y = 100;
//    headsUpDisplay.addActor(towerMiniMap);

    gameLayers = Lists.newArrayList();
    gameLayers.add(new SkyLayer(gameGrid, weatherService));
    gameLayers.add(new CityScapeLayer(gameGrid));
    gameLayers.add(new RainLayer(gameGrid, weatherService));
    gameLayers.add(new CloudLayer(gameGrid, weatherService));
    gameLayers.add(new GroundLayer(gameGrid));
    gameLayers.add(gameGridRenderer);
    gameLayers.add(gameGrid);
    gameLayers.add(AvatarLayer.initialize(gameGrid));

    gameTips = new GameTips(gameGrid);

    gestureDelegater = new GestureDelegater(camera, gameLayers);
    gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, gestureDelegater);

    gridSoundDispatcher = new GameGridSoundDispatcher();

    InputSystem.instance().addInputProcessor(gestureDetector, 100);
    InputSystem.instance().setGestureDelegator(gestureDelegater);
    InputSystem.instance().switchTool(GestureTool.PICKER, null);
    keybindings = new DefaultKeybindings(this);
    keybindings.bindKeys();

    gameState.loadSavedGame();
    AchievementEngine.instance().registerGameGrid(gameGrid);

    populationCalculator = new PopulationCalculator(gameGrid, TowerConsts.ROOM_UPDATE_FREQUENCY);
    earnoutCalculator = new EarnoutCalculator(gameGrid, TowerConsts.PLAYER_EARNOUT_FREQUENCY);
    employmentCalculator = new EmploymentCalculator(gameGrid, TowerConsts.JOB_UPDATE_FREQUENCY);
    desirabilityCalculator = new DesirabilityCalculator(gameGrid, TowerConsts.ROOM_UPDATE_FREQUENCY);
    saveAction = new GameSaveAction(gameState);
    transportCalculator = new TransportCalculator(gameGrid, TowerConsts.TRANSPORT_CALCULATOR_FREQUENCY);
    transportCalculator.run();

    attachActions();
  }

  private void attachActions() {
    ActionManager.instance().addAction(transportCalculator);
    ActionManager.instance().addAction(populationCalculator);
    ActionManager.instance().addAction(earnoutCalculator);
    ActionManager.instance().addAction(employmentCalculator);
    ActionManager.instance().addAction(desirabilityCalculator);

    // SHOULD ALWAYS BE LAST.
    ActionManager.instance().addAction(saveAction);
  }

  @Override
  public void pause() {
    gameState.saveGame(true);
    gridSoundDispatcher.setGameGrid(null);
  }

  @Override
  public void resume() {
    gridSoundDispatcher.setGameGrid(gameGrid);
  }

  @Override
  public void render(float deltaTime) {
    updateGameObjects(deltaTime);

    for (GameLayer layer : gameLayers) {
      layer.render(getSpriteBatch(), getCamera());
    }
  }

  @Override
  public void dispose() {
    InputSystem.instance().removeInputProcessor(gestureDetector);
    InputSystem.instance().setGestureDelegator(null);
    keybindings.unbindKeys();

    AchievementEngine.instance().unregisterGameGrid();

    ActionManager.instance().removeAction(transportCalculator);
    ActionManager.instance().removeAction(populationCalculator);
    ActionManager.instance().removeAction(earnoutCalculator);
    ActionManager.instance().removeAction(employmentCalculator);
    ActionManager.instance().removeAction(desirabilityCalculator);

    // SHOULD ALWAYS BE LAST.
    ActionManager.instance().removeAction(saveAction);
  }


  private void updateGameObjects(float deltaTime) {
    deltaTime *= timeMultiplier;

    for (GameLayer layer : gameLayers) {
      layer.update(deltaTime);
    }

    headsUpDisplay.act(deltaTime);
    weatherService.update(deltaTime);
  }

  public float getTimeMultiplier() {
    return timeMultiplier;
  }

  public void setTimeMultiplier(float timeMultiplier) {
    this.timeMultiplier = timeMultiplier;
  }

  public GameGrid getGameGrid() {
    return gameGrid;
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
}
