/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.employee.JobCandidate;
import com.happydroids.droidtowers.entities.CommercialSpace;

public class EmployeeHiredEvent extends GridObjectEvent {
  public final JobCandidate employee;

  public EmployeeHiredEvent(CommercialSpace commercialSpace, JobCandidate employee) {
    super(commercialSpace);

    this.employee = employee;
  }
}
