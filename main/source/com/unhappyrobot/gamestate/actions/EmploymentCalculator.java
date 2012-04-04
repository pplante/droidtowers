/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gamestate.actions;

import com.unhappyrobot.entities.CommercialSpace;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.types.CommercialType;

import java.util.Set;

public class EmploymentCalculator extends GameGridAction {
  public EmploymentCalculator(GameGrid gameGrid, float frequency) {
    super(gameGrid, frequency);
  }

  @Override
  public void run() {
    Set<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
    int jobsFilled = 0;
    int maxJobs = 0;
    if (commercialSpaces != null) {
      for (GridObject gridObject : commercialSpaces) {
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
