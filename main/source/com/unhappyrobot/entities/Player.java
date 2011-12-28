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
    coins = 400;
    gold = 1;
  }

  public int getCoins() {
    return coins;
  }

  public int getGold() {
    return gold;
  }

  public void subtractCurrency(int coins, int gold) {
    this.coins -= coins;
    this.gold -= gold;
  }

  public void addCurrency(int coins, int gold) {
    this.coins += coins;
    this.gold += gold;
  }
}
