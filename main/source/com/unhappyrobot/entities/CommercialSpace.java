package com.unhappyrobot.entities;

import com.unhappyrobot.types.CommercialType;
import com.unhappyrobot.utils.Random;

public class CommercialSpace extends Room {
  private int attractedPopulation;
  private int jobsFilled;
  private long lastJobUpdateTime;
  private static final long JOB_UPDATE_FREQUENCY = 10000;

  public CommercialSpace(CommercialType commercialType, GameGrid gameGrid) {
    super(commercialType, gameGrid);
  }

  @Override
  public void update(float deltaTime) {
    if (shouldUpdate()) {
      CommercialType commercialType = (CommercialType) getGridObjectType();
      int populationAttraction = commercialType.getPopulationAttraction();
      attractedPopulation = Random.randomInt(populationAttraction / 2, populationAttraction);
    }
  }

  public int getJobsFilled() {
    return jobsFilled;
  }

  public int getAttractedPopulation() {
    return attractedPopulation;
  }

  public void calculateJobs(int totalPopulation, int currentJobsFilled) {
    if (lastJobUpdateTime + JOB_UPDATE_FREQUENCY < System.currentTimeMillis()) {
      lastJobUpdateTime = System.currentTimeMillis();
      CommercialType commercialType = (CommercialType) getGridObjectType();

      if (totalPopulation > commercialType.getPopulationRequired()) {
        jobsFilled = Random.randomInt(attractedPopulation, commercialType.getJobsProvided());
      }
    }
  }
}
