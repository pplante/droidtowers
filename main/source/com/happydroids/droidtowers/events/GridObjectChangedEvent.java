/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

public class GridObjectChangedEvent extends GridObjectEvent {
  protected String nameOfParamChanged;

  public GridObjectChangedEvent() {
  }

  @Override
  public String toString() {
    return "GridObjectChangedEvent{" +
                   "nameOfParamChanged='" + getNameOfParamChanged() + '\'' +
                   '}';
  }

  public String getNameOfParamChanged() {
    return nameOfParamChanged;
  }

  public void setNameOfParamChanged(String nameOfParamChanged) {
    this.nameOfParamChanged = nameOfParamChanged;
  }

  @Override
  public void reset() {
    super.reset();
    nameOfParamChanged = null;
  }
}
