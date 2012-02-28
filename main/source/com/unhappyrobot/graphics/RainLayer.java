package com.unhappyrobot.graphics;

import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.Rain;
import com.unhappyrobot.grid.GameGrid;

public class RainLayer extends GameLayer {
  private final GameGrid gameGrid;

  public RainLayer(GameGrid gameGrid) {
    this.gameGrid = gameGrid;

    addChild(new Rain(gameGrid));
    addChild(new Rain(gameGrid));
  }
}
