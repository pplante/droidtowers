/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.actions;

import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.gamestate.GameState;

public class GameSaveAction extends TimeDelayedAction {
  private final GameState gameState;

  public GameSaveAction(final GameState gameState) {
    super(TowerConsts.GAME_SAVE_FREQUENCY);
    this.gameState = gameState;
    reset();
  }

  @Override
  public void run() {
    gameState.saveGame(false);
  }
}
