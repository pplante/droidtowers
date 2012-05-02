/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities.elevator;

import com.google.common.collect.Sets;

import java.util.Set;

public class ElevatorStop {
  public final int floorNumber;
  public final Set<Passenger> boarding;
  public final Set<Passenger> disembarking;

  public ElevatorStop(int floorNumber) {
    this.floorNumber = floorNumber;
    boarding = Sets.newHashSet();
    disembarking = Sets.newHashSet();
  }

  @Override
  public String toString() {
    return "ElevatorStop{" +
                   "floorNumber=" + floorNumber +
                   ", boarding=" + boarding +
                   ", disembarking=" + disembarking +
                   '}';
  }
}
