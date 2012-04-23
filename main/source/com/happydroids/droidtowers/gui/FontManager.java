/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.google.common.collect.Maps;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.platform.Display;

import java.util.HashMap;

import static com.happydroids.droidtowers.platform.Display.scale;

public enum FontManager {
  Default("fonts/roboto_white_14.fnt", "fonts/roboto_white_28.fnt", Color.WHITE),
  Roboto18("fonts/roboto_white_18.fnt", "fonts/roboto_white_32.fnt", Color.WHITE, 8, 16),
  RobotoBold18("fonts/roboto_bold_white_18.fnt", "fonts/roboto_white_32.fnt", Color.WHITE, 8, 16),
  Roboto32("fonts/roboto_white_32.fnt", "fonts/roboto_white_48.fnt", Color.WHITE),
  Roboto64("fonts/roboto_white_64.fnt", "fonts/roboto_white_96.fnt", Color.WHITE),
  Roboto24("fonts/roboto_white_24.fnt", "fonts/roboto_white_36.fnt", Color.WHITE),
  Roboto12("fonts/roboto_white_12.fnt", "fonts/roboto_white_24.fnt", Color.WHITE);

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
    this.buttonPadTop = scale(buttonPadTop);
    this.buttonPadLeft = scale(buttonPadLeft);
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
      BitmapFont font;
      if (TowerAssetManager.assetManager().isLoaded(fontPath)) {
        font = TowerAssetManager.bitmapFont(fontPath);
      } else {
        font = new BitmapFont(Gdx.files.internal(fontPath), false);
      }

      font.setUseIntegerPositions(true);
      bitmapFonts.put(fontPath, font);
    }

    return bitmapFonts.get(fontPath);
  }

  private void reset() {
    BitmapFont font = bitmapFonts.remove(fontPath);
    if (font != null) {
      font.dispose();
    }

    labelStyle = null;
  }

  public Label makeLabel(String text) {
    return new Label(text, labelStyle());
  }

  public TransparentTextButton makeTransparentButton(String labelText, final Skin skin) {
    return applyTextButtonLabelStyle(new TransparentTextButton(labelText, skin));
  }

  public TextButton makeTextButton(String labelText, Skin skin) {
    return applyTextButtonLabelStyle(new TextButton(labelText, skin));
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

  public static void resetAll() {
    for (FontManager fontManager : values()) {
      fontManager.reset();
    }
  }
}
