/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities.elevator;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.controllers.AvatarSteeringManager;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPosition;
import com.happydroids.droidtowers.utils.Random;

public class ElevatorPassengerEntry {
  private final AvatarSteeringManager steeringManager;
  private final GridPosition boardingFloor;
  private final GridPosition destinationFloor;
  private Runnable runnable;
  public boolean hasBoarded;
  public boolean isQueued;
  public float offsetX;
  private boolean isBoarding;
  private boolean isDisembarking;

  public ElevatorPassengerEntry(AvatarSteeringManager steeringManager, GridPosition boardingFloor, GridPosition destinationFloor) {
    this.steeringManager = steeringManager;
    this.boardingFloor = boardingFloor;
    this.destinationFloor = destinationFloor;

    offsetX = Random.randomInt(8, 36);
  }

  public void addCallback(Runnable runnable) {
    this.runnable = runnable;
  }

  public void runCompleteCallback() {
    runnable.run();
  }

  public GridPosition getBoardingFloor() {
    return boardingFloor;
  }

  public GridPosition getDestinationFloor() {
    return destinationFloor;
  }

  public boolean isHasBoarded() {
    return hasBoarded;
  }

  public boolean isQueued() {
    return isQueued;
  }

  public float getOffsetX() {
    return offsetX;
  }

  public AvatarSteeringManager getSteeringManager() {
    return steeringManager;
  }

  public void boardElevator(GameGrid gameGrid) {
    isBoarding = true;
    Vector2 avatarWalkToElevator = steeringManager.getCurrentPosition().toWorldVector2();
    avatarWalkToElevator.x += offsetX;

    steeringManager.moveAvatarTo(avatarWalkToElevator, new TweenCallback() {
      public void onEvent(int type, BaseTween source) {
        isBoarding = false;
      }
    });
  }

  public void disembarkFromElevator(GameGrid gameGrid) {
    isDisembarking = true;
    steeringManager.moveAvatarTo(steeringManager.getNextPosition().toWorldVector2(), new TweenCallback() {
      public void onEvent(int type, BaseTween source) {
        isDisembarking = false;
        runCompleteCallback();
      }
    });
  }

  public boolean shouldWaitFor() {
    return isBoarding || isDisembarking;
  }
}
