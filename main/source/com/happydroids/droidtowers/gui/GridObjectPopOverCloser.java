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
    objectPopOver.remove();

    InputSystem.instance().removeInputProcessor(this);

    return true;
  }

  @Override
  public boolean touchDown(int x, int y, int pointer, int button) {
    return closePopOver();
  }

  @Override
  public boolean touchDragged(int x, int y, int pointer) {
    return closePopOver();
  }

  @Override
  public boolean keyDown(int keycode) {
    return (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) && closePopOver();
  }
}
