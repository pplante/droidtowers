/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.happydroids.droidtowers.TowerAssetManager;

public class InnerShadow extends NinePatchDrawable {
  public InnerShadow() {
    super(TowerAssetManager.ninePatch("swatches/inner-shadow.png", Color.WHITE, 22, 22, 22, 22));
  }

  @Override public void draw(SpriteBatch batch, float x, float y, float width, float height) {
    super.draw(batch, x, y, width, height);
  }
}
