/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;

import static com.happydroids.droidtowers.TowerAssetManager.texture;

public class PopOverMenu extends Table {
  protected Texture triangle;

  public PopOverMenu() {
    triangle = texture(TowerAssetManager.WHITE_SWATCH_TRIANGLE);
    setBackground(TowerAssetManager.ninePatch(TowerAssetManager.WHITE_SWATCH, Colors.TRANSPARENT_BLACK));

    defaults().top().left().pad(6);
  }

  @Override
  protected void drawBackground(SpriteBatch batch, float parentAlpha) {
    super.drawBackground(batch, parentAlpha);

    batch.setColor(Colors.TRANSPARENT_BLACK);
    batch.draw(triangle, x + width - triangle.getWidth() * 1.3f, y + height);
  }

  public int getOffset() {
    return triangle.getHeight() / 2;
  }
}
