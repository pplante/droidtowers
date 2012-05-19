/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.google.common.collect.Maps;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.platform.Display;

import java.util.HashMap;

import static com.happydroids.droidtowers.platform.Display.scale;

public enum FontManager {
  Default("fonts/roboto_white_14.fnt", "fonts/roboto_white_24.fnt"),
  Roboto18("fonts/roboto_white_18.fnt", "fonts/roboto_white_32.fnt", 8, 16),
  RobotoBold18("fonts/roboto_bold_white_18.fnt", "fonts/roboto_white_32.fnt", 8, 16),
  Roboto32("fonts/roboto_white_32.fnt", "fonts/roboto_white_48.fnt"),
  Roboto64("fonts/roboto_white_64.fnt", "fonts/roboto_white_96.fnt"),
  Roboto24("fonts/roboto_white_24.fnt", "fonts/roboto_white_36.fnt"),
  Roboto12("fonts/roboto_white_12.fnt", "fonts/roboto_white_18.fnt"),
  BankGothic32("fonts/bank_gothic_32.fnt", Color.WHITE); // elevators shouldn't scale!

  private static HashMap<String, BitmapFont> bitmapFonts = Maps.newHashMap();
  private Label.LabelStyle labelStyle;
  private final String fontPath;
  private final int buttonPadTop;
  private final int buttonPadLeft;
  private TextField.TextFieldStyle textFieldStyle;

  FontManager(String fontPath, Color color) {
    this(fontPath, fontPath, 0, 0);
  }

  FontManager(String mdpiFontPath, String hdpiFontPath, int buttonPadTop, int buttonPadLeft) {
    this.fontPath = Display.getScaledDensity() > 1f ? hdpiFontPath : mdpiFontPath;
    this.buttonPadTop = scale(buttonPadTop);
    this.buttonPadLeft = scale(buttonPadLeft);
  }

  FontManager(String mdpiFontPath, String hdpiFontPath) {
    this(mdpiFontPath, hdpiFontPath, 0, 0);
  }

  private Label.LabelStyle labelStyle() {
    if (labelStyle == null) {
      labelStyle = labelStyle(Color.WHITE);
    }

    return labelStyle;
  }

  private Label.LabelStyle labelStyle(Color color) {
    return new Label.LabelStyle(getFont(), color);
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
    return makeLabel(text, Color.WHITE);
  }

  public TransparentTextButton makeTransparentButton(String labelText) {
    return applyTextButtonLabelStyle(new TransparentTextButton(labelText, TowerAssetManager.getCustomSkin()));
  }

  public TextButton makeTextButton(String labelText) {
    return applyTextButtonLabelStyle(new TextButton(labelText, TowerAssetManager.getCustomSkin()));
  }

  public CheckBox makeCheckBox(String labelText) {
    return applyTextButtonLabelStyle(new CheckBox(labelText, TowerAssetManager.getCustomSkin()));
  }

  private <T extends TextButton> T applyTextButtonLabelStyle(T textButton) {
    textButton.getLabel().setStyle(labelStyle());
    textButton.getLabelCell().pad(buttonPadTop, buttonPadLeft, buttonPadTop, buttonPadLeft);
    textButton.invalidate();
    textButton.pack();

    return textButton;
  }

  public TextField makeTextField(String labelText, String hintText) {
    if (textFieldStyle == null) {
      TextField.TextFieldStyle defaultStyle = TowerAssetManager.getCustomSkin().getStyle(TextField.TextFieldStyle.class);
      textFieldStyle = new TextField.TextFieldStyle(getFont(), defaultStyle.fontColor, getFont(), defaultStyle.messageFontColor, defaultStyle.cursor, defaultStyle.selection, defaultStyle.background);
    }

    return new TextField(labelText, hintText, textFieldStyle);
  }

  public static void resetAll() {
    for (FontManager fontManager : values()) {
      fontManager.reset();
    }
  }

  public Label makeLabel(String text, Color fontColor) {
    return new Label(text, labelStyle(fontColor));
  }

  public Label makeLabel(String text, Color fontColor, int textAlignment) {
    Label label = new Label(text, labelStyle(fontColor));
    label.setAlignment(textAlignment);
    return label;
  }


}
