package com.unhappyrobot.gamestate.actions;

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
      if (!roomType.isLobby()) {
        room.setConnectedToTransport(false);
      }

      for (GridObject transport : transports) {
        if (room.getPosition().y >= transport.getContentPosition().y && room.getPosition().y <= transport.getContentPosition().y + transport.getContentSize().y) {
          room.setConnectedToTransport(true);
          break;
        }
      }
    }
  }
}
