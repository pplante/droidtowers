/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.employee.JobCandidate;

public class EmployeeFiredEvent extends GridObjectEvent {
  private JobCandidate employee;

  @Override
  public void reset() {
    super.reset();
    setEmployee(null);
  }

  public JobCandidate getEmployee() {
    return employee;
  }

  public void setEmployee(JobCandidate employee) {
    this.employee = employee;
  }
}
