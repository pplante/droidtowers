/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.math.GridPoint;

public class GridObjectBoundsChangeEvent extends GridObjectEvent {
  protected GridPoint prevSize;
  protected GridPoint prevPosition;

  public GridObjectBoundsChangeEvent() {
    prevPosition = new GridPoint();
    prevSize = new GridPoint();
  }

  public GridPoint getPrevSize() {
    return prevSize;
  }

  public void setPrevSize(GridPoint prevSize) {
    this.prevSize.set(prevSize);
  }

  public GridPoint getPrevPosition() {
    return prevPosition;
  }

  public void setPrevPosition(GridPoint prevPosition) {
    this.prevPosition.set(prevPosition);
  }

  @Override
  public void reset() {
    super.reset();
    prevPosition.set(0, 0);
    prevSize.set(0, 0);
  }

  @Override
  public void setGridObject(GridObject gridObject) {
    super.setGridObject(gridObject);
    if (prevPosition == null) {
      prevPosition = new GridPoint();
    }

    if (prevSize == null) {
      prevSize = new GridPoint();
    }

    prevPosition.set(gridObject.getPosition());
    prevSize.set(gridObject.getSize());
  }
}
