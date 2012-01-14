package com.unhappyrobot.gamestate.actions;

import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.Player;

public class EarnoutCalculator extends GameStateAction {
  public EarnoutCalculator(GameGrid gameGrid, long frequency) {
    super(gameGrid, frequency);
  }

  @Override
  public void run() {
    int coinsEarned = 0;
    for (GridObject object : gameGrid.getObjects()) {
      coinsEarned += object.getCoinsEarned();
    }

    System.out.println(String.format("Player earned: %d coins", coinsEarned));
    Player.getInstance().addCurrency(coinsEarned);
  }
}
