/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.aetrion.activesupport.Inflection;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;
import com.happydroids.droidtowers.types.ProviderType;

import java.text.NumberFormat;
import java.util.Set;

public enum AchievementThing {
  MONEY,
  EMPLOYEE,
  PROVIDER_TYPE,
  OBJECT_TYPE,
  ACHIEVEMENT;

  static String displayStringForThing(AchievementThing thing, double amount, String thingId, ProviderType[] thingProviderTypes) {
    switch (thing) {
      case MONEY:
        return TowerConsts.CURRENCY_SYMBOL + NumberFormat.getInstance().format(amount);
      case OBJECT_TYPE:
        return Inflection.pluralize(getThingObjectType(thing, thingId).getName());
      case PROVIDER_TYPE:
        Set<String> objectNames = Sets.newHashSet();

        for (ProviderType providerType : thingProviderTypes) {
          Set<GridObjectType> gridObjectTypes = GridObjectTypeFactory.findByProviderTypeFromAnyFactory(providerType);
          for (GridObjectType gridObjectType : gridObjectTypes) {
            if (!gridObjectType.isLocked()) {
              objectNames.add(gridObjectType.getName());
            }
          }
        }

        if (objectNames.size() > 1) {
          return "any of these: " + Joiner.on(", ").join(objectNames);
        } else if (objectNames.size() == 1) {
          return Inflection.pluralize(Iterables.getFirst(objectNames, ""));
        }

        return "WUT WUT?";
      case ACHIEVEMENT:
        return Achievement.findById(thingId).getName();
    }

    return "";
  }

  static GridObjectType getThingObjectType(AchievementThing thing, String thingId) {
    if (thing == OBJECT_TYPE && thingId != null) {
      GridObjectType objectType = GridObjectTypeFactory.findTypeById(thingId);
      if (objectType == null) {
        throw new RuntimeException(String.format("Cannot find a type for: %s", thingId));
      }

      return objectType;
    }

    throw new RuntimeException("Cannot find a type for null!");
  }
}
