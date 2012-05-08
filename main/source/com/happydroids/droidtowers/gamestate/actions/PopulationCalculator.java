/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.types.RoomType;

import java.util.Set;

public class PopulationCalculator extends GameGridAction {
  public PopulationCalculator(GameGrid gameGrid, float frequency) {
    super(gameGrid, frequency);
  }

  @Override
  public void run() {
    Set<GridObject> rooms = gameGrid.getInstancesOf(Room.class);
    Set<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
    int currentResidency = 0;
    int attractedPopulation = 0;
    int maxPopulation = 0;

    if (rooms != null) {
      for (GridObject gridObject : rooms) {
        Room room = (Room) gridObject;
        room.updatePopulation();

        maxPopulation += ((RoomType) gridObject.getGridObjectType()).getPopulationMax();
        currentResidency += room.getCurrentResidency();
      }
    }

    if (commercialSpaces != null) {
      for (GridObject gridObject : commercialSpaces) {
        CommercialSpace commercialSpace = (CommercialSpace) gridObject;
        commercialSpace.updatePopulation();

        attractedPopulation += commercialSpace.getAttractedPopulation();
      }
    }

    Player.instance().setPopulationMax(maxPopulation);
    Player.instance().setPopulationResidency(currentResidency);
  }
}
