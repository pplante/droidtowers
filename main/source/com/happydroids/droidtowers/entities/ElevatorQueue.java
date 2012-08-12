/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.utils.Array;
import com.happydroids.droidtowers.controllers.AvatarSteeringManager;
import com.happydroids.droidtowers.entities.elevator.Passenger;

import java.util.Iterator;

import static com.happydroids.droidtowers.math.Direction.DOWN;

public class ElevatorQueue {
  public static final int INVALID_FLOOR = -1;
  private Array<Passenger> passengersWaiting;
  private Array<Passenger> currentRiders;
  private Array<Integer> floorNumbers;
  private int currentFloor;
  private int nextFloor;

  public ElevatorQueue(Elevator elevator) {
    passengersWaiting = new Array<Passenger>(5);
    currentRiders = new Array<Passenger>(5);
    currentFloor = INVALID_FLOOR;
    nextFloor = INVALID_FLOOR;
    floorNumbers = new Array<Integer>();
  }

  public void add(Passenger passenger) {
    passengersWaiting.add(passenger);
  }

  public Array<Passenger> getPassengersWaiting() {
    return passengersWaiting;
  }

  public boolean determinePickups() {
    if (passengersWaiting.size == 0) {
      return false;
    }

    Passenger firstPassenger = passengersWaiting.removeIndex(0);
    currentRiders.clear();
    currentRiders.add(firstPassenger);


    Iterator<Passenger> iterator = passengersWaiting.iterator();
    while (iterator.hasNext()) {
      Passenger otherPassenger = iterator.next();
      if (firstPassenger.travelContains(otherPassenger)) {
        currentRiders.add(otherPassenger);
        iterator.remove();
      }
    }

    floorNumbers.clear();
    for (Passenger passenger : currentRiders) {
      floorNumbers.add(passenger.boardingFloor);
      floorNumbers.add(passenger.destinationFloor);
    }

    floorNumbers.sort();

    if (firstPassenger.travelDirection.equals(DOWN)) {
      floorNumbers.reverse();
    }

    return true;
  }

  public boolean moveToNextStop() {
    nextFloor = INVALID_FLOOR;

    if (floorNumbers.size == 0) {
      return false;
    }

    nextFloor = floorNumbers.removeIndex(0);
    return true;
  }

  public int getCurrentFloor() {
    return currentFloor;
  }

  public void arrivedAt(int nextFloor) {
    currentFloor = nextFloor;
    Iterator<Passenger> riderIterator = currentRiders.iterator();
    while (riderIterator.hasNext()) {
      Passenger rider = riderIterator.next();
      if (!rider.isRiding() && rider.boardingFloor == currentFloor) {
        rider.boardNow();
      } else if (rider.isRiding() && rider.destinationFloor == currentFloor) {
        rider.disembarkNow();
        riderIterator.remove();
      }
    }
  }

  public boolean waitingOnRiders() {
    for (Passenger currentRider : currentRiders) {
      if (currentRider.shouldWaitFor()) {
        return true;
      }
    }

    return false;
  }

  public Array<Passenger> getCurrentRiders() {
    return currentRiders;
  }

  public void removePassenger(AvatarSteeringManager avatarSteeringManager) {
    Iterator<Passenger> ridersIterator = currentRiders.iterator();
    while (ridersIterator.hasNext()) {
      if (ridersIterator.next().getSteeringManager().equals(avatarSteeringManager)) {
        ridersIterator.remove();
        break;
      }
    }

    ridersIterator = passengersWaiting.iterator();
    while (ridersIterator.hasNext()) {
      if (ridersIterator.next().getSteeringManager().equals(avatarSteeringManager)) {
        ridersIterator.remove();
        break;
      }
    }
  }

  public void clear() {
    passengersWaiting.clear();
    currentRiders.clear();
  }

  public void informPassengersOfServiceChange() {
    for (Passenger passenger : passengersWaiting) {
      passenger.informOfServiceChange();
    }

    for (Passenger currentRider : currentRiders) {
      currentRider.informOfServiceChange();
    }

    floorNumbers.clear();
    currentFloor = INVALID_FLOOR;
    nextFloor = INVALID_FLOOR;
  }

  public int getNextFloor() {
    return nextFloor;
  }
}
