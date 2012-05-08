/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.grid;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.events.GameGridResizeEvent;
import com.happydroids.droidtowers.events.GridObjectBoundsChangeEvent;
import com.happydroids.droidtowers.events.GridObjectPlacedEvent;
import com.happydroids.droidtowers.events.GridObjectRemovedEvent;
import com.happydroids.droidtowers.math.GridPoint;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridPositionCache {
  private GridPosition[][] gridPositions;
  private GridPoint gridSize;
  private final GameGrid gameGrid;
  private float[][] noiseLevels;

  public GridPositionCache(GameGrid gameGrid) {
    this.gameGrid = gameGrid;
    this.gameGrid.events().register(this);
  }

  @Subscribe
  public void handleGameGridResizeEvent(GameGridResizeEvent event) {
    gridPositions = null;

    gridSize = gameGrid.getGridSize();
    System.out.println("gridSize = " + gridSize);
    gridPositions = new GridPosition[gridSize.x + 1][gridSize.y + 1];

    for (int x = 0; x <= gridSize.x; x++) {
      for (int y = 0; y <= gridSize.y; y++) {
        gridPositions[x][y] = new GridPosition(x, y);
      }
    }
  }

  private void addGridObjectToPosition(GridObject gridObject) {
    List<GridPoint> pointsOccupied = gridObject.getGridPointsOccupied();
    for (GridPoint gridPoint : pointsOccupied) {
      GridPosition position = getObjectSetForPosition(gridPoint);
      if (position != null) {
        position.add(gridObject);
      }
    }
  }

  @Subscribe
  public void GameGrid_onGridObjectPlaced(GridObjectPlacedEvent event) {
    GridObject gridObject = event.gridObject;
    if (!gridObject.isPlaced()) {
      return;
    }

    addGridObjectToPosition(gridObject);
  }

  @Subscribe
  public void GameGrid_onGridObjectBoundsChange(GridObjectBoundsChangeEvent event) {
    GridObject gridObject = event.gridObject;
    if (!gridObject.isPlaced()) {
      return;
    }

    for (int x = event.prevPosition.x; x < event.prevPosition.x + event.prevSize.x; x++) {
      for (int y = event.prevPosition.y; y < event.prevPosition.y + event.prevSize.y; y++) {
        GridPosition position = getPosition(x, y);
        if (position != null) {
          position.remove(event.gridObject);
        }
      }
    }

    addGridObjectToPosition(event.gridObject);
  }

  @Subscribe
  public void GameGrid_onGridObjectRemoved(GridObjectRemovedEvent event) {
    GridObject gridObject = event.gridObject;
    for (GridPoint gridPoint : gridObject.getGridPointsOccupied()) {
      GridPosition position = getObjectSetForPosition(gridPoint);
      if (position != null) {
        position.remove(gridObject);
      }
    }
  }

  private GridPosition getObjectSetForPosition(GridPoint gridPoint) {
    return !checkBounds(gridPoint.x, gridPoint.y) ? null : gridPositions[gridPoint.x][gridPoint.y];
  }

  public Set<GridObject> getObjectsAt(GridPoint position, GridPoint size, GridObject... gridObjectsToIgnore) {
    Set<GridObject> objects = new HashSet<GridObject>();

    int maxX = Math.min(gridSize.x, position.x + size.x);
    int maxY = Math.min(gridSize.y, position.y + size.y);

    GridPoint currentPos = position.cpy();
    for (int x = position.x; x < maxX; x++) {
      for (int y = position.y; y < maxY; y++) {
        currentPos.set(x, y);

        GridPosition forPosition = getObjectSetForPosition(currentPos);
        if (forPosition != null) {
          objects.addAll(forPosition.getObjects());
        }
      }
    }

    if (gridObjectsToIgnore != null) {
      objects.removeAll(Lists.newArrayList(gridObjectsToIgnore));
    }

    return objects;
  }

  public Set<GridObject> getObjectsAt(GridPoint gridPoint) {
    return getObjectsAt(gridPoint, TowerConsts.SINGLE_POINT);
  }

  public GridPosition getPosition(GridPoint gridPoint) {
    return getPosition(gridPoint.x, gridPoint.y);
  }

  public GridPosition getPosition(int x, int y) {
    return !checkBounds(x, y) ? null : gridPositions[x][y];
  }

  private boolean checkBounds(int x, int y) {
    if (x >= gridSize.x || x < 0) {
      return false;
    } else if (y >= gridSize.y || y < 0) {
      return false;
    }
    return true;
  }

  public GridPosition[][] getPositions() {
    return gridPositions;
  }

  public void pauseEvents() {
    gameGrid.events().unregister(this);
  }


  public void resumeEvents() {
    gameGrid.events().register(this);
  }

  public void updateNoiseLevels() {
    for (GridPosition[] row : gridPositions) {
      for (GridPosition position : row) {
        position.findMaxNoise();
      }
    }

    for (GridPosition[] row : gridPositions) {
      for (GridPosition position : row) {
        position.calculateNoise(gridPositions);
      }
    }
  }

  public void normalizeTransitDistances() {
    float minVal = Float.MAX_VALUE;
    float maxVal = Float.MIN_VALUE;
    for (GridPosition[] row : gridPositions) {
      for (GridPosition position : row) {
        minVal = Math.min(position.distanceFromTransit, minVal);
        maxVal = Math.max(position.distanceFromTransit, maxVal);
      }
    }
    System.out.println("maxVal = " + maxVal);
    if (maxVal != Float.MIN_VALUE) {
      for (GridPosition[] row : gridPositions) {
        for (GridPosition position : row) {
          if (position.distanceFromTransit > 5) {
            position.normalizedDistanceFromTransit = position.distanceFromTransit / maxVal;
          } else {
            position.normalizedDistanceFromTransit = 0f;
          }
        }
      }
    }
  }
}
