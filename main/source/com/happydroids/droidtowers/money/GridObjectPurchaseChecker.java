/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.money;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.HeadsUpDisplay;
import com.happydroids.droidtowers.input.GestureTool;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.types.GridObjectType;

public class GridObjectPurchaseChecker {
  public static final String LOG_TAG = GridObjectPurchaseChecker.class.getSimpleName();

  private final GameGrid gameGrid;
  private GridObjectType gridObjectType;
  private int numPurchases;

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
    if (MathUtils.random(10) % 5 == 0) {
      new CousinVinnieLoanDialog(gameGrid).show();
    } else {
      HeadsUpDisplay.showToast("You do not have enough money for this purchase.");
    }
  }

  public void makePurchase() {
    Gdx.app.log(LOG_TAG, "Made purchase: " + gridObjectType.getName());
    Player player = Player.instance();

    player.subtractCurrency(gridObjectType.getCoins());
    player.addExperience(gridObjectType.getExperienceAward());
    numPurchases += 1;

    if (!gridObjectType.allowContinuousPurchase()) {
      InputSystem.instance().switchTool(GestureTool.PICKER, null);
    }
  }
}
