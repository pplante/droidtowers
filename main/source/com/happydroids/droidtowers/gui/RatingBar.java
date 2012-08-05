/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.platform.Display;

import static com.happydroids.droidtowers.ColorUtil.rgba;

class RatingBar extends Widget {
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
  private final int starTextureHeight;

  RatingBar() {
    this(5, 5);
  }

  public RatingBar(float stars, int maxValue) {
    super();
    this.stars = stars;
    this.maxValue = maxValue;

    setTextures(STAR_ICON);

    valueLabel = FontManager.RobotoBold18.makeLabel("5.0");
    valueLabel.setAlignment(Align.center);

    setValue(stars);
    starTextureHeight = maskTexture.getHeight();
  }

  @Override
  public float getMinWidth() {
    return maxValue * textureWidth + valueLabel.getMinWidth() + Display.scale(8);
  }

  @Override
  public float getMinHeight() {
    return starTextureHeight;
  }

  public void setValue(float value) {
    this.stars = MathUtils.clamp(value, 0, maxValue);

    valueLabel.setText(String.format("%.1f", stars));
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    batch.setColor(1, 1, 1, 0.35f * parentAlpha);
    batch.draw(maskTexture,
                      (int) getX(),
                      (int) getY(),
                      textureWidth * maxValue,
                      (int) getHeight(),
                      0, 0,
                      maxValue,
                      -1f);

    float starWidth = Math.round(stars * textureWidth);
    batch.setColor(MASK_COLOR.r, MASK_COLOR.g, MASK_COLOR.b, MASK_COLOR.a * parentAlpha);
    batch.draw(maskTexture,
                      (int) getX(),
                      (int) getY(),
                      (int) starWidth,
                      starTextureHeight,
                      0f, 0f,
                      stars, -1f);

    valueLabel.setX((getX() + getWidth()) - valueLabel.getMinWidth());
    valueLabel.setY(getY());
    valueLabel.draw(batch, parentAlpha);
  }

  @Override
  public void layout() {
    valueLabel.setWidth(textureWidth);
    valueLabel.setHeight(getHeight());
  }


  public int getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(int maxValue) {
    this.maxValue = maxValue;
  }

  public void setValue(double experienceLevel) {
    setValue((float) experienceLevel);
  }

  public void setTextures(String maskTextureFilename) {
    this.maskTexture = TowerAssetManager.texture(maskTextureFilename);
    this.maskTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    textureWidth = this.maskTexture.getWidth();
  }
}
