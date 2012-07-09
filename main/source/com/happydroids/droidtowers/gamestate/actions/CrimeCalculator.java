/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.ServiceRoom;
import com.happydroids.droidtowers.events.GridObjectEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.math.GridPoint;

import static com.happydroids.droidtowers.types.ProviderType.ELEVATOR;
import static com.happydroids.droidtowers.types.ProviderType.SECURITY;

public class CrimeCalculator extends GameGridAction {
  private static final String TAG = CrimeCalculator.class.getSimpleName();

  public CrimeCalculator(GameGrid gameGrid, float frequency) {
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
        gridPosition.connectedToSecurity = false;
        gridPosition.distanceFromSecurity = 0f;
        gridPosition.normalizedDistanceFromSecurity = 0f;
      }
    }

    for (GridObject gridObject : gameGrid.getObjects()) {
      gridObject.setConnectedToSecurity(gridObject.provides(SECURITY, ELEVATOR));
    }

    for (GridObject serviceRoom : gameGrid.getInstancesOf(ServiceRoom.class)) {
      if (!serviceRoom.isPlaced()) continue;

      for (GridPoint gridPoint : serviceRoom.getGridPointsTouched()) {
        int x = gridPoint.x;
        int y = gridPoint.y;

        GridPosition gridPosition = gameGrid.positionCache().getPosition(x, y);
        if (gridPosition != null) {
          gridPosition.connectedToSecurity = true;

          scanForRooms(x, y, -1, gridPosition.x);
          scanForRooms(x, y, 1, gridPosition.x);
        }
      }
    }

    gameGrid.positionCache().normalizeSecurityDistances();
  }

  private void scanForRooms(int x, int y, int stepX, int parentPosX) {
    GridPosition gridPosition = gameGrid.positionCache().getPosition(x, y);
    while (gridPosition != null && gridPosition.size() > 0) {
      gridPosition.connectedToSecurity = true;
      gridPosition.distanceFromSecurity = Math.abs(x - parentPosX);
      for (GridObject gridObject : gridPosition.getObjects()) {
        gridObject.setConnectedToSecurity(true);
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
