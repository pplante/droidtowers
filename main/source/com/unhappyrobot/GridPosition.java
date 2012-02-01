package com.unhappyrobot;

import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.Transit;
import com.unhappyrobot.math.GridPoint;

import java.util.HashSet;
import java.util.Set;

public class GridPosition {
  private Set<GridObject> objects = new HashSet<GridObject>();
  public boolean containsTransit;
  public boolean connectedToTransit;
  public boolean containsElevator;
  public boolean containsStair;

  public Set<GridObject> getObjects() {
    return objects;
  }

  public void add(GridObject gridObject) {
    objects.add(gridObject);
  }

  public void remove(GridObject gridObject) {
    objects.remove(gridObject);
  }

  public int size() {
    return objects.size();
  }

  public Vector2 getBiggestTransitSize() {
    if (!containsElevator && !containsStair) {
      return null;
    }

    Vector2 biggestSize = new Vector2(Integer.MIN_VALUE, Integer.MIN_VALUE);

    for (GridObject object : objects) {
      if (object instanceof Transit) {
        GridPoint objectSize = object.getSize();
        biggestSize.x = Math.max(biggestSize.x, objectSize.x);
        biggestSize.y = Math.max(biggestSize.y, objectSize.y);
      }
    }

    return biggestSize;
  }
}
