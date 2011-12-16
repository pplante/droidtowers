package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.grid.GridPosition;

import java.util.HashSet;
import java.util.Set;

public class GameGrid {
  public Vector2 unitSize;
  public Color gridColor;
  public Vector2 gridOrigin;
  public Vector2 gridSize;

  private HashSet<GridObject> children;
  private GridPosition[][] gridLayout;
  private Vector2 worldSize;

  public GameGrid() {
    gridColor = Color.GREEN;
    gridOrigin = new Vector2();
    gridSize = new Vector2(8, 8);
    unitSize = new Vector2(16, 16);

    children = new HashSet<GridObject>();

    resizeGrid();
  }

  public void resizeGrid() {
    GridPosition[][] newGrid = new GridPosition[(int) gridSize.x][(int) gridSize.y];
    if (gridLayout != null) {
      GridPosition[][] oldGrid = gridLayout;

      for (int x = 0; x < Math.min(newGrid.length, oldGrid.length); x++) {
        for (int y = 0; y < Math.min(newGrid[x].length, oldGrid[x].length); y++) {
          if (oldGrid[x][y] != null) {
            newGrid[x][y] = new GridPosition();
            newGrid[x][y].addAll(oldGrid[x][y]);
          }
        }
      }
    }

    gridLayout = newGrid;
    worldSize = new Vector2(gridSize.x * unitSize.x, gridSize.y * unitSize.y);
  }

  public void setUnitSize(int width, int height) {
    unitSize.set(width, height);
  }

  public void setGridSize(int width, int height) {
    gridSize.set(width, height);
  }

  public GameGridRenderer getRenderer() {
    return new GameGridRenderer(this);
  }

  public void setGridOrigin(float x, float y) {
    gridOrigin.set(x, y);
  }

  public Vector2 getWorldSize() {
    return worldSize;
  }

  public boolean addObject(GridObject gridObject) {
    if (gridObject.position == null) {
      return false;
    }

    children.add(gridObject);

    for (int x = (int) gridObject.position.x; x < gridObject.position.x + gridObject.size.x; x++) {
      for (int y = (int) gridObject.position.y; y < gridObject.position.y + gridObject.size.y; y++) {
        if (gridLayout[x][y] == null) {
          gridLayout[x][y] = new GridPosition();
        }

        gridLayout[x][y].add(gridObject);
      }
    }

    return true;
  }

  public Set<GridObject> getObjects() {
    return children;
  }

  public boolean canObjectBeAt(GridObject object, Vector2 otherPosition) {
    for (int x = (int) otherPosition.x; x < otherPosition.x + object.size.x; x++) {
      for (int y = (int) otherPosition.y; y < otherPosition.y + object.size.y; y++) {
        for (GridObject otherObject : getObjectsAtPosition(x, y)) {
          if (!otherObject.canShareSpace()) {
            return false;
          }
        }
      }
    }

    return true;
  }

  public GridPosition[][] getLayout() {
    return gridLayout;
  }

  public GridPosition getObjectsAtPosition(int x, int y) {
    return gridLayout[x][y];
  }

  public void setGridColor(float r, float g, float b, float a) {
    gridColor.set(r, g, b, a);
  }

  public Vector2 convertScreenPointToGridPoint(float x, float y) {
    float gridX = (float) Math.floor(x / unitSize.x);
    float gridY = (float) Math.floor(y / unitSize.y);

    gridX = Math.max(0, Math.min(gridX, gridSize.x - 1));
    gridY = Math.max(0, Math.min(gridY, gridSize.y - 1));

    return new Vector2(gridX, gridY);
  }
}
