/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.happydroids.droidtowers.controllers.AvatarLayer;

import static com.happydroids.droidtowers.types.ProviderType.HOTEL_ROOMS;

public class Maid extends Janitor {
  public Maid(AvatarLayer avatarLayer) {
    super(avatarLayer);

    setServicesTheseProviderTypes(HOTEL_ROOMS);
  }

  protected String addFramePrefix(String frameName) {
    return "maid/" + frameName;
  }

  @Override
  protected boolean canService(CommercialSpace commercialSpace) {
    return !commercialSpace.isBeingServiced() && commercialSpace.provides(HOTEL_ROOMS) || super.canService(commercialSpace);
  }
}
