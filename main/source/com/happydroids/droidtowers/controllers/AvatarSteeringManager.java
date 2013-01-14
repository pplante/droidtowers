/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.controllers;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.Avatar;
import com.happydroids.droidtowers.entities.Stair;
import com.happydroids.droidtowers.graphics.TransitLine;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.math.Direction;
import com.happydroids.droidtowers.tween.TweenSystem;
import com.happydroids.droidtowers.utils.Random;

import java.util.List;

import static aurelienribon.tweenengine.TweenCallback.COMPLETE;
import static com.happydroids.droidtowers.controllers.AvatarState.MOVING;
import static com.happydroids.droidtowers.math.Direction.*;
import static com.happydroids.droidtowers.tween.GameObjectAccessor.POSITION;

public class AvatarSteeringManager {
  private static final String TAG = AvatarSteeringManager.class.getSimpleName();
  public static final float MOVEMENT_SPEED = 30;

  private final Avatar avatar;
  private final GameGrid gameGrid;
  private Array<GridPosition> path;
  private boolean running;
  private GridPosition currentPos;
  private Vector2 currentWorldPos;
  private Vector2 nextWorldPos;
  private TransitLine transitLine = new TransitLine();
  private boolean movingHorizontally;
  private Direction horizontalDirection;
  private int currentState;
  private Runnable completeCallback;
  private int pointsTraveled;
  private float randomSpeedModifier;


  public AvatarSteeringManager(Avatar avatar, GameGrid gameGrid) {
    this.avatar = avatar;
    this.gameGrid = gameGrid;

    currentWorldPos = new Vector2();
    nextWorldPos = new Vector2();

    currentState = 0;
    transitLine = new TransitLine();
    transitLine.setVisible(avatar.isVisible());
    transitLine.setColor(avatar.getColor());

    gameGrid.getRenderer().addTransitLine(transitLine);
  }

  public void start() {
    running = true;
    pointsTraveled = 0;

    currentState = 0;
    randomSpeedModifier = MathUtils.random(0.5f, 1.75f);
    transitLine.clear();
    for (int i = 0, pathSize = path.size; i < pathSize; i++) {
      GridPosition position = path.get(i);
      transitLine.addPoint(position.worldPoint());
    }

    advancePosition();
  }

  public void finished() {
    if (!running) {
      return;
    }

    if (currentPos != null && currentPos.elevator != null) {
      currentPos.elevator.removePassenger(this);
    }

    running = false;
    pointsTraveled = 0;
    TweenSystem.manager().killTarget(avatar);

    avatar.afterReachingTarget();
  }

  private void advancePosition() {
    if (!running) {
      return;
    }

    pointsTraveled += 1;

    if (path.size == 0 || pointsTraveled == path.size) {
      finished();
      return;
    }

    transitLine.highlightPoint(pointsTraveled);
    currentPos = path.get(pointsTraveled);
    currentWorldPos.set(currentPos.worldPoint());

    if (pointsTraveled + 1 < path.size) {
      GridPosition next = path.get(pointsTraveled + 1);
      if (next != null && next.y != currentPos.y) {
        if (currentPos.stair != null) {
          pointsTraveled++;
          traverseStair(next);
          return;
        } else if (currentPos.elevator != null) {
          GridPosition endOfElevator = null;
          do {
            if (next != null && next.elevator != null && next.elevator
                .equals(currentPos.elevator) && currentPos.y != next.y) {
              transitLine.highlightPoint(pointsTraveled++);
              endOfElevator = next;
            } else {
              break;
            }

            next = pointsTraveled + 1 < path.size ? path.get(pointsTraveled + 1) : null;
          } while (next != null);

          if (endOfElevator != null) {
            traverseElevator(endOfElevator);
            return;
          }
        }
      }
    }

    moveAvatarTo(currentPos, new TweenCallback() {
      public void onEvent(int type, BaseTween source) {
        advancePosition();
      }
    });
  }

  private void traverseElevator(final GridPosition destination) {
    int offsetX = TowerConsts.GRID_UNIT_SIZE + Random.randomInt(0, TowerConsts.GRID_UNIT_SIZE);
    nextWorldPos.set(currentPos.worldPoint());


    GridPosition leftOfElevator = gameGrid.positionCache().getPosition(currentPos.x - 1, currentPos.y);
    if (leftOfElevator != null && !leftOfElevator.isEmpty()) {
      nextWorldPos.x -= -offsetX;
    } else {
      GridPosition rightOfElevator = gameGrid.positionCache().getPosition(currentPos.x + 1, currentPos.y);
      if (rightOfElevator != null && !rightOfElevator.isEmpty()) {
        nextWorldPos.x += offsetX;
      }
    }

    moveAvatarTo(nextWorldPos, new TweenCallback() {
      @Override
      public void onEvent(int type, BaseTween source) {
        if (currentPos.elevator == null) {
          finished();
          return;
        }

        boolean addedPassenger = currentPos.elevator
            .addPassenger(AvatarSteeringManager.this, currentPos.y, destination.y, uponArrivalAtElevatorDestination(destination));
        if (!addedPassenger) {
          Gdx.app.error(TAG, "ZOMG CANNOT REACH FLOOR!!!");
          finished();
        }
      }
    });
  }

  private Runnable uponArrivalAtElevatorDestination(final GridPosition destination) {
    return new Runnable() {
      @Override
      public void run() {
        currentPos = destination;
        moveAvatarTo(currentPos, new TweenCallback() {
          @Override
          public void onEvent(int type, BaseTween source) {
            currentState = AvatarState.MOVING;
            advancePosition();
          }
        });
      }
    };
  }

  private void traverseStair(final GridPosition nextPosition) {
    if (currentPos.stair == null) {
      finished();
      return;
    }

    currentState = AvatarState.USING_STAIRS;

    Direction verticalDir = nextPosition.y < currentPos.y ? DOWN : UP;
    Stair stair = verticalDir.equals(UP) ? currentPos.stair : nextPosition.stair;
    if (stair == null) {
      finished();
      return;
    }


    Rectangle stairBounds = stair.getWorldBounds();
    Vector2 stairBottomRight = new Vector2(stairBounds.x + stairBounds.width - avatar.getWidth(), stairBounds.y);
    final Vector2 stairTopLeft = new Vector2(stairBounds.x, stairBounds.y + stairBounds.height);

    List<Vector2> points = Lists.newArrayList();
    points.add(currentPos.worldPoint());

    if (verticalDir.equals(UP)) {
      points.add(stairBottomRight);
      points.add(stairTopLeft);
    } else {
      points.add(stairTopLeft);
      points.add(stairBottomRight);
    }
    points.add(nextPosition.worldPoint());

    final TransitLine stairLine = new TransitLine();
    stairLine.addPoints(points);
    stairLine.setColor(avatar.getColor());
    gameGrid.getRenderer().addTransitLine(stairLine);

    Timeline sequence = Timeline.createSequence();

    Vector2 lastPos = currentPos.worldPoint();
    for (Vector2 point : points) {
      sequence.push(Tween.to(avatar, POSITION, lastPos.dst(point) * MOVEMENT_SPEED * randomSpeedModifier)
          .target(point.x, point.y)
          .ease(Linear.INOUT));
      lastPos = point;
    }

    sequence.setCallback(new TweenCallback() {
      public void onEvent(int type, BaseTween source) {
        gameGrid.getRenderer().removeTransitLine(stairLine);
        currentState = AvatarState.MOVING;
        advancePosition();
      }
    });
    sequence.start(TweenSystem.manager());
  }

  public void moveAvatarTo(GridPosition gridPosition, TweenCallback endCallback) {
    moveAvatarTo(nextWorldPos.set(gridPosition.worldPoint()), endCallback);
  }

  public void moveAvatarTo(Vector2 endPoint, final TweenCallback endCallback) {
    currentState |= MOVING;

    TweenSystem.manager().killTarget(avatar);

    horizontalDirection = (int) endPoint.x < (int) avatar.getX() ? LEFT : RIGHT;
    float distanceBetweenPoints = endPoint.dst(avatar.getX(), avatar.getY());
    Tween.to(avatar, POSITION, (int) (distanceBetweenPoints * MOVEMENT_SPEED * randomSpeedModifier))
        .ease(Linear.INOUT)
        .target(endPoint.x - avatar.getWidth(), endPoint.y)
        .setCallback(new TweenCallback() {
          @Override
          public void onEvent(int type, BaseTween source) {
            currentState &= ~MOVING;
            endCallback.onEvent(type, source);
          }
        })
        .setCallbackTriggers(COMPLETE)
        .start(TweenSystem.manager());
  }

  public boolean isRunning() {
    return running;
  }

  public Avatar getAvatar() {
    return avatar;
  }

  public Direction horizontalDirection() {
    return horizontalDirection;
  }

  public int getCurrentState() {
    return currentState;
  }

  public void setCompleteCallback(Runnable runnable) {
    completeCallback = runnable;
  }

  public void boardElevator(final Runnable runnable) {
    currentState = AvatarState.USING_ELEVATOR;

    nextWorldPos.set(currentPos.worldPoint());
    nextWorldPos.x += Random.randomInt(8, 40);
    moveAvatarTo(nextWorldPos, new TweenCallback() {
      @Override
      public void onEvent(int type, BaseTween source) {
        currentState = AvatarState.MOVING;
        runnable.run();
      }
    });
  }

  public void setPath(Array<GridPosition> path) {
    this.path = path;
  }

  public GridPosition getCurrentPos() {
    return currentPos;
  }
}
