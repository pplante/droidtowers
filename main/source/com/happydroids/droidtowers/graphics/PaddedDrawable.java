/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class PaddedDrawable extends NinePatchDrawable {
  private final int padding;

  public PaddedDrawable(NinePatch patch, int padding) {
    super(patch);
    this.padding = padding;
  }

  @Override public void draw(SpriteBatch batch, float x, float y, float width, float height) {
    super.draw(batch, x - padding, y - padding, width + padding * 2, height + padding * 2);
  }

  @Override public float getLeftWidth() {
    return padding;
  }

  @Override public float getBottomHeight() {
    return padding;
  }

  @Override public float getMinHeight() {
    return padding;
  }

  @Override public float getMinWidth() {
    return padding;
  }

  @Override public float getRightWidth() {
    return padding;
  }

  @Override public float getTopHeight() {
    return padding;
  }
}
