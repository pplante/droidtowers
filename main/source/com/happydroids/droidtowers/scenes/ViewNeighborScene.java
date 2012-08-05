/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.WeatherService;
import com.happydroids.droidtowers.entities.CloudLayer;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.events.RespondsToWorldSizeChange;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.gamestate.GameState;
import com.happydroids.droidtowers.gamestate.server.FriendCloudGameSave;
import com.happydroids.droidtowers.gamestate.server.FriendCloudGameSaveCollection;
import com.happydroids.droidtowers.graphics.CityScapeLayer;
import com.happydroids.droidtowers.graphics.GroundLayer;
import com.happydroids.droidtowers.graphics.RainLayer;
import com.happydroids.droidtowers.graphics.SkyLayer;
import com.happydroids.droidtowers.grid.NeighborGameGrid;
import com.happydroids.droidtowers.gui.NeighborMenuBuilder;
import com.happydroids.droidtowers.gui.ViewNeighborHUD;
import com.happydroids.droidtowers.input.*;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.types.TowerNameBillboard;
import com.happydroids.events.CollectionChangeEvent;

import java.util.List;

import static com.happydroids.droidtowers.TowerConsts.GAME_GRID_START_SIZE;
import static com.happydroids.droidtowers.TowerConsts.GRID_UNIT_SIZE;

public class ViewNeighborScene extends Scene {
  private List<GameLayer> gameLayers;
  private GestureDelegater gestureDelegater;
  private GestureDetector gestureDetector;
  private ViewNeighborHUD neighborHUD;
  private GameLayer billboardLayer;
  private GameState playerGameState;
  private Vector2 worldSize;
  private int neighborGameGridX;

  @Override
  public void create(Object... args) {
    playerGameState = (GameState) args[0];

    neighborHUD = new ViewNeighborHUD(playerGameState);
    neighborHUD.pack();
    neighborHUD.setX(0);
    neighborHUD.setY(Gdx.graphics.getHeight() - neighborHUD.getHeight());
    getStage().addActor(neighborHUD);

    InputSystem.instance().bind(TowerConsts.NEGATIVE_BUTTON_KEYS, goBackHomeCallback);

    WeatherService weatherService = new WeatherService();

    gameLayers = Lists.newArrayList();
    gameLayers.add(new SkyLayer(weatherService));
    gameLayers.add(new CityScapeLayer());
    gameLayers.add(new CloudLayer(weatherService));
    gameLayers.add(new RainLayer(weatherService));
    gameLayers.add(new GroundLayer());

    billboardLayer = new GameLayer();

    gestureDelegater = new GestureDelegater(camera, gameLayers, null, getCameraController());
    gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, gestureDelegater);
    InputSystem.instance().addInputProcessor(gestureDetector, 100);
    InputSystem.instance().setGestureDelegate(gestureDelegater);
    InputSystem.instance().switchTool(GestureTool.PICKER, null);

    updateWorldSize(new Vector2(4000, 4000));
    cameraController.updateCameraConstraints(new Vector2(4000, 2000));

    worldSize = new Vector2();
    neighborGameGridX = 0;

    createNeighborTowers(playerGameState.getCloudGameSave().getNeighborGameSaves());
    playerGameState.getCloudGameSave().getNeighborGameSaves().events().register(this);
  }

  private void createNeighborTowers(FriendCloudGameSaveCollection friendGameSaves) {
    if (friendGameSaves.isEmpty()) {
      return;
    }

    for (final FriendCloudGameSave friendCloudGameSave : friendGameSaves.getObjects()) {
      addNeighborGameGridToWorld(friendCloudGameSave);
    }

    updateWorldConstraints();
  }

  private void updateWorldConstraints() {
    gameLayers.remove(billboardLayer);
    gameLayers.add(billboardLayer);

    updateWorldSize(worldSize);
  }

  private void addNeighborGameGridToWorld(FriendCloudGameSave friendCloudGameSave) {
    NeighborGameGrid neighborGameGrid = new NeighborGameGrid(getCamera(), new GridPoint(neighborGameGridX, 0));
    neighborGameGrid.setGridScale(1f);
    GameSave gameSave = friendCloudGameSave.getGameSave();

    if (!gameSave.hasGridObjects()) {
      System.out.println("Skipping, no objects! " + friendCloudGameSave);
      return;
    }

    gameSave.attachToGame(neighborGameGrid, camera, cameraController);
    neighborGameGrid.findLimits();
    neighborGameGridX += (neighborGameGrid.getGridSize().x + 6) * GRID_UNIT_SIZE;

    neighborGameGrid.setOwnerName(friendCloudGameSave.getOwner().getFirstName());
    neighborGameGrid.addListener(new NeighborMenuBuilder(this));


    TowerNameBillboard billboard = new TowerNameBillboard(neighborGameGrid);
    billboard.setPosition(neighborGameGrid.getWorldBounds().x - (2 * GRID_UNIT_SIZE), TowerConsts.GROUND_HEIGHT);
    billboardLayer.addChild(billboard);

    worldSize.y = Math.max(worldSize.y, neighborGameGrid.getWorldSize().y);
    worldSize.x = neighborGameGrid.getWorldBounds().x + neighborGameGrid.getWorldBounds().width;

    gameLayers.add(neighborGameGrid.getRenderer());
    gameLayers.add(neighborGameGrid);
  }

  private void updateWorldSize(Vector2 worldSize) {
    worldSize.x = Math.max(GAME_GRID_START_SIZE * GRID_UNIT_SIZE, worldSize.x);
    worldSize.y = Math.max(GAME_GRID_START_SIZE * GRID_UNIT_SIZE, worldSize.y);
    for (GameLayer gameLayer : gameLayers) {
      if (gameLayer instanceof RespondsToWorldSizeChange) {
        ((RespondsToWorldSizeChange) gameLayer).updateWorldSize(worldSize);
      }
    }

    cameraController.updateCameraConstraints(worldSize);
    camera.zoom = CameraController.ZOOM_MAX / 2;
    camera.position.set(worldSize.x / 2 - Display.getWidth() / 2, TowerConsts.GROUND_HEIGHT, 0f);
  }

  @Override
  public void pause() {
    InputSystem.instance().removeInputProcessor(gestureDetector);
    InputSystem.instance().setGestureDelegate(null);
  }

  @Override
  public void resume() {
    InputSystem.instance().addInputProcessor(gestureDetector, 100);
    InputSystem.instance().setGestureDelegate(gestureDelegater);
    InputSystem.instance().switchTool(GestureTool.PICKER, null);
  }

  @Override
  public void render(float deltaTime) {
    for (GameLayer gameLayer : gameLayers) {
      gameLayer.update(deltaTime);
      gameLayer.render(getSpriteBatch(), getCamera());
    }
  }

  @Override
  public void dispose() {
    playerGameState.getCloudGameSave().getNeighborGameSaves().events().unregister(this);
    InputSystem.instance().unbind(TowerConsts.NEGATIVE_BUTTON_KEYS, goBackHomeCallback);
  }

  private InputCallback goBackHomeCallback = new InputCallback() {
    @Override
    public boolean run(float timeDelta) {
      SceneManager.popScene();
      return true;
    }
  };

  public ViewNeighborHUD getNeighborHUD() {
    return neighborHUD;
  }

  @Subscribe
  public void NeighborGameSave_onChange(CollectionChangeEvent event) {
    addNeighborGameGridToWorld((FriendCloudGameSave) event.object);
    updateWorldConstraints();
  }
}
