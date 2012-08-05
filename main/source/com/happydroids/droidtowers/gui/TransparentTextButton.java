/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.happydroids.droidtowers.TowerAssetManager;

public class TransparentTextButton extends TextButton {
  public TransparentTextButton(String text, Skin skin, Color upColor, Color downColor) {
    super(text, skin);

    TextButtonStyle style = new TextButtonStyle(getStyle());
    style.down = TowerAssetManager.ninePatchDrawable(TowerAssetManager.WHITE_SWATCH, upColor);
    style.up = TowerAssetManager.ninePatchDrawable(TowerAssetManager.WHITE_SWATCH, downColor);
    style.checked = TowerAssetManager.ninePatchDrawable(TowerAssetManager.WHITE_SWATCH, downColor);

    setStyle(style);
  }
}
