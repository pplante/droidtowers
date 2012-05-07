/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.controllers;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.Avatar;
import com.happydroids.droidtowers.entities.Stair;
import com.happydroids.droidtowers.events.GridObjectBoundsChangeEvent;
import com.happydroids.droidtowers.graphics.TransitLine;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.math.Direction;
import com.happydroids.droidtowers.tween.TweenSystem;
import com.happydroids.droidtowers.utils.Random;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import static aurelienribon.tweenengine.TweenCallback.COMPLETE;
import static com.happydroids.droidtowers.controllers.AvatarState.MOVING;
import static com.happydroids.droidtowers.math.Direction.*;
import static com.happydroids.droidtowers.tween.GameObjectAccessor.POSITION;

public class AvatarSteeringManager {
  public static final float MOVEMENT_SPEED = 30;

  private final Avatar avatar;
  private final GameGrid gameGrid;
  private final LinkedList<GridPosition> path;
  private boolean running;
  private GridPosition currentPos;
  private TransitLine transitLine = new TransitLine();
  private boolean movingHorizontally;
  private Direction horizontalDirection;
  private Set<AvatarState> currentState;
  private Runnable completeCallback;
  private Set<Stair> stairsUsed;
  private int pointsTraveled;

  public AvatarSteeringManager(Avatar avatar, GameGrid gameGrid, LinkedList<GridPosition> path) {
    this.avatar = avatar;
    this.gameGrid = gameGrid;
    this.path = path;
  }

  public void start() {
    running = true;
    pointsTraveled = 0;

    currentState = Sets.newHashSet();
    stairsUsed = Sets.newHashSet();
    transitLine = new TransitLine();
    transitLine.setColor(avatar.getColor());
    for (GridPosition position : path) {
      transitLine.addPoint(position.toWorldVector2());
    }

    gameGrid.events().register(this);
    gameGrid.getRenderer().addTransitLine(transitLine);

    advancePosition();
  }

  public void finished() {
    cancel();

    if (completeCallback != null) {
      completeCallback.run();
      completeCallback = null;
    }
  }

  public void cancel() {
    if (!running) {
      return;
    }

    if (currentPos != null && currentPos.elevator != null) {
      currentPos.elevator.getCar().removePassenger(this);
    }

    gameGrid.events().unregister(this);

    running = false;
    pointsTraveled = 0;
    TweenSystem.getTweenManager().killTarget(avatar);
    gameGrid.getRenderer().removeTransitLine(transitLine);
  }

  private void advancePosition() {
    if (currentState.size() > 0) return;

    if (path.isEmpty()) {
      finished();
      return;
    }

    transitLine.highlightPoint(pointsTraveled++);
    currentPos = path.pollFirst();

    if (path.size() > 0) {
      if (currentPos.stair != null) {
        GridPosition next = path.peek();
        if (next.stair != null && next.stair.equals(currentPos.stair) && next.y != currentPos.y) {
          path.remove(next);
          traverseStair(next);
          return;
        }
      } else if (currentPos.elevator != null) {
        int before = path.size();
        GridPosition endOfElevator = null;
        Iterator<GridPosition> iterator = path.iterator();
        while (iterator.hasNext()) {
          GridPosition next = iterator.next();
          if (next.elevator != null && next.elevator.equals(currentPos.elevator)) {
            transitLine.highlightPoint(pointsTraveled++);
            endOfElevator = next;
            iterator.remove();
          } else {
            break;
          }
        }

        if (endOfElevator != null) {
          traverseElevator(endOfElevator);
          return;
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
    currentState.add(AvatarState.USING_ELEVATOR);

    Vector2 nextToElevator = currentPos.toWorldVector2();
    nextToElevator.x += Random.randomInt(0, TowerConsts.GRID_UNIT_SIZE);
    moveAvatarTo(nextToElevator, new TweenCallback() {
      @Override
      public void onEvent(int type, BaseTween source) {
        if (currentPos.elevator == null) {
          cancel();
          return;
        }

        currentPos.elevator.getCar().enqueue(AvatarSteeringManager.this, currentPos.y, destination.y, new Runnable() {
          @Override
          public void run() {
            currentPos = destination;
            moveAvatarTo(currentPos, new TweenCallback() {
              @Override
              public void onEvent(int type, BaseTween source) {
                currentState.remove(AvatarState.USING_ELEVATOR);
                advancePosition();
              }
            });
          }
        });
      }
    });
  }

  private void traverseStair(GridPosition nextPosition) {
    if (currentPos.stair == null) {
      cancel();
      return;
    }

    currentState.add(AvatarState.USING_STAIRS);

    Stair stair = currentPos.stair;
    Direction verticalDir = nextPosition.y < currentPos.y ? DOWN : UP;

    final Vector2 start = verticalDir.equals(UP) ? stair.getBottomRightWorldPoint() : stair.getTopLeftWorldPoint();
    final Vector2 goal = verticalDir.equals(UP) ? stair.getTopLeftWorldPoint() : stair.getBottomRightWorldPoint();

    moveAvatarTo(start, new TweenCallback() {
      public void onEvent(int type, BaseTween source) {
        moveAvatarTo(goal, new TweenCallback() {
          public void onEvent(int type, BaseTween source) {
            advancePosition();
          }
        });
      }
    });

    stairsUsed.add(stair);
  }

  public void moveAvatarTo(GridPosition gridPosition, TweenCallback endCallback) {
    moveAvatarTo(gridPosition.toWorldVector2(), endCallback);
  }

  public void moveAvatarTo(Vector2 endPoint, final TweenCallback endCallback) {
    currentState.add(MOVING);

    TweenSystem.getTweenManager().killTarget(avatar);

    horizontalDirection = (int) endPoint.x < (int) avatar.getX() ? LEFT : RIGHT;
    float distanceBetweenPoints = endPoint.dst(avatar.getX(), avatar.getY());
    Tween.to(avatar, POSITION, (int) (distanceBetweenPoints * MOVEMENT_SPEED))
            .ease(Linear.INOUT)
            .target(endPoint.x, endPoint.y)
            .setCallback(new TweenCallback() {
              @Override
              public void onEvent(int type, BaseTween source) {
                currentState.remove(MOVING);
                endCallback.onEvent(type, source);
              }
            })
            .setCallbackTriggers(COMPLETE)
            .start(TweenSystem.getTweenManager());
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

  public Set<AvatarState> getCurrentState() {
    return currentState;
  }

  public void setCompleteCallback(Runnable runnable) {
    completeCallback = runnable;
  }

  public void boardElevator(final Runnable runnable) {
    currentState.add(AvatarState.USING_ELEVATOR);

    Vector2 posInCar = currentPos.toWorldVector2();
    posInCar.x += Random.randomInt(8, 58);
    moveAvatarTo(posInCar, new TweenCallback() {
      @Override
      public void onEvent(int type, BaseTween source) {
        runnable.run();
      }
    });
  }

  @Subscribe
  public void GameGrid_onGridObjectBoundsChange(GridObjectBoundsChangeEvent event) {
    if (!event.gridObject.isPlaced()) {
      return;
    }

    for (GridPosition position : path) {
      if (position.getObjects().contains(event.gridObject) || position.elevator == event.gridObject) {
        cancel();
        break;
      }
    }
  }
}
