package com.unhappyrobot.money;

import com.unhappyrobot.entities.Player;
import com.unhappyrobot.types.GridObjectType;

public class PurchaseCheck {
  public boolean canPurchase(GridObjectType gridObjectType) {
    if (!gridObjectType.continuousPlacement()) {
      return false;
    }

    if (gridObjectType.getCoins() != 0 && Player.getInstance().getCoins() < gridObjectType.getCoins()) {
      return false;
    } else if (gridObjectType.getGold() != 0 && Player.getInstance().getGold() < gridObjectType.getGold()) {
      return false;
    }

    return true;
  }
}
