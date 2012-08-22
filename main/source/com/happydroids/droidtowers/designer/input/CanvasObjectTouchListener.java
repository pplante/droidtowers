/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer.input;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.happydroids.droidtowers.designer.Canvas;

public class CanvasObjectTouchListener extends InputListener {
  private final Canvas canvas;
  private Actor selectedObject;
  private Vector2 touchOffset;

  public CanvasObjectTouchListener(Canvas canvas) {
    this.canvas = canvas;
    touchOffset = new Vector2();
  }

  @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
    event.stop();
    selectedObject = event.getListenerActor();
    touchOffset.set(x, y);
    return true;
  }

  @Override public void touchDragged(InputEvent event, float x, float y, int pointer) {
    if (selectedObject != null) {
      float xPos = x;
      float yPos = y;

//      int step = 2;
//      xPos = step * MathUtils.floor(xPos / step);
//      yPos = step * MathUtils.floor(yPos / step);

//      float scale = canvas.getScaleX();
//      xPos = MathUtils.clamp(xPos, 0, (canvas.getWidth() - selectedObject.getWidth()));
//      yPos = MathUtils.clamp(yPos, 0, (canvas.getHeight() - selectedObject.getHeight()));

      selectedObject.setPosition(xPos, yPos);

      event.stop();
    }
  }

  @Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
    if (selectedObject != null) {
      canvas.addActor(selectedObject);
      selectedObject = null;
      event.stop();
    }
  }
}
