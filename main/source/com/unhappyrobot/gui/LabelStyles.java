package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.google.common.collect.Maps;

import java.util.HashMap;

public enum LabelStyles {
  BankGothic64("fonts/bank_gothic_64.fnt", Color.WHITE),
  Default("fonts/helvetica_neue_12_white.fnt", Color.WHITE);

  private static HashMap<String, BitmapFont> bitmapFonts = Maps.newHashMap();
  private Label.LabelStyle labelStyle;
  private final String fontPath;
  private final Color color;

  LabelStyles(String fontPath, Color color) {
    this.fontPath = fontPath;
    this.color = color;
  }

  private Label.LabelStyle labelStyle() {
    if (labelStyle == null) {
      if (!bitmapFonts.containsKey(fontPath)) {
        bitmapFonts.put(fontPath, new BitmapFont(Gdx.files.internal(fontPath), false));
      }

      labelStyle = new Label.LabelStyle(bitmapFonts.get(fontPath), color);
    }

    return labelStyle;
  }

  public Label makeLabel(String text) {
    return new Label(text, labelStyle());
  }
}
