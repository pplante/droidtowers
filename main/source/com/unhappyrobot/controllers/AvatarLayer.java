package com.unhappyrobot.controllers;

import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.Avatar;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.GameObject;

public class AvatarLayer extends GameLayer {
  private static AvatarLayer instance;
  private final GameGrid gameGrid;
  private static final int MAX_AVATARS = 20;
  private static final float SPAWN_RATE = 0.5f;
  private float timeSinceLastSpawn;

  public static void initialize(GameGrid gameGrid) {
    instance = new AvatarLayer(gameGrid);
  }

  public static AvatarLayer instance() {
    return instance;
  }

  AvatarLayer(GameGrid gameGrid) {
    this.gameGrid = gameGrid;
    timeSinceLastSpawn = SPAWN_RATE;
    setTouchEnabled(true);
  }

  public GameGrid getGameGrid() {
    return gameGrid;
  }

  @Override
  public void update(float timeDelta) {
    super.update(timeDelta);

    timeSinceLastSpawn += timeDelta;

    if (gameObjects.size() < MAX_AVATARS && timeSinceLastSpawn >= SPAWN_RATE) {
      timeSinceLastSpawn = 0;
      addChild(new Avatar(this));
    }
  }

  @Override
  public boolean tap(Vector2 worldPoint, int count) {
    for (GameObject gameObject : gameObjects) {
      if (gameObject.getBoundingRectangle().contains(worldPoint.x, worldPoint.y)) {
        Avatar avatar = (Avatar) gameObject;
        avatar.tap(worldPoint, count);

        return true;
      }
    }

    return false;
  }
}
