/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.google.common.eventbus.EventBus;

public class SafeEventBus extends EventBus {
  public SafeEventBus() {
    super();
  }

  public SafeEventBus(String identifier) {
    super(identifier);
  }

  @Override
  public void unregister(Object object) {
    try {

    } catch (IllegalArgumentException iae) {
      if (!iae.getMessage().startsWith("missing event handler for an annotated method")) {
        throw iae;
      }
    }
  }
}
