/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.grid.GameGrid;

import java.util.ArrayList;

public class PopulationCalculator extends GameGridAction {

  public PopulationCalculator(GameGrid gameGrid, float frequency) {
    super(gameGrid, frequency);
  }

  @Override
  public void run() {
    ArrayList<GridObject> rooms = gameGrid.getInstancesOf(Room.class);
    int currentResidency = 0;
    int maxPopulation = 0;

    if (rooms != null) {
      for (GridObject gridObject : rooms) {
        Room room = (Room) gridObject;
        maxPopulation += room.getNumSupportedResidents();
        currentResidency += room.getNumResidents();
      }
    }

    Player.instance().setPopulationMax(maxPopulation);
    Player.instance().setPopulationResidency(currentResidency);
  }
}
