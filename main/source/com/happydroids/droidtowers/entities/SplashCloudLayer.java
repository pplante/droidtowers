/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.google.common.collect.Iterables;
import com.happydroids.droidtowers.utils.Random;

public class SplashCloudLayer extends WidgetGroup {
  public static final int CLOUD_SPAWN_DELAY = 2;
  public static final double CLOUD_SPAWN_MIN = 0.4;
  public static final double CLOUD_SPAWN_MAX = 0.8;
  public static final int MAX_ACTIVE_CLOUDS = 6;
  private float timeSinceSpawn;
  protected Vector2 worldSize;
  private final Array<TextureAtlas.AtlasRegion> cloudRegions;

  public SplashCloudLayer(Stage stage, Array<TextureAtlas.AtlasRegion> cloudRegions) {
    super();
    this.setStage(stage);
    worldSize = new Vector2(stage.getWidth(), stage.getHeight());
    this.cloudRegions = cloudRegions;


    int cloudsToSpawn = MAX_ACTIVE_CLOUDS;
    for (int i = 0; i < cloudsToSpawn; i++) {
      spawnCloudNow(true);
    }

    float cloudWidths = 0;
    for (Actor cloud : getChildren()) {
      cloudWidths += ((Image) cloud).getImageWidth();
    }

    float spawnDistanceX = (worldSize.x - cloudWidths) / cloudsToSpawn;
    for (int i = 0; i < getChildren().size; i++) {
      getChildren().get(i).setX(spawnDistanceX * i);
    }
  }

  protected Image spawnCloudNow(boolean spawnOnScreen) {
    if (worldSize.x == 0 || worldSize.y == 0) {
      return null;
    }

    final Image cloud = new Image(Iterables.get(cloudRegions, MathUtils.random(cloudRegions.size - 1)));
    cloud.setScaling(Scaling.fit);
    cloud.setHeight(Math.min(cloud.getHeight(), getStage().getHeight() * 0.18f));
    if (spawnOnScreen) {
      cloud.setX(Random.randomInt(0, worldSize.x));
      cloud.setY(Random.randomInt(worldSize.y * CLOUD_SPAWN_MIN, worldSize.y * CLOUD_SPAWN_MAX));
    } else {
      cloud.setX(-cloud.getWidth());
      cloud.setY(Random.randomInt(worldSize.y * CLOUD_SPAWN_MIN, worldSize.y * CLOUD_SPAWN_MAX));
    }

    cloud.getColor().a = 0f;
    cloud.addAction(makeFlyAction(cloud));
    addActor(cloud);

    return cloud;
  }

  private Action makeFlyAction(final Image cloud) {
    Action moveTo = Actions.moveTo(worldSize.x, cloud.getY(), MathUtils.random(30f, 45f));
    RunnableAction finished = Actions.run(new Runnable() {
      @Override
      public void run() {
        cloud.remove();
      }
    });

    return Actions.sequence(Actions.fadeIn(0.15f), moveTo, finished);
  }

  public void update(float deltaTime) {
    timeSinceSpawn += deltaTime;

    if (timeSinceSpawn >= CLOUD_SPAWN_DELAY) {
      timeSinceSpawn = 0;
      spawnCloudNow(false);
    }
  }

  @Override
  public float getPrefWidth() {
    return getStage().getWidth();
  }

  @Override
  public float getPrefHeight() {
    return getStage().getHeight();
  }
}
