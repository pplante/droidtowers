/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;
import com.happydroids.droidtowers.types.ProviderType;

import java.util.Set;

import static com.happydroids.droidtowers.achievements.AchievementThing.OBJECT_TYPE;
import static com.happydroids.droidtowers.achievements.AchievementThing.PROVIDER_TYPE;
import static com.happydroids.droidtowers.entities.GridObjectPlacementState.PLACED;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class AchievementRequirement {
  private RequirementType type;
  private AchievementThing thing;
  private ProviderType[] thingProviderTypes;
  private String thingTypeId;
  private double amount;

  public boolean isCompleted(GameGrid gameGrid) {
    switch (type) {
      case POPULATION:
        return Player.instance().getTotalPopulation() >= amount;
      case BUILD:
        return handleBuildRequirement(gameGrid);
      case UNLOCK:
        return handleUnlockRequirement();
      default:
        assert false;
        break;
    }

    return false;
  }

  private boolean handleUnlockRequirement() {
    Set<Achievement> completedAchievements = AchievementEngine.instance().getCompletedAchievements();
    if (completedAchievements.isEmpty()) {
      return false;
    }

    GridObjectType objectType = GridObjectTypeFactory.findTypeById(thingTypeId);
    return objectType != null && !objectType.isLocked();
  }

  private boolean handleBuildRequirement(GameGrid gameGrid) {
    if (thing == null) {
      throw new RuntimeException(String.format("AchievementRequirement %s does not contain 'thing' parameter.", type));
    } else if (thingProviderTypes == null && thingTypeId == null) {
      throw new RuntimeException(String.format("AchievementRequirement %s does not contain 'thingProviderTypes' or 'thingTypeId' parameter.", type));
    }

    if (gameGrid == null) {
      return false;
    }


    int numMatches = 0;
    for (GridObject gridObject : gameGrid.getObjects()) {
      if (!gridObject.getPlacementState().equals(PLACED)) {
        continue;
      }

      GridObjectType gridObjectType = gridObject.getGridObjectType();
      if (thing.equals(PROVIDER_TYPE) && gridObjectType.provides(thingProviderTypes)) {
        numMatches++;
      } else if (thing.equals(OBJECT_TYPE) && gridObjectType.getId().equalsIgnoreCase(thingTypeId)) {
        numMatches++;
      }

      if (numMatches >= amount) {
        return true;
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
