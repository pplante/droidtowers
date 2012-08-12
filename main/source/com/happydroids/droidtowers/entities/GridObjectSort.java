/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import java.util.Comparator;

public class GridObjectSort {
  public static final Comparator<GridObject> byDesirability = new Comparator<GridObject>() {
    @Override
    public int compare(GridObject gridObject, GridObject gridObject1) {
      return compareValues(gridObject.getDesirability(), gridObject1.getDesirability());
    }
  };

  public static Comparator<GridObject> byZIndex = new Comparator<GridObject>() {
    @Override
    public int compare(GridObject gridObject, GridObject gridObject1) {
      return compareValues(gridObject.getGridObjectType().getZIndex(), gridObject1.getGridObjectType().getZIndex());
    }
  };

  public static final Comparator<GridObject> byDirtLevel = new Comparator<GridObject>() {
    @Override
    public int compare(GridObject left, GridObject right) {
      return compareValues(left.getDirtLevel(), right.getDirtLevel());
    }
  };

  private static int compareValues(float left, float right) {
    if (left < right) {
      return -1;
    } else if (left > right) {
      return 1;
    } else {
      return 0;
    }
  }
}
