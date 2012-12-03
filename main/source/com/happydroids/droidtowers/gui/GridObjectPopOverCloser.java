/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.droidtowers.input.InputSystem;

class GridObjectPopOverCloser extends InputAdapter {
  private final GridObjectPopOver objectPopOver;

  public GridObjectPopOverCloser(GridObjectPopOver objectPopOver) {
    this.objectPopOver = objectPopOver;
  }

  private boolean checkTouchPoint(int screenX, int screenY) {
    Vector2 touchDown = new Vector2(screenX, screenY);
    objectPopOver.getStage().screenToStageCoordinates(touchDown);
    objectPopOver.stageToLocalCoordinates(touchDown);

    if (objectPopOver.hit(touchDown.x, touchDown.y, true) == null) {
      closePopOver();
    }

    return false;
  }

  private void closePopOver() {
    objectPopOver.remove();

    InputSystem.instance().removeInputProcessor(this);
  }

  @Override
  public boolean touchDown(int x, int y, int pointer, int button) {
    return checkTouchPoint(x, y);
  }

  @Override
  public boolean touchDragged(int x, int y, int pointer) {
    return checkTouchPoint(x, y);
  }

  @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return checkTouchPoint(screenX, screenY);
  }

  @Override
  public boolean keyDown(int keycode) {
    if ((keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK)) {
      closePopOver();

      return true;
    }

    return false;
  }
}
