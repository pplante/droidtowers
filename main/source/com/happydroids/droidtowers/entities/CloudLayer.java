/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.WeatherService;
import com.happydroids.droidtowers.events.RespondsToWorldSizeChange;
import com.happydroids.droidtowers.events.WeatherStateChangeEvent;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.tween.GameObjectAccessor;
import com.happydroids.droidtowers.tween.TweenSystem;
import com.happydroids.droidtowers.utils.Random;

import java.util.ArrayList;
import java.util.List;

public class CloudLayer extends GameLayer<GameObject> implements RespondsToWorldSizeChange {
  public static final int CLOUD_SPAWN_DELAY = 2;
  public static final String CLOUDS_ATLAS = "backgrounds/clouds.txt";
  public double CLOUD_SPAWN_MIN = 0.35;
  public double CLOUD_SPAWN_MAX = 0.98;
  public static final int MAX_ACTIVE_CLOUDS = 40;
  private final TextureAtlas textureAtlas;
  private float timeSinceSpawn;
  protected Vector2 worldSize;
  private List<GameObject> deadClouds;
  private final int numberOfCloudTypes;
  private final WeatherService weatherService;

  public CloudLayer(WeatherService weatherService) {
    super();
    this.weatherService = weatherService;

    textureAtlas = TowerAssetManager.textureAtlas(CLOUDS_ATLAS);
    numberOfCloudTypes = textureAtlas.getRegions().size;
    deadClouds = new ArrayList<GameObject>(5);

    if (weatherService != null) {
      weatherService.events().register(this);
    }
  }

  @Override
  public void update(float timeDelta) {
    super.update(timeDelta);

    removeDeadClouds();
    spawnCloudWhenItsTime(timeDelta);
  }

  private void spawnCloudWhenItsTime(float timeDelta) {
    timeSinceSpawn += timeDelta;

    if (timeSinceSpawn > CLOUD_SPAWN_DELAY && gameObjects.size < MAX_ACTIVE_CLOUDS) {
      timeSinceSpawn = 0;

      spawnCloudNow(false);
    }
  }

  protected void spawnCloudNow(boolean spawnOnScreen) {
    if (worldSize.x == 0 || worldSize.y == 0) {
      return;
    }

    GameObject cloud;
    if (!deadClouds.isEmpty()) {
      cloud = deadClouds.remove(0);
    } else {
      cloud = new GameObject(textureAtlas.findRegion("cloud", Random.randomInt(1, numberOfCloudTypes)));
    }

    if (weatherService != null) {
      cloud.setColor(weatherService.currentState().cloudColor);
    }

    if (spawnOnScreen) {
      cloud.setPosition(Random.randomInt(-cloud.getWidth(), worldSize.x), Random.randomInt(worldSize.y * CLOUD_SPAWN_MIN, worldSize.y * CLOUD_SPAWN_MAX));
    } else {
      cloud.setPosition(-(cloud.getWidth() + Display.getBiggestScreenDimension()), Random.randomInt(worldSize.y * CLOUD_SPAWN_MIN, worldSize.y * CLOUD_SPAWN_MAX));
    }
    cloud.setVelocity(Random.randomInt(5, 25), 0);
    cloud.setScale(2f);

    cloud.setOpacity(0);
    Tween.to(cloud, GameObjectAccessor.OPACITY, 2000).target(1.0f).start(TweenSystem.manager());

    addChild(cloud);
  }

  private void removeDeadClouds() {
    for (final GameObject cloud : gameObjects) {
      if (cloud.getX() >= worldSize.x + Display.getBiggestScreenDimension()) {
        Tween.to(cloud, GameObjectAccessor.OPACITY, 2000)
                .target(0f)
                .setCallback(new TweenCallback() {
                  public void onEvent(int eventType, BaseTween source) {
                    cloud.markToRemove(true);
                  }
                })
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(TweenSystem.manager());
      }
    }
  }

  @Subscribe
  public void WeatherService_onWeatherChange(WeatherStateChangeEvent event) {
    Color cloudColor = null;

    switch (weatherService.currentState()) {
      case RAINING:
        cloudColor = Color.DARK_GRAY;
        break;

      case SUNNY:
        cloudColor = Color.WHITE;
        break;
    }

    if (cloudColor != null) {
      for (GameObject cloud : gameObjects) {
        Tween.to(cloud, GameObjectAccessor.COLOR, TowerConsts.WEATHER_SERVICE_STATE_CHANGE_DURATION)
                .target(cloudColor.r, cloudColor.g, cloudColor.b, cloudColor.a)
                .start(TweenSystem.manager());
      }
    }
  }

  public void setWorldSize(Vector2 worldSize) {

  }

  @Override
  public void updateWorldSize(Vector2 worldSize) {
    this.worldSize = worldSize;

    if (gameObjects.size == 0) {
      for (int i = 0; i < MAX_ACTIVE_CLOUDS; i++) {
        spawnCloudNow(true);
      }
    }
  }
}
