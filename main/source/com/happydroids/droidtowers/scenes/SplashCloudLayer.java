/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.entities.CloudLayer;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.utils.Random;

public class SplashCloudLayer extends CloudLayer {
  public SplashCloudLayer() {
    super(null);

    CLOUD_SPAWN_MIN = 0;
    CLOUD_SPAWN_MAX = 1;

    worldSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);

    for (int i = 0; i < MAX_ACTIVE_CLOUDS; i++) {
      spawnCloudNow(false);
    }

    for (GameObject gameObject : gameObjects) {
      gameObject.setPosition(Random.randomInt(0, worldSize.x), Random.randomInt(0, worldSize.y));
    }
  }
}
