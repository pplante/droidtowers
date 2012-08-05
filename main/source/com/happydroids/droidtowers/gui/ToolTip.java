/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;

public class ToolTip extends Table {
  private final Label label;

  public ToolTip() {
    setVisible(false);
    label = FontManager.Default.makeLabel("");

    defaults();
    setBackground(TowerAssetManager.ninePatchDrawable(TowerAssetManager.WHITE_SWATCH, Colors.TRANSPARENT_BLACK));
    pad(4);
    add(label);
    pack();
  }

  public void setText(String message) {
    label.setText(message);
    pack();
  }
}
