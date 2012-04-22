/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.happydroids.droidtowers.graphics.PixmapGenerator;

import static com.happydroids.droidtowers.ColorUtil.rgba;

public class HorizontalRule extends Widget {
  private static final Color DEFAULT_COLOR = rgba("#0099CC"); // android ICS blue!
  private final int desiredHeight;
  private PixmapGenerator pixmapGenerator;

  public HorizontalRule() {
    this(DEFAULT_COLOR, 2);
  }

  public HorizontalRule(final Color desiredColor, int desiredHeight) {
    this.desiredHeight = desiredHeight;

    pixmapGenerator = new PixmapGenerator() {
      @Override
      protected Pixmap generate() {
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGB565);
        pixmap.setColor(desiredColor);
        pixmap.fill();
        return pixmap;
      }
    };
  }

  public HorizontalRule(int height) {
    this(DEFAULT_COLOR, height);
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    batch.draw(pixmapGenerator.getTexture(), x, y, width, height);
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

  @Override
  public void remove() {
    pixmapGenerator.dispose();

    super.remove();
  }
}
