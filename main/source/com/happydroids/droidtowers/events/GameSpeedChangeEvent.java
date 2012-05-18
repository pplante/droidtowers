/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.scenes.Scene;

public class GameSpeedChangeEvent {
  public final Scene scene;

  public GameSpeedChangeEvent(Scene scene) {
    this.scene = scene;
  }
}
