/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.math;

import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.happydroids.droidtowers.TowerConsts.GRID_UNIT_SIZE;

public class GridPoint extends Vector2i {
  public GridPoint() {

  }

  public GridPoint(int x, int y) {
    super(x, y);
  }

  public GridPoint(Vector2i vec) {
    this(vec.x, vec.y);
  }

  @JsonIgnore
  public float getWorldX() {
    return (GRID_UNIT_SIZE * x);
  }

  @JsonIgnore
  public float getWorldY() {
    return (GRID_UNIT_SIZE * y);
  }

  @Override
  public GridPoint cpy() {
    return new GridPoint(x, y);
  }

  public Vector2 toWorldVector2() {
    return new Vector2(getWorldX(), getWorldY());
  }

  public Vector2 toVector2() {
    return new Vector2(x, y);
  }

  @Override
  public boolean equals(Object otherObject) {
    if (otherObject != null && (otherObject instanceof GridPoint || otherObject instanceof Vector2)) {
      GridPoint otherPoint = (GridPoint) otherObject;
      return otherPoint.x == this.x && otherPoint.y == this.y;
    }

    return false;
  }
}
