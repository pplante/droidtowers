/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.types.ProviderType;

public class Maid extends Janitor {
  public Maid(AvatarLayer avatarLayer) {
    super(avatarLayer);
  }

  protected String addFramePrefix(String frameName) {
    return "maid/" + frameName;
  }

  public static boolean checkProviderType(ProviderType providerType) {
    return providerType.equals(ProviderType.HOTEL_ROOMS);
  }
}
