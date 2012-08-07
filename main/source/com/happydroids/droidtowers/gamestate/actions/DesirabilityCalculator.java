/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.badlogic.gdx.utils.Array;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.math.GridPoint;

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

    Array<GridObject> rooms = gameGrid.getObjects();
    if (rooms != null) {
      for (int i = 0, roomsSize = rooms.size; i < roomsSize; i++) {
        GridObject gridObject = rooms.get(i);
        float maxNoiseLevel = 0f;
        float maxCrimeLevel = 0f;

        GridPoint position = gridObject.getPosition();
        GridPoint size = gridObject.getSize();

        for (int x = position.x; x < position.x + size.x; x++) {
          for (int y = position.y; y < position.y + size.y; y++) {
            maxNoiseLevel = Math.max(maxNoiseLevel, gameGrid.positionCache().getPosition(x, y).getNoiseLevel());
            maxCrimeLevel = Math.max(maxCrimeLevel, gameGrid.positionCache().getPosition(x, y).getCrimeLevel());
          }
        }

        gridObject.setSurroundingNoiseLevel(maxNoiseLevel);
        gridObject.setSurroundingCrimeLevel(maxCrimeLevel);
      }
    }
  }
}
