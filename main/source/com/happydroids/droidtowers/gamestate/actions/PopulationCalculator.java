/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.entities.CommercialSpace;
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
    for (GridObject gridObject : rooms) {
      if (gridObject.isConnectedToTransport()) {
        Room room = (Room) gridObject;
        int populationMax = ((RoomType) gridObject.getGridObjectType()).getPopulationMax();
        float populationCurrent = room.getNumResidents();
        double populationDesired = Math.ceil(populationMax * gridObject.getDesirability());
      }
    }


    ArrayList<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
    int currentResidency = 0;
    int attractedPopulation = 0;
    int maxPopulation = 0;

    if (rooms != null) {
      for (GridObject gridObject : rooms) {
        Room room = (Room) gridObject;
        maxPopulation += room.getNumSupportedResidents();
        currentResidency += room.getNumResidents();
      }
    }

    if (commercialSpaces != null) {
      for (GridObject gridObject : commercialSpaces) {
        CommercialSpace commercialSpace = (CommercialSpace) gridObject;
        attractedPopulation += commercialSpace.getAttractedPopulation();
      }
    }

    Player.instance().setPopulationMax(maxPopulation);
    Player.instance().setPopulationResidency(currentResidency);
  }
}
