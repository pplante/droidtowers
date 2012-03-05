package com.unhappyrobot.pathfinding;

import com.unhappyrobot.grid.GridPosition;
import com.unhappyrobot.grid.GridPositionCache;

import java.util.LinkedList;
import java.util.List;

public class TransitPathFinder extends AStar<GridPosition> {
  public TransitPathFinder(GridPosition start, GridPosition goal) {
    super(start, goal);
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
//    return (double) Math.abs(goal.x - to.x) + Math.abs(goal.y - to.y);
    return Math.pow(goal.x - to.x, 2) + Math.pow(goal.y - to.y, 2);
  }

  @Override
  protected List<GridPosition> generateSuccessors(GridPosition point) {
    List<GridPosition> successors = new LinkedList<GridPosition>();

    int x = point.x;
    int y = point.y;

    if (point.elevator != null || point.stair != null) {
      checkGridPosition(successors, x, y + 1);
      checkGridPosition(successors, x, y - 1);
    }

    checkGridPosition(successors, x + 1, y);
    checkGridPosition(successors, x - 1, y);

    return successors;
  }

  private void checkGridPosition(List<GridPosition> successors, float x, float y) {
    checkGridPosition(successors, (int) x, (int) y);
  }

  private void checkGridPosition(List<GridPosition> successors, int x, int y) {
    GridPosition position = GridPositionCache.instance().getPosition(x, y);
    if (position != null && (position.connectedToTransit || y == 4)) {
      successors.add(position);
    }
  }
}
