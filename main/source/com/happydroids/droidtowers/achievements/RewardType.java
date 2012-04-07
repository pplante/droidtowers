/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

public enum RewardType {
  GIVE("Received"),
  UNLOCK("Unlocked");

  public final String displayString;

  private RewardType(String displayString) {
    this.displayString = displayString;
  }
}
