/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

public enum DifficultyLevel {
  EASY(500000),
  MEDIUM(350000),
  HARD(100000);
  private final int startingMoney;


  DifficultyLevel(int startingMoney) {
    this.startingMoney = startingMoney;
  }

  public int getStartingMoney() {
    return startingMoney;
  }
}
