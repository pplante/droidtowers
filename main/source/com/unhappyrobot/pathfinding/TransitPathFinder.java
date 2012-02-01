package com.unhappyrobot.pathfinding;

import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.GridPosition;
import com.unhappyrobot.GridPositionCache;
import com.unhappyrobot.math.GridPoint;

import java.util.LinkedList;
import java.util.List;

public class TransitPathFinder extends AStar<GridPoint> {
  private final Vector2 goal;

  public TransitPathFinder(GridPoint goal) {
    this.goal = goal;
  }

  @Override
  protected boolean isGoal(GridPoint gridPoint) {
    return goal.equals(gridPoint);
  }

  @Override
  protected Double g(GridPoint from, GridPoint to) {
    if (from.equals(to)) {
      return 0.0;
    }

    GridPosition position = GridPositionCache.instance().getPosition(to);
    if (position != null) {
      if (position.containsElevator) {
        return 0.25;
      } else if (position.containsStair) {
        return 0.75;
      } else if (position.connectedToTransit) {
        return 1.0;
      } else if (to.y == 4.0f) {
        return 2.0;
      }
    }

    return Double.MAX_VALUE;
  }

  @Override
  protected Double h(GridPoint from, GridPoint to) {
//      return Math.sqrt(Math.pow((from.x - to.x), 2) + Math.pow((from.y - to.y), 2));
    /* Use the Manhattan distance heuristic.  */
    return (double) Math.abs(goal.x - to.x) + Math.abs(goal.y - to.y);
  }

  @Override
  protected List<GridPoint> generateSuccessors(GridPoint point) {
    List<GridPoint> successors = new LinkedList<GridPoint>();
    int x = (int) point.x;
    int y = (int) point.y;

    checkGridPosition(successors, x, y + 1);
    checkGridPosition(successors, x, y - 1);

    checkGridPosition(successors, x + 1, y);
    checkGridPosition(successors, x - 1, y);

    return successors;
  }

  private void checkGridPosition(List<GridPoint> successors, int x, int y) {
    GridPosition position = GridPositionCache.instance().getPosition(x, y);
    if (position != null && (position.connectedToTransit || y == 4)) {
//      System.out.println("x: " + x + ", y: " + y);
      successors.add(new GridPoint(x, y));
    }
  }

  public boolean isWorking() {
    return working;
  }
}
