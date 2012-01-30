package com.unhappyrobot;

import com.unhappyrobot.entities.GridObject;

import java.util.HashSet;
import java.util.Set;

public class GridPosition {
  private Set<GridObject> objects = new HashSet<GridObject>();
  public boolean containsTransit;
  public boolean connectedToTransit;
  public boolean containsElevator;
  public boolean containsStair;

  public Set<GridObject> getObjects() {
    return objects;
  }

  public void add(GridObject gridObject) {
    objects.add(gridObject);
  }

  public void remove(GridObject gridObject) {
    objects.remove(gridObject);
  }

  public int size() {
    return objects.size();
  }
}
