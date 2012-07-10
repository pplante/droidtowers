/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.happydroids.droidtowers.input.InputSystem;

class GridObjectPopOverCloser extends InputAdapter {
  private final GridObjectPopOver objectPopOver;

  public GridObjectPopOverCloser(GridObjectPopOver objectPopOver) {
    this.objectPopOver = objectPopOver;
  }

  private boolean closePopOver() {
    objectPopOver.markToRemove(true);

    InputSystem.instance().removeInputProcessor(this);

    return true;
  }

  @Override
  public boolean touchDown(int x, int y, int pointer, int button) {
    closePopOver();

    return false;
  }

  @Override
  public boolean touchDragged(int x, int y, int pointer) {
    closePopOver();

    return false;
  }

  @Override
  public boolean keyDown(int keycode) {
    return (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) && closePopOver();
  }
}
