/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.types.ProviderType;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class AchievementRequirement {
  private RequirementType type;
  private AchievementThing thing;
  private ProviderType[] thingProviderTypes;
  private double amount;

  public boolean isCompleted(GameGrid gameGrid) {
    switch (type) {
      case POPULATION:
        return Player.instance().getTotalPopulation() >= amount;

      case BUILD:
        return handleBuildRequirement(gameGrid);

      default:
        assert false;
        break;
    }

    return false;
  }

  private boolean handleBuildRequirement(GameGrid gameGrid) {
    if (thing == null) {
      throw new RuntimeException(String.format("AchievementRequirement %s does not contain 'thing' parameter.", type));
    } else if (thingProviderTypes == null) {
      throw new RuntimeException(String.format("AchievementRequirement %s does not contain 'thingProviderTypes' parameter.", type));
    }


    if (gameGrid == null) {
      return false;
    }

    int numMatches = 0;
    for (GridObject gridObject : gameGrid.getObjects()) {
      if (numMatches >= amount) {
        return true;
      }

      if (gridObject.getGridObjectType().provides(thingProviderTypes)) {
        numMatches++;
      }
    }

    return false;
  }

  @Override
  public String toString() {
    return "AchievementRequirement{" +
                   "amount=" + amount +
                   ", type=" + type +
                   ", thing=" + thing +
                   '}';
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public void setThing(AchievementThing thing) {
    this.thing = thing;
  }

  public void setThingProviderTypes(ProviderType... thingProviderTypes) {
    this.thingProviderTypes = thingProviderTypes;
  }

  public void setType(RequirementType type) {
    this.type = type;
  }
}
