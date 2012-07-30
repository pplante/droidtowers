/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;
import com.happydroids.droidtowers.types.ProviderType;

import static com.happydroids.droidtowers.achievements.AchievementThing.ACHIEVEMENT;
import static com.happydroids.droidtowers.achievements.AchievementThing.OBJECT_TYPE;
import static com.happydroids.droidtowers.achievements.RewardType.UNLOCK;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Reward {
  private RewardType type;
  private AchievementThing thing;
  private ProviderType[] thingProviderTypes;
  private String thingId;
  private double amount;

  public Reward() {

  }

  public Reward(RewardType type, AchievementThing thing) {
    this(type, thing, 0);
  }

  public Reward(RewardType type, AchievementThing thing, int amount) {
    this.type = type;
    this.thing = thing;
    this.amount = amount;
  }

  public Reward(RewardType type, AchievementThing objectType, String objectTypeId) {
    this.type = type;
    thing = objectType;
    thingId = objectTypeId;
  }

  public void give() {
    if (type == null) {
      throw new RuntimeException("Reward does not contain 'type' parameter.");
    } else if (thing == null) {
      throw new RuntimeException(String.format("Reward %s does not contain 'thing' parameter.", type));
    }

    switch (type) {
      case GIVE:
        handleGiveReward();
        break;
      case UNLOCK:
        handleUnlockReward();
        break;

      case COMPLETE:
        if (thing.equals(ACHIEVEMENT)) {
          if (AchievementEngine.instance().findById(thingId) != null) {
            AchievementEngine.instance().complete(thingId);
          } else if (TutorialEngine.instance().findById(thingId) != null) {
            TutorialEngine.instance().complete(thingId);
          } else {
            throw new RuntimeException("Could not find Achievement with id: " + thingId);
          }
        }
        break;
    }
  }

  private void handleGiveReward() {
    switch (thing) {
      case MONEY:
        Player.instance().addCurrency((int) amount);
        break;
    }
  }

  private void handleUnlockReward() {
    switch (thing) {
      case OBJECT_TYPE:
        getThingObjectType().removeLock();
        break;
      case PROVIDER_TYPE:
        handleProviderTypeReward();
        break;
      case ACHIEVEMENT:
        Achievement.findById(thingId).removeLock();
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
            } else if (gridObjectType.getLock() == this) {
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
          Achievement.findById(thingId).addLock(this);
          break;
      }
    }
  }

  public String getRewardString(boolean pastTense) {
    return displayStringForType() + (pastTense ? "ed" : "") + " " + AchievementThing.displayStringForThing(thing, amount, thingId, thingProviderTypes);
  }

  private String displayStringForType() {
    switch (type) {
      case GIVE:
        return "Award";
      case UNLOCK:
        return "Unlock";
    }

    return "";
  }

  public RewardType getType() {
    return type;
  }

  public AchievementThing getThing() {
    return thing;
  }


  public void unlock() {
    switch (type) {
      case UNLOCK:
        handleUnlockReward();
        break;

      case COMPLETE:
        if (thing.equals(ACHIEVEMENT)) {
          Achievement achievement = Achievement.findById(thingId);
          achievement.setCompleted(true);
          achievement.giveReward();
        }
        break;
    }
  }

  public double getAmount() {
    return amount;
  }
}
