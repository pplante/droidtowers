/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.types.ProviderType;

public class Janitor extends Avatar {
  public static final ProviderType[] JANITOR_SERVICES_PROVIDER_TYPES = new ProviderType[]{ProviderType.FOOD, ProviderType.OFFICE_SERVICES, ProviderType.RESTROOM};

  protected ProviderType[] servicesTheseProviderTypes;

  public Janitor(AvatarLayer avatarLayer) {
    super(avatarLayer.getGameGrid());
    setColor(Color.WHITE);
    setServicesTheseProviderTypes(JANITOR_SERVICES_PROVIDER_TYPES);
    setVisible(true);
  }

  @Override
  protected String addFramePrefix(String frameName) {
    return "janitor/" + frameName;
  }

  @Override
  protected void findPlaceToVisit() {
    Array<GridObject> gridObjects = gameGrid.getInstancesOf(CommercialSpace.class, HotelRoom.class);
    if (gridObjects != null && gridObjects.size > 0) {
      if (gridObjects.size > 1) {
        gridObjects.sort(GridObjectSort.byDirtLevel);
      }

      for (int i = 0, gridObjectsSize = gridObjects.size; i < gridObjectsSize; i++) {
        GridObject gridObject = gridObjects.get(i);

        if (canService((CommercialSpace) gridObject)) {
          navigateToGridObject(gridObject);
          break;
        }
      }
    }
  }

  protected boolean canService(CommercialSpace commercialSpace) {
    return !commercialSpace.isBeingServiced() && commercialSpace.provides(servicesTheseProviderTypes);
  }

  public void setServicesTheseProviderTypes(ProviderType... types) {
    servicesTheseProviderTypes = types;
  }
}
