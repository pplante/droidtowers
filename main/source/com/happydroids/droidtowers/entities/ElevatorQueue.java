/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.controllers.AvatarSteeringManager;
import com.happydroids.droidtowers.entities.elevator.Passenger;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static com.happydroids.droidtowers.math.Direction.DOWN;

public class ElevatorQueue {
  public static final int INVALID_FLOOR = -1;
  private List<Passenger> passengersWaiting;
  private List<Passenger> currentRiders;
  private List<Integer> floorNumbers;
  private int currentFloor;
  public static final Predicate<Passenger> PASSENGER_REMOVAL_PREDICATE = new Predicate<Passenger>() {
    @Override
    public boolean apply(@Nullable Passenger input) {
      return input.isMarkedForRemoval();
    }
  };
  private int nextFloor;

  public ElevatorQueue(Elevator elevator) {
    passengersWaiting = Lists.newArrayList();
    currentRiders = Lists.newArrayList();
    currentFloor = INVALID_FLOOR;
    nextFloor = INVALID_FLOOR;
    floorNumbers = Lists.newArrayList();
  }

  public void add(Passenger passenger) {
    passengersWaiting.add(passenger);
  }

  public List<Passenger> getPassengersWaiting() {
    return passengersWaiting;
  }

  public boolean determinePickups() {
    Iterables.removeIf(passengersWaiting, PASSENGER_REMOVAL_PREDICATE);

    if (passengersWaiting.isEmpty()) {
      return false;
    }

    Passenger firstPassenger = passengersWaiting.remove(0);
    currentRiders.clear();
    currentRiders.add(firstPassenger);


    for (int i = 0, passengersWaitingSize = passengersWaiting.size(); i < passengersWaitingSize; i++) {
      Passenger otherPassenger = passengersWaiting.get(i);
      if (firstPassenger.travelContains(otherPassenger)) {
        currentRiders.add(otherPassenger);
      }
    }

    passengersWaiting.removeAll(currentRiders);

    floorNumbers = Lists.newLinkedList();
    for (int i = 0, currentRidersSize = currentRiders.size(); i < currentRidersSize; i++) {
      Passenger passenger = currentRiders.get(i);
      floorNumbers.add(passenger.boardingFloor);
      floorNumbers.add(passenger.destinationFloor);
    }

    Collections.sort(floorNumbers);

    if (firstPassenger.travelDirection.equals(DOWN)) {
      Collections.reverse(floorNumbers);
    }

    return true;
  }

  public boolean moveToNextStop() {
    nextFloor = INVALID_FLOOR;

    if (floorNumbers.isEmpty()) {
      return false;
    }

    nextFloor = floorNumbers.remove(0);
    return true;
  }

  public int getCurrentFloor() {
    return currentFloor;
  }

  public void arrivedAt(int nextFloor) {
    currentFloor = nextFloor;
    Passenger currentRider;
    for (int i = 0, currentRidersSize = currentRiders.size(); i < currentRidersSize; i++) {
      currentRider = currentRiders.get(i);
      if (!currentRider.isRiding() && currentRider.boardingFloor == currentFloor) {
        currentRider.boardNow();
      } else if (currentRider.isRiding() && currentRider.destinationFloor == currentFloor) {
        currentRider.disembarkNow();
        currentRider.markToRemove();
      }
    }
  }

  public boolean waitingOnRiders() {
    Passenger passenger;
    for (int i1 = 0, passengersWaitingSize = passengersWaiting.size(); i1 < passengersWaitingSize; i1++) {
      passenger = passengersWaiting.get(i1);
      if (passenger.isMarkedForRemoval()) {
        passengersWaiting.remove(i1);
        passengersWaitingSize--;
        i1--;
      }
    }

    for (int i1 = 0, currentRidersSize1 = currentRiders.size(); i1 < currentRidersSize1; i1++) {
      passenger = currentRiders.get(i1);
      if (passenger.isMarkedForRemoval()) {
        currentRiders.remove(passenger);
        currentRidersSize1--;
        i1--;
      }
    }

    for (int i = 0, currentRidersSize = currentRiders.size(); i < currentRidersSize; i++) {
      passenger = currentRiders.get(i);
      if (passenger.shouldWaitFor()) {
        return true;
      }
    }

    return false;
  }

  public List<Passenger> getCurrentRiders() {
    return currentRiders;
  }

  public void removePassenger(AvatarSteeringManager avatarSteeringManager) {
    for (int i = 0, currentRidersSize = currentRiders.size(); i < currentRidersSize; i++) {
      Passenger rider = currentRiders.get(i);
      if (rider.getSteeringManager().equals(avatarSteeringManager)) {
        rider.markToRemove();
        return;
      }
    }
    for (int i = 0, passengersWaitingSize = passengersWaiting.size(); i < passengersWaitingSize; i++) {
      Passenger rider = passengersWaiting.get(i);
      if (rider.getSteeringManager().equals(avatarSteeringManager)) {
        rider.markToRemove();
        return;
      }
    }
  }

  public void clear() {
    passengersWaiting.clear();
    currentRiders.clear();
  }

  public void informPassengersOfServiceChange() {
    for (int i = 0, passengersWaitingSize = passengersWaiting.size(); i < passengersWaitingSize; i++) {
      Passenger passenger = passengersWaiting.get(i);
      passenger.informOfServiceChange();
    }

    for (int i = 0, currentRidersSize = currentRiders.size(); i < currentRidersSize; i++) {
      Passenger currentRider = currentRiders.get(i);
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
