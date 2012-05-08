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
import com.happydroids.droidtowers.types.RoomType;

public class TransportCalculator extends GameGridAction {
  private static final String TAG = TransportCalculator.class.getSimpleName();

  private final Class transportClasses[] = {Elevator.class, Stair.class};
  private final Class roomClasses[] = {Room.class, CommercialSpace.class};

  public TransportCalculator(GameGrid gameGrid, float frequency) {
    super(gameGrid, frequency, false);

    gameGrid.events().register(this);
  }

  @Subscribe
  public void update(GridObjectEvent event) {
    if (isPaused()) return;

    reset();
  }

  @Override
  public void run() {
    for (GridPosition[] gridPositions : gameGrid.positionCache().getPositions()) {
      for (GridPosition gridPosition : gridPositions) {
        gridPosition.connectedToTransit = false;
        gridPosition.distanceFromTransit = 0f;
      }
    }

    for (GridObject gridObject : gameGrid.getInstancesOf(roomClasses)) {
      Room room = (Room) gridObject;
      RoomType roomType = (RoomType) room.getGridObjectType();
      if (roomType.provides(ProviderType.LOBBY)) {
        room.setConnectedToTransport(true, false);
      } else {
        room.setConnectedToTransport(false, false);
      }
    }

    for (GridObject transport : gameGrid.getInstancesOf(transportClasses)) {
      if (!transport.isPlaced()) continue;

      for (GridPoint gridPoint : transport.getGridPointsTouched()) {
        int x = gridPoint.x;
        int y = gridPoint.y;

        GridPosition gridPosition = gameGrid.positionCache().getPosition(x, y);
        if (gridPosition != null) {
          gridPosition.connectedToTransit = true;
        }

        scanForRooms(x, y, -1, gridPosition.x);
        scanForRooms(x, y, 1, gridPosition.x);
      }
    }

    gameGrid.positionCache().normalizeTransitDistances();

    gameGrid.events().post(new GameGridTransportCalculationComplete(gameGrid));
  }

  private void scanForRooms(int x, int y, int stepX, int transitX) {
    GridPosition gridPosition = gameGrid.positionCache().getPosition(x, y);
    while (gridPosition != null && gridPosition.size() > 0) {
      gridPosition.connectedToTransit = true;
      gridPosition.distanceFromTransit = Math.abs(x - transitX);
      for (GridObject gridObject : gridPosition.getObjects()) {
        if (gridObject instanceof Room) {
          Room room = (Room) gridObject;
          room.setConnectedToTransport(true, true);
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
