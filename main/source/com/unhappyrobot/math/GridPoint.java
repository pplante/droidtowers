package com.unhappyrobot.math;

import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.GameGrid;

public class GridPoint extends Vector2 {
  private final GameGrid gameGrid;

  public GridPoint(GameGrid gameGrid, float x, float y) {
    super(x, y);

    this.gameGrid = gameGrid;
  }

  public GridPoint(GameGrid gameGrid, GridPoint point) {
    this(gameGrid, point.x, point.y);
  }

  public float getWorldX() {
    return x * gameGrid.unitSize.x;
  }

  public float getWorldY() {
    return y * gameGrid.unitSize.y;
  }

  @Override
  public GridPoint cpy() {
    return new GridPoint(gameGrid, x, y);
  }

  public Vector2 toVector2() {
    return new Vector2(getWorldX(), getWorldY());
  }
}
