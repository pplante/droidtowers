/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.happydroids.droidtowers.TowerAssetManager;

import static com.happydroids.droidtowers.ColorUtil.rgba;

public class TransparentTextButton extends TextButton {
  public TransparentTextButton(String text, Skin skin) {
    super(text, skin);

    setStyle(new TextButtonStyle(new NinePatch(TowerAssetManager.texture("hud/toast-bg.png")), new NinePatch(TowerAssetManager.texture("hud/horizontal-rule.png"), rgba("#007399")), null, 0, 0, 0, 0, getStyle().font, getStyle().fontColor, getStyle().downFontColor, getStyle().checkedFontColor));
  }
}
