/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.happydroids.droidtowers.TowerAssetManager;

public class DropShadow extends NinePatchDrawable {
  public DropShadow() {
    super(TowerAssetManager.ninePatch("swatches/drop-shadow.png", Color.WHITE, 22, 22, 22, 22));
  }

  @Override public void draw(SpriteBatch batch, float x, float y, float width, float height) {
    super.draw(batch, x - 22, y - 22, width + 44, height + 44);
  }
}
