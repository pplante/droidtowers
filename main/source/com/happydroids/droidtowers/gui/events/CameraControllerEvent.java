/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui.events;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

@SuppressWarnings("FieldCanBeLocal")
public class CameraControllerEvent implements Pool.Poolable {
  private Vector3 position;
  private Vector3 delta;
  private float zoom;

  public CameraControllerEvent() {
    position = new Vector3();
    delta = new Vector3();
  }

  @Override
  public String toString() {
    return "CameraControllerEvent{" +
                   "position=" + position +
                   ", zoom=" + zoom +
                   '}';
  }

  public Vector3 getPosition() {
    return position;
  }

  public void setPosition(Vector3 position) {
    this.position.set(position);
  }

  public Vector3 getDelta() {
    return delta;
  }

  public void setDelta(Vector3 delta) {
    this.delta.set(delta);
  }

  public float getZoom() {
    return zoom;
  }

  public void setZoom(float zoom) {
    this.zoom = zoom;
  }


  @Override
  public void reset() {
    zoom = 0f;
    position.set(0, 0, 0);
    delta.set(0, 0, 0);
  }
}
