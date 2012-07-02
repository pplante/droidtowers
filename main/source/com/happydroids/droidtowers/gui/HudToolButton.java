/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.happydroids.droidtowers.Colors;

public class HudToolButton extends ColorizedImageButton {
  private final ImageButtonStyle originalStyle;

  public HudToolButton(TextureAtlas hudAtlas) {
    super(hudAtlas.findRegion("tool-sprite"), Colors.ICS_BLUE);

    originalStyle = getStyle();
  }

  public void resetStyle() {
    setStyle(originalStyle);
  }
}
