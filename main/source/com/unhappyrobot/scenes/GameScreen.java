package com.unhappyrobot.scenes;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.common.collect.Lists;
import com.unhappyrobot.WeatherService;
import com.unhappyrobot.controllers.AvatarLayer;
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
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.types.CommercialTypeFactory;
import com.unhappyrobot.types.ElevatorTypeFactory;
import com.unhappyrobot.types.RoomTypeFactory;
import com.unhappyrobot.types.StairTypeFactory;

import java.util.List;

public class GameScreen extends Scene {
  private List<GameLayer> gameLayers;
  private GameGrid gameGrid;
  private GameGridRenderer gameGridRenderer;
  private GameState gameState;
  private float timeMultiplier;
  private final OrthographicCamera camera;
  private FileHandle gameSaveLocation;
  private WeatherService weatherService;
  private HeadsUpDisplay headsUpDisplay;

  public GameScreen(SpriteBatch spriteBatch_, OrthographicCamera camera_, FileHandle gameSaveLocation_) {
    super(spriteBatch_);
    camera = camera_;
    gameSaveLocation = gameSaveLocation_;
    timeMultiplier = 1f;
  }

  @Override
  public void create() {
    RoomTypeFactory.instance();
    CommercialTypeFactory.instance();
    ElevatorTypeFactory.instance();
    StairTypeFactory.instance();

    gameGrid = new GameGrid(camera);
    gameGridRenderer = gameGrid.getRenderer();
    gameState = new GameState(camera, gameGrid, gameSaveLocation);

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

    gameGrid.setUnitSize(64, 64);
    gameGrid.setGridSize(40, 40);
    gameGrid.setGridColor(0.1f, 0.1f, 0.1f, 0.1f);
    gameGrid.updateWorldSize();

    InputSystem.instance().setup(camera, gameLayers);
    InputSystem.instance().addInputProcessor(getStage(), 10);

    gameState.loadSavedGame();
  }

  @Override
  public void pause() {
    gameState.saveGame();
  }

  @Override
  public void resume() {
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
  }

  public float getTimeMultiplier() {
    return timeMultiplier;
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

  public OrthographicCamera getCamera() {
    return camera;
  }
}
