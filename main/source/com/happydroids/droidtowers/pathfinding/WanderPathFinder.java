/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.pathfinding;

import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.GuavaSet;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.grid.GridPositionCache;
import com.happydroids.droidtowers.utils.Random;

public class WanderPathFinder extends TransitPathFinder {
  public WanderPathFinder(GridPosition start) {
    super(start, null);
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

    Vector2 gridSize = GridPositionCache.instance().getGridSize();

    if (start.y == TowerConsts.LOBBY_FLOOR) {
      discoveredPath.add(GridPositionCache.instance().getPosition(Random.randomInt(0, gridSize.x), TowerConsts.LOBBY_FLOOR));
      discoveredPath.add(GridPositionCache.instance().getPosition(Random.randomInt(0, gridSize.x), TowerConsts.LOBBY_FLOOR));
    } else {
      for (int i = 1; i < 5; i++) {
        GridPosition positionRight = GridPositionCache.instance().getPosition(start.x + i, start.y);
        if (positionRight.size() > 0) {
          discoveredPath.add(positionRight);
        }

        GridPosition positionLeft = GridPositionCache.instance().getPosition(start.x - i, start.y);
        if (positionLeft.size() > 0) {
          discoveredPath.add(positionLeft);
        }
      }

      GuavaSet<GridPosition> positions = new GuavaSet<GridPosition>(discoveredPath);

      discoveredPath = Lists.newLinkedList();

      for (int i = 0; i < positions.size() / 4; i++) {
        discoveredPath.add(positions.getRandomEntry());
      }
    }
  }
}
