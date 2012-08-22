/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer.input;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.happydroids.droidtowers.designer.Canvas;

public class CanvasMouseZoomListener extends InputListener {
  private Canvas canvas;

  public CanvasMouseZoomListener(Canvas canvas) {
    this.canvas = canvas;
  }

  @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
    canvas.getStage().setScrollFocus(canvas);
  }

  @Override public boolean scrolled(InputEvent event, int amount) {
    event.stop();

    canvas.setScale(canvas.getScaleX() + amount / 10f);

    return true;
  }
}
