package com.unhappyrobot.controllers;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.unhappyrobot.GridPosition;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.entities.Avatar;
import com.unhappyrobot.entities.ElevatorCar;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.Stair;
import com.unhappyrobot.graphics.TransitLine;
import com.unhappyrobot.math.Direction;

import java.util.LinkedList;

import static aurelienribon.tweenengine.TweenCallback.EventType.END;
import static com.unhappyrobot.controllers.GameObjectAccessor.POSITION;
import static com.unhappyrobot.math.Direction.*;

public class AvatarSteeringManager {
  public static final float MOVEMENT_SPEED = 30;

  private final Avatar avatar;
  private final GameGrid gameGrid;
  private final LinkedList<GridPosition> discoveredPath;
  private boolean running;
  private GridPosition currentPosition;
  private GridPosition nextPosition;
  private TransitLine transitLine;
  private boolean movingHorizontally;
  private Direction horizontalDirection;
  private Direction verticalDirection;

  public AvatarSteeringManager(Avatar avatar, GameGrid gameGrid, LinkedList<GridPosition> discoveredPath) {
    this.avatar = avatar;
    this.gameGrid = gameGrid;
    this.discoveredPath = discoveredPath;
  }

  public void start() {
    running = true;

    transitLine = new TransitLine();
    transitLine.setColor(avatar.getColor());
    for (GridPosition position : Lists.newArrayList(discoveredPath)) {
      transitLine.addPoint(position.toWorldVector2(gameGrid));
    }

    gameGrid.getRenderer().addTransitLine(transitLine);

    advancePosition();
  }

  private void stop() {
    running = false;
    gameGrid.getRenderer().removeTransitLine(transitLine);
  }

  private void advancePosition() {
    if (discoveredPath.peek() == null) {
      stop();
      return;
    }

    currentPosition = discoveredPath.poll();

    if (discoveredPath.size() > 0) {
      if (currentPosition.stair != null) {
        GridPosition positionToCheck = discoveredPath.peek();
        if (positionToCheck != null && positionToCheck.y != currentPosition.y) {
          traverseStair(positionToCheck);
          return;
        }
      } else if (currentPosition.elevator != null) {
        GridPosition positionToCheck;
        while ((positionToCheck = discoveredPath.peek()) != null && positionToCheck.elevator == currentPosition.elevator) {
          nextPosition = discoveredPath.poll();
        }

        if (nextPosition != null) {
          traverseElevator();
          return;
        }
      }
    }

    moveAvatarTo(currentPosition, new TweenCallback() {
      public void onEvent(EventType eventType, BaseTween source) {
        advancePosition();
      }
    });
  }

  private void traverseElevator() {
    final TransitLine elevatorLine = new TransitLine();
    elevatorLine.addPoint(currentPosition.toWorldVector2(gameGrid));
    elevatorLine.addPoint(nextPosition.toWorldVector2(gameGrid));

    gameGrid.getRenderer().addTransitLine(elevatorLine);

    final ElevatorCar elevatorCar = currentPosition.elevator.getCar();
    elevatorCar.addPassenger(this)
            .addCallback(new Runnable() {
              public void run() {
                elevatorCar.removePassenger(AvatarSteeringManager.this);
                gameGrid.getRenderer().removeTransitLine(elevatorLine);
                advancePosition();
              }
            });
  }

  private void traverseStair(GridPosition nextPosition) {
    Stair stair = currentPosition.stair;
    Direction verticalDir = nextPosition.y < currentPosition.y ? DOWN : UP;

    final Vector2 start = verticalDir.equals(UP) ? stair.getBottomRightWorldPoint() : stair.getTopLeftWorldPoint();
    final Vector2 goal = verticalDir.equals(UP) ? stair.getTopLeftWorldPoint() : stair.getBottomRightWorldPoint();

    final TransitLine stairLine = new TransitLine();
    stairLine.addPoint(start);
    stairLine.addPoint(goal);

    gameGrid.getRenderer().addTransitLine(stairLine);

    moveAvatarTo(start, new TweenCallback() {
      public void onEvent(EventType eventType, BaseTween source) {
        moveAvatarTo(goal, new TweenCallback() {
          public void onEvent(EventType eventType, BaseTween source) {
            gameGrid.getRenderer().removeTransitLine(stairLine);
            advancePosition();
          }
        });
      }
    });
  }

  public void moveAvatarTo(GridPosition gridPosition, TweenCallback endCallback) {
    moveAvatarTo(gridPosition.toWorldVector2(gameGrid), endCallback);
  }

  private void moveAvatarTo(Vector2 endPoint, TweenCallback endCallback) {
    horizontalDirection = endPoint.x < avatar.getX() ? LEFT : RIGHT;
    verticalDirection = endPoint.y < avatar.getY() ? DOWN : UP;
    float distanceBetweenPoints = endPoint.dst(avatar.getX(), avatar.getY());
    Tween.to(avatar, POSITION, (int) (distanceBetweenPoints * MOVEMENT_SPEED))
            .target(endPoint.x, endPoint.y)
            .addCallback(END, endCallback)
            .start(TowerGame.getTweenManager());
  }

  public boolean isRunning() {
    return running;
  }

  public GridPosition getCurrentPosition() {
    return currentPosition;
  }

  public GridPosition getNextPosition() {
    return nextPosition;
  }

  public Avatar getAvatar() {
    return avatar;
  }

  public Direction horizontalDirection() {
    return horizontalDirection;
  }
}
