/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.scenes.Scene;
import com.happydroids.droidtowers.scenes.TowerScene;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;
import com.happydroids.droidtowers.types.ProviderType;

import java.text.NumberFormat;

import static com.happydroids.droidtowers.achievements.AchievementThing.*;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Requirement {
  private RequirementType type;
  private AchievementThing thing;
  private ProviderType[] thingProviderTypes;
  private String thingId;
  private int amount;
  private int currentWeight;
  private int totalWeight;

  public boolean validate(GameGrid gameGrid) {
    switch (type) {
      case EMPLOYMENT:
        currentWeight = Player.instance().getJobsFilled();
        return currentWeight >= amount;
      case POPULATION:
        currentWeight = Player.instance().getTotalPopulation();
        return currentWeight >= amount;
      case DESIRABILITY:
        currentWeight = (int) (Player.instance().getDesirabilityRating() * 100);
        return currentWeight >= amount;
      case BUILD:
        return handleBuildRequirement(gameGrid);
      case UNLOCK:
        return handleUnlockRequirement(gameGrid);
      case HAPPYDROIDS_CONNECT:
        return TowerGameService.instance().isAuthenticated();
      case ADD_NEIGHBOR:
        return handleAddNeighborRequirement();
      default:
        assert false;
        break;
    }

    return false;
  }

  private boolean handleAddNeighborRequirement() {
    Scene activeScene = SceneManager.activeScene();
    currentWeight = 0;
    if (activeScene instanceof TowerScene) {
      GameSave cloudGameSave = ((TowerScene) activeScene).getCurrentGameSave();
      currentWeight = cloudGameSave.numNeighbors();

      return currentWeight >= amount;
    }
    return false;
  }

  private boolean handleUnlockRequirement(GameGrid gameGrid) {
    if (thing.equals(OBJECT_TYPE)) {
      GridObjectType objectType = GridObjectTypeFactory.findTypeById(thingId);
      return objectType != null && !objectType.isLocked();
    } else if (thing.equals(ACHIEVEMENT)) {
      Achievement achievement = AchievementEngine.instance().findById(thingId);
      if (achievement == null) {
        achievement = TutorialEngine.instance().findById(thingId);
      }

      if (achievement != null) {
        achievement.checkRequirements(gameGrid);
        return achievement.isCompleted();
      }
    }

    return false;
  }

  private boolean handleBuildRequirement(GameGrid gameGrid) {
    if (thing == null) {
      throw new RuntimeException(String.format("Requirement %s does not contain 'thing' parameter.", type));
    } else if (thingProviderTypes == null && thingId == null) {
      throw new RuntimeException(String.format("Requirement %s does not contain 'thingProviderTypes' or 'thingId' parameter.", type));
    }

    if (gameGrid == null) {
      return false;
    }

    currentWeight = 0;
    for (GridObject gridObject : gameGrid.getObjects()) {
      if (!gridObject.isPlaced()) {
        continue;
      }

      GridObjectType gridObjectType = gridObject.getGridObjectType();
      if (thing.equals(PROVIDER_TYPE) && gridObjectType.provides(thingProviderTypes)) {
        currentWeight++;
      } else if (thing.equals(OBJECT_TYPE) && gridObjectType.getId().equalsIgnoreCase(thingId)) {
        currentWeight++;
      }
    }

    return currentWeight >= amount;
  }

  @Override
  public String toString() {
    return "Requirement{" +
                   "amount=" + amount +
                   ", type=" + type +
                   ", thing=" + thing +
                   '}';
  }

  public void setAmount(int amount) {
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

  public int getCurrentWeight() {
    return currentWeight;
  }

  public int getAmount() {
    return amount;
  }

  public String displayString() {
    String display = null;
    switch (type) {
      case BUILD:
        display = "Build " + displayStringForThing(thing, amount, thingId, thingProviderTypes);
        break;

      case UNLOCK:
        if (thing.equals(ACHIEVEMENT)) {
          return "Complete Achievement: " + displayStringForThing(thing, amount, thingId, thingProviderTypes);
        }

      case DESIRABILITY:
        return "Maintain a " + amount + "% desirability rating.";

      case EMPLOYMENT:
        return "Create " + NumberFormat.getInstance().format(amount) + " Jobs";

      case POPULATION:
        return "Attract " + NumberFormat.getInstance().format(amount) + " Residents";
      case HAPPYDROIDS_CONNECT:
        return "Connect your Device to happydroids.com.";

      case ADD_NEIGHBOR:
        return "Add a Neighbor to your Tower.";
    }

    return display;
  }

  public int getProgress() {
    return (int) (((float) currentWeight / (float) getAmount()) * 100);
  }

  public RequirementType getType() {
    return type;
  }
}
