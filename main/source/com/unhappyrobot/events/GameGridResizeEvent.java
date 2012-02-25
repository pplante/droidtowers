package com.unhappyrobot.events;

import com.unhappyrobot.grid.GameGrid;

public class GameGridResizeEvent {
  public final GameGrid gameGrid;

  public GameGridResizeEvent(GameGrid gameGrid) {
    if (gameGrid == null) {
      throw new RuntimeException("GameGridResizeEvent cannot be created with out a valid GameGrid");
    }

    this.gameGrid = gameGrid;
  }
}
