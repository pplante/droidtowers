/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.platform.Platform;

public class BudgetCalculator extends GameGridAction {
  public static final String TAG = BudgetCalculator.class.getSimpleName();

  public BudgetCalculator(GameGrid gameGrid, float frequency) {
    super(gameGrid, frequency);
    reset();
  }

  @Override
  public void run() {
    int coinsEarnedFromRent = 0;
    int coinsSpentOnUpkeep = 0;
    Array<GridObject> objects = gameGrid.getObjects();
    for (int i = 0, objectsSize = objects.size; i < objectsSize; i++) {
      GridObject object = objects.get(i);
      coinsEarnedFromRent += object.getCoinsEarned();
      coinsSpentOnUpkeep += object.getUpkeepCost();
    }

    if (!Platform.getPurchaseManager().hasPurchasedUnlimitedVersion()) {
      coinsEarnedFromRent *= 0.5f;
      coinsSpentOnUpkeep *= 0.5f;
    }

    if (HappyDroidConsts.DEBUG) {
      Gdx.app
              .debug(TAG, String.format("Income report: %d earned, %d spent on upkeep", coinsEarnedFromRent, coinsSpentOnUpkeep));
    }

    Player.instance().addCurrency(coinsEarnedFromRent - coinsSpentOnUpkeep);
    Player.instance().setCurrentIncome(coinsEarnedFromRent);
    Player.instance().setCurrentExpenses(coinsSpentOnUpkeep);
  }
}
