package com.unhappyrobot.entities;

public class Player {
  private int coins;
  private static Player instance;
  private int gold;

  public static Player getInstance() {
    if (instance == null) {
      instance = new Player();
    }

    return instance;
  }

  private Player() {

  }

  public int getCoins() {
    return coins;
  }

  public int getGold() {
    return gold;
  }
}
