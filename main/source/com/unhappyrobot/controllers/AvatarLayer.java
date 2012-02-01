package com.unhappyrobot.controllers;

import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.Avatar;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.GameObject;

public class AvatarLayer extends GameLayer {
  private static AvatarLayer instance;
  private final GameGrid gameGrid;

  public static void initialize(GameGrid gameGrid) {
    instance = new AvatarLayer(gameGrid);
  }

  public static AvatarLayer instance() {
    return instance;
  }

  AvatarLayer(GameGrid gameGrid) {
    this.gameGrid = gameGrid;
    setTouchEnabled(true);
  }

  public GameGrid getGameGrid() {
    return gameGrid;
  }

  @Override
  public void update(float timeDelta) {
    if (gameObjects.size() == 0) {
      addChild(new Avatar(this));
    }

    super.update(timeDelta);
  }

  @Override
  public boolean tap(Vector2 worldPoint, int count) {
    for (GameObject gameObject : gameObjects) {
      if (gameObject.getBounds().containsPoint(worldPoint)) {
        Avatar avatar = (Avatar) gameObject;
        avatar.findCommercialSpace();

        return true;
      }
    }


    return false;
  }
}
