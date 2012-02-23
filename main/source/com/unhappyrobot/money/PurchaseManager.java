package com.unhappyrobot.money;

import com.badlogic.gdx.Gdx;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.gui.Dialog;
import com.unhappyrobot.gui.OnClickCallback;
import com.unhappyrobot.gui.ResponseType;
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

    if (gridObjectType.getCoins() != 0 && Player.instance().getCoins() < gridObjectType.getCoins()) {
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
        Player.instance().addCurrency(1000);
        dialog.dismiss();
      }
    }).addButton(ResponseType.NEGATIVE, "No thanks!", new OnClickCallback() {
      @Override
      public void onClick(Dialog dialog) {
        dialog.dismiss();
      }
    }).centerOnScreen().show();
  }

  public void makePurchase() {
    Gdx.app.log(LOG_TAG, "Made purchase: " + gridObjectType.getName());
    Player player = Player.instance();

    player.subtractCurrency(gridObjectType.getCoins());
    player.addExperience(gridObjectType.getExperienceAward());
    purchaseCount++;
  }
}
