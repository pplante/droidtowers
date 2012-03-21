package com.unhappyrobot.controllers;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.unhappyrobot.entities.Avatar;
import com.unhappyrobot.entities.ElevatorCar;
import com.unhappyrobot.entities.Stair;
import com.unhappyrobot.graphics.TransitLine;
import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.grid.GridPosition;
import com.unhappyrobot.math.Direction;
import com.unhappyrobot.tween.TweenSystem;

import java.util.LinkedList;
import java.util.Set;

import static com.unhappyrobot.math.Direction.*;
import static com.unhappyrobot.tween.GameObjectAccessor.POSITION;

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
  private Set<AvatarState> currentState;
  private Runnable completeCallback;
  private Set<Stair> stairsUsed;

  public AvatarSteeringManager(Avatar avatar, GameGrid gameGrid, LinkedList<GridPosition> discoveredPath) {
    this.avatar = avatar;
    this.gameGrid = gameGrid;
    this.discoveredPath = discoveredPath;
  }

  public void start() {
    running = true;

    currentState = Sets.newHashSet();
    stairsUsed = Sets.newHashSet();
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

    if (completeCallback != null) {
      completeCallback.run();
      completeCallback = null;
    }
  }

  private void advancePosition() {
    if (discoveredPath.peek() == null) {
      stop();
      return;
    }

    currentState.clear();
    currentPosition = discoveredPath.poll();

    if (discoveredPath.size() > 0) {
      if (currentPosition.stair != null) {
        GridPosition positionToCheck = discoveredPath.peek();
        if (positionToCheck != null && positionToCheck.y != currentPosition.y) {
          currentState.add(AvatarState.USING_STAIRS);
          traverseStair(positionToCheck);
          return;
        }
      } else if (currentPosition.elevator != null) {
        GridPosition positionToCheck;
        while ((positionToCheck = discoveredPath.peek()) != null && positionToCheck.elevator == currentPosition.elevator) {
          nextPosition = discoveredPath.poll();
        }

        if (nextPosition != null) {
          currentState.add(AvatarState.USING_ELEVATOR);
          traverseElevator();
          return;
        }
      }
    }

    moveAvatarTo(currentPosition, new TweenCallback() {
      public void onEvent(int type, BaseTween source) {
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
      public void onEvent(int type, BaseTween source) {
        moveAvatarTo(goal, new TweenCallback() {
          public void onEvent(int type, BaseTween source) {
            gameGrid.getRenderer().removeTransitLine(stairLine);
            advancePosition();
          }
        });
      }
    });

    stairsUsed.add(stair);
  }

  public void moveAvatarTo(GridPosition gridPosition, TweenCallback endCallback) {
    moveAvatarTo(gridPosition.toWorldVector2(gameGrid), endCallback);
  }

  public void moveAvatarTo(Vector2 endPoint, TweenCallback endCallback) {
    currentState.add(AvatarState.MOVING);

    TweenSystem.getTweenManager().killTarget(this);

    horizontalDirection = (int) endPoint.x < (int) avatar.getX() ? LEFT : RIGHT;
    verticalDirection = (int) endPoint.y < (int) avatar.getY() ? DOWN : UP;
    float distanceBetweenPoints = endPoint.dst(avatar.getX(), avatar.getY());
    Tween.to(avatar, POSITION, (int) (distanceBetweenPoints * MOVEMENT_SPEED))
            .ease(Linear.INOUT)
            .target(endPoint.x, endPoint.y)
            .setCallback(endCallback)
            .setCallbackTriggers(TweenCallback.END)
            .start(TweenSystem.getTweenManager());
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

  public Set<AvatarState> getCurrentState() {
    return currentState;
  }

  public void setCompleteCallback(Runnable runnable) {
    completeCallback = runnable;
  }
}
