/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

public class LayeredDrawable extends BaseDrawable {
  private Array<Drawable> layers;

  public LayeredDrawable() {
    layers = new Array<Drawable>();
  }

  public void add(Drawable drawable) {
    layers.add(drawable);
  }

  @Override public void draw(SpriteBatch batch, float x, float y, float width, float height) {
    for (Drawable layer : layers) {
      layer.draw(batch, x, y, width, height);
    }
  }
}
