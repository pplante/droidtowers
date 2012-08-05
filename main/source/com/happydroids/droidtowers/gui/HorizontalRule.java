/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;

public class HorizontalRule extends Widget {
  private final Color desiredColor;
  private final int desiredHeight;
  private Texture texture;

  public HorizontalRule() {
    this(Colors.ICS_BLUE, 2);
  }

  public HorizontalRule(Color desiredColor, int desiredHeight) {
    this.desiredColor = desiredColor;
    this.desiredHeight = desiredHeight;

    texture = TowerAssetManager.texture(TowerAssetManager.WHITE_SWATCH);
  }

  public HorizontalRule(int height) {
    this(Colors.ICS_BLUE, height);
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    Color prevColor = batch.getColor();
    batch.setColor(desiredColor);
    batch.draw(texture, getX(), getY(), getWidth(), getHeight());
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
}
