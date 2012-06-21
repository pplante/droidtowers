/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.MoveTo;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.google.common.collect.Iterables;
import com.happydroids.droidtowers.utils.Random;

import java.util.List;

public class SplashCloudLayer extends Group {
  public static final int CLOUD_SPAWN_DELAY = 2;
  public static final double CLOUD_SPAWN_MIN = 0.4;
  public static final double CLOUD_SPAWN_MAX = 0.8;
  public static final int MAX_ACTIVE_CLOUDS = 6;
  private float timeSinceSpawn;
  protected Vector2 worldSize;
  private final List<TextureAtlas.AtlasRegion> cloudRegions;

  public SplashCloudLayer(Stage stage, List<TextureAtlas.AtlasRegion> cloudRegions) {
    super();
    this.stage = stage;
    worldSize = new Vector2(stage.width(), stage.height());
    this.cloudRegions = cloudRegions;


    int cloudsToSpawn = MAX_ACTIVE_CLOUDS;
    for (int i = 0; i < cloudsToSpawn; i++) {
      spawnCloudNow(true);
    }

    float cloudWidths = 0;
    for (Actor cloud : children) {
      cloudWidths += ((Image) cloud).getImageWidth();
    }

    float spawnDistanceX = (worldSize.x - cloudWidths) / cloudsToSpawn;
    for (int i = 0; i < children.size(); i++) {
      children.get(i).x = spawnDistanceX * i;
    }
  }

  protected Image spawnCloudNow(boolean spawnOnScreen) {
    if (worldSize.x == 0 || worldSize.y == 0) {
      return null;
    }

    final Image cloud = new Image(Iterables.get(cloudRegions, MathUtils.random(cloudRegions.size() - 1)), Scaling.fit);
    cloud.height = Math.min(cloud.getRegion().getRegionHeight(), stage.height() * 0.18f);
    if (spawnOnScreen) {
      cloud.x = Random.randomInt(0, worldSize.x);
      cloud.y = Random.randomInt(worldSize.y * CLOUD_SPAWN_MIN, worldSize.y * CLOUD_SPAWN_MAX);
    } else {
      cloud.x = -cloud.width;
      cloud.y = Random.randomInt(worldSize.y * CLOUD_SPAWN_MIN, worldSize.y * CLOUD_SPAWN_MAX);
    }

    cloud.action(makeFlyAction(cloud));
    addActor(cloud);

    return cloud;
  }

  private MoveTo makeFlyAction(final Image cloud) {
    MoveTo moveTo = MoveTo.$(worldSize.x, cloud.y, MathUtils.random(30f, 45f));
    moveTo.setCompletionListener(new OnActionCompleted() {
      @Override
      public void completed(Action action) {
        cloud.markToRemove(true);
      }
    });
    return moveTo;
  }

  public void update(float deltaTime) {
    timeSinceSpawn += deltaTime;

    if (timeSinceSpawn >= CLOUD_SPAWN_DELAY) {
      timeSinceSpawn = 0;
      spawnCloudNow(false);
    }
  }
}
