package com.unhappyrobot.gamestate.actions;

import com.unhappyrobot.actions.TimeDelayedAction;
import com.unhappyrobot.grid.GameGrid;

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
