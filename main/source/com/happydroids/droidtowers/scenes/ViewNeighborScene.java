/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.WeatherService;
import com.happydroids.droidtowers.entities.CloudLayer;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.events.RespondsToWorldSizeChange;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.gamestate.server.FriendCloudGameSave;
import com.happydroids.droidtowers.gamestate.server.FriendCloudGameSaveCollection;
import com.happydroids.droidtowers.graphics.CityScapeLayer;
import com.happydroids.droidtowers.graphics.GroundLayer;
import com.happydroids.droidtowers.graphics.RainLayer;
import com.happydroids.droidtowers.graphics.SkyLayer;
import com.happydroids.droidtowers.grid.NeighborGameGrid;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.gui.OnClickCallback;
import com.happydroids.droidtowers.gui.ViewNeighborHUD;
import com.happydroids.droidtowers.input.*;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.tween.GameObjectAccessor;
import com.happydroids.droidtowers.tween.TweenSystem;
import com.happydroids.droidtowers.types.TowerNameType;
import com.happydroids.droidtowers.utils.Random;
import com.happydroids.server.ApiCollectionRunnable;
import com.happydroids.server.HappyDroidServiceCollection;
import com.happydroids.utils.BackgroundTask;
import org.apache.http.HttpResponse;

import java.util.List;

public class ViewNeighborScene extends Scene {
  private List<GameLayer> gameLayers;
  private Label fetchingLabel;
  private boolean fetchingNeighbors;
  private float timeSinceDroidSpawn;
  private GameObject droid;
  private GestureDelegater gestureDelegater;
  private GestureDetector gestureDetector;
  private SkyLayer skyLayer;
  private WeatherService weatherService;
  private ViewNeighborHUD neighborHUD;

  @Override
  public void create(Object... args) {
    getCamera().position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
    getCamera().zoom = CameraController.ZOOM_MIN;

    neighborHUD = new ViewNeighborHUD();
    neighborHUD.x = 0;
    neighborHUD.y = Gdx.graphics.getHeight();

    getStage().addActor(neighborHUD);

    InputSystem.instance().bind(TowerConsts.NEGATIVE_BUTTON_KEYS, goBackHomeCallback);

    weatherService = new WeatherService();

    gameLayers = Lists.newArrayList();
    gameLayers.add(new SkyLayer(weatherService));
    gameLayers.add(new CityScapeLayer());
    gameLayers.add(new CloudLayer(weatherService));
    gameLayers.add(new RainLayer(weatherService));
    gameLayers.add(new GroundLayer());

    fetchingNeighbors = true;
    droid = new GameObject(new Texture("happy-droid.png"));
    droid.setPosition(Random.randomInt(Gdx.graphics.getWidth() / 2), Random.randomInt(Gdx.graphics.getHeight()) / 2);
    Tween.to(droid, GameObjectAccessor.OPACITY, 1000)
            .target(0f)
            .setCallback(new TweenCallback() {
              @Override
              public void onEvent(int type, BaseTween source) {
                droid.setPosition(Random.randomInt(Gdx.graphics.getWidth() / 2), Random.randomInt(Gdx.graphics.getHeight()) / 2);
              }
            })
            .setCallbackTriggers(TweenCallback.END)
            .repeat(Tween.INFINITY, 100)
            .start(TweenSystem.getTweenManager());

    fetchingLabel = FontManager.Roboto64.makeLabel("fetching neighbors :D");
    getStage().addActor(fetchingLabel);

    gestureDelegater = new GestureDelegater(camera, gameLayers, null, getCameraController());
    gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, gestureDelegater);
    InputSystem.instance().addInputProcessor(gestureDetector, 100);
    InputSystem.instance().setGestureDelegator(gestureDelegater);
    InputSystem.instance().switchTool(GestureTool.PICKER, null);

    new BackgroundTask() {
      private FriendCloudGameSaveCollection friendGames;
      public boolean fetchWasSuccessful;

      @Override
      protected void execute() {
        friendGames = new FriendCloudGameSaveCollection();
        friendGames.fetch(new ApiCollectionRunnable<HappyDroidServiceCollection<FriendCloudGameSave>>() {
          @Override
          public void onError(HttpResponse response, int statusCode, HappyDroidServiceCollection<FriendCloudGameSave> collection) {
            System.out.println("collection = " + collection);
          }

          @Override
          public void onSuccess(HttpResponse response, HappyDroidServiceCollection<FriendCloudGameSave> collection) {
            fetchWasSuccessful = true;
          }
        });
      }

      @Override
      public synchronized void afterExecute() {
        if (fetchWasSuccessful) {
          createNeighborTowers(friendGames);
        } else {
          new Dialog()
                  .setTitle("Connection Failed")
                  .setMessage("Sorry, but we were not able to fetch your neighborhood.\n\nPlease check your internet connection and try again.")
                  .addButton("Okay", new OnClickCallback() {
                    @Override
                    public void onClick(Dialog dialog) {
                      dialog.dismiss();
                    }
                  })
                  .setDismissCallback(new Runnable() {
                    @Override
                    public void run() {
                      TowerGame.popScene();
                    }
                  })
                  .show();
        }
      }
    }.run();
  }

  private void createNeighborTowers(HappyDroidServiceCollection<FriendCloudGameSave> collection) {
    Vector2 worldSize = new Vector2();
    int gridX = 0;
    for (final FriendCloudGameSave friendCloudGameSave : collection.getObjects()) {
      NeighborGameGrid neighborGameGrid = new NeighborGameGrid(getCamera(), new GridPoint(gridX, 0));
      neighborGameGrid.setGridScale(1f);
      GameSave gameSave = friendCloudGameSave.getGameSave();

      if (!gameSave.hasGridObjects()) {
        System.out.println("Skipping, no objects! " + friendCloudGameSave);
        continue;
      }

      gameSave.attachToGame(neighborGameGrid, camera, cameraController);
      neighborGameGrid.findLimits();
      gridX += (neighborGameGrid.getGridSize().x + 6) * TowerConsts.GRID_UNIT_SIZE;

      neighborGameGrid.setOwnerName(friendCloudGameSave.getOwner().getFirstName());
      neighborGameGrid.setClickListener(new Runnable() {
        public void run() {
          neighborHUD.showToast("HELLO FROM " + friendCloudGameSave.getOwner().getFirstName());
        }
      });

      TowerNameType towerNameType = new TowerNameType();
      GridObject towerNameSign = towerNameType.makeGridObject(neighborGameGrid);
      neighborGameGrid.addObject(towerNameSign);
      towerNameSign.setPlaced(true);
      towerNameSign.setPosition(0, TowerConsts.LOBBY_FLOOR);

      worldSize.y = Math.max(worldSize.y, neighborGameGrid.getWorldSize().y);
      worldSize.x = neighborGameGrid.getWorldBounds().x + neighborGameGrid.getWorldBounds().width;

      gameLayers.add(neighborGameGrid.getRenderer());
      gameLayers.add(neighborGameGrid);
    }

    for (GameLayer gameLayer : gameLayers) {
      if (gameLayer instanceof RespondsToWorldSizeChange) {
        ((RespondsToWorldSizeChange) gameLayer).updateWorldSize(worldSize);
      }
    }

    cameraController.updateCameraConstraints(worldSize);
    camera.position.set(worldSize.x / 2 - Gdx.graphics.getWidth() / 2, TowerConsts.GROUND_HEIGHT, 0f);
    fetchingLabel.markToRemove(true);
    fetchingNeighbors = false;
  }

  @Override
  public void pause() {
    InputSystem.instance().removeInputProcessor(gestureDetector);
    InputSystem.instance().setGestureDelegator(null);
  }

  @Override
  public void resume() {
    InputSystem.instance().addInputProcessor(gestureDetector, 100);
    InputSystem.instance().setGestureDelegator(gestureDelegater);
    InputSystem.instance().switchTool(GestureTool.PICKER, null);
  }

  @Override
  public void render(float deltaTime) {
    SpriteBatch batch = getSpriteBatch();
    if (fetchingNeighbors) {
      batch.begin();
      droid.draw(batch);
      batch.end();
    } else {
      for (GameLayer gameLayer : gameLayers) {
        gameLayer.update(deltaTime);
        gameLayer.render(batch);
      }
    }
  }

  @Override
  public void dispose() {
    InputSystem.instance().unbind(TowerConsts.NEGATIVE_BUTTON_KEYS, goBackHomeCallback);
  }

  private InputCallback goBackHomeCallback = new InputCallback() {
    @Override
    public boolean run(float timeDelta) {
      TowerGame.popScene();
      return true;
    }
  };
}
