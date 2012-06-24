/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.happydroids.droidtowers.controllers.AvatarLayer;

import static com.happydroids.droidtowers.types.ProviderType.COMMERCIAL;

public class SecurityGuard extends Janitor {
  public SecurityGuard(AvatarLayer avatarLayer) {
    super(avatarLayer);

    setServicesTheseProviderTypes(COMMERCIAL);
  }

  protected String addFramePrefix(String frameName) {
    return "security-guard/" + frameName;
  }
}
