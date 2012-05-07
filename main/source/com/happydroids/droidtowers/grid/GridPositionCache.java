/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.grid;

import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
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
  public static final Vector2 SINGLE_POINT = new Vector2(1, 1);

  private GridPosition[][] gridPositions;
  private Vector2 gridSize;
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
    gridPositions = new GridPosition[(int) gridSize.x + 1][(int) gridSize.y + 1];

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
    System.out.println(event);
    for (int x = (int) event.prevPosition.x; x < event.prevPosition.x + event.prevSize.x; x += 1) {
      for (int y = (int) event.prevPosition.y; y < event.prevPosition.y + event.prevSize.y; y += 1) {
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
    List<GridPoint> pointsOccupied = gridObject.getGridPointsOccupied();
    for (GridPoint gridPoint : pointsOccupied) {
      GridPosition position = getObjectSetForPosition(gridPoint);
      if (position != null) {
        position.remove(gridObject);
      }
    }
  }

  private GridPosition getObjectSetForPosition(GridPoint gridPoint) {
    Set<GridObject> objectsAtPoint;

    int x = (int) gridPoint.x;
    int y = (int) gridPoint.y;

    if (!checkBounds(x, y)) return null;

    if (gridPositions[x][y] == null) {
      gridPositions[x][y] = new GridPosition(x, y);
    }

    return gridPositions[x][y];
  }

  public Set<GridObject> getObjectsAt(GridPoint position, Vector2 size, GridObject... gridObjectsToIgnore) {
    Set<GridObject> objects = new HashSet<GridObject>();

    float maxX = Math.min(gridSize.x, position.x + size.x);
    float maxY = Math.min(gridSize.y, position.y + size.y);

    GridPoint currentPos = position.cpy();
    for (float x = position.x; x < maxX; x++) {
      for (float y = position.y; y < maxY; y++) {
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
    return getObjectsAt(gridPoint, SINGLE_POINT);
  }

  public GridPosition getPosition(Vector2 gridPoint) {
    return getPosition((int) gridPoint.x, (int) gridPoint.y);
  }

  public GridPosition getPosition(int x, int y) {
    if (!checkBounds(x, y)) return null;

    return gridPositions[x][y];
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

  public Vector2 getGridSize() {
    return gridSize;
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
}
