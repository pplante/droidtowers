package com.unhappyrobot;

import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.Elevator;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.Stair;
import com.unhappyrobot.entities.Transit;

import java.util.HashSet;
import java.util.Set;

public class GridPosition {
  private Set<GridObject> objects = new HashSet<GridObject>();
  public boolean containsTransit;
  public boolean connectedToTransit;
  public Elevator elevator;
  public Stair stair;

  public Set<GridObject> getObjects() {
    return objects;
  }

  public void add(GridObject gridObject) {
    if (objects.add(gridObject)) {
      containsTransit = gridObject instanceof Transit;

      if (gridObject instanceof Elevator) {
        elevator = (Elevator) gridObject;
      } else if (gridObject instanceof Stair) {
        stair = (Stair) gridObject;
      }
    }
  }

  public void remove(GridObject gridObject) {
    if (objects.remove(gridObject)) {
      if (gridObject instanceof Elevator) {
        elevator = null;
      } else if (gridObject instanceof Stair) {
        stair = null;
      }

      containsTransit = stair != null || elevator != null;
    }
  }

  public int size() {
    return objects.size();
  }

  public Vector2 getBiggestTransitSize() {
    if (elevator == null && stair == null) {
      return null;
    }

    return new Vector2(Math.max(stair.getSize().x, elevator.getSize().x), Math.max(stair.getSize().y, elevator.getSize().y));
  }
}
