package com.unhappyrobot.gamestate.actions;

import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.Subscribe;
import com.unhappyrobot.GridPosition;
import com.unhappyrobot.GridPositionCache;
import com.unhappyrobot.entities.*;
import com.unhappyrobot.events.GameEvents;
import com.unhappyrobot.events.GridObjectEvent;
import com.unhappyrobot.types.RoomType;

public class TransportCalculator extends GameStateAction {
  private final Class transportClasses[] = {Elevator.class, Stair.class};
  private final Class roomClasses[] = {Room.class, CommercialSpace.class};

  public TransportCalculator(GameGrid gameGrid, long frequency) {
    super(gameGrid, frequency, false);

    GameEvents.register(this);
  }

  @Subscribe
  public void update(GridObjectEvent event) {
    if (isPaused()) return;

    resetInterval();
  }

  @Override
  public void run() {
    System.out.println("running!!!!");

    for (GridPosition[] gridPositions : GridPositionCache.instance().getPositions()) {
      for (GridPosition gridPosition : gridPositions) {
        gridPosition.connectedToTransit = false;
      }
    }

    for (GridObject gridObject : gameGrid.getInstancesOf(roomClasses)) {
      Room room = (Room) gridObject;
      RoomType roomType = (RoomType) room.getGridObjectType();
      if (roomType.isLobby()) {
        room.setConnectedToTransport(true);
      } else {
        room.setConnectedToTransport(false);
      }
    }

    for (GridObject transport : gameGrid.getInstancesOf(transportClasses)) {
      if (transport.getPlacementState().equals(GridObjectPlacementState.INVALID)) continue;

      Vector2 position = transport.getContentPosition();
      Vector2 size = transport.getContentSize();

      for (int x = (int) position.x; x < position.x + size.x; x++) {
        for (int y = (int) position.y; y < position.y + size.y; y++) {
          GridPosition gridPosition = GridPositionCache.instance().getPosition(x, y);
          if (gridPosition != null) {
            gridPosition.connectedToTransit = true;
          }

          scanForRooms(x, y, -1);
          scanForRooms(x, y, 1);
        }
      }
    }
  }

  private void scanForRooms(int x, int y, int stepX) {
    GridPosition gridPosition = GridPositionCache.instance().getPosition(x, y);
    while (gridPosition != null && gridPosition.size() > 0) {
      gridPosition.connectedToTransit = true;
      for (GridObject gridObject : gridPosition.getObjects()) {
        if (gridObject instanceof Room) {
          Room room = (Room) gridObject;
          room.setConnectedToTransport(true);
        }
      }

      x += stepX;
      gridPosition = GridPositionCache.instance().getPosition(x, y);
    }
  }

  @Override
  public void pause() {
    super.pause();

    GameEvents.unregister(this);
  }

  @Override
  public void unpause() {
    super.unpause();

    GameEvents.register(this);
  }
}
