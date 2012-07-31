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
    List<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
    if (commercialSpaces != null && !commercialSpaces.isEmpty()) {
      GridObject dirtiestPlace = null;
      float highestDirtLevel = 0;
      for (int i = 0, commercialSpacesSize = commercialSpaces.size(); i < commercialSpacesSize; i++) {
        CommercialSpace commercialSpace = (CommercialSpace) commercialSpaces.get(i);
        if (!commercialSpace.getEmployees().isEmpty() && !commercialSpace.isBeingServiced() && commercialSpace.provides(this.servicesTheseProviderTypes) && highestDirtLevel < commercialSpace.getDirtLevel()) {
          highestDirtLevel = commercialSpace.getLastServicedAt();
          dirtiestPlace = commercialSpace;
        }
      }

      if (dirtiestPlace != null) {
        navigateToGridObject(dirtiestPlace);
      }
    }
  }

  public void setServicesTheseProviderTypes(ProviderType... types) {
    servicesTheseProviderTypes = types;
  }
}
