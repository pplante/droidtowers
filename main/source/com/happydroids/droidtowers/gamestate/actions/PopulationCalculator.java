/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.grid.GameGrid;

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
    int currentResidency = 0;
    int maxPopulation = 0;

    if (rooms != null) {
      for (GridObject gridObject : rooms) {
        Room room = (Room) gridObject;
        maxPopulation += room.getNumSupportedResidents();
      }
    }

    Player.instance().setPopulationMax(maxPopulation);
    Player.instance().setPopulationResidency(avatarLayer.getNumAvatars());
  }
}
