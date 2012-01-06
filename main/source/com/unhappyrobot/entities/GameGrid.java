package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.math.Bounds2d;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameGrid {
  public Vector2 unitSize;
  public Color gridColor;
  public Vector2 gridOrigin;
  public Vector2 gridSize;

  private HashSet<GridObject> objects;
  private List<GridObject> objectsRenderOrder;
  private Vector2 worldSize;

  public GameGrid() {
    gridColor = Color.GREEN;
    gridOrigin = new Vector2();
    gridSize = new Vector2(8, 8);
    unitSize = new Vector2(16, 16);
    updateWorldSize();

    objects = new HashSet<GridObject>();
  }

  public void updateWorldSize() {
    worldSize = new Vector2(gridSize.x * unitSize.x, gridSize.y * unitSize.y);
  }

  public void setUnitSize(int width, int height) {
    unitSize.set(width, height);
    updateWorldSize();
  }

  public void setGridSize(int width, int height) {
    gridSize.set(width, height);
    updateWorldSize();
  }

  public void setGridOrigin(float x, float y) {
    gridOrigin.set(x, y);
  }

  public void setGridColor(float r, float g, float b, float a) {
    gridColor.set(r, g, b, a);
  }

  public GameGridRenderer getRenderer() {
    return new GameGridRenderer(this);
  }

  public Vector2 getWorldSize() {
    return worldSize.cpy();
  }

  public boolean addObject(GridObject gridObject) {
    if (gridObject.position == null) {
      return false;
    }

    gridObject.position.set(clampPosition(gridObject.position, gridObject.size));

    objects.add(gridObject);
    objectsRenderOrder = Ordering.natural().onResultOf(new Function<GridObject, Integer>() {
      public Integer apply(@Nullable GridObject gridObject) {
        if (gridObject != null) {
          return gridObject.getGridObjectType().getZIndex();
        }
        return 1000000;
      }
    }).sortedCopy(objects);

    return true;
  }

  public Set<GridObject> getObjects() {
    return objects;
  }

  public boolean canObjectBeAt(GridObject gridObject) {
    if (!gridObject.canBeAt()) {
      return false;
    }

    Bounds2d boundsOfGridObjectToCheck = gridObject.getBounds();
    for (GridObject child : objects) {
      if (child != gridObject) {
        if (child.getBounds().overlaps(boundsOfGridObjectToCheck) && !child.canShareSpace()) {
          return false;
        }
      }
    }

    return true;
  }

  public List<GridObject> getObjectsAt(Bounds2d targetBounds) {
    List<GridObject> objects = Lists.newArrayList();

    for (GridObject child : this.objects) {
      if (child.getBounds().overlaps(targetBounds)) {
        objects.add(child);
      }
    }

    return objects;
  }

  public Vector2 convertScreenPointToGridPoint(float x, float y) {
    float gridX = (float) Math.floor(x / unitSize.x);
    float gridY = (float) Math.floor(y / unitSize.y);

    gridX = Math.max(0, Math.min(gridX, gridSize.x - 1));
    gridY = Math.max(0, Math.min(gridY, gridSize.y - 1));

    return new Vector2(gridX, gridY);
  }

  public Vector2 clampPosition(Vector2 position, Vector2 size) {
    if (position.x < 0) {
      position.x = 0;
    } else if (position.x + size.x > gridSize.x) {
      position.x = gridSize.x - size.x;
    }

    if (position.y < 0) {
      position.y = 0;
    } else if (position.y + size.y > gridSize.y) {
      position.y = gridSize.y - size.y;
    }

    return position;
  }

  public void removeObject(GridObject gridObject) {
    objects.remove(gridObject);
  }

  public float gridScaleX(float x) {
    return unitSize.x * x;
  }

  public float gridScaleY(float y) {
    return unitSize.y * y;
  }

  public List<GridObject> getObjectsInRenderOrder() {
    return objectsRenderOrder;
  }
}
