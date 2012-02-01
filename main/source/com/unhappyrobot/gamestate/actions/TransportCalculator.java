package com.unhappyrobot.gamestate.actions;

import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.GridPosition;
import com.unhappyrobot.GridPositionCache;
import com.unhappyrobot.entities.*;
import com.unhappyrobot.types.RoomType;

import java.util.Set;

public class TransportCalculator extends GameStateAction {
  private final Class transportClasses[] = {Elevator.class, Stair.class};
  private final Class roomClasses[] = {Room.class, CommercialSpace.class};

  public TransportCalculator(GameGrid gameGrid, long frequency) {
    super(gameGrid, frequency);
    shouldRepeat = false;
  }

  @Override
  public void run() {
    Set<GridObject> transports = gameGrid.getInstancesOf(transportClasses);
    Set<GridObject> rooms = gameGrid.getInstancesOf(roomClasses);

    if (transports == null || rooms == null) {
      return;
    }

    for (GridObject gridObject : rooms) {
      Room room = (Room) gridObject;
      RoomType roomType = (RoomType) room.getGridObjectType();
      if (roomType.isLobby()) {
        room.setConnectedToTransport(true);
      } else {
        room.setConnectedToTransport(false);
      }
    }

    for (GridObject transport : transports) {
      Vector2 position = transport.getContentPosition();
      Vector2 size = transport.getContentSize();
      boolean isElevator = transport instanceof Elevator;
      boolean isStair = transport instanceof Stair;
      for (int x = (int) position.x; x < position.x + size.x; x++) {
        for (int y = (int) position.y; y < position.y + size.y; y++) {
          GridPosition gridPosition = GridPositionCache.instance().getPosition(x, y);
          if (gridPosition != null) {
            gridPosition.containsElevator = isElevator;
            gridPosition.containsStair = isStair;
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
}
