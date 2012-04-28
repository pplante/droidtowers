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

public class HorizontalRule extends Widget {
  private static final Color DEFAULT_COLOR = rgba("#0099CC"); // android ICS blue!
  private final Color desiredColor;
  private final int desiredHeight;
  private Texture texture;

  public HorizontalRule() {
    this(DEFAULT_COLOR, 2);
  }

  public HorizontalRule(Color desiredColor, int desiredHeight) {
    this.desiredColor = desiredColor;
    this.desiredHeight = desiredHeight;

    texture = TowerAssetManager.texture(TowerAssetManager.WHITE_SWATCH);
  }

  public HorizontalRule(int height) {
    this(DEFAULT_COLOR, height);
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    Color prevColor = batch.getColor();
    batch.setColor(desiredColor);
    batch.draw(texture, x, y, width, height);
    batch.setColor(prevColor);
  }

  @Override
  public float getPrefHeight() {
    return desiredHeight;
  }

  @Override
  public float getMaxHeight() {
    return desiredHeight;
  }

  @Override
  public float getMinHeight() {
    return desiredHeight;
  }

  @Override
  public float getPrefWidth() {
    return parent.width;
  }
}
