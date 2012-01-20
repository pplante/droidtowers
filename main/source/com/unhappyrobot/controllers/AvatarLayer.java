package com.unhappyrobot.controllers;

import com.unhappyrobot.entities.Avatar;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GameLayer;

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
}
