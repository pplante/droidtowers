package com.unhappyrobot.gamestate.actions;

import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.grid.GameGrid;

public class EarnoutCalculator extends GameStateAction {
  public EarnoutCalculator(GameGrid gameGrid, float frequency) {
    super(gameGrid, frequency);
  }

  @Override
  public void run() {
    int coinsEarned = 0;
    for (GridObject object : gameGrid.getObjects()) {
      coinsEarned += object.getCoinsEarned();
    }

    System.out.println(String.format("Player earned: %d coins", coinsEarned));
    Player.instance().addCurrency(coinsEarned);
  }
}
