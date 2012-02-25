package com.unhappyrobot.types;

import com.unhappyrobot.entities.CommercialSpace;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.grid.GameGrid;
import org.codehaus.jackson.annotate.JsonAutoDetect;

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
