package com.unhappyrobot.scenes;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.input.GestureDetector;
import com.google.common.collect.Lists;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.WeatherService;
import com.unhappyrobot.achievements.AchievementEngine;
import com.unhappyrobot.audio.GameGridSoundDispatcher;
import com.unhappyrobot.controllers.AvatarLayer;
import com.unhappyrobot.controllers.GameTips;
import com.unhappyrobot.entities.CloudLayer;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.gamestate.GameState;
import com.unhappyrobot.graphics.CityScapeLayer;
import com.unhappyrobot.graphics.GroundLayer;
import com.unhappyrobot.graphics.RainLayer;
import com.unhappyrobot.graphics.SkyLayer;
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
  private WeatherService weatherService;
  private HeadsUpDisplay headsUpDisplay;
  private GameTips gameTips;
  private GestureDetector gestureDetector;
  private GestureDelegater gestureDelegater;
  private GameGridSoundDispatcher gridSoundDispatcher;

  public TowerScene() {
    gameSaveLocation = Gdx.files.external(Gdx.app.getType().equals(Application.ApplicationType.Desktop) ? ".towergame/test.json" : "test.json");
    timeMultiplier = 1f;
  }

  @Override
  public void create() {
    RoomTypeFactory.instance();
    CommercialTypeFactory.instance();
    ElevatorTypeFactory.instance();
    StairTypeFactory.instance();

    gameGrid = new GameGrid(getCamera());
    gameGridRenderer = gameGrid.getRenderer();
    gameState = new GameState(getCamera(), gameGrid, gameSaveLocation);

    GridPositionCache.reset(gameGrid);

    headsUpDisplay = new HeadsUpDisplay(this);
    weatherService = new WeatherService();

    gameLayers = Lists.newArrayList();
    gameLayers.add(new SkyLayer(gameGrid, weatherService));
    gameLayers.add(new CityScapeLayer(gameGrid));
    gameLayers.add(new RainLayer(gameGrid, weatherService));
    gameLayers.add(new CloudLayer(gameGrid, weatherService));
    gameLayers.add(new GroundLayer(gameGrid));
    gameLayers.add(gameGridRenderer);
    gameLayers.add(gameGrid);
    gameLayers.add(AvatarLayer.initialize(gameGrid));

    gameGrid.setGridSize(TowerConsts.GAME_GRID_START_SIZE, TowerConsts.GAME_GRID_START_SIZE);
    gameGrid.updateWorldSize();

    gameTips = new GameTips(gameGrid);

    gestureDelegater = new GestureDelegater(camera, gameLayers);
    gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, gestureDelegater);

    gridSoundDispatcher = new GameGridSoundDispatcher();
  }

  @Override
  public void pause() {
    gameState.saveGame();
    InputSystem.instance().removeInputProcessor(gestureDetector);
    AchievementEngine.instance().unregisterGameGrid();
    gridSoundDispatcher.setGameGrid(null);
  }

  @Override
  public void resume() {
    InputSystem.instance().addInputProcessor(gestureDetector, 100);
    InputSystem.instance().setGestureDelegator(gestureDelegater);
    InputSystem.instance().switchTool(GestureTool.PICKER, null);
    DefaultKeybindings.initialize(this);
    if (!gameState.hasLoadedSavedGame()) {
      gameState.loadSavedGame();
    }
    AchievementEngine.instance().registerGameGrid(gameGrid);
    gridSoundDispatcher.setGameGrid(gameGrid);
  }

  @Override
  public void render(float deltaTime) {
    updateGameObjects(deltaTime);

    for (GameLayer layer : gameLayers) {
      layer.render(getSpriteBatch(), getCamera());
    }
  }

  private void updateGameObjects(float deltaTime) {
    deltaTime *= timeMultiplier;

    for (GameLayer layer : gameLayers) {
      layer.update(deltaTime);
    }

    headsUpDisplay.act(deltaTime);
    weatherService.update(deltaTime);
    gameState.update(deltaTime);
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
