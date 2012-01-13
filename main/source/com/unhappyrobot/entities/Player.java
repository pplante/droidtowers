package com.unhappyrobot.entities;

import com.unhappyrobot.types.CommercialType;
import com.unhappyrobot.types.RoomType;
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
  private int residency;
  private int jobsFilled;
  private int jobsProvided;
  private long lastEarnoutTime;
  private int attractedPopulation;
  private int maxPopulation;

  public static Player getInstance() {
    if (instance == null) {
      instance = new Player();
    }

    return instance;
  }

  private Player() {
    coins = 4000;
    gold = 1;
    residency = 0;
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
    return residency;
  }

  public int getAttractedPopulation() {
    return attractedPopulation;
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


    residency = 0;
    attractedPopulation = 0;
    maxPopulation = 0;
    jobsFilled = 0;
    jobsProvided = 0;

    for (GridObject gridObject : gridObjects) {
      if (gridObject.getClass().equals(Room.class)) {
        Room room = (Room) gridObject;
        residency += room.getCurrentResidency();
        maxPopulation += ((RoomType) room.getGridObjectType()).getPopulationMax();
      } else if (gridObject.getClass().equals(CommercialSpace.class)) {
        CommercialSpace commercialSpace = (CommercialSpace) gridObject;
        CommercialType commercialType = (CommercialType) commercialSpace.getGridObjectType();
        attractedPopulation += commercialSpace.getAttractedPopulation();
        maxPopulation += commercialType.getPopulationAttraction();
        jobsFilled += commercialSpace.getJobsFilled();
        jobsProvided += commercialType.getJobsProvided();
      }
    }
  }

  @JsonIgnore
  public int getTotalPopulation() {
    return residency + attractedPopulation;
  }


  public int getMaxPopulation() {
    return maxPopulation;
  }
}
