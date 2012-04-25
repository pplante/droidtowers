/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class HoverWidget extends Widget {
  private final Actor widget;
  private float opacity;

  public HoverWidget(Actor widget) {
    this.widget = widget;
    opacity = 0.5f;

    touchable = true;
  }

  @Override
  public float getPrefWidth() {
    return widget.width;
  }

  @Override
  public float getPrefHeight() {
    return widget.height;
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    widget.x = x;
    widget.y = y;
    widget.height = height;
    widget.width = width;

    widget.draw(batch, opacity);
  }

  @Override
  public boolean touchDown(float x, float y, int pointer) {
    return widget.touchDown(x, y, pointer);
  }

  @Override
  public void touchDragged(float x, float y, int pointer) {
    widget.touchDragged(x, y, pointer);
  }

  @Override
  public void touchUp(float x, float y, int pointer) {
    widget.touchUp(x, y, pointer);
  }

  @Override
  public Actor hit(float x, float y) {
    Actor wasHit = widget.hit(x, y);
    if (wasHit != null) {
      opacity = 1f;
    } else {
      opacity = 0.5f;
    }

    return wasHit;
  }
}
