/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class ColorizedImageButton extends ImageButton {
  public ColorizedImageButton(TextureRegion region, Color downColor) {
    super(new NinePatchDrawable(new NinePatch(region, Color.WHITE)), new NinePatchDrawable(new NinePatch(region, downColor)));
  }
}
