/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.types.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.happydroids.droidtowers.achievements.AchievementThing.OBJECT_TYPE;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AchievementReward {
  private RewardType type;
  private AchievementThing thing;
  private String thingTypeId;
  private double amount;

  @JsonIgnore
  private GridObjectType thingObjectType;

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

  public void give() {
    switch (type) {
      case GIVE:
        handleGiveReward();
        break;
      case UNLOCK:
        handleUnlockReward();
        break;
    }
  }

  private void handleUnlockReward() {
    if (thing == null) {
      throw new RuntimeException(String.format("AchievementReward %s does not contain 'thing' parameter.", type));
    }


    switch (thing) {
      case MAIDS_OFFICE:
        handleThingUnlockReward();
        break;
      case JANITORS_CLOSET:
        for (ServiceRoomType serviceRoomType : ServiceRoomTypeFactory.instance().all()) {
          if (serviceRoomType.provides() == ProviderType.JANITORS) {
            serviceRoomType.setLocked(false);
          }
        }
        break;

      case OBJECT_TYPE:
        if (thingObjectType == null) {
          throw new RuntimeException(String.format("Cannot find a type for: %s", thingTypeId));
        }

        thingObjectType.setLocked(false);
    }
  }

  private void handleThingUnlockReward() {
    for (ServiceRoomType serviceRoomType : ServiceRoomTypeFactory.instance().all()) {
      if (serviceRoomType.provides() == ProviderType.MAIDS) {
        serviceRoomType.setLocked(false);
      }
    }
  }

  private void handleGiveReward() {
    if (thing == null) {
      throw new RuntimeException(String.format("AchievementReward %s does not contain 'thing' parameter.", type));
    }

    switch (thing) {
      case MONEY:
        Player.instance().addCurrency((int) amount);
        break;
    }
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

  public double getAmount() {
    return amount;
  }

  public String getFormattedString() {
    List<String> parts = Lists.newArrayList();
    if (type != null) {
      parts.add(type.displayString);
    }

    if (thing != null) {
      parts.add(thing.displayString);
    }

    if (amount > 0) {
      parts.add("" + (int) amount);
    }

    return StringUtils.join(parts, " ") + "\n";
  }

  public void validate() {
    if (thing != null && thing.equals(OBJECT_TYPE) && thingTypeId != null) {
      GridObjectType objectType = GridObjectTypeFactory.findTypeById(thingTypeId);
      if (objectType == null) {
        throw new RuntimeException(String.format("Cannot find a type for: %s", thingTypeId));
      }

      thingObjectType = objectType;
    }
  }
}
