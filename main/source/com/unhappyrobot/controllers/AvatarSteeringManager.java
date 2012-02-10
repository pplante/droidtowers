package com.unhappyrobot.controllers;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.unhappyrobot.GridPosition;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.entities.Avatar;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.Stair;
import com.unhappyrobot.graphics.TransitLine;
import com.unhappyrobot.math.Direction;
import com.unhappyrobot.math.GridPoint;

import java.util.LinkedList;

import static aurelienribon.tweenengine.TweenCallback.EventType.END;
import static com.unhappyrobot.controllers.GameObjectAccessor.POSITION;
import static com.unhappyrobot.math.Direction.UP;

public class AvatarSteeringManager {
  private final Avatar avatar;
  private final GameGrid gameGrid;
  private final LinkedList<GridPosition> discoveredPath;
  private boolean running;
  private GridPosition currentPosition;
  private GridPosition nextPosition;
  private TransitLine transitLine;

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
      GridPoint gridPoint = new GridPoint(position.x, position.y);

      transitLine.addPoint(gridPoint.toWorldVector2(gameGrid));
    }

    gameGrid.getRenderer().addTransitLine(transitLine);

    advancePosition();
  }

  private void advancePosition() {
    TowerGame.getTweenManager().killTarget(avatar);

    if (discoveredPath.peek() == null) {
      stop();
      return;
    }

    currentPosition = discoveredPath.poll();
    System.out.println("currentPosition = " + currentPosition);

    if (discoveredPath.size() > 0 && currentPosition.stair != null) {
      GridPosition nextPosition = discoveredPath.element();
      if (nextPosition.y != currentPosition.y) {
        traverseStair(nextPosition);
        return;
      }
    }

    GridPoint gridPoint = new GridPoint(currentPosition.x, currentPosition.y);
    moveAvatarTo(gridPoint.toWorldVector2(gameGrid), new TweenCallback() {
      public void onEvent(EventType eventType, BaseTween source) {
        advancePosition();
      }
    });
  }

  private void stop() {
    running = false;
    gameGrid.getRenderer().removeTransitLine(transitLine);
  }

  private void traverseStair(GridPosition nextPosition) {
    System.out.println("traverse stair!");
    Stair stair = currentPosition.stair;
    Vector2 bottomRight = stair.getBottomRightWorldPoint();
    Vector2 topLeft = stair.getTopLeftWorldPoint();
    Direction verticalDir = nextPosition.y < currentPosition.y ? Direction.DOWN : UP;

    Timeline sequence = Timeline.createSequence();
    final TransitLine stairLine = new TransitLine();

    sequence.push(Tween.set(avatar, POSITION).target(avatar.getPosition().x, avatar.getPosition().y));

    final Vector2 start = verticalDir.equals(UP) ? bottomRight : topLeft;
    final Vector2 goal = verticalDir.equals(UP) ? topLeft : bottomRight;

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

  private void moveAvatarTo(Vector2 endPoint, TweenCallback endCallback) {
    Tween.to(avatar, POSITION, 1000)
            .delay(50)
            .target(endPoint.x, endPoint.y)
            .addCallback(END, endCallback)
            .start(TowerGame.getTweenManager());
  }

  public boolean isRunning() {
    return running;
  }
}
