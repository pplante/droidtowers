/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.math.MathUtils;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.CommercialSpacePopOver;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.types.CommercialType;
import com.happydroids.droidtowers.utils.Random;

public class CommercialSpace extends Room {
  private int attractedPopulation;
  private int jobsFilled;
  private long lastJobUpdateTime;

  public CommercialSpace(CommercialType commercialType, GameGrid gameGrid) {
    super(commercialType, gameGrid);
  }

  @Override
  public GridObjectPopOver makePopOver() {
    return new CommercialSpacePopOver(this);
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
        attractedPopulation = Random.randomInt(0, populationAttraction);
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

  @Override
  public int getCoinsEarned() {
    if (jobsFilled > 0 && isConnectedToTransport()) {
      return (gridObjectType.getCoinsEarned()) * attractedPopulation;
    }

    return 0;
  }

  @Override
  public int getUpkeepCost() {
    if (jobsFilled > 0 && isConnectedToTransport()) {
      return (int) Math.ceil((float) gridObjectType.getUpkeepCost() * ((float) jobsFilled / (float) ((CommercialType) gridObjectType).getJobsProvided()));
    }

    return 0;
  }

  @Override
  public float getDesirability() {
    return MathUtils.clamp(super.getDesirability() - (0.1f * getNumVisitors()), 0f, 1f);
  }

  public float getEmploymentLevel() {
    int jobsProvided = ((CommercialType) gridObjectType).getJobsProvided();
    if (jobsProvided > 0) {
      return jobsFilled / jobsProvided;
    }

    return 0;
  }

  public int getJobsProvided() {
    return ((CommercialType) gridObjectType).getJobsProvided();
  }

  public float getAttractedPopulationLevel() {
    int populationAttraction = ((CommercialType) gridObjectType).getPopulationAttraction();
    if (populationAttraction == 0) {
      return 0.0f;
    }

    return attractedPopulation / populationAttraction;
  }
}
