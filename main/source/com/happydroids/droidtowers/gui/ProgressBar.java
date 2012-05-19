/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;

class ProgressBar extends Widget {
  private final Label valueLabel;
  private NinePatch patch;
  private int padding;
  private int value;

  ProgressBar() {
    this(0);
  }

  public ProgressBar(int progress) {
    super();

    padding = 3;

    patch = new NinePatch(TowerAssetManager.texture(TowerAssetManager.WHITE_SWATCH));

    valueLabel = FontManager.Default.makeLabel("100%");
    valueLabel.setAlignment(Align.CENTER);

    setValue(progress);
  }

  @Override
  public float getMinWidth() {
    return 100;
  }

  @Override
  public float getMinHeight() {
    return 16;
  }

  public void setValue(int value) {
    this.value = value;
    valueLabel.setText(this.value + "%");
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    validate();

    patch.setColor(Color.DARK_GRAY);
    patch.draw(batch, x, y, width, height);

    if (value > 0) {
      patch.setColor(Colors.ICS_BLUE);
      patch.draw(batch, x + padding,
                        y + padding,
                        Math.min(((width / 100) * value), width) - (padding * 2),
                        height - (padding * 2));
    }

    valueLabel.x = x;
    valueLabel.y = y;
    valueLabel.draw(batch, parentAlpha);
  }

  @Override
  public void layout() {
    valueLabel.width = width;
    valueLabel.height = height;
  }
}
