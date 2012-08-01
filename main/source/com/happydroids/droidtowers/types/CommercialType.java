/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.HotelRoom;
import com.happydroids.droidtowers.entities.MovieTheater;
import com.happydroids.droidtowers.grid.GameGrid;

import static com.happydroids.droidtowers.types.ProviderType.HOTEL_ROOMS;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CommercialType extends RoomType {
  public static final String COMMERCIAL_STATS_LINE = "MAX EMPLOYEES: {maxEmployees}\nMAX INCOME: {maxIncome}\nSERVICED BY: {servicedBy}";
  private int jobsProvided;
  private int populationAttraction;

  public CommercialType() {
    statsLine = COMMERCIAL_STATS_LINE;
  }

  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    if (getId().equalsIgnoreCase("MOVIE-THEATER")) {
      return new MovieTheater(this, gameGrid);
    } else if (this.provides(HOTEL_ROOMS)) {
      return new HotelRoom(this, gameGrid);
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
