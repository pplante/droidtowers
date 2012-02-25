package com.unhappyrobot.gamestate.actions;

import com.unhappyrobot.actions.TimeDelayedAction;
import com.unhappyrobot.grid.GameGrid;

public abstract class GameStateAction extends TimeDelayedAction {
  protected final GameGrid gameGrid;

  public GameStateAction(GameGrid gameGrid, float frequency, boolean shouldRepeat) {
    super(frequency, shouldRepeat);
    this.gameGrid = gameGrid;
  }

  public GameStateAction(GameGrid gameGrid, float frequency) {
    this(gameGrid, frequency, true);
  }
}
