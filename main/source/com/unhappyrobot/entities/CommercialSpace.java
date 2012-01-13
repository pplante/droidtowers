package com.unhappyrobot.entities;

import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.actions.TimeDelayedAction;
import com.unhappyrobot.types.CommercialType;
import com.unhappyrobot.utils.Random;

public class CommercialSpace extends Room {
  private int attractedPopulation;
  private int jobsFilled;
  private long lastJobUpdateTime;

  public CommercialSpace(CommercialType commercialType, GameGrid gameGrid) {
    super(commercialType, gameGrid);

    addAction(makeEmploymentUpdateAction());
  }

  private TimeDelayedAction makeEmploymentUpdateAction() {
    return new TimeDelayedAction(TowerConsts.JOB_UPDATE_FREQUENCY) {
      @Override
      public void run() {
        CommercialType commercialType = (CommercialType) getGridObjectType();

        if (Player.getInstance().getTotalPopulation() > commercialType.getPopulationRequired()) {
          int jobsProvided = commercialType.getJobsProvided();
          jobsFilled = Random.randomInt(jobsProvided / 2, jobsProvided);
        }
      }
    };
  }

  @Override
  protected void updatePopulation() {
    CommercialType commercialType = (CommercialType) getGridObjectType();
    int populationAttraction = commercialType.getPopulationAttraction();
    attractedPopulation = Random.randomInt(populationAttraction / 2, populationAttraction);
  }

  public int getJobsFilled() {
    return jobsFilled;
  }

  public int getAttractedPopulation() {
    return attractedPopulation;
  }
}
