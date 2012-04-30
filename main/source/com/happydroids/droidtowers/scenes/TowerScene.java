/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.WeatherService;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.actions.ActionManager;
import com.happydroids.droidtowers.actions.GameSaveAction;
import com.happydroids.droidtowers.audio.GameGridSoundDispatcher;
import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.entities.CloudLayer;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.gamestate.GameState;
import com.happydroids.droidtowers.gamestate.actions.*;
import com.happydroids.droidtowers.gamestate.server.FriendCloudGameSave;
import com.happydroids.droidtowers.gamestate.server.FriendCloudGameSaveCollection;
import com.happydroids.droidtowers.graphics.*;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GameGridRenderer;
import com.happydroids.droidtowers.grid.NeighborGameGrid;
import com.happydroids.droidtowers.gui.HeadsUpDisplay;
import com.happydroids.droidtowers.input.DefaultKeybindings;
import com.happydroids.droidtowers.input.GestureDelegater;
import com.happydroids.droidtowers.input.GestureTool;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;
import com.happydroids.server.ApiCollectionRunnable;
import com.happydroids.server.HappyDroidServiceCollection;
import org.apache.http.HttpResponse;

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
  private AchievementEngineCheck achievementEngineCheck;

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

    gameGrid = new GameGrid(camera);
    gameGridRenderer = gameGrid.getRenderer();
    gameState = new GameState(camera, gameSaveLocation, gameSave, gameGrid);

    headsUpDisplay = new HeadsUpDisplay(this, this.getStage(), this.getCamera(), this.getGameGrid());
    weatherService = new WeatherService();

//    towerMiniMap.x = 100;
//    towerMiniMap.y = 100;
//    headsUpDisplay.addActor(towerMiniMap);

    gameLayers = Lists.newArrayList();
    gameLayers.add(new SkyLayer(gameGrid, weatherService));
    gameLayers.add(new CityScapeLayer(gameGrid));
    gameLayers.add(new CloudLayer(gameGrid, weatherService));
    gameLayers.add(new RainLayer(gameGrid, weatherService));
    gameLayers.add(new GroundLayer(gameGrid));
    gameLayers.add(gameGridRenderer);
    gameLayers.add(gameGrid);
    gameLayers.add(AvatarLayer.initialize(gameGrid));

    gestureDelegater = new GestureDelegater(camera, gameLayers, gameGrid);
    gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, gestureDelegater);

    gridSoundDispatcher = new GameGridSoundDispatcher();

    InputSystem.instance().addInputProcessor(gestureDetector, 100);
    InputSystem.instance().setGestureDelegator(gestureDelegater);
    InputSystem.instance().switchTool(GestureTool.PICKER, null);
    keybindings = new DefaultKeybindings(this);
    keybindings.bindKeys();

    gameState.loadSavedGame();
    AchievementEngine.instance().registerGameGrid(gameGrid);
    TutorialEngine.instance().registerGameGrid(gameGrid);

    populationCalculator = new PopulationCalculator(gameGrid, TowerConsts.ROOM_UPDATE_FREQUENCY);
    earnoutCalculator = new EarnoutCalculator(gameGrid, TowerConsts.PLAYER_EARNOUT_FREQUENCY);
    employmentCalculator = new EmploymentCalculator(gameGrid, TowerConsts.JOB_UPDATE_FREQUENCY);
    desirabilityCalculator = new DesirabilityCalculator(gameGrid, TowerConsts.ROOM_UPDATE_FREQUENCY);
    achievementEngineCheck = new AchievementEngineCheck(gameGrid, TowerConsts.ACHIEVEMENT_ENGINE_FREQUENCY);
    saveAction = new GameSaveAction(gameState);
    transportCalculator = new TransportCalculator(gameGrid, TowerConsts.TRANSPORT_CALCULATOR_FREQUENCY);
    transportCalculator.run();

    attachActions();

    final int[] neighborGridX = {0};
    FriendCloudGameSaveCollection friendGames = new FriendCloudGameSaveCollection();
    friendGames.fetch(new ApiCollectionRunnable<HappyDroidServiceCollection<FriendCloudGameSave>>() {
      @Override
      public void onError(HttpResponse response, int statusCode, HappyDroidServiceCollection<FriendCloudGameSave> collection) {
        System.out.println("collection = " + collection);
      }

      @Override
      public void onSuccess(HttpResponse response, HappyDroidServiceCollection<FriendCloudGameSave> collection) {
        for (final FriendCloudGameSave friendCloudGameSave : collection.getObjects()) {
          NeighborGameGrid neighborGameGrid = new NeighborGameGrid(getCamera(), new Vector2(neighborGridX[0], TowerConsts.NEIGHBOR_GROUND_HEIGHT));
          neighborGameGrid.getRenderer().setRenderTintColor(Color.GRAY);
          GameSave gameSave = friendCloudGameSave.getGameSave();

          if (!gameSave.hasGridObjects()) {
            System.out.println("Skipping, no objects! " + friendCloudGameSave);
            continue;
          }

          gameSave.attachToGame(neighborGameGrid, camera);
          neighborGameGrid.findLimits();
          neighborGridX[0] += (neighborGameGrid.getGridSize().x + 2) * TowerConsts.GRID_UNIT_SIZE;

          neighborGameGrid.setOwnerName(friendCloudGameSave.getOwner().getFirstName());
          neighborGameGrid.setClickListener(new Runnable() {
            public void run() {
              HeadsUpDisplay.showToast("HELLO FROM " + friendCloudGameSave.getOwner().getFirstName());
            }
          });

          int indexOfPlayerGameGrid = gameLayers.indexOf(gameGrid);
          gameLayers.add(indexOfPlayerGameGrid - 1, neighborGameGrid.getRenderer());
          gameLayers.add(indexOfPlayerGameGrid - 1, neighborGameGrid);
        }
      }
    });
  }

  private void attachActions() {
    ActionManager.instance().addAction(transportCalculator);
    ActionManager.instance().addAction(populationCalculator);
    ActionManager.instance().addAction(earnoutCalculator);
    ActionManager.instance().addAction(employmentCalculator);
    ActionManager.instance().addAction(desirabilityCalculator);
    ActionManager.instance().addAction(achievementEngineCheck);

    // SHOULD ALWAYS BE LAST.
    ActionManager.instance().addAction(saveAction);
  }

  private void detachActions() {
    ActionManager.instance().removeAction(achievementEngineCheck);
    ActionManager.instance().removeAction(transportCalculator);
    ActionManager.instance().removeAction(populationCalculator);
    ActionManager.instance().removeAction(earnoutCalculator);
    ActionManager.instance().removeAction(employmentCalculator);
    ActionManager.instance().removeAction(desirabilityCalculator);

    // SHOULD ALWAYS BE LAST.
    ActionManager.instance().removeAction(saveAction);
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
      layer.render(getSpriteBatch());
    }
  }

  @Override
  public void dispose() {
    detachActions();

    InputSystem.instance().removeInputProcessor(gestureDetector);
    InputSystem.instance().setGestureDelegator(null);
    keybindings.unbindKeys();

    TutorialEngine.instance().unregisterGameGrid();
    TutorialEngine.instance().resetState();

    AchievementEngine.instance().unregisterGameGrid();
    AchievementEngine.instance().resetState();
    for (GridObjectTypeFactory typeFactory : GridObjectTypeFactory.allFactories()) {
      for (Object o : typeFactory.all()) {
        ((GridObjectType) o).removeLock();
      }
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

  public GameSave getCurrentGameSave() {
    return gameSave;
  }
}
