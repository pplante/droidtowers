/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.GridObjectPopOver;

public class TestGridObject extends GridObject {
  public TestGridObject(TestGridObjectType type, GameGrid gameGrid) {
    super(type, gameGrid);
  }

  @Override
  public Sprite getSprite() {
    return new Sprite();
  }

  @Override
  public float getDesirability() {
    return 0;
  }

  @Override
  public GridObjectPopOver makePopOver() {
    return null;
  }

  @Override
  protected boolean hasPopOver() {
    return false;
  }
}
