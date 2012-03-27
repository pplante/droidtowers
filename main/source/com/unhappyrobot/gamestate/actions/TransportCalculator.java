package com.unhappyrobot.gamestate.actions;

import com.badlogic.gdx.Gdx;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.entities.*;
import com.unhappyrobot.events.GridObjectEvent;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.grid.GridPosition;
import com.unhappyrobot.grid.GridPositionCache;
import com.unhappyrobot.math.GridPoint;
import com.unhappyrobot.types.RoomType;

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
    Gdx.app.debug(TAG, "running.");
    for (GridPosition[] gridPositions : GridPositionCache.instance().getPositions()) {
      for (GridPosition gridPosition : gridPositions) {
        gridPosition.connectedToTransit = false;
      }
    }

    for (GridObject gridObject : gameGrid.getInstancesOf(roomClasses)) {
      Room room = (Room) gridObject;
      RoomType roomType = (RoomType) room.getGridObjectType();
      if (roomType.isLobby()) {
        room.setConnectedToTransport(true, false);
      } else {
        room.setConnectedToTransport(false, false);
      }
    }

    for (GridObject transport : gameGrid.getInstancesOf(transportClasses)) {
      if (transport.getPlacementState().equals(GridObjectPlacementState.INVALID)) continue;

      for (GridPoint gridPoint : transport.getGridPointsTouched()) {
        int x = (int) gridPoint.x;
        int y = (int) gridPoint.y;

        GridPosition gridPosition = GridPositionCache.instance().getPosition(x, y);
        if (gridPosition != null) {
          gridPosition.connectedToTransit = true;
        }

        scanForRooms(x, y, -1);
        scanForRooms(x, y, 1);
      }
    }

    gameGrid.events().post(new GameGridTransportCalculationComplete(gameGrid));
  }

  private void scanForRooms(int x, int y, int stepX) {
    GridPosition gridPosition = GridPositionCache.instance().getPosition(x, y);
    while (gridPosition != null && gridPosition.size() > 0) {
      gridPosition.connectedToTransit = true;
      for (GridObject gridObject : gridPosition.getObjects()) {
        if (gridObject instanceof Room) {
          Room room = (Room) gridObject;
          room.setConnectedToTransport(true, true);
        }
      }

      x += stepX;
      gridPosition = GridPositionCache.instance().getPosition(x, y);
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
