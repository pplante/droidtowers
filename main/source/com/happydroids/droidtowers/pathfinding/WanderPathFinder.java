/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.pathfinding;

import com.google.common.collect.Lists;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.GuavaSet;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.math.Vector2i;
import com.happydroids.droidtowers.utils.Random;

public class WanderPathFinder extends TransitPathFinder {
  public WanderPathFinder(GameGrid gameGrid, GridPosition start) {
    super(gameGrid, start, null);
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

    Vector2i gridSize = gameGrid.getGridSize();

    if (start.y == TowerConsts.LOBBY_FLOOR) {
      discoveredPath.add(gameGrid.positionCache().getPosition(Random.randomInt(0, gridSize.x), TowerConsts.LOBBY_FLOOR));
      discoveredPath.add(gameGrid.positionCache().getPosition(Random.randomInt(0, gridSize.x), TowerConsts.LOBBY_FLOOR));
    } else {
      for (int i = 1; i < 5; i++) {
        GridPosition positionRight = gameGrid.positionCache().getPosition(start.x + i, start.y);
        if (positionRight.size() > 0) {
          discoveredPath.add(positionRight);
        } else {
          break;
        }
      }

      for (int i = 1; i < 5; i++) {
        GridPosition positionLeft = gameGrid.positionCache().getPosition(start.x - i, start.y);
        if (positionLeft.size() > 0) {
          discoveredPath.add(positionLeft);
        } else {
          break;
        }
      }

      GuavaSet<GridPosition> positions = new GuavaSet<GridPosition>(discoveredPath);

      discoveredPath = Lists.newLinkedList();

      for (int i = 0; i < positions.size() / 4; i++) {
        discoveredPath.add(positions.randomEntry());
      }
    }
  }
}
