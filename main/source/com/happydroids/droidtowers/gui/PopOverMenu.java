/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;

import static com.happydroids.droidtowers.TowerAssetManager.texture;

public class PopOverMenu extends Table {
  protected Texture triangle;
  private int arrowAlignment;

  public PopOverMenu() {
    triangle = texture(TowerAssetManager.WHITE_SWATCH_TRIANGLE);
    color.set(Colors.TRANSPARENT_BLACK);
    originX = 0f;
    originY = 0f;
    setBackground(TowerAssetManager.ninePatch(TowerAssetManager.WHITE_SWATCH, Colors.TRANSPARENT_BLACK));

    defaults().top().left().pad(6);
  }

  @Override
  protected void drawBackground(SpriteBatch batch, float parentAlpha) {
    super.drawBackground(batch, parentAlpha);

    float xOffset = (arrowAlignment & Align.RIGHT) != 0 ? width - triangle.getWidth() : 0;
    batch.draw(triangle, x + xOffset, y + height);
  }

  public int getOffset() {
    return triangle.getHeight();
  }

  public void alignArrow(int arrowAlignment) {
    this.arrowAlignment = arrowAlignment;
  }
}
