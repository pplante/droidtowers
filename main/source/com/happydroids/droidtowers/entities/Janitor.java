/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.types.ProviderType;

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
    List<GridObject> commercialSpaces = gameGrid.getObjects();
    if (commercialSpaces != null && !commercialSpaces.isEmpty()) {
      GridObject oldestServicedSpace = null;
      long oldestServiceTime = 0;
      for (int i = 0, commercialSpacesSize = commercialSpaces.size(); i < commercialSpacesSize; i++) {
        GridObject commercialSpace = commercialSpaces.get(i);
        if (oldestServiceTime < commercialSpace.getLastServicedAt()) {
          oldestServiceTime = commercialSpace.getLastServicedAt();
          oldestServicedSpace = commercialSpace;
        }
      }

      if (oldestServicedSpace != null) {
        navigateToGridObject(oldestServicedSpace);
      }
    }
  }

  public void setServicesTheseProviderTypes(ProviderType... types) {
    servicesTheseProviderTypes = types;
  }
}
