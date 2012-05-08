/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.controllers.AvatarSteeringManager;
import com.happydroids.droidtowers.entities.elevator.Passenger;
import com.happydroids.droidtowers.math.Direction;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.happydroids.droidtowers.math.Direction.DOWN;

public class ElevatorQueue {
  public static final int INVALID_FLOOR = -1;
  private LinkedList<Passenger> passengersWaiting;
  private Set<Passenger> currentRiders;
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
    currentRiders = Sets.newHashSet();
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
      if (firstPassenger.travelContains(otherPassenger)) {
        currentRiders.add(otherPassenger);
      }
    }

    passengersWaiting.removeAll(currentRiders);
    makeStops(firstPassenger.travelDirection);
    moveToNextStop();

    return true;
  }

  private void makeStops(Direction travelDirection) {
    floorNumbers = Lists.newLinkedList();
    for (Passenger passenger : currentRiders) {
      floorNumbers.add(passenger.boardingFloor);
      floorNumbers.add(passenger.destinationFloor);
    }

    Collections.sort(floorNumbers);

    if (travelDirection.equals(DOWN)) {
      Collections.reverse(floorNumbers);
    }
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

  public void informPassengers() {
    for (Passenger currentRider : currentRiders) {
      if (currentRider.boardingFloor == currentFloor) {
        currentRider.boardNow();
      } else if (currentRider.destinationFloor == currentFloor) {
        currentRider.disembarkNow();
        currentRider.markToRemove();
      }
    }
  }

  public boolean waitingOnRiders() {
    Iterables.removeIf(passengersWaiting, PASSENGER_REMOVAL_PREDICATE);
    Iterables.removeIf(currentRiders, PASSENGER_REMOVAL_PREDICATE);

    for (Passenger currentRider : currentRiders) {
      if (currentRider.shouldWaitFor()) {
        return true;
      }
    }

    return false;
  }

  public Set<Passenger> getCurrentRiders() {
    return currentRiders;
  }

  public void removePassenger(AvatarSteeringManager avatarSteeringManager) {
    for (Passenger rider : currentRiders) {
      if (rider.getSteeringManager().equals(avatarSteeringManager)) {
        rider.markToRemove();
        return;
      }
    }
    for (Passenger rider : passengersWaiting) {
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
