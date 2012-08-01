/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.types.ProviderType;

import static com.happydroids.droidtowers.types.ProviderType.HOTEL_ROOMS;
import static com.happydroids.droidtowers.types.ProviderType.OFFICE_SERVICES;

public class Maid extends Janitor {
  public static final ProviderType[] MAID_SERVICES_PROVIDER_TYPES = new ProviderType[]{HOTEL_ROOMS, OFFICE_SERVICES};

  public Maid(AvatarLayer avatarLayer) {
    super(avatarLayer);

    setServicesTheseProviderTypes(MAID_SERVICES_PROVIDER_TYPES);
  }

  protected String addFramePrefix(String frameName) {
    return "maid/" + frameName;
  }

  @Override
  protected boolean canService(CommercialSpace commercialSpace) {
    return !commercialSpace.isBeingServiced() && commercialSpace.provides(HOTEL_ROOMS) || super.canService(commercialSpace);
  }
}
