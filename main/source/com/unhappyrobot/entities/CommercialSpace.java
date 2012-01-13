package com.unhappyrobot.entities;

import com.unhappyrobot.types.CommercialType;
import com.unhappyrobot.types.RoomType;
import com.unhappyrobot.utils.Random;

public class CommercialSpace extends Room {
  private int jobsFilled;

  public CommercialSpace(RoomType roomType, GameGrid gameGrid) {
    super(roomType, gameGrid);
  }

  @Override
  public void update(float deltaTime) {
    if (shouldUpdate()) {
      CommercialType commercialType = (CommercialType) getGridObjectType();
      if (Player.getInstance().getPopulation() > commercialType.getPopulationRequired()) {
        jobsFilled = Random.randomInt(0, commercialType.getJobsProvided());
      }
    }
  }

  public int getJobsFilled() {
    return jobsFilled;
  }
}
