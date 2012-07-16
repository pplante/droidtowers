/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.HappyDroidConsts;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GameUpdateCollection extends HappyDroidServiceCollection<GameUpdate> {
  public GameUpdateCollection() {
    super();
  }

  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/gameupdate/";
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }
}
