package com.unhappyrobot.achievements;

public enum RewardType {
  GIVE("Received"),
  UNLOCK("Unlocked");

  public final String displayString;

  private RewardType(String displayString) {
    this.displayString = displayString;
  }
}
