/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities.elevator;

import com.happydroids.droidtowers.controllers.AvatarSteeringManager;
import com.happydroids.droidtowers.math.Direction;

import static com.happydroids.droidtowers.math.Direction.DOWN;
import static com.happydroids.droidtowers.math.Direction.UP;

public class Passenger {
  public final int boardingFloor;
  public final int destinationFloor;
  private final Runnable disembarkCallback;
  public final Direction travelDirection;
  private final AvatarSteeringManager steeringManager;
  private boolean waitFor;

  public Passenger(AvatarSteeringManager steeringManager, int boarding, int destination, Runnable disembarkCallback) {
    this.steeringManager = steeringManager;
    boardingFloor = boarding;
    destinationFloor = destination;
    this.disembarkCallback = disembarkCallback;

    travelDirection = boardingFloor < destinationFloor ? UP : DOWN;
  }

  public boolean travelContains(Passenger otherPassenger) {
    if (!otherPassenger.travelDirection.equals(travelDirection)) {
      return false;
    }

    switch (travelDirection) {
      case UP:
        return otherPassenger.boardingFloor >= boardingFloor && otherPassenger.destinationFloor <= destinationFloor;
      case DOWN:
        return otherPassenger.boardingFloor <= boardingFloor && otherPassenger.destinationFloor >= destinationFloor;
    }

    return false;
  }

  public void boardNow() {
    waitFor = true;
    steeringManager.boardElevator(new Runnable() {
      @Override
      public void run() {
        waitFor = false;
      }
    });
  }

  public void disembarkNow() {
    disembarkCallback.run();
  }

  public boolean shouldWaitFor() {
    return waitFor;
  }

  public void updatePosition(float y) {
    steeringManager.getAvatar().setY(y);
  }
}
