/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.grid.GameGrid;

public class BudgetCalculator extends GameGridAction {
  public BudgetCalculator(GameGrid gameGrid, float frequency) {
    super(gameGrid, frequency);
    reset();
  }

  @Override
  public void run() {
    int coinsEarnedFromRent = 0;
    int coinsSpentOnUpkeep = 0;
    for (GridObject object : gameGrid.getObjects()) {
      coinsEarnedFromRent += object.getCoinsEarned();
      coinsSpentOnUpkeep += object.getUpkeepCost();
    }

    if (HappyDroidConsts.DEBUG)
      System.out.println(String.format("Income report: %d earned, %d spent on upkeep", coinsEarnedFromRent, coinsSpentOnUpkeep));

    Player.instance().addCurrency(coinsEarnedFromRent - coinsSpentOnUpkeep);
    Player.instance().setCurrentIncome(coinsEarnedFromRent);
    Player.instance().setCurrentExpenses(coinsSpentOnUpkeep);
  }
}
