/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.pathfinding;

import com.google.common.collect.Lists;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.math.GridPoint;
import com.happydroids.droidtowers.utils.Random;

import java.util.List;

public class WanderPathFinder extends TransitPathFinder {
  public WanderPathFinder(GameGrid gameGrid, GridPosition start) {
    super(gameGrid, start, null, false);
  }

  @Override
  protected boolean isGoal(GridPosition gridPosition) {
    return true;
  }

  @Override
  public void start() {
    working = false;
    lastCost = 1.0;
    discoveredPath = Lists.newLinkedList();

    GridPoint gridSize = gameGrid.getGridSize();

    if (start.y == TowerConsts.LOBBY_FLOOR) {
      discoveredPath.add(gameGrid.positionCache().getPosition(Random.randomInt(0, gridSize.x), TowerConsts.LOBBY_FLOOR));
      discoveredPath.add(gameGrid.positionCache().getPosition(Random.randomInt(0, gridSize.x), TowerConsts.LOBBY_FLOOR));
    } else {
      for (int i = 1; i < 5; i++) {
        GridPosition positionRight = gameGrid.positionCache().getPosition(start.x + i, start.y);
        if (positionRight != null && positionRight.size() > 0) {
          discoveredPath.add(positionRight);
        } else {
          break;
        }
      }

      for (int i = 1; i < 5; i++) {
        GridPosition positionLeft = gameGrid.positionCache().getPosition(start.x - i, start.y);
        if (positionLeft != null && positionLeft.size() > 0) {
          discoveredPath.add(positionLeft);
        } else {
          break;
        }
      }

      List<GridPosition> positions = Lists.newArrayList(discoveredPath);

      discoveredPath = Lists.newLinkedList();

      int numPositions = positions.size();
      for (int i = 0; i < numPositions / 4; i++) {
        discoveredPath.add(positions.get(Random.randomInt(numPositions - 1)));
      }
    }
  }
}
