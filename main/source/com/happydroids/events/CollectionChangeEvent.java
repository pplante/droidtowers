/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.events;

import com.happydroids.server.HappyDroidServiceObject;

public class CollectionChangeEvent {
  public final HappyDroidServiceObject object;

  public CollectionChangeEvent(HappyDroidServiceObject object) {

    this.object = object;
  }
}
