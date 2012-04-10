/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.types.ProviderType;
import com.happydroids.droidtowers.types.RoomType;

import javax.annotation.Nullable;
import java.util.List;

public class Janitor extends Avatar {
  public Janitor(AvatarLayer avatarLayer) {
    super(avatarLayer);
    setColor(Color.WHITE);
  }

  @Override
  protected String addFramePrefix(String frameName) {
    return "janitor/" + frameName;
  }

  @Override
  public void beginNextAction() {
    GuavaSet<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
    if (commercialSpaces != null) {
      List<GridObject> sortedObjects = commercialSpaces.filterBy(new Predicate<GridObject>() {
        public boolean apply(@Nullable GridObject input) {
          ProviderType providerType = RoomType.class.cast(input.getGridObjectType()).provides();
          int numVisitors = CommercialSpace.class.cast(input).getNumVisitors();
          return numVisitors > 0 && checkProviderType(providerType);
        }
      }).sortedBy(new Function<GridObject, Long>() {
        public Long apply(@Nullable GridObject gridObject) {
          return ((CommercialSpace) gridObject).getLastCleanedAt();
        }
      });

      navigateToGridObject(Iterables.getFirst(sortedObjects, null));
    } else {
      wanderAround();
    }
  }

  public static boolean checkProviderType(ProviderType providerType) {
    return providerType.equals(ProviderType.FOOD) || providerType.equals(ProviderType.OFFICE_SERVICES);
  }
}
