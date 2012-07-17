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
import java.util.LinkedList;
import java.util.List;

import static com.happydroids.droidtowers.math.Direction.DOWN;

public class ElevatorQueue {
  public static final int INVALID_FLOOR = -1;
  private LinkedList<Passenger> passengersWaiting;
  private List<Passenger> currentRiders;
  private List<Integer> floorNumbers;
  private int currentFloor;
  public static final Predicate<Passenger> PASSENGER_REMOVAL_PREDICATE = new Predicate<Passenger>() {
    @Override
    public boolean apply(@Nullable Passenger input) {
      return input.isMarkedForRemoval();
    }
  };

  public ElevatorQueue(Elevator elevator) {
    passengersWaiting = Lists.newLinkedList();
    currentRiders = Lists.newArrayList();
    currentFloor = INVALID_FLOOR;
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

    if (passengersWaiting.isEmpty()) return false;

    Passenger firstPassenger = passengersWaiting.poll();
    currentRiders.clear();
    currentRiders.add(firstPassenger);

    for (Passenger otherPassenger : passengersWaiting) {
      if (firstPassenger.travelContains(otherPassenger) && currentRiders.size() < 8 && !currentRiders.contains(otherPassenger)) {
        currentRiders.add(otherPassenger);
      }
    }

    passengersWaiting.removeAll(currentRiders);
    floorNumbers = Lists.newLinkedList();
    for (Passenger passenger : currentRiders) {
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
    currentFloor = INVALID_FLOOR;

    if (floorNumbers.isEmpty()) {
      return false;
    }

    currentFloor = floorNumbers.remove(0);
    return true;
  }

  public int getCurrentFloor() {
    return currentFloor;
  }

  public void informPassengersOfStop() {
    for (int i = 0, currentRidersSize = currentRiders.size(); i < currentRidersSize; i++) {
      Passenger currentRider = currentRiders.get(i);
      if (currentRider.boardingFloor == currentFloor) {
        currentRider.boardNow();
      } else if (currentRider.destinationFloor == currentFloor) {
        currentRider.disembarkNow();
        currentRider.markToRemove();
      }
    }
  }

  public boolean waitingOnRiders() {
    for (int i1 = 0, passengersWaitingSize = passengersWaiting.size(); i1 < passengersWaitingSize; i1++) {
      Passenger passenger = passengersWaiting.get(i1);
      if (PASSENGER_REMOVAL_PREDICATE.apply(passenger)) {
        passengersWaiting.remove(i1);
        passengersWaitingSize--;
        i1--;
      }
    }

    for (int i1 = 0, currentRidersSize1 = currentRiders.size(); i1 < currentRidersSize1; i1++) {
      Passenger currentRider = currentRiders.get(i1);
      if (PASSENGER_REMOVAL_PREDICATE.apply(currentRider)) {
        currentRiders.remove(currentRider);
        currentRidersSize1--;
        i1--;
      }
    }

    for (int i = 0, currentRidersSize = currentRiders.size(); i < currentRidersSize; i++) {
      Passenger currentRider = currentRiders.get(i);
      if (currentRider.shouldWaitFor()) {
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
    for (Passenger passenger : passengersWaiting) {
      passenger.informOfServiceChange();
    }

    for (Passenger currentRider : currentRiders) {
      currentRider.informOfServiceChange();
    }

    floorNumbers.clear();
    currentFloor = INVALID_FLOOR;
  }
}
