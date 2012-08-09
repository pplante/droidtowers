/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.pathfinding;

import com.badlogic.gdx.utils.Array;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.Elevator;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;

import static com.happydroids.droidtowers.types.ProviderType.SERVICE_ELEVATOR;

public class TransitPathFinder extends AStar<GridPosition> {
  protected final GameGrid gameGrid;
  private final boolean canUseServiceRoutes;
  private final Array<GridPosition> tmpArray;

  public TransitPathFinder(GameGrid gameGrid, boolean canUseServiceRoutes) {
    super();
    this.gameGrid = gameGrid;
    this.canUseServiceRoutes = canUseServiceRoutes;
    tmpArray = new Array<GridPosition>(4);
  }

  @Override
  protected boolean isGoal(GridPosition gridPosition) {
    return goal.x == gridPosition.x && goal.y == gridPosition.y;
  }

  @Override
  protected int g(GridPosition from, GridPosition to) {
    if (from.equals(to)) {
      return 0;
    }

    if (to != null) {
      if (to.elevator != null && to.elevator.servicesFloor(to.y) && to.elevator.getNumElevatorCars() > 0) {
        if (canUseServiceRoutes && to.elevator.provides(SERVICE_ELEVATOR)) {
          return 5;
        }

        return 25;
      } else if (to.stair != null) {
        return 75;
      } else if (to.connectedToTransit) {
        return 100;
      }
    }

    if (to.y == TowerConsts.LOBBY_FLOOR) {
      // fail safe that an avatar that ran away will come back!
      return 200;
    }

    return Integer.MAX_VALUE;
  }

  @Override
  protected int h(GridPosition from, GridPosition to) {
    /* Use the Manhattan distance heuristic.  */
    return Math.abs(goal.x - to.x) + Math.abs(goal.y - to.y);
//    return Math.pow(goal.x - to.x, 2) + Math.pow(goal.y - to.y, 2);
  }

  @Override
  protected Array<GridPosition> generateSuccessors(GridPosition point) {
    tmpArray.clear();

    Elevator elevator = point.elevator;
    if (elevator != null && elevator.getNumElevatorCars() > 0) {
      if ((canUseServiceRoutes && elevator.provides(SERVICE_ELEVATOR) || !canUseServiceRoutes && !elevator.provides(SERVICE_ELEVATOR))) {
        if (elevator.servicesFloor(point.y + 1)) {
          checkGridPositionY(tmpArray, point.x, point.y + 1);
        }
        if (elevator.servicesFloor(point.y - 1)) {
          checkGridPositionY(tmpArray, point.x, point.y - 1);
        }
      }
    } else if (point.stair != null) {
      checkGridPositionY(tmpArray, point.x, point.y + 1);
      checkGridPositionY(tmpArray, point.x, point.y - 1);
    }

    checkGridPositionX(tmpArray, point.x + 1, point.y);
    checkGridPositionX(tmpArray, point.x - 1, point.y);

    return tmpArray;
  }

  private void checkGridPositionX(Array<GridPosition> successors, int x, int y) {
    GridPosition position = gameGrid.positionCache().getPosition(x, y);
    if (position != null && (!position.isEmpty() || y == TowerConsts.LOBBY_FLOOR)) {
      successors.add(position);
    }
  }

  private void checkGridPositionY(Array<GridPosition> successors, int x, int y) {
    GridPosition position = gameGrid.positionCache().getPosition(x, y);
    if (position != null && (position.connectedToTransit || y == TowerConsts.LOBBY_FLOOR)) {
      if (position.elevator != null && position.elevator.servicesFloor(y)) {
        successors.add(position);
      } else if (position.stair != null) {
        successors.add(position);
      }
    }
  }
}
