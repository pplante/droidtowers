/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;

import static com.happydroids.droidtowers.ColorUtil.rgba;

public class TransparentTextButton extends TextButton {
  public TransparentTextButton(String text, Skin skin) {
    super(text, skin);

    setStyle(new TextButtonStyle(TowerAssetManager.ninePatch(TowerAssetManager.WHITE_SWATCH, Colors.DARK_GRAY),
                                        new NinePatch(TowerAssetManager.texture(TowerAssetManager.WHITE_SWATCH), rgba("#007399")),
                                        new NinePatch(TowerAssetManager.texture(TowerAssetManager.WHITE_SWATCH), rgba("#007399")),
                                        getStyle().pressedOffsetX,
                                        getStyle().pressedOffsetY,
                                        getStyle().unpressedOffsetX,
                                        getStyle().unpressedOffsetY,
                                        getStyle().font,
                                        getStyle().fontColor,
                                        getStyle().downFontColor,
                                        getStyle().checkedFontColor));
  }
}
