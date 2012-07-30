/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.types.RoomType;

import java.util.ArrayList;

public class PopulationCalculator extends GameGridAction {

  private final AvatarLayer avatarLayer;

  public PopulationCalculator(GameGrid gameGrid, AvatarLayer avatarLayer, float frequency) {
    super(gameGrid, frequency);
    this.avatarLayer = avatarLayer;
  }

  @Override
  public void run() {
    ArrayList<GridObject> rooms = gameGrid.getInstancesOf(Room.class);
    int supportedResidency = 0;
    int maxPopulation = 0;

    if (rooms != null) {
      for (int i = 0, roomsSize = rooms.size(); i < roomsSize; i++) {
        GridObject gridObject = rooms.get(i);
        maxPopulation += ((RoomType) gridObject.getGridObjectType()).getPopulationMax();
        supportedResidency += ((Room) gridObject).getNumSupportedResidents();
      }
    }

    Player.instance().setPopulationMax(maxPopulation);
    Player.instance().setSupportedResidency(supportedResidency);
    Player.instance().setPopulationResidency(avatarLayer.getNumAvatars());
  }
}
