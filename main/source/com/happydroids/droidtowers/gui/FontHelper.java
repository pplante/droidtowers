/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.platform.Display;

public class FontHelper {
  private final String fontPath;
  private float pixelHeight;
  private final int buttonPadTop;
  private final int buttonPadLeft;
  private BitmapFont bitmapFont;
  private Label.LabelStyle labelStyle;
  private TextField.TextFieldStyle textFieldStyle;


  public FontHelper(String fontPath, Color color) {
    this(fontPath, fontPath, 0, 0);
  }

  public FontHelper(String mdpiFontPath, String hdpiFontPath, int buttonPadTop, int buttonPadLeft) {
    this.fontPath = Display.getScaledDensity() > 1f ? hdpiFontPath : mdpiFontPath;
    this.buttonPadTop = Display.devicePixel(buttonPadTop);
    this.buttonPadLeft = Display.devicePixel(buttonPadLeft);
  }

  public FontHelper(String mdpiFontPath, String hdpiFontPath) {
    this(mdpiFontPath, hdpiFontPath, 0, 0);
  }

  public FontHelper(String fileName, int pixelHeight, int buttonPadTop, int buttonPadLeft) {
    this.fontPath = fileName;
    this.pixelHeight = pixelHeight * Display.getScaledDensity();
    this.buttonPadTop = Display.devicePixel(buttonPadTop);
    this.buttonPadLeft = Display.devicePixel(buttonPadLeft);
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
    if (bitmapFont == null) {
      if (pixelHeight > 0) {
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        bitmapFont = fontGenerator.generateFont((int) pixelHeight);
        fontGenerator.dispose();
      } else {
        bitmapFont = new BitmapFont(Gdx.files.internal(fontPath), false);
      }

      bitmapFont.setUseIntegerPositions(true);
    }

    return bitmapFont;
  }

  public Label makeLabel(String text) {
    return makeLabel(text, Color.WHITE);
  }

  public TransparentTextButton makeTransparentButton(String labelText, Color downColor, Color upColor) {
    return applyTextButtonLabelStyle(new TransparentTextButton(labelText, TowerAssetManager.getCustomSkin(), upColor, downColor), Color.WHITE);
  }

  public TextButton makeTextButton(String labelText) {
    return makeTextButton(labelText, Color.WHITE);
  }

  public TextButton makeTextButton(String buttonText, Color color) {
    return applyTextButtonLabelStyle(new TextButton(buttonText, TowerAssetManager.getCustomSkin()), color);
  }

  public CheckBox makeCheckBox(String labelText) {
    return applyTextButtonLabelStyle(new CheckBox(labelText, TowerAssetManager.getCustomSkin()), Color.WHITE);
  }

  private <T extends TextButton> T applyTextButtonLabelStyle(T textButton, Color labelColor) {
    if (labelColor != Color.WHITE) {
      TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(textButton.getStyle());
      style.fontColor = labelColor;

      textButton.setStyle(style);
    }
    textButton.getLabel().setStyle(labelStyle(labelColor));
    textButton.getLabelCell().pad(buttonPadTop, buttonPadLeft, buttonPadTop, buttonPadLeft);
    textButton.invalidate();
    textButton.pack();

    return textButton;
  }

  public TextField makeTextField(String labelText, String hintText) {
    if (textFieldStyle == null) {
      TextField.TextFieldStyle defaultStyle = TowerAssetManager.getCustomSkin().get(TextField.TextFieldStyle.class);
      textFieldStyle = new TextField.TextFieldStyle(getFont(), defaultStyle.fontColor, defaultStyle.cursor, defaultStyle.selection, defaultStyle.background);
    }

    return new TextField(labelText, textFieldStyle);
  }

  public Label makeLabel(String text, Color fontColor) {
    return makeLabel(text, fontColor, Align.left);
  }

  public Label makeLabel(String text, Color fontColor, int textAlignment) {
    Label label = new Label(text, labelStyle());
    label.setColor(fontColor);
    label.setAlignment(textAlignment);
    return label;
  }


  public TextButton makeTextToggleButton(String labelText) {
    return applyTextButtonLabelStyle(new TextButton(labelText, TowerAssetManager.getCustomSkin()
                                                                       .get("toggle-button", TextButton.TextButtonStyle.class)), Color.WHITE);
  }

  public void dispose() {
//    if (bitmapFont != null) {
//      bitmapFont.dispose();
//    }
//
//    bitmapFont = null;
//    labelStyle = null;
//    textFieldStyle = null;
  }
}
