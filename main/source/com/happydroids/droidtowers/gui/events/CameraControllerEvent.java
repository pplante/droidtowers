/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui.events;

import com.badlogic.gdx.math.Vector3;

@SuppressWarnings("FieldCanBeLocal")
public class CameraControllerEvent {
  public final Vector3 position;
  public final Vector3 delta;
  public final float zoom;

  public CameraControllerEvent(Vector3 position, Vector3 delta, float zoom) {
    this.position = position;
    this.delta = delta;
    this.zoom = zoom;
  }

  @Override
  public String toString() {
    return "CameraControllerEvent{" +
                   "position=" + position +
                   ", zoom=" + zoom +
                   '}';
  }
}
