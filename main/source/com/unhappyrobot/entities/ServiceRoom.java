package com.unhappyrobot.entities;

import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.types.ServiceRoomType;

public class ServiceRoom extends CommercialSpace {
  public ServiceRoom(ServiceRoomType serviceRoomType, GameGrid gameGrid) {
    super(serviceRoomType, gameGrid);
  }
}
