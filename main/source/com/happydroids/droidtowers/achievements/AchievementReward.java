/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;
import com.happydroids.droidtowers.types.ProviderType;

import static com.happydroids.droidtowers.achievements.AchievementThing.ACHIEVEMENT;
import static com.happydroids.droidtowers.achievements.AchievementThing.OBJECT_TYPE;
import static com.happydroids.droidtowers.achievements.RewardType.UNLOCK;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AchievementReward {
  private RewardType type;
  private AchievementThing thing;
  private ProviderType[] thingProviderTypes;
  private String thingId;
  private double amount;

  public AchievementReward() {

  }

  public AchievementReward(RewardType type, AchievementThing thing) {
    this(type, thing, 0);
  }

  public AchievementReward(RewardType type, AchievementThing thing, int amount) {
    this.type = type;
    this.thing = thing;
    this.amount = amount;
  }

  public AchievementReward(RewardType type, AchievementThing objectType, String objectTypeId) {
    this.type = type;
    thing = objectType;
    thingId = objectTypeId;
  }

  public void give() {
    if (type == null) {
      throw new RuntimeException("AchievementReward does not contain 'type' parameter.");
    } else if (thing == null) {
      throw new RuntimeException(String.format("AchievementReward %s does not contain 'thing' parameter.", type));
    }

    switch (type) {
      case UNLOCK:
        handleUnlockReward();
        break;

      case COMPLETE:
        if (thing.equals(ACHIEVEMENT)) {
          Achievement achievement = findAchievementById();
          achievement.setCompleted(true);
          achievement.giveReward();
        }
        break;
    }
  }

  private void handleUnlockReward() {
    switch (thing) {
      case MONEY:
        Player.instance().addCurrency((int) amount);
        break;
      case OBJECT_TYPE:
        getThingObjectType().removeLock();
        break;
      case PROVIDER_TYPE:
        handleProviderTypeReward();
        break;
      case ACHIEVEMENT:
        findAchievementById().removeLock();
        break;
    }
  }

  private Achievement findAchievementById() {
    Achievement achievement = AchievementEngine.instance().findById(thingId);
    if (achievement == null) {
      achievement = TutorialEngine.instance().findById(thingId);
    }

    if (achievement != null) {
      return achievement;
    }

    throw new RuntimeException("Could not find Achievement with id: " + thingId);
  }

  private void handleProviderTypeReward() {
    if (thingProviderTypes == null || thingProviderTypes.length == 0) {
      throw new RuntimeException("Reward with 'thing' value of 'PROVIDER_TYPE' needs the parameter 'thingProviderTypes' to be set.");
    }

    setProviderTypeLockState(false);
  }

  private void setProviderTypeLockState(boolean locked) {
    if (thingProviderTypes == null || thingProviderTypes.length == 0) {
      throw new RuntimeException("Reward with 'thing' value of 'PROVIDER_TYPE' needs the parameter 'thingProviderTypes' to be set.");
    }

    for (ProviderType providerType : thingProviderTypes) {
      for (GridObjectTypeFactory typeFactory : GridObjectTypeFactory.allFactories()) {
        for (Object objectType : typeFactory.findByProviderType(providerType)) {
          GridObjectType gridObjectType = (GridObjectType) objectType;
          if (gridObjectType.provides(providerType)) {
            if (locked) {
              gridObjectType.addLock(this);
            } else {
              gridObjectType.removeLock();
            }
          }
        }
      }
    }
  }

  protected GridObjectType getThingObjectType() {
    if (thing == OBJECT_TYPE && thingId != null) {
      GridObjectType objectType = GridObjectTypeFactory.findTypeById(thingId);
      if (objectType == null) {
        throw new RuntimeException(String.format("Cannot find a type for: %s", thingId));
      }

      return objectType;
    }

    throw new RuntimeException("Cannot find a type for null!");
  }

  public void resetState() {
    if (type.equals(UNLOCK)) {
      switch (thing) {
        case OBJECT_TYPE:
          getThingObjectType().addLock(this);
          break;

        case PROVIDER_TYPE:
          setProviderTypeLockState(true);
          break;

        case ACHIEVEMENT:
          findAchievementById().addLock(this);
          break;
      }
    }
  }

  public String getRewardString() {
    return displayStringForType() + " " + displayStringForThing();
  }

  private String displayStringForType() {
    switch (type) {
      case GIVE:
        return "Awarded";
      case UNLOCK:
        return "Unlocked";
    }

    return "";
  }

  private String displayStringForThing() {
    switch (thing) {
      case MONEY:
        return TowerConsts.CURRENCY_SYMBOL + (int) amount;
      case OBJECT_TYPE:
        return getThingObjectType().getName();
    }

    return "";
  }

  public RewardType getType() {
    return type;
  }

  public AchievementThing getThing() {
    return thing;
  }


}
