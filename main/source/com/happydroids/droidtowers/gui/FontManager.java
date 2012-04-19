/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.google.common.collect.Maps;
import com.happydroids.droidtowers.platform.Display;

import java.util.HashMap;

public enum FontManager {
  Default("fonts/roboto_white_14.fnt", "fonts/roboto_white_28.fnt", Color.WHITE),
  RobotoBold18("fonts/roboto_bold_white_18.fnt", "fonts/roboto_white_32.fnt", Color.WHITE, 8, 16),
  Roboto32("fonts/roboto_white_32.fnt", "fonts/roboto_white_64.fnt", Color.WHITE),
  Roboto64("fonts/roboto_white_64.fnt", "fonts/roboto_white_128.fnt", Color.WHITE);

  private static HashMap<String, BitmapFont> bitmapFonts = Maps.newHashMap();
  private Label.LabelStyle labelStyle;
  private final String fontPath;
  private final Color color;
  private final int buttonPadTop;
  private final int buttonPadLeft;
  private TextField.TextFieldStyle textFieldStyle;

  FontManager(String fontPath, Color color) {
    this(fontPath, fontPath, color, 0, 0);
  }

  FontManager(String mdpiFontPath, String hdpiFontPath, Color color, int buttonPadTop, int buttonPadLeft) {
    this.fontPath = Display.getScaledDensity() > 1f ? hdpiFontPath : mdpiFontPath;
    this.color = color;
    this.buttonPadTop = buttonPadTop;
    this.buttonPadLeft = buttonPadLeft;
  }

  FontManager(String mdpiFontPath, String hdpiFontPath, Color color) {
    this(mdpiFontPath, hdpiFontPath, color, 0, 0);
  }

  private Label.LabelStyle labelStyle() {
    if (labelStyle == null) {
      getFont();

      labelStyle = new Label.LabelStyle(getFont(), color);
    }

    return labelStyle;
  }

  public BitmapFont getFont() {
    if (!bitmapFonts.containsKey(fontPath)) {
      BitmapFont font = new BitmapFont(Gdx.files.internal(fontPath), false);
      font.setUseIntegerPositions(true);
      bitmapFonts.put(fontPath, font);
    }

    return bitmapFonts.get(fontPath);
  }

  public Label makeLabel(String text) {
    return new Label(text, labelStyle());
  }

  public TextButton makeTextButton(String labelText, Skin skin) {
    return applyTextButtonLabelStyle(new TextButton(labelText, skin));
  }

  public void reset() {
    bitmapFonts.remove(fontPath);
    labelStyle = null;
  }

  public CheckBox makeCheckBox(String labelText, Skin skin) {
    return applyTextButtonLabelStyle(new CheckBox(labelText, skin));
  }

  private <T extends TextButton> T applyTextButtonLabelStyle(T textButton) {
    textButton.getLabel().setStyle(labelStyle());
    textButton.getLabelCell().pad(buttonPadTop, buttonPadLeft, buttonPadTop, buttonPadLeft);
    textButton.invalidate();

    return textButton;
  }

  public TextField makeTextField(String labelText, String hintText, Skin skin) {
    if (textFieldStyle == null) {
      TextField.TextFieldStyle defaultStyle = skin.getStyle(TextField.TextFieldStyle.class);
      textFieldStyle = new TextField.TextFieldStyle(getFont(), color, getFont(), defaultStyle.messageFontColor, defaultStyle.cursor, defaultStyle.selection, defaultStyle.background);
    }

    return new TextField(labelText, hintText, textFieldStyle);
  }
}
