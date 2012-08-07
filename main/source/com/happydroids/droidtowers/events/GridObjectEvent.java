/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.badlogic.gdx.utils.Pool;
import com.happydroids.droidtowers.entities.GridObject;

public class GridObjectEvent implements Pool.Poolable {
  protected GridObject gridObject;

  @Override
  public void reset() {
    gridObject = null;
  }

  public GridObject getGridObject() {
    return gridObject;
  }

  public void setGridObject(GridObject gridObject) {
    this.gridObject = gridObject;
  }
}
