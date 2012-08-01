/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.types.ProviderType;

import static com.happydroids.droidtowers.types.ProviderType.COMMERCIAL;

public class SecurityGuard extends Janitor {
  public static final ProviderType SECURITY_GUARD_SERVICE_PROVIDER_TYPES = COMMERCIAL;

  public SecurityGuard(AvatarLayer avatarLayer) {
    super(avatarLayer);

    setServicesTheseProviderTypes(SECURITY_GUARD_SERVICE_PROVIDER_TYPES);
  }

  protected String addFramePrefix(String frameName) {
    return "security-guard/" + frameName;
  }
}
