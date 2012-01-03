package com.unhappyrobot.math;

import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.GameGrid;

public class GridPoint {
  private final GameGrid gameGrid;
  private final Vector2 position;

  public GridPoint(GameGrid gameGrid, Vector2 position) {
    this.gameGrid = gameGrid;
    this.position = position.cpy();
  }

  public float getX() {
    return gameGrid.gridOrigin.x + position.x * gameGrid.unitSize.x;
  }

  public float getY() {
    return (gameGrid.gridOrigin.y + position.y) * gameGrid.unitSize.y;
  }

  public void add(int x, int y) {
    position.add(x, y);
  }
}
