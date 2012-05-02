/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.grid;

import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.entities.Elevator;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.GuavaSet;
import com.happydroids.droidtowers.entities.Stair;
import com.happydroids.droidtowers.math.GridPoint;

public class GridPosition {
  public final int x;
  public final int y;
  private GuavaSet<GridObject> objects = new GuavaSet<GridObject>();
  public boolean connectedToTransit;
  public Elevator elevator;
  public Stair stair;

  public GridPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public GuavaSet<GridObject> getObjects() {
    return objects;
  }

  public void add(GridObject gridObject) {
    if (objects.add(gridObject)) {

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

  public GridPoint toGridPoint() {
    return new GridPoint(x, y);
  }

  public Vector2 toWorldVector2() {
    return toGridPoint().toWorldVector2();
  }

  @SuppressWarnings("RedundantIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GridPosition)) return false;

    GridPosition that = (GridPosition) o;

    if (connectedToTransit != that.connectedToTransit) return false;
    if (x != that.x) return false;
    if (y != that.y) return false;
    if (elevator != null ? !elevator.equals(that.elevator) : that.elevator != null) return false;
    if (objects != null ? !objects.equals(that.objects) : that.objects != null) return false;
    if (stair != null ? !stair.equals(that.stair) : that.stair != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = x;
    result = 31 * result + y;
    result = 31 * result + (objects != null ? objects.hashCode() : 0);
    result = 31 * result + (connectedToTransit ? 1 : 0);
    result = 31 * result + (elevator != null ? elevator.hashCode() : 0);
    result = 31 * result + (stair != null ? stair.hashCode() : 0);
    return result;
  }
}
