package com.unhappyrobot.money;

import com.badlogic.gdx.Gdx;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.gui.Dialog;
import com.unhappyrobot.gui.OnClickCallback;
import com.unhappyrobot.types.GridObjectType;

public class PurchaseManager {
  public static final String LOG_TAG = "PurchaseManager";
  private GridObjectType gridObjectType;
  private int purchaseCount;

  public PurchaseManager(GridObjectType gridObjectType) {
    this.gridObjectType = gridObjectType;
    purchaseCount = 0;
  }

  public boolean canPurchase() {
    Gdx.app.log(LOG_TAG, "Checking purchase: " + gridObjectType.getName());
    if (purchaseCount > 0 && !gridObjectType.continuousPlacement()) {
      Gdx.app.log(LOG_TAG, "Cannot continuously purchase: " + gridObjectType.getName());
      return false;
    }

    if (gridObjectType.getCoins() != 0 && Player.getInstance().getCoins() < gridObjectType.getCoins()) {
      displayCurrencyDialog();
      return false;
    } else if (gridObjectType.getGold() != 0 && Player.getInstance().getGold() < gridObjectType.getGold()) {
      displayCurrencyDialog();
      return false;
    }

    Gdx.app.log(LOG_TAG, "Allowing purchase: " + gridObjectType.getName());
    return true;
  }

  private void displayCurrencyDialog() {
    Gdx.app.log(LOG_TAG, "Out of money for purchase: " + gridObjectType.getName());

    new Dialog().setTitle("Not enough money :(").setMessage("Would you like to purchase more monies?").addButton("Yes", new OnClickCallback() {
      @Override
      public void onClick(Dialog dialog) {
        Player.getInstance().addCurrency(1000, 1);
        dialog.dismiss();
      }
    }).addButton("No thanks!", new OnClickCallback() {
      @Override
      public void onClick(Dialog dialog) {
        dialog.dismiss();
      }
    }).centerOnScreen().show();
  }

  public void makePurchase() {
    Gdx.app.log(LOG_TAG, "Made purchase: " + gridObjectType.getName());
    Player player = Player.getInstance();

    player.subtractCurrency(gridObjectType.getCoins(), gridObjectType.getGold());
    purchaseCount++;
  }
}
