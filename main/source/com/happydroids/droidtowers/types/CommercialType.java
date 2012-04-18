/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.MovieTheater;
import com.happydroids.droidtowers.grid.GameGrid;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CommercialType extends RoomType {
  private int jobsProvided;
  private int populationAttraction;

  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    if (getId().equalsIgnoreCase("MOVIE-THEATER")) {
      return new MovieTheater(this, gameGrid);
    }

    return new CommercialSpace(this, gameGrid);
  }

  public int getJobsProvided() {
    return jobsProvided;
  }

  public int getPopulationAttraction() {
    return populationAttraction;
  }
}
