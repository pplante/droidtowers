/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.happydroids.droidtowers.actions.TimeDelayedAction;
import com.happydroids.droidtowers.grid.GameGrid;

public abstract class GameGridAction extends TimeDelayedAction {
  protected final GameGrid gameGrid;

  public GameGridAction(GameGrid gameGrid, float frequency, boolean shouldRepeat) {
    super(frequency, shouldRepeat);
    this.gameGrid = gameGrid;
  }

  public GameGridAction(GameGrid gameGrid, float frequency) {
    this(gameGrid, frequency, true);
  }
}
