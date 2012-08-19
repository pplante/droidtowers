/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer.server;

import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.designer.types.DesignerObjectType;
import com.happydroids.server.HappyDroidServiceCollection;

public class DesignerObjectTypeCollection extends HappyDroidServiceCollection<DesignerObjectType> {
  @Override protected boolean requireAuthentication() {
    return false;
  }

  @Override public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/designerobjecttype/";
  }

  @Override protected int getCacheMaxAge() {
    return HappyDroidConsts.ONE_WEEK;
  }

  @Override protected boolean isCachingAllowed() {
    return true;
  }
}
