/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.money;

import com.badlogic.gdx.Gdx;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.types.GridObjectType;

public class GridObjectPurchaseChecker {
  public static final String LOG_TAG = GridObjectPurchaseChecker.class.getSimpleName();

  private final GameGrid gameGrid;
  private GridObjectType gridObjectType;

  public GridObjectPurchaseChecker(GameGrid gameGrid, GridObjectType gridObjectType) {
    this.gameGrid = gameGrid;
    this.gridObjectType = gridObjectType;
  }

  public boolean canPurchase() {
    Gdx.app.log(LOG_TAG, "Checking purchase: " + gridObjectType.getName());
    if (gridObjectType.getCoins() != 0 && Player.instance().getCoins() < gridObjectType.getCoins()) {
      displayCurrencyDialog();
      return false;
    }

    Gdx.app.log(LOG_TAG, "Allowing purchase: " + gridObjectType.getName());
    return true;
  }

  private void displayCurrencyDialog() {
    Gdx.app.log(LOG_TAG, "Out of money for purchase: " + gridObjectType.getName());
    new CousinVinnieLoanDialog(gameGrid).show();
  }

  public void makePurchase() {
    Gdx.app.log(LOG_TAG, "Made purchase: " + gridObjectType.getName());
    Player player = Player.instance();

    player.subtractCurrency(gridObjectType.getCoins());
    player.addExperience(gridObjectType.getExperienceAward());
  }
}
