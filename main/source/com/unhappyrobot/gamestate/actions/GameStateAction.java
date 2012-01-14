package com.unhappyrobot.gamestate.actions;

import com.unhappyrobot.actions.TimeDelayedAction;
import com.unhappyrobot.entities.GameGrid;

public abstract class GameStateAction extends TimeDelayedAction {
  protected final GameGrid gameGrid;

  public GameStateAction(GameGrid gameGrid, long frequency) {
    super(frequency);
    this.gameGrid = gameGrid;
  }
}
