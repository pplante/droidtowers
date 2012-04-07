/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

public enum DifficultyLevel {
  EASY(50000),
  MEDIUM(35000),
  HARD(10000);
  private final int startingMoney;


  DifficultyLevel(int startingMoney) {
    this.startingMoney = startingMoney;
  }

  public int getStartingMoney() {
    return startingMoney;
  }
}
