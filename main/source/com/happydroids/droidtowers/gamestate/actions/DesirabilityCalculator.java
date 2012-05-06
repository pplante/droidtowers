/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.actions;

import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.events.GameGridResizeEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.math.GridPoint;

import java.util.Set;

public class DesirabilityCalculator extends GameGridAction {
  private float[][] noiseLevels;
  private int gridSizeX;
  private int gridSizeY;

  public DesirabilityCalculator(GameGrid gameGrid, float roomUpdateFrequency) {
    super(gameGrid, roomUpdateFrequency);

    allocateLevelsStorage();
    gameGrid.events().register(this);
  }

  @Override
  public void run() {
    for (int x = 0; x < gridSizeX; x++) {
      for (int y = 0; y < gridSizeY; y++) {

        GridPosition position = gameGrid.positionCache().getPosition(x, y);
        if (!position.connectedToTransit || position.isEmpty()) {
          continue;
        }
        float totalNoise = 0f;
        int objectCount = position.size();

        for (GridObject gridObject : position.getObjects()) {
          totalNoise += gridObject.getNoiseLevel();
        }

        noiseLevels[x][y] = totalNoise / objectCount;
      }
    }


    Set<GridObject> rooms = gameGrid.getObjects();
    if (rooms != null) {
      for (GridObject gridObject : rooms) {
        if (!(gridObject instanceof Room)) continue;

        Room room = (Room) gridObject;

        float maxNoiseLevel = 0f;
        for (GridPoint gridPoint : gridObject.getGridPointsOccupied()) {
          maxNoiseLevel = Math.max(maxNoiseLevel, noiseLevels[((int) gridPoint.x)][((int) gridPoint.y)]);
        }

        room.setSurroundingNoiseLevel(maxNoiseLevel);
      }
    }
  }

  @Subscribe
  public void GameGrid_onGameGridResize(GameGridResizeEvent event) {
    allocateLevelsStorage();
  }

  private void allocateLevelsStorage() {
    gridSizeX = (int) gameGrid.getGridSize().x;
    gridSizeY = (int) gameGrid.getGridSize().y;
    noiseLevels = new float[gridSizeX][gridSizeY];

    for (int x = 0; x < gridSizeX; x++) {
      for (int y = 0; y < gridSizeY; y++) {
        noiseLevels[x][y] = 0f;
      }
    }
  }

}
