/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.platform.Display;

import static com.happydroids.droidtowers.ColorUtil.rgba;

class RatingBar extends Table {
  public static final Color MASK_COLOR = rgba("#ffbb33");
  public static final String STAR_ICON = "hud/rating-bars/star.png";
  public static final String NO_SIGN_ICON = "hud/rating-bars/no-sign.png";
  public static final String COCKROACH_ICON = "hud/rating-bars/cockroach.png";
  public static final String SECURITY_ICON = "hud/rating-bars/security.png";
  private final Label valueLabel;
  private float stars;
  private int maxValue;
  private Texture maskTexture;
  private Texture starTextureMask;
  private int textureWidth;
  private final int textureHeight;
  private Label unitLabel;
  private NoOpWidget starPlaceholder;

  RatingBar() {
    this(5, 5);
  }

  public RatingBar(float stars, int maxValue) {
    super();
    this.stars = stars;
    this.maxValue = maxValue;

    defaults().left().space(Display.devicePixel(2));

    setTextures(STAR_ICON);

    starPlaceholder = new NoOpWidget();
    valueLabel = FontManager.RobotoBold18.makeLabel("5.0");
    valueLabel.setAlignment(Align.center);

    setValue(stars);
    textureHeight = maskTexture.getHeight();

    updateLayout();
  }

  public void setValue(float value) {
    this.stars = MathUtils.clamp(value, 0, maxValue);

    valueLabel.setText(String.format("%.1f", stars));
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    super.draw(batch, parentAlpha);

    batch.setColor(1, 1, 1, 0.35f * parentAlpha);
    batch.draw(maskTexture,
                      (int) getX() + starPlaceholder.getX(),
                      (int) getY() + starPlaceholder.getY(),
                      textureWidth * maxValue,
                      textureHeight,
                      0, 0,
                      maxValue,
                      -1f);

    float starWidth = Math.round(stars * textureWidth);
    batch.setColor(MASK_COLOR.r, MASK_COLOR.g, MASK_COLOR.b, MASK_COLOR.a * parentAlpha);
    batch.draw(maskTexture,
                      (int) getX() + starPlaceholder.getX(),
                      (int) getY() + starPlaceholder.getY(),
                      (int) starWidth,
                      textureHeight,
                      0f, 0f,
                      stars, -1f);
  }

  public int getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(int maxValue) {
    this.maxValue = maxValue;

    updateLayout();
  }

  public void setValue(double experienceLevel) {
    setValue((float) experienceLevel);
  }

  public void setTextures(String maskTextureFilename) {
    this.maskTexture = TowerAssetManager.texture(maskTextureFilename);
    this.maskTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    textureWidth = this.maskTexture.getWidth();

    updateLayout();
  }

  public void setUnitLabel(Label unitLabel) {
    this.unitLabel = unitLabel;

    updateLayout();
  }

  private void updateLayout() {
    clear();

    if (unitLabel != null) {
      row();
      add(unitLabel);
    }

    row();
    add(starPlaceholder).width(textureWidth * maxValue).height(textureHeight);
    add(valueLabel);
  }
}
