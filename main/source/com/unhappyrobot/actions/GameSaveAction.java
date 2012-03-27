package com.unhappyrobot.actions;

import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.gamestate.GameState;

public class GameSaveAction extends TimeDelayedAction {
  private final GameState gameState;

  public GameSaveAction(GameState gameState) {
    super(TowerConsts.GAME_SAVE_FREQUENCY);
    this.gameState = gameState;
    reset();
  }

  @Override
  public void run() {
    gameState.saveGame();
  }
}
