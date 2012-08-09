/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.grid;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.happydroids.droidtowers.entities.Elevator;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Stair;
import com.happydroids.droidtowers.math.GridPoint;

import static com.happydroids.droidtowers.TowerConsts.GRID_UNIT_SIZE;

public class GridPosition {
  public final int x;
  public final int y;
  private final Vector2 worldVector;
  private Array<GridObject> objects = new Array<GridObject>();
  public Elevator elevator;
  public Stair stair;
  public boolean connectedToTransit;
  public float distanceFromTransit;
  public float normalizedDistanceFromTransit;
  public boolean connectedToSecurity;
  public float distanceFromSecurity;
  public float normalizedDistanceFromSecurity;
  private float maxNoiseLevel;
  private float maxCrimeLevel;
  private float noiseLevel;
  private float crimeLevel;


  public GridPosition(int x, int y) {
    this.x = x;
    this.y = y;
    worldVector = new Vector2(x * GRID_UNIT_SIZE, y * GRID_UNIT_SIZE);
  }

  public Array<GridObject> getObjects() {
    return objects;
  }

  public void add(GridObject gridObject) {
    if (!objects.contains(gridObject, true)) {
      objects.add(gridObject);

      if (gridObject instanceof Elevator) {
        GridPoint position = gridObject.getPosition();
        GridPoint size = gridObject.getSize();
        if (position.x == x && (position.y == y || position.y + size.y == y)) {
          return;
        }

        elevator = (Elevator) gridObject;
      } else if (gridObject instanceof Stair) {
        stair = (Stair) gridObject;
      }
    }
  }

  public void remove(GridObject gridObject) {
    if (objects.contains(gridObject, true)) {
      objects.removeValue(gridObject, true);

      if (gridObject instanceof Elevator) {
        elevator = null;
      } else if (gridObject instanceof Stair) {
        stair = null;
      }
    }
  }

  public int size() {
    return objects.size;
  }

  public boolean isEmpty() {
    return objects.size == 0;
  }

  public boolean contains(GridObject gridObject) {
    return objects.contains(gridObject, true);
  }

  public Vector2 worldPoint() {
    return worldVector;
  }

  public void findMaxValues() {
    maxNoiseLevel = 0;
    maxCrimeLevel = 0;
    for (GridObject gridObject : objects) {
      maxNoiseLevel = Math.max(gridObject.getNoiseLevel(), maxNoiseLevel);
      maxCrimeLevel = Math.max(gridObject.getCrimeLevel(), maxCrimeLevel);
    }
  }

  public void calculateValuesForPosition(GridPosition[][] gridPositions) {
    noiseLevel = 0;
    crimeLevel = 0;

    float totalNoise = 0f;
    float totalCrime = 0f;
    int distance = 2;
    for (int xx = x - distance; xx < x + distance; xx++) {
      for (int yy = y - distance; yy < y + distance; yy++) {
        if (xx < 0 || yy < 0 || xx >= gridPositions.length || yy >= gridPositions[xx].length) {
          continue;
        }
        if (xx == x && yy == y) {
          continue;
        }

        totalNoise += gridPositions[xx][yy].maxNoiseLevel;
        totalCrime += gridPositions[xx][yy].maxCrimeLevel;
      }
    }

    noiseLevel = Math.min(1f, maxNoiseLevel + totalNoise / 8);
    crimeLevel = Math.min(1f, maxCrimeLevel + totalCrime / 8);
  }

  public float getNoiseLevel() {
    return noiseLevel;
  }

  public float getCrimeLevel() {
    return crimeLevel;
  }

  @SuppressWarnings("RedundantIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof GridPosition)) {
      return false;
    }

    GridPosition that = (GridPosition) o;

    if (connectedToTransit != that.connectedToTransit) {
      return false;
    }
    if (x != that.x) {
      return false;
    }
    if (y != that.y) {
      return false;
    }
    if (elevator != null ? !elevator.equals(that.elevator) : that.elevator != null) {
      return false;
    }
    if (objects != null ? !objects.equals(that.objects) : that.objects != null) {
      return false;
    }
    if (stair != null ? !stair.equals(that.stair) : that.stair != null) {
      return false;
    }

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
