/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.types;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.unhappyrobot.entities.CommercialSpace;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.grid.GameGrid;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CommercialType extends RoomType {
  private int jobsProvided;
  private int populationAttraction;

  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    return new CommercialSpace(this, gameGrid);
  }

  public int getJobsProvided() {
    return jobsProvided;
  }

  public int getPopulationAttraction() {
    return populationAttraction;
  }
}
