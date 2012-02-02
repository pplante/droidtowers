package com.unhappyrobot.gamestate.actions;

import com.unhappyrobot.actions.TimeDelayedAction;
import com.unhappyrobot.entities.GameGrid;

public abstract class GameStateAction extends TimeDelayedAction {
  protected final GameGrid gameGrid;

  public GameStateAction(GameGrid gameGrid, long frequency, boolean shouldRepeat) {
    super(frequency, shouldRepeat);
    this.gameGrid = gameGrid;
  }

  public GameStateAction(GameGrid gameGrid, long frequency) {
    this(gameGrid, frequency, false);
  }
}
