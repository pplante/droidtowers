/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.grid.GameGrid;

import java.util.Set;

public class DesirabilityCalculator extends GameGridAction {
  private float[][] noiseLevels;
  private int gridSizeX;
  private int gridSizeY;

  public DesirabilityCalculator(GameGrid gameGrid, float roomUpdateFrequency) {
    super(gameGrid, roomUpdateFrequency);
  }

  @Override
  public void run() {
    gameGrid.positionCache().updateNoiseLevels();

    Set<GridObject> rooms = gameGrid.getObjects();
    if (rooms != null) {
      for (GridObject gridObject : rooms) {
        if (!(gridObject instanceof Room)) continue;

        Room room = (Room) gridObject;

        float maxNoiseLevel = 0f;

        for (int x = (int) room.getPosition().x; x < room.getPosition().x + room.getSize().x; x++) {
          for (int y = (int) room.getPosition().y; y < room.getPosition().y + room.getSize().y; y++) {
            maxNoiseLevel = Math.max(maxNoiseLevel, gameGrid.positionCache().getPosition(x, y).getNoiseLevel());
          }
        }

        room.setSurroundingNoiseLevel(maxNoiseLevel);
      }
    }
  }
}
