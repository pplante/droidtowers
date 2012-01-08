package com.unhappyrobot.entities;

public class Player {
  private static Player instance;

  private long coins;
  private long gold;
  private long experience;

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

  public long getCoins() {
    return coins;
  }

  public long getGold() {
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

  public void addExperience(int exp) {
    experience = exp;
  }

  public long getExperience() {
    return experience;
  }
}
