/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer.input;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.happydroids.droidtowers.designer.Canvas;

public class CanvasTouchListener extends InputListener {
  private final Canvas canvas;
  private Actor selectedObject;
  private Vector2 touchOffset;

  public CanvasTouchListener(Canvas canvas) {
    this.canvas = canvas;
  }

  @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
    Rectangle rect = new Rectangle();
    float scale = canvas.getScaleX();
    for (Actor child : canvas.getChildren()) {
      rect.set(child.getX() * scale, child.getY() * scale, child.getWidth() * scale, child.getHeight() * scale);
      if (rect.contains(x, y)) {
        selectedObject = child;
        touchOffset = new Vector2(x - child.getX() * scale, y - child.getY() * scale);
        event.stop();
        break;
      }
    }

    return true;
  }

  @Override public void touchDragged(InputEvent event, float x, float y, int pointer) {
    if (selectedObject != null) {
      float xPos = x - touchOffset.x;
      float yPos = y - touchOffset.y;

      int step = 8;
      xPos = step * MathUtils.floor(xPos / step);
      yPos = step * MathUtils.floor(yPos / step);

      float scale = canvas.getScaleX();
      xPos = MathUtils.clamp(xPos, 0, (canvas.getWidth() - selectedObject.getWidth()) * scale);
      yPos = MathUtils.clamp(yPos, 0, (canvas.getHeight() - selectedObject.getHeight()) * scale);

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

  @Override public boolean scrolled(InputEvent event, float x, float y, int amount) {
    float scale = canvas.getScaleX();
    scale += (float) amount / 10;
    System.out.println("scale = " + scale);
    canvas.setScale(scale);

    return true;
  }

  public void setSelectedObject(Actor selectedObject) {
    this.selectedObject = selectedObject;
  }

  public Actor getSelectedObject() {
    return selectedObject;
  }

  public void setTouchOffset(Vector2 touchOffset) {
    this.touchOffset = touchOffset;
  }

  public Vector2 getTouchOffset() {
    return touchOffset;
  }
}
