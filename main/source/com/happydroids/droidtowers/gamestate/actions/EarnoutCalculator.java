/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.grid.GameGrid;

public class EarnoutCalculator extends GameGridAction {
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
