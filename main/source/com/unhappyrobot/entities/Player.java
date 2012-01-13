package com.unhappyrobot.entities;

import com.unhappyrobot.types.CommercialType;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Set;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Player {
  @JsonIgnore
  private static Player instance;
  private static final long EARN_OUT_INTERVAL_MILLIS = 5000;

  private long coins;
  private long gold;
  private long experience;
  private int population;
  private int jobsFilled;
  private int jobsProvided;
  private long lastEarnoutTime;
  private int attractedPopulation;

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

  public int getResidency() {
    return population;
  }

  public void setResidency(int population) {
    this.population = population;
  }

  public void setAttractedPopulation(int attractedPopulation) {
    this.attractedPopulation = attractedPopulation;
  }

  public int getAttractedPopulation() {
    return attractedPopulation;
  }

  public void setJobsFilled(int jobsFilled) {
    this.jobsFilled = jobsFilled;
  }

  public void setJobsProvided(int jobsProvided) {
    this.jobsProvided = jobsProvided;
  }

  public int getJobsProvided() {
    return jobsProvided;
  }

  public int getJobsFilled() {
    return jobsFilled;
  }

  public void update(float deltaTime, GameGrid gameGrid) {
    Set<GridObject> gridObjects = gameGrid.getObjects();

    if ((lastEarnoutTime + EARN_OUT_INTERVAL_MILLIS) < System.currentTimeMillis()) {
      lastEarnoutTime = System.currentTimeMillis();

      int coinsEarned = 0;
      int goldEarned = 0;
      for (GridObject object : gridObjects) {
        coinsEarned += object.getCoinsEarned();
        goldEarned += object.getGoldEarned();
      }
      System.out.println(String.format("Player earned: %d coins and %d gold", coinsEarned, goldEarned));
      addCurrency(coinsEarned, goldEarned);
    }

    int currentResidency = 0;
    int currentJobsFilled = 0;
    int maxJobsProvided = 0;
    int attractedPopulation = 0;
    for (GridObject gridObject : gridObjects) {
      if (gridObject.getClass().equals(Room.class)) {
        currentResidency += ((Room) gridObject).getCurrentResidency();
      } else if (gridObject.getClass().equals(CommercialSpace.class)) {
        attractedPopulation += ((CommercialSpace) gridObject).getAttractedPopulation();
      }
    }

    int totalPopulation = currentResidency + attractedPopulation;

    for (GridObject gridObject : gridObjects) {
      if (gridObject instanceof CommercialSpace) {
        CommercialSpace commercialSpace = (CommercialSpace) gridObject;
        commercialSpace.calculateJobs(totalPopulation, currentJobsFilled);
        currentJobsFilled += commercialSpace.getJobsFilled();
        maxJobsProvided += ((CommercialType) commercialSpace.getGridObjectType()).getJobsProvided();
      }
    }

    setResidency(currentResidency);
    setAttractedPopulation(attractedPopulation);
    setJobsFilled(currentJobsFilled);
    setJobsProvided(maxJobsProvided);
  }
}
