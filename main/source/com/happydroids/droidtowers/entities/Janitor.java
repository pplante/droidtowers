/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.types.ProviderType;

import javax.annotation.Nullable;
import java.util.List;

public class Janitor extends Avatar {
  protected ProviderType[] servicesTheseProviderTypes;

  public Janitor(AvatarLayer avatarLayer) {
    super(avatarLayer);
    setColor(Color.WHITE);
    setServicesTheseProviderTypes(ProviderType.FOOD, ProviderType.OFFICE_SERVICES, ProviderType.RESTROOM);
  }

  @Override
  protected String addFramePrefix(String frameName) {
    return "janitor/" + frameName;
  }

  @Override
  public void beginNextAction() {
    List<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
    if (commercialSpaces != null) {
      List<GridObject> sortedObjects = Ordering.natural().onResultOf(new Function<GridObject, Long>() {
        public Long apply(@Nullable GridObject gridObject) {
          return ((CommercialSpace) gridObject).getLastCleanedAt();
        }
      }).sortedCopy(Iterables.filter(commercialSpaces, new Predicate<GridObject>() {
        public boolean apply(@Nullable GridObject input) {
          return ((CommercialSpace) input).getNumVisitors() > 0 && input.getGridObjectType().provides(servicesTheseProviderTypes);
        }
      }));

      navigateToGridObject(Iterables.getFirst(sortedObjects, null));
    } else {
      wanderAround();
    }
  }

  public void setServicesTheseProviderTypes(ProviderType... types) {
    servicesTheseProviderTypes = types;
  }
}
