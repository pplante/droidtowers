/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server.events;

import com.happydroids.server.HappyDroidServiceObject;

public class HappyDroidServiceObjectEvent<T extends HappyDroidServiceObject> {
  public final T objectInstance;

  public HappyDroidServiceObjectEvent(T objectInstance) {
    this.objectInstance = objectInstance;
  }
}
