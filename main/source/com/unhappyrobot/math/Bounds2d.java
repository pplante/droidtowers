package com.unhappyrobot.math;

import com.badlogic.gdx.math.Vector2;

public class Bounds2d {
  private Vector2 position;
  private Vector2 size;

  public Bounds2d(Vector2 position, Vector2 size) {
    this.position = position;
    this.size = size;
  }

  public boolean contains(Bounds2d other) {
    return (top() <= other.top()) && (left() <= other.left()) && (bottom() >= other.bottom()) && (right() >= other.right());
  }

  public boolean contains(Vector2 point) {
    return (top() <= point.y) && (left() <= point.x) && (bottom() >= point.y) && (right() >= point.x);
  }

  public float top() {
    return position.y;
  }

  public float bottom() {
    return position.y + size.y - 1;
  }

  public float left() {
    return position.x;
  }

  public float right() {
    return position.x + size.x - 1;
  }

  public boolean overlaps(Bounds2d other) {
    if (bottom() < other.top()) {
      return false;
    } else if (top() > other.bottom()) {
      return false;
    } else if (right() < other.left()) {
      return false;
    } else if (left() > other.right()) {
      return false;
    }

    return true;
  }
}
