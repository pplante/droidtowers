package com.unhappyrobot.entities;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Player {
  @JsonIgnore
  private static Player instance;

  private long coins;
  private long experience;
  private int jobsFilled;
  private int jobsMax;
  private int populationAttracted;
  private int maxPopulation;
  private int populationMax;
  private int populationResidency;

  public static Player instance() {
    if (instance == null) {
      instance = new Player();
    }

    return instance;
  }

  private Player() {
    coins = 4000;
  }

  public static void setInstance(Player newInstance) {
    Player.instance = newInstance;
  }

  public long getCoins() {
    return coins;
  }

  public void subtractCurrency(int coins) {
    this.coins -= coins;
  }

  public void addCurrency(int coins) {
    this.coins += coins;
  }

  public void addExperience(int exp) {
    experience = exp;
  }

  public long getExperience() {
    return experience;
  }

  public int getPopulationResidency() {
    return populationResidency;
  }

  public int getPopulationAttracted() {
    return populationAttracted;
  }

  public int getJobsMax() {
    return this.jobsMax;
  }

  public int getJobsFilled() {
    return jobsFilled;
  }

  @JsonIgnore
  public int getTotalPopulation() {
    return populationResidency + populationAttracted;
  }

  public int getMaxPopulation() {
    return populationMax;
  }

  public void setPopulationMax(int populationMax) {
    this.populationMax = populationMax;
  }

  public void setPopulationResidency(int populationResidency) {
    this.populationResidency = populationResidency;
  }

  public void setJobsMax(int jobsMax) {
    this.jobsMax = jobsMax;
  }

  public void setPopulationAttracted(int populationAttracted) {
    this.populationAttracted = populationAttracted;
  }

  public void setJobsFilled(int jobsFilled) {
    this.jobsFilled = jobsFilled;
  }
}
