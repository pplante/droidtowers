/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.controllers.AvatarSteeringManager;
import com.happydroids.droidtowers.entities.elevator.ElevatorStop;
import com.happydroids.droidtowers.entities.elevator.Passenger;
import com.happydroids.droidtowers.math.Direction;

import java.util.*;

import static com.happydroids.droidtowers.math.Direction.DOWN;

public class ElevatorQueue {
  private LinkedList<Passenger> passengersWaiting;
  private LinkedList<ElevatorStop> stops;
  private ElevatorStop currentStop;
  private Set<Passenger> currentRiders;
  private float queueTime;
  private final Elevator elevator;
  private boolean killingPassengers;

  public ElevatorQueue(Elevator elevator) {
    this.elevator = elevator;
    passengersWaiting = Lists.newLinkedList();
    currentRiders = Sets.newHashSet();
    stops = Lists.newLinkedList();
  }

  public void add(Passenger passenger) {
    passengersWaiting.add(passenger);
  }

  public List<Passenger> getPassengersWaiting() {
    return passengersWaiting;
  }

  public boolean determinePickups() {
    Iterator<Passenger> passengerIterator = passengersWaiting.iterator();
    while (passengerIterator.hasNext()) {
      Passenger passenger = passengerIterator.next();
      if (!elevator.servicesFloor(passenger.boardingFloor) || !elevator.servicesFloor(passenger.destinationFloor)) {
        passengerIterator.remove();
        passenger.killByElevator();
      }
    }

    if (passengersWaiting.isEmpty()) return false;

    Passenger firstPassenger = passengersWaiting.poll();
    Set<Passenger> currentLoad = Sets.newHashSet();

    for (Passenger otherPassenger : passengersWaiting) {
      if (firstPassenger.travelContains(otherPassenger)) {
        currentLoad.add(otherPassenger);
      }
    }

    currentLoad.add(firstPassenger);
    passengersWaiting.removeAll(currentLoad);
    makeStops(currentLoad, firstPassenger.travelDirection);
    moveToNextStop();

    return true;
  }

  private void makeStops(Set<Passenger> currentLoad, Direction travelDirection) {
    List<Integer> floorNumbers = Lists.newArrayList();
    for (Passenger passenger : currentLoad) {
      floorNumbers.add(passenger.boardingFloor);
      floorNumbers.add(passenger.destinationFloor);
    }

    Collections.sort(floorNumbers);

    if (travelDirection.equals(DOWN)) {
      Collections.reverse(floorNumbers);
    }

    for (Integer floorNumber : floorNumbers) {
      ElevatorStop stop = new ElevatorStop(floorNumber);
      for (Passenger passenger : currentLoad) {
        if (passenger.boardingFloor == floorNumber) {
          stop.boarding.add(passenger);
        } else if (passenger.destinationFloor == floorNumber) {
          stop.disembarking.add(passenger);
        }
      }

      stops.add(stop);
    }
  }

  public boolean moveToNextStop() {
    currentStop = null;

    if (stops.isEmpty()) {
      return false;
    }

    currentStop = stops.poll();

    return currentStop != null;
  }

  public ElevatorStop getCurrentStop() {
    return currentStop;
  }

  public void informPassengers() {
    for (Passenger passenger : currentStop.boarding) {
      currentRiders.add(passenger);
      passenger.boardNow();
    }

    for (Passenger passenger : currentStop.disembarking) {
      currentRiders.remove(passenger);
      passenger.disembarkNow();
    }
  }

  public boolean waitingOnRiders() {
    for (Passenger passenger : currentRiders) {
      if (passenger.shouldWaitFor()) {
        return true;
      }
    }

    return false;
  }

  public Set<Passenger> getCurrentRiders() {
    return currentRiders;
  }

  public void killPassengers() {
    killingPassengers = true;
    for (Passenger currentRider : currentRiders) {
      currentRider.killByElevator();
    }

    currentRiders.clear();
    currentStop = null;
    stops.clear();
    killingPassengers = false;
  }

  public void removePassenger(AvatarSteeringManager avatarSteeringManager) {
    if (killingPassengers) return;

    for (Passenger rider : currentRiders) {
      if (rider.getSteeringManager().equals(avatarSteeringManager)) {
        currentRiders.remove(rider);

        break;
      }
    }
  }

  public void clear() {
    passengersWaiting.clear();
    currentRiders.clear();
    currentStop = null;
  }
}
