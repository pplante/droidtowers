/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.happydroids.droidtowers.TowerConsts;

public enum AchievementThing {
  MONEY(TowerConsts.CURRENCY_SYMBOL),
  HOTEL_ROOM("Hotel Room"),
  COMMERCIAL_SPACE("Commercial Space"),
  HOUSING("Housing"),
  MAIDS_OFFICE("Maid's Office"),
  JANITORS_CLOSET("Janitor's Closet"),
  PROVIDER_TYPE(null),
  OBJECT_TYPE(null);

  public final String displayString;

  private AchievementThing(String displayString) {
    this.displayString = displayString;
  }
}
