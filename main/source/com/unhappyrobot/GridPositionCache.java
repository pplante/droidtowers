package com.unhappyrobot;

import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.events.*;
import com.unhappyrobot.math.GridPoint;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridPositionCache {
  public static final Vector2 SINGLE_POINT = new Vector2(1, 1);
  private GridPosition[][] gridPositions;
  private static GridPositionCache instance;
  private Vector2 gridSize;

  private GridPositionCache() {
    GameEvents.register(this);
  }

  public static GridPositionCache instance() {
    if (instance == null) {
      instance = new GridPositionCache();
    }

    return instance;
  }

  @Subscribe
  public void handleGameGridResizeEvent(GameGridResizeEvent event) {
    gridPositions = null;

    gridSize = event.gameGrid.gridSize;
    gridPositions = new GridPosition[(int) gridSize.x][(int) gridSize.y];

    for (int x = 0; x < gridSize.x; x++) {
      for (int y = 0; y < gridSize.y; y++) {
        gridPositions[x][y] = new GridPosition();
      }
    }

    for (GridObject gridObject : event.gameGrid.getObjects()) {
      getObjectSetForPosition(gridObject.getPosition()).add(gridObject);
    }
  }

  @Subscribe
  public void handleElevatorResizeEvent(ElevatorResizeEvent event) {
    GridPoint currentPos = event.prevPosition.cpy();
    for (float y = 0; y < event.prevSize.y; y++) {
      getObjectSetForPosition(currentPos).remove(event.gridObject);
      currentPos.add(0, 1);
    }

    for (GridPoint gridPoint : event.gridObject.getGridPointsOccupied()) {
      getObjectSetForPosition(gridPoint).add(event.gridObject);
    }
  }

  @Subscribe
  public void handleGridObjectAdded(GridObjectAddedEvent event) {
    if (event.gridObject == null) {
      return;
    }

    List<GridPoint> pointsOccupied = event.gridObject.getGridPointsOccupied();
    for (GridPoint gridPoint : pointsOccupied) {
      getObjectSetForPosition(gridPoint).add(event.gridObject);
    }
  }

  @Subscribe
  public void handleGridObjectRemoved(GridObjectRemovedEvent event) {
    if (event.gridObject == null) {
      return;
    }

    List<GridPoint> pointsOccupied = event.gridObject.getGridPointsOccupied();
    for (GridPoint gridPoint : pointsOccupied) {
      getObjectSetForPosition(gridPoint).remove(event.gridObject);
    }
  }

  private GridPosition getObjectSetForPosition(GridPoint gridPoint) {
    Set<GridObject> objectsAtPoint;

    int x = (int) gridPoint.x;
    int y = (int) gridPoint.y;

    if (gridPositions[x][y] == null) {
      gridPositions[x][y] = new GridPosition();
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
        objects.addAll(getObjectSetForPosition(currentPos).getObjects());
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
    if (x == gridSize.x || x < 0) {
      return null;
    } else if (y == gridSize.y || y < 0) {
      return null;
    }

    return gridPositions[x][y];
  }

  public GridPosition[][] getPositions() {
    return gridPositions;
  }
}
