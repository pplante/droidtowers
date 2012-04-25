/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.TowerAssetManager;

import static com.happydroids.droidtowers.ColorUtil.rgba;

class ProgressBar extends Table {
  public static final Color ICS_BLUE = rgba("#0099CC");
  private final Label valueLabel;
  private final NoOpWidget barPlaceholder;
  private NinePatch patch;
  private int padding;
  private int value;

  ProgressBar() {
    super();

    padding = 3;

    defaults().top().left().fill();

    patch = new NinePatch(TowerAssetManager.texture("hud/horizontal-rule.png"));

    barPlaceholder = new NoOpWidget();
    add(barPlaceholder).expand().left().width("100%");

    valueLabel = FontManager.Default.makeLabel("100%");
    valueLabel.setAlignment(Align.RIGHT);
    add(valueLabel).width(45);
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
    valueLabel.setText(value + "%");
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    batch.disableBlending();

    patch.setColor(Color.DARK_GRAY);
    patch.draw(batch, x + barPlaceholder.x, y + barPlaceholder.y, barPlaceholder.width, barPlaceholder.height);

    if (value > 0) {
      patch.setColor(ICS_BLUE);
      patch.draw(batch, x + barPlaceholder.x + padding,
                        y + barPlaceholder.y + padding,
                        ((barPlaceholder.width / 100) * value) - (padding * 2),
                        barPlaceholder.height - (padding * 2));
    }

    batch.enableBlending();
    super.draw(batch, parentAlpha);
  }
}
