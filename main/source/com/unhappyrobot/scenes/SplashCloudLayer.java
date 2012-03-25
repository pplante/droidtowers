package com.unhappyrobot.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.CloudLayer;
import com.unhappyrobot.entities.GameObject;
import com.unhappyrobot.utils.Random;

public class SplashCloudLayer extends CloudLayer {
  public SplashCloudLayer() {
    super(null, null);

    CLOUD_SPAWN_MIN = 0;
    CLOUD_SPAWN_MAX = 1;

    worldSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);

    for (int i = 0; i < MAX_ACTIVE_CLOUDS; i++) {
      spawnCloudNow();
    }

    for (GameObject gameObject : gameObjects) {
      gameObject.setPosition(Random.randomInt(0, worldSize.x), Random.randomInt(0, worldSize.y));
    }
  }
}
