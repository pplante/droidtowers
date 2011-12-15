package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.utils.Random;

import java.util.Iterator;

public class CloudLayer extends GameLayer {
  public static final int HORIZONTAL_PADDING = 20;
  public static final int CLOUD_SPAWN_DELAY = 2;
  private final TextureAtlas textureAtlas;
  private float timeSinceSpawn;
  private Vector2 worldSize;

  public CloudLayer(Vector2 worldSize) {
    super();
    this.worldSize = worldSize;

    textureAtlas = new TextureAtlas(Gdx.files.internal("backgrounds/background1.txt"));

    spawnCloudNow();
  }

  @Override
  public void update(float timeDelta) {
    super.update(timeDelta);

    removeDeadClouds();
    spawnCloudWhenItsTime(timeDelta);
  }

  private void spawnCloudWhenItsTime(float timeDelta) {
    timeSinceSpawn += timeDelta;

    if (timeSinceSpawn > CLOUD_SPAWN_DELAY && gameObjects.size() < 10) {
      timeSinceSpawn = 0;

      spawnCloudNow();
    }
  }

  private void spawnCloudNow() {
    Sprite sprite = textureAtlas.createSprite("cloud", Random.randomInt(1) + 1);
    GameObject cloud = new GameObject(-sprite.getWidth() - HORIZONTAL_PADDING, Random.randomInt(400, 550), Math.max(0.4f, Random.randomFloat()));
    cloud.setVelocity(Random.randomInt(5, 25), 0);
    cloud.setSprite(sprite);

    addChild(cloud);
  }

  private void removeDeadClouds() {
    Iterator<GameObject> gameObjectIterator = gameObjects.iterator();
    while (gameObjectIterator.hasNext()) {
      GameObject cloud =  gameObjectIterator.next();
      if (cloud.position.x >= worldSize.x + HORIZONTAL_PADDING) {
        gameObjectIterator.remove();
      }
    }
  }
}
