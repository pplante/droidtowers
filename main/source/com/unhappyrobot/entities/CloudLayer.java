package com.unhappyrobot.entities;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.Game;
import com.unhappyrobot.utils.Random;

import java.util.ArrayList;
import java.util.List;

public class CloudLayer extends GameLayer {
  public static final int PADDING = 20;
  public static final int CLOUD_SPAWN_DELAY = 2;
  private final TextureAtlas textureAtlas;
  private float timeSinceSpawn;
  private Vector2 worldSize;
  private List<GameObject> cloudsToRemove;

  public CloudLayer(Vector2 worldSize) {
    super();
    this.worldSize = worldSize;

    textureAtlas = new TextureAtlas(Gdx.files.internal("backgrounds/background1.txt"));
    cloudsToRemove = new ArrayList<GameObject>(5);

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

    float scale = Math.max(0.4f, Random.randomFloat());
    float cloudX = (sprite.getWidth() * scale) + PADDING;

    GameObject cloud = new GameObject(-cloudX, Random.randomInt(400, 550), scale);
    cloud.setVelocity(Random.randomInt(5, 25), 0);
    cloud.setSprite(sprite);
    cloud.setOpacity(0);

    Tween.to(cloud, GameObject.TWEEN_OPACITY, 2000, Linear.INOUT).target(1.0f).addToManager(Game.getTweenManager());

    addChild(cloud);
  }

  private void removeDeadClouds() {
    if (cloudsToRemove.size() > 0) {
      gameObjects.removeAll(cloudsToRemove);
      cloudsToRemove.clear();
    }

    for (final GameObject cloud : gameObjects) {
      if (cloud.position.x >= worldSize.x + PADDING) {
        Tween.to(cloud, GameObject.TWEEN_OPACITY, 2000, Linear.INOUT).target(0f).addCompleteCallback(new TweenCallback() {
          public void tweenEventOccured(Types types, Tween tween) {
            markForRemoval(cloud);
          }
        }).addToManager(Game.getTweenManager());
      }
    }
  }

  private void markForRemoval(GameObject cloud) {
    cloudsToRemove.add(cloud);
  }
}
