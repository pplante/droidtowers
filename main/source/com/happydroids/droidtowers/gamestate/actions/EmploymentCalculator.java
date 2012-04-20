/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.types.CommercialType;

public class EmploymentCalculator extends GameGridAction {
  public EmploymentCalculator(GameGrid gameGrid, float frequency) {
    super(gameGrid, frequency);
  }

  @Override
  public void run() {
    int jobsFilled = 0;
    int maxJobs = 0;

    for (GridObject gridObject : gameGrid.getObjects()) {
      if (gridObject instanceof CommercialSpace) {
        CommercialSpace commercialSpace = (CommercialSpace) gridObject;
        commercialSpace.updateJobs();
        
        maxJobs += ((CommercialType) commercialSpace.getGridObjectType()).getJobsProvided();
        jobsFilled += commercialSpace.getJobsFilled();
      }
    }

    Player.instance().setJobsMax(maxJobs);
    Player.instance().setJobsFilled(jobsFilled);
  }
}
