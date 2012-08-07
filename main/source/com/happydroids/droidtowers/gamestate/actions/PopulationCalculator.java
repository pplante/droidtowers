/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.badlogic.gdx.utils.Array;
import com.happydroids.droidtowers.controllers.AvatarLayer;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.types.RoomType;

public class PopulationCalculator extends GameGridAction {

  private final AvatarLayer avatarLayer;

  public PopulationCalculator(GameGrid gameGrid, AvatarLayer avatarLayer, float frequency) {
    super(gameGrid, frequency);
    this.avatarLayer = avatarLayer;
  }

  @Override
  public void run() {
    Array<GridObject> rooms = gameGrid.getInstancesOf(Room.class);
    int supportedResidency = 0;
    int maxPopulation = 0;

    if (rooms != null) {
      for (GridObject gridObject : rooms) {
        maxPopulation += ((RoomType) gridObject.getGridObjectType()).getPopulationMax();
        supportedResidency += ((Room) gridObject).getNumSupportedResidents();
      }
    }

    Player.instance().setPopulationMax(maxPopulation);
    Player.instance().setSupportedResidency(supportedResidency);
    Player.instance().setPopulationResidency(avatarLayer.getNumAvatars());
  }
}
