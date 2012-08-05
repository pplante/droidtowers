/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.server;

import com.happydroids.HappyDroidConsts;
import com.happydroids.server.HappyDroidServiceCollection;

public class MovieCollection extends HappyDroidServiceCollection<Movie> {
  @Override
  protected boolean requireAuthentication() {
    return false;
  }

  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/movie/";
  }

  @Override
  protected int getCacheMaxAge() {
    return HappyDroidConsts.ONE_DAY;
  }

  @Override
  protected boolean isCachingAllowed() {
    return true;
  }
}
