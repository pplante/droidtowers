package com.unhappyrobot.entities;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Player {
  @JsonIgnore
  private static Player instance;

  private long coins;
  private long gold;
  private long experience;
  private int population;

  public static Player getInstance() {
    if (instance == null) {
      instance = new Player();
    }

    return instance;
  }

  private Player() {
    coins = 4000;
    gold = 1;
    population = 0;
  }

  public static void setInstance(Player newInstance) {
    Player.instance = newInstance;
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

  public int getPopulation() {
    return population;
  }

  public void setPopulation(int population) {
    this.population = population;
  }
}
