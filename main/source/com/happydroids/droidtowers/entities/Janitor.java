/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.types.ProviderType;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Janitor extends Avatar {
  protected ProviderType[] servicesTheseProviderTypes;

  public Janitor(AvatarLayer avatarLayer) {
    super(avatarLayer.getGameGrid());
    setColor(Color.WHITE);
    setServicesTheseProviderTypes(ProviderType.FOOD, ProviderType.OFFICE_SERVICES, ProviderType.RESTROOM);
  }

  @Override
  protected String addFramePrefix(String frameName) {
    return "janitor/" + frameName;
  }

  @Override
  protected void findPlaceToVisit() {
    List<GridObject> gridObjects = gameGrid.getInstancesOf(CommercialSpace.class, HotelRoom.class);
    if (gridObjects != null && !gridObjects.isEmpty()) {
      Collections.sort(gridObjects, FIND_DIRTIEST);

      for (int i = 0, gridObjectsSize = gridObjects.size(); i < gridObjectsSize; i++) {
        GridObject gridObject = gridObjects.get(i);

        if (canService((CommercialSpace) gridObject)) {
          navigateToGridObject(gridObject);
          break;
        }
      }
    }
  }

  protected boolean canService(CommercialSpace commercialSpace) {
    return !commercialSpace.isBeingServiced() && commercialSpace.canEmployDroids() && commercialSpace.getJobsFilled() > 0 && commercialSpace.provides(servicesTheseProviderTypes);
  }

  public void setServicesTheseProviderTypes(ProviderType... types) {
    servicesTheseProviderTypes = types;
  }

  public static final Comparator<GridObject> FIND_DIRTIEST = new Comparator<GridObject>() {
    @Override
    public int compare(GridObject left, GridObject right) {
      return left.getDirtLevel() > right.getDirtLevel() ? -1 : 1;
    }
  };
}
