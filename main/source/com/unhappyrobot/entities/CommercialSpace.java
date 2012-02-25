package com.unhappyrobot.entities;

import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.types.CommercialType;
import com.unhappyrobot.utils.Random;

public class CommercialSpace extends Room {
  private int attractedPopulation;
  private int jobsFilled;
  private long lastJobUpdateTime;
  private int numVisitors;
  private long lastCleanedAt;

  public CommercialSpace(CommercialType commercialType, GameGrid gameGrid) {
    super(commercialType, gameGrid);
  }

  public void updateJobs() {
    jobsFilled = 0;
    if (isConnectedToTransport()) {
      CommercialType commercialType = (CommercialType) getGridObjectType();

      if (Player.instance().getTotalPopulation() > commercialType.getPopulationRequired()) {
        int jobsProvided = commercialType.getJobsProvided();
        if (jobsProvided > 0) {
          jobsFilled = Random.randomInt(jobsProvided / 2, jobsProvided);
        }
      }
    }
  }

  @Override
  public void updatePopulation() {
    attractedPopulation = 0;

    if (isConnectedToTransport()) {
      CommercialType commercialType = (CommercialType) getGridObjectType();
      int populationAttraction = commercialType.getPopulationAttraction();
      if (populationAttraction > 0) {
        attractedPopulation = Random.randomInt(populationAttraction / 2, populationAttraction);
      }
    }
  }

  public int getJobsFilled() {
    return jobsFilled;
  }

  public int getAttractedPopulation() {
    return attractedPopulation;
  }

  @Override
  public float getNoiseLevel() {
    if (jobsFilled > 0) {
      return gridObjectType.getNoiseLevel() * ((float) jobsFilled / ((CommercialType) gridObjectType).getJobsProvided());
    }

    return 0;
  }

  public void recordVisitor(Avatar avatar) {
    if (avatar instanceof Janitor || avatar instanceof Maid) {
      numVisitors = 0;
      lastCleanedAt = System.currentTimeMillis();
    } else {
      numVisitors += 1;
    }
  }

  public int getNumVisitors() {
    return numVisitors;
  }

  @Override
  public float getDesirability() {
    return super.getDesirability() - (0.1f * numVisitors);
  }

  public long getLastCleanedAt() {
    return lastCleanedAt;
  }
}
