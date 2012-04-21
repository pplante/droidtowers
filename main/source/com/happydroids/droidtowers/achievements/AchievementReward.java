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

import static com.happydroids.droidtowers.achievements.AchievementThing.OBJECT_TYPE;
import static com.happydroids.droidtowers.achievements.AchievementThing.PROVIDER_TYPE;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AchievementReward {
  private RewardType type;
  private AchievementThing thing;
  private ProviderType[] thingProviderTypes;
  private String thingTypeId;
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
    thingTypeId = objectTypeId;
  }

  public void give() {
    if (type == null) {
      throw new RuntimeException("AchievementReward does not contain 'type' parameter.");
    } else if (thing == null) {
      throw new RuntimeException(String.format("AchievementReward %s does not contain 'thing' parameter.", type));
    }

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
    }
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
    if (thing == OBJECT_TYPE && thingTypeId != null) {
      GridObjectType objectType = GridObjectTypeFactory.findTypeById(thingTypeId);
      if (objectType == null) {
        throw new RuntimeException(String.format("Cannot find a type for: %s", thingTypeId));
      }

      return objectType;
    }

    throw new RuntimeException("Cannot find a type for null!");
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

  @Override
  public String toString() {
    return "AchievementReward{" +
                   "amount=" + amount +
                   ", type=" + type +
                   ", thing=" + thing +
                   '}';
  }

  public RewardType getType() {
    return type;
  }

  public AchievementThing getThing() {
    return thing;
  }

  public void resetState() {
    if (type.equals(RewardType.UNLOCK)) {
      if (thing.equals(OBJECT_TYPE)) {
        getThingObjectType().addLock(this);
      } else if (thing.equals(PROVIDER_TYPE)) {
        setProviderTypeLockState(true);
      }
    }
  }
}
