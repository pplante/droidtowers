/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.employee.JobCandidate;

public class EmployeeHiredEvent extends GridObjectEvent {
  protected JobCandidate employee;

  @Override
  public void reset() {
    super.reset();
    employee = null;
  }

  public JobCandidate getEmployee() {
    return employee;
  }

  public void setEmployee(JobCandidate employee) {
    this.employee = employee;
  }
}
