package com.unhappyrobot;

import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.unhappyrobot.entities.Elevator;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.events.ElevatorResizeEvent;
import com.unhappyrobot.events.EventListener;
import com.unhappyrobot.events.GameGridResizeEvent;
import com.unhappyrobot.events.GridObjectAddedEvent;
import com.unhappyrobot.math.GridPoint;

import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridPositionCache extends EventListener {
  private GridPosition[][] gridPositions;
  private static GridPositionCache instance;

  private GridPositionCache() {
    gridPositions = new GridPosition[10][10];
  }

  public static GridPositionCache instance() {
    if (instance == null) {
      instance = new GridPositionCache();
    }

    return instance;
  }

  @Override
  public void receiveEvent(EventObject event) {
    if (event instanceof GridObjectAddedEvent) {
      handleGridObjectAdded((GridObjectAddedEvent) event);
    } else if (event instanceof ElevatorResizeEvent) {
      handleElevatorResizeEvent((ElevatorResizeEvent) event);
    } else if (event instanceof GameGridResizeEvent) {
      handleGameGridResizeEvent((GameGridResizeEvent) event);
    }
  }

  private void handleGameGridResizeEvent(GameGridResizeEvent event) {
    gridPositions = null;

    GameGrid gameGrid = (GameGrid) event.getSource();

    gridPositions = new GridPosition[(int) gameGrid.gridSize.x][(int) gameGrid.gridSize.y];

    for (GridObject gridObject : gameGrid.getObjects()) {
      getObjectSetForPosition(gridObject.getPosition()).objects.add(gridObject);
    }
  }

  private void handleElevatorResizeEvent(ElevatorResizeEvent event) {
    Elevator elevator = (Elevator) event.getSource();

    GridPoint currentPos = event.prevPosition.cpy();
    for (float y = 0; y < event.prevSize.y; y++) {
      getObjectSetForPosition(currentPos).objects.remove(elevator);
      currentPos.add(0, 1);
    }

    for (GridPoint gridPoint : elevator.getGridPointsOccupied()) {
      getObjectSetForPosition(gridPoint).objects.add(elevator);
    }
  }

  private void handleGridObjectAdded(GridObjectAddedEvent event) {
    GridObject gridObject = event.getSource();

    if (gridObject == null) {
      return;
    }

    List<GridPoint> pointsOccupied = gridObject.getGridPointsOccupied();
    for (GridPoint gridPoint : pointsOccupied) {
      getObjectSetForPosition(gridPoint).objects.add(gridObject);
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
