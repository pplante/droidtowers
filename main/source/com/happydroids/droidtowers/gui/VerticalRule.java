/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.happydroids.droidtowers.TowerAssetManager;

import static com.happydroids.droidtowers.ColorUtil.rgba;

public class VerticalRule extends Widget {
  private static final Color DEFAULT_COLOR = rgba("#0099CC"); // android ICS blue!
  private final Color desiredColor;
  private final int desiredWidth;
  private Texture texture;

  public VerticalRule() {
    this(DEFAULT_COLOR, 2);
  }

  public VerticalRule(Color desiredColor, int desiredWidth) {
    this.desiredColor = desiredColor;
    this.desiredWidth = desiredWidth;

    texture = TowerAssetManager.texture(TowerAssetManager.WHITE_SWATCH);
  }

  public VerticalRule(int height) {
    this(DEFAULT_COLOR, height);
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    Color prevColor = batch.getColor();
    batch.setColor(desiredColor);
    batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    batch.setColor(prevColor);
  }

  @Override
  public float getPrefWidth() {
    return desiredWidth;
  }
}
