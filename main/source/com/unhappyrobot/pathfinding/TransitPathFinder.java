package com.unhappyrobot.pathfinding;

import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.GridPosition;
import com.unhappyrobot.GridPositionCache;
import com.unhappyrobot.math.GridPoint;

import java.util.LinkedList;
import java.util.List;

public class TransitPathFinder extends AStar<GridPosition> {
  private final Vector2 goal;

  public TransitPathFinder(GridPoint goal) {
    this.goal = goal;
  }

  @Override
  protected boolean isGoal(GridPosition gridPosition) {
    return goal.x == gridPosition.x && goal.y == gridPosition.y;
  }

  @Override
  protected Double g(GridPosition from, GridPosition to) {
    if (from.equals(to)) {
      return 0.0;
    }

    if (to != null) {
      if (to.elevator != null) {
        return 0.25;
      } else if (to.stair != null) {
        return 0.75;
      } else if (to.connectedToTransit) {
        return 1.0;
      }
    }

    if (to.y == 4) {
      // fail safe that an avatar that ran away will come back!
      return 2.0;
    }

    return Double.MAX_VALUE;
  }

  @Override
  protected Double h(GridPosition from, GridPosition to) {
    /* Use the Manhattan distance heuristic.  */
    return (double) Math.abs(goal.x - to.x) + Math.abs(goal.y - to.y);
  }

  @Override
  protected List<GridPosition> generateSuccessors(GridPosition point) {
    List<GridPosition> successors = new LinkedList<GridPosition>();

    int x = point.x;
    int y = point.y;

    if (point.elevator != null) {
      checkGridPosition(successors, x, y + 1);
      checkGridPosition(successors, x, y - 1);
    } else if (point.stair != null) {
      checkGridPosition(successors, x, y + 1);
      checkGridPosition(successors, x, y - 1);
    }

    checkGridPosition(successors, x + 1, y);
    checkGridPosition(successors, x - 1, y);

    return successors;
  }

  private void checkGridPosition(List<GridPosition> successors, int x, int y) {
    GridPosition position = GridPositionCache.instance().getPosition(x, y);
    if (position != null && (position.connectedToTransit || y == 4)) {
      successors.add(position);
    }
  }

  public boolean isWorking() {
    return working;
  }
}
