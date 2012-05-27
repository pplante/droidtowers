/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.actions;

import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.grid.GameGrid;

/**
 * A callback for when an actor is clicked.
 *
 * @author Nathan Sweet
 */
public interface GameGridClickListener {
  public void click(GameGrid gameGrid, GridObject gridObject, float x, float y);
}
