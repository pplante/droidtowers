package com.unhappyrobot.math;

import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.GameGrid;
import org.codehaus.jackson.annotate.JsonIgnore;

public class GridPoint extends Vector2 {
  public GridPoint() {

  }

  public GridPoint(float x, float y) {
    super(x, y);
  }

  public GridPoint(Vector2 vec) {
    this(vec.x, vec.y);
  }

  @JsonIgnore
  public float getWorldX(GameGrid gameGrid) {
    return gameGrid.unitSize.x * x;
  }

  @JsonIgnore
  public float getWorldY(GameGrid gameGrid) {
    return gameGrid.unitSize.y * y;
  }

  @Override
  public GridPoint cpy() {
    return new GridPoint(x, y);
  }

  public Vector2 toWorldVector2(GameGrid gameGrid) {
    return new Vector2(getWorldX(gameGrid), getWorldY(gameGrid));
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
