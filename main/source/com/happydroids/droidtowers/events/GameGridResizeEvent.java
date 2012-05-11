/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.grid.GameGrid;

public class GameGridResizeEvent {
  public final GameGrid gameGrid;
  public final boolean copyGridPositions;

  public GameGridResizeEvent(GameGrid gameGrid, boolean copyGridPositions) {
    this.copyGridPositions = copyGridPositions;
    if (gameGrid == null) {
      throw new RuntimeException("GameGridResizeEvent cannot be created with out a valid GameGrid");
    }

    this.gameGrid = gameGrid;
  }
}
