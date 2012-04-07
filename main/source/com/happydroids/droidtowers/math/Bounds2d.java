/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.math;

import com.badlogic.gdx.math.Vector2;

public class Bounds2d {
  private final Vector2 position;
  private final Vector2 size;

  public Bounds2d(Vector2 position, Vector2 size) {
    this.position = position;
    this.size = size;
  }

  public boolean overlaps(Bounds2d other) {
    return (left() < other.right()) && (right() > other.left()) && (bottom() < other.top()) && (top() > other.bottom());
  }

  public boolean containsPoint(Vector2 point) {
    return (left() <= point.x) && (right() >= point.x) && (bottom() <= point.y) && (top() >= point.y);
  }

  public boolean intersects(Bounds2d other) {
    return !(left() >= other.right() || right() <= other.left() || bottom() >= other.top() || bottom() <= other.top());
  }

  public float top() {
    return position.y + size.y;
  }

  public float bottom() {
    return position.y;
  }

  public float left() {
    return position.x;
  }

  public float right() {
    return position.x + size.x;
  }

  @Override
  public String toString() {
    return String.format("Bounds2d(%s, %s)", position.toString(), size.toString());
  }
}
