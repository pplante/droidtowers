package com.unhappyrobot.pathfinding;

import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.unhappyrobot.entities.GuavaSet;
import com.unhappyrobot.grid.GridPosition;
import com.unhappyrobot.grid.GridPositionCache;
import com.unhappyrobot.utils.Random;

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

    if (start.y == 4) {
      discoveredPath.add(GridPositionCache.instance().getPosition(Random.randomInt(0, gridSize.x), 4));
      discoveredPath.add(GridPositionCache.instance().getPosition(Random.randomInt(0, gridSize.x), 4));
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
