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

  @JsonIgnore
  public float getWorldX(GameGrid gameGrid) {
    return x * gameGrid.unitSize.x;
  }

  @JsonIgnore
  public float getWorldY(GameGrid gameGrid) {
    return y * gameGrid.unitSize.y;
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GridPoint gridPoint = (GridPoint) o;

    return x == gridPoint.x && y == gridPoint.y;
  }

  @Override
  public int hashCode() {
    return (int) (x + y);
  }
}
