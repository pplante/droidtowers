/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer.input;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.happydroids.droidtowers.designer.Canvas;

public class CanvasTouchZoomListener extends ActorGestureListener {
  private float initialScale;
  private Canvas canvas;

  public CanvasTouchZoomListener(Canvas canvas) {
    this.canvas = canvas;
  }

  @Override public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
    initialScale = canvas.getScaleX();
  }

  @Override public void zoom(InputEvent event, float initialDistance, float distance) {
    event.stop();
    canvas.setScale(initialScale * (distance / initialDistance));
  }
}
