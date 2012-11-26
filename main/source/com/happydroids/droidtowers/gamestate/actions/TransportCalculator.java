/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.entities.*;
import com.happydroids.droidtowers.events.GridObjectEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.types.ProviderType;

import java.util.List;

public class TransportCalculator extends GameGridAction {
  private static final String TAG = TransportCalculator.class.getSimpleName();

  private final Class transportClasses[] = {Elevator.class, Stair.class, ParkingGarageRamp.class, ParkingGarageSpace.class};

  public TransportCalculator(GameGrid gameGrid, float frequency) {
    super(gameGrid, frequency, false);

    gameGrid.events().register(this);
  }

  @Override
  public void run() {
    GridPosition[][] positions = gameGrid.positionCache().getPositions();
    for (int i = 0, positionsLength = positions.length; i < positionsLength; i++) {
      GridPosition[] gridPositions = positions[i];
      for (GridPosition gridPosition : gridPositions) {
        gridPosition.connectedToTransit = false;
        gridPosition.distanceFromTransit = 0f;
      }
    }

    for (GridObject gridObject : gameGrid.getObjects()) {
      gridObject.setConnectedToTransport(gridObject.provides(ProviderType.LOBBY));
    }

    for (GridObject transport : gameGrid.getInstancesOf(transportClasses)) {
      if (!transport.isPlaced()) {
        continue;
      }

      List<GridPoint> gridPointsTouched = transport.getGridPointsTouched();
      for (int i1 = 0, gridPointsTouchedSize = gridPointsTouched.size(); i1 < gridPointsTouchedSize; i1++) {
        GridPoint gridPoint = gridPointsTouched.get(i1);
        int x = gridPoint.x;
        int y = gridPoint.y;

        GridPosition gridPosition = gameGrid.positionCache().getPosition(x, y);
        if (gridPosition != null) {
          gridPosition.connectedToTransit = !(transport instanceof Elevator) || ((Elevator) transport).servicesFloor(gridPosition.y);

          if (gridPosition.connectedToTransit) {
            scanForRooms(x, y, -1, gridPosition.x);
            scanForRooms(x, y, 1, gridPosition.x);
          }
        }
      }
    }

    gameGrid.positionCache().normalizeTransitDistances();
  }

  @Subscribe
  public void update(GridObjectEvent event) {
    if (isPaused()) {
      return;
    }

    reset();
  }

  private void scanForRooms(int x, int y, int stepX, int transitX) {
    GridPosition gridPosition = gameGrid.positionCache().getPosition(x, y);
    while (gridPosition != null && gridPosition.size() > 0) {
      gridPosition.connectedToTransit = true;
      gridPosition.distanceFromTransit = Math.abs(x - transitX);
      for (GridObject gridObject : gridPosition.getObjects()) {
        if (gridObject instanceof Room) {
          Room room = (Room) gridObject;
          room.setConnectedToTransport(true);
        }
      }

      x += stepX;
      gridPosition = gameGrid.positionCache().getPosition(x, y);
    }
  }

  @Override
  public void pause() {
    super.pause();

    gameGrid.events().unregister(this);
  }

  @Override
  public void unpause() {
    super.unpause();

    gameGrid.events().register(this);
  }
}
