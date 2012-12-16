/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.math.MathUtils;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.CommercialSpacePopOver;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.types.CommercialType;
import com.happydroids.droidtowers.utils.Random;

public class CommercialSpace extends Room {
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

  public int getJobsFilled() {
    return jobsFilled;
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
      return (int) Math.ceil(gridObjectType.getCoinsEarned() * getDesirability() + getUpkeepCost() + gridObjectType.getCoinsEarned() * 0.06125f * getNumVisitors());
    }

    return 0;
  }

  @Override
  public int getUpkeepCost() {
    if (jobsFilled == 0) {
      return 0;
    }

    return jobsFilled * 500;
  }

  @Override
  public float getDesirability() {
    if (canEmployDroids() && jobsFilled == 0) {
      return 0f;
    }

    return super.getDesirability();
  }

  public float getEmploymentLevel() {
    int jobsProvided = ((CommercialType) gridObjectType).getJobsProvided();

    if (jobsProvided > 0) {
      return MathUtils.clamp(jobsFilled / (float) jobsProvided, 0, 1);
    }

    return 0;
  }

  public int getJobsProvided() {
    return ((CommercialType) gridObjectType).getJobsProvided();
  }

  @Override
  protected void checkDecals() {
    super.checkDecals();

    boolean unlockedJanitors = AchievementEngine.instance().findById("build5commercialspaces").hasGivenReward();
    boolean unlockedMaids = AchievementEngine.instance().findById("build8hotelroom").hasGivenReward();
    if (unlockedJanitors && unlockedMaids && getDirtLevel() >= 0.95f && jobsFilled > 0) {
      decalsToDraw.add(DECAL_DIRTY);
    } else {
      decalsToDraw.remove(DECAL_DIRTY);
    }
  }

  @Override public boolean needsDroids() {
    return false;
  }

  protected boolean canEmployDroids() {
    return true;
  }

  @Override
  public float getDirtLevel() {
    if (canEmployDroids() && jobsFilled == 0) {
      return 0;
    }

    return super.getDirtLevel();
  }
}
