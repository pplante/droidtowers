package com.unhappyrobot.math;

import com.badlogic.gdx.math.Vector2;

public class Bounds2 {
  private Vector2 position;
  private Vector2 size;

  public Bounds2(Vector2 position, Vector2 size) {

  }

  public boolean contains(Bounds2 other) {
    return (top() <= other.top()) && (left() <= other.left()) && (bottom() >= other.bottom()) && (right() >= other.right());
  }

  public float top() {
    return position.y;
  }

  public float bottom() {
    return position.y + size.y;
  }

  public float left() {
    return position.x;
  }

  public float right() {
    return position.x + size.x;
  }
}
