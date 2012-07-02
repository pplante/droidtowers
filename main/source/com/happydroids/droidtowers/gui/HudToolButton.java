/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

public class HudToolButton extends ImageButton {
  private final ImageButtonStyle originalStyle;

  public HudToolButton(TextureAtlas hudAtlas) {
    super(hudAtlas.findRegion("tool-sprite"));

    originalStyle = getStyle();
  }

  public void resetStyle() {
    setStyle(originalStyle);
  }
}
