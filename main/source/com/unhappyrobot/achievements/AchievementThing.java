/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.achievements;

import com.unhappyrobot.TowerConsts;

public enum AchievementThing {
  MONEY(TowerConsts.CURRENCY_SYMBOL),
  HOTEL_ROOM("Hotel Room"),
  COMMERCIAL_SPACE("Commercial Space"),
  MAIDS_OFFICE("Maid's Office"),
  JANITORS_CLOSET("Janitor's Closet");

  public final String displayString;

  private AchievementThing(String displayString) {
    this.displayString = displayString;
  }
}
