package com.unhappyrobot;

import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.events.ElevatorResizeEvent;
import com.unhappyrobot.events.GameEvents;
import com.unhappyrobot.events.GameGridResizeEvent;
import com.unhappyrobot.events.GridObjectAddedEvent;
import com.unhappyrobot.math.GridPoint;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridPositionCache {
  private GridPosition[][] gridPositions;
  private static GridPositionCache instance;

  private GridPositionCache() {
    gridPositions = new GridPosition[10][10];
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

    Vector2 gridSize = event.gameGrid.gridSize;
    gridPositions = new GridPosition[(int) gridSize.x][(int) gridSize.y];

    for (GridObject gridObject : event.gameGrid.getObjects()) {
      getObjectSetForPosition(gridObject.getPosition()).objects.add(gridObject);
    }
  }

  @Subscribe
  public void handleElevatorResizeEvent(ElevatorResizeEvent event) {
    GridPoint currentPos = event.prevPosition.cpy();
    for (float y = 0; y < event.prevSize.y; y++) {
      getObjectSetForPosition(currentPos).objects.remove(event.gridObject);
      currentPos.add(0, 1);
    }

    for (GridPoint gridPoint : event.gridObject.getGridPointsOccupied()) {
      getObjectSetForPosition(gridPoint).objects.add(event.gridObject);
    }
  }

  @Subscribe
  public void handleGridObjectAdded(GridObjectAddedEvent event) {
    if (event.gridObject == null) {
      return;
    }

    List<GridPoint> pointsOccupied = event.gridObject.getGridPointsOccupied();
    for (GridPoint gridPoint : pointsOccupied) {
      getObjectSetForPosition(gridPoint).objects.add(event.gridObject);
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

    GridPoint currentPos = position.cpy();
    for (int x = 0; x < size.x; x++) {
      for (int y = 0; y < size.y; y++) {
        objects.addAll(getObjectSetForPosition(currentPos).objects);
        currentPos.add(0, 1);
      }
      currentPos.add(1, 0);
    }

    if (gridObjectsToIgnore != null) {
      objects.removeAll(Lists.newArrayList(gridObjectsToIgnore));
    }

    return objects;
  }

  private class GridPosition {
    public Set<GridObject> objects = new HashSet<GridObject>();
  }
}
