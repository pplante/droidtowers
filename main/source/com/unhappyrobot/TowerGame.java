package com.unhappyrobot;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.google.common.collect.Lists;
import com.unhappyrobot.achievements.AchievementEngine;
import com.unhappyrobot.controllers.AvatarLayer;
import com.unhappyrobot.controllers.GameTips;
import com.unhappyrobot.controllers.PathSearchManager;
import com.unhappyrobot.entities.CloudLayer;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.gamestate.GameState;
import com.unhappyrobot.gamestate.server.HappyDroidService;
import com.unhappyrobot.graphics.CityScapeLayer;
import com.unhappyrobot.graphics.GroundLayer;
import com.unhappyrobot.graphics.RainLayer;
import com.unhappyrobot.graphics.SkyLayer;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.grid.GameGridRenderer;
import com.unhappyrobot.grid.GridPositionCache;
import com.unhappyrobot.gui.HeadsUpDisplay;
import com.unhappyrobot.input.DefaultKeybindings;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.tween.TweenSystem;
import com.unhappyrobot.types.CommercialTypeFactory;
import com.unhappyrobot.types.ElevatorTypeFactory;
import com.unhappyrobot.types.RoomTypeFactory;
import com.unhappyrobot.types.StairTypeFactory;
import com.unhappyrobot.utils.AsyncTask;

import java.util.List;

public class TowerGame implements ApplicationListener {
  private static OrthographicCamera camera;
  private SpriteBatch spriteBatch;

  private static List<GameLayer> gameLayers;
  private Matrix4 matrix;

  private static GameGrid gameGrid;
  private Stage guiStage;
  private static GameGridRenderer gameGridRenderer;
  private GameState gameState;
  private boolean loadedSavedGame;
  private FileHandle gameSaveLocation;
  public static float timeMultiplier;
  private long nextGameStateSaveTime;
  private final String operatingSystemName;
  private final String operatingSystemVersion;

  public TowerGame(String operatingSystemName, String operatingSystemVersion) {
    this.operatingSystemName = operatingSystemName;
    this.operatingSystemVersion = operatingSystemVersion;
  }


  public void create() {
    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

    new AsyncTask() {
      @Override
      public void execute() {
        HappyDroidService.instance().setDeviceOSName(operatingSystemName);
        HappyDroidService.instance().setDeviceOSVersion(operatingSystemVersion);
        HappyDroidService.instance().registerDevice();
      }
    }.run();

    TweenSystem.getTweenManager();
    AchievementEngine.instance();
    GameTips.instance();
    RoomTypeFactory.instance();
    CommercialTypeFactory.instance();
    ElevatorTypeFactory.instance();
    StairTypeFactory.instance();

    timeMultiplier = 1f;

    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    spriteBatch = new SpriteBatch(100);
    guiStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, spriteBatch);

    GridPositionCache.instance();

    gameGrid = new GameGrid();

    gameState = new GameState(gameGrid);

    HeadsUpDisplay.instance().initialize(camera, gameGrid, guiStage, spriteBatch);


    gameGridRenderer = gameGrid.getRenderer();

    gameLayers = Lists.newArrayList();
    gameLayers.add(new SkyLayer(gameGrid));
    gameLayers.add(new CityScapeLayer());
    gameLayers.add(new RainLayer(gameGrid));
    gameLayers.add(new CloudLayer());
    gameLayers.add(new GroundLayer());
    gameLayers.add(gameGridRenderer);
    gameLayers.add(gameGrid);
    gameLayers.add(AvatarLayer.initialize(gameGrid));

    gameGrid.setUnitSize(64, 64);
    gameGrid.setGridSize(60, 40);
    gameGrid.setGridColor(0.1f, 0.1f, 0.1f, 0.1f);

//    BEGIN INPUT SETUP:
    InputSystem.instance().setup(camera, gameLayers);
    InputSystem.instance().addInputProcessor(guiStage, 10);
    Gdx.input.setInputProcessor(InputSystem.instance());

    DefaultKeybindings.initialize(this);

    gameSaveLocation = Gdx.files.external(Gdx.app.getType().equals(Application.ApplicationType.Desktop) ? ".towergame/test.json" : "test.json");
    gameState.loadSavedGame(gameSaveLocation, camera);
    nextGameStateSaveTime = System.currentTimeMillis() + TowerConsts.GAME_SAVE_FREQUENCY;
  }

  public void render() {
    Gdx.gl.glClearColor(0.48f, 0.729f, 0.870f, 1.0f);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glEnable(GL10.GL_BLEND);
    Gdx.gl.glEnable(GL10.GL_TEXTURE_2D);

    float deltaTime = Gdx.graphics.getDeltaTime();

    camera.update();
    InputSystem.instance().update(deltaTime);
    PathSearchManager.instance().update(deltaTime);

    spriteBatch.setProjectionMatrix(camera.combined);

    updateGameObjects();

    for (GameLayer layer : gameLayers) {
      layer.render(spriteBatch, camera);
    }

    guiStage.draw();
    Table.drawDebug(guiStage);
  }

  private void updateGameObjects() {
    float deltaTime = Gdx.graphics.getDeltaTime();

    deltaTime *= timeMultiplier;

    TweenSystem.getTweenManager().update((int) (deltaTime * 1000));
    gameState.update(deltaTime, gameGrid);
    guiStage.act(deltaTime);

    for (GameLayer layer : gameLayers) {
      layer.update(deltaTime);
    }

    HeadsUpDisplay.instance().act(deltaTime);
    WeatherService.instance().update(deltaTime);

    if (nextGameStateSaveTime <= System.currentTimeMillis()) {
      nextGameStateSaveTime = System.currentTimeMillis() + TowerConsts.GAME_SAVE_FREQUENCY;
      gameState.saveGame(gameSaveLocation, camera);
    }
  }

  public void resize(int width, int height) {
    Gdx.app.log("lifecycle", "resizing!");
    camera.viewportWidth = width;
    camera.viewportHeight = height;
    spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    Gdx.gl.glViewport(0, 0, width, height);
  }

  public void pause() {
    Gdx.app.log("lifecycle", "pausing!");
    gameState.saveGame(gameSaveLocation, camera);
    System.exit(0);
  }

  public void resume() {
    Gdx.app.log("lifecycle", "resuming!");
  }

  public void dispose() {
    spriteBatch.dispose();
    guiStage.dispose();
  }

  public static OrthographicCamera getCamera() {
    return camera;
  }

  public static float getTimeMultiplier() {
    return timeMultiplier;
  }

  public static GameGrid getGameGrid() {
    return gameGrid;
  }

  public static GameGridRenderer getGameGridRenderer() {
    return gameGridRenderer;
  }
}
