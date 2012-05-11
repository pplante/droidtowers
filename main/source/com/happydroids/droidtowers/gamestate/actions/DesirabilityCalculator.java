/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.GuavaSet;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.grid.GameGrid;

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

    GuavaSet<GridObject> rooms = gameGrid.getObjects();
    if (rooms != null) {
      for (GridObject gridObject : rooms) {
        if (!(gridObject instanceof Room)) continue;

        Room room = (Room) gridObject;

        float maxNoiseLevel = 0f;

        for (int x = room.getPosition().x; x < room.getPosition().x + room.getSize().x; x++) {
          for (int y = room.getPosition().y; y < room.getPosition().y + room.getSize().y; y++) {
            maxNoiseLevel = Math.max(maxNoiseLevel, gameGrid.positionCache().getPosition(x, y).getNoiseLevel());
          }
        }

        room.setSurroundingNoiseLevel(maxNoiseLevel);
      }
    }
  }
}
