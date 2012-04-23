/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.TowerAssetManager;

public class ToolTip extends Table {
  private final Label label;

  public ToolTip(Skin skin) {
    visible = false;
    label = new Label(skin);

    defaults();
    setBackground(new NinePatch(TowerAssetManager.texture("hud/toast-bg.png")));
    pad(4);
    add(label);
    pack();
  }

  public void setText(String message) {
    label.setText(message);
    pack();
  }
}
