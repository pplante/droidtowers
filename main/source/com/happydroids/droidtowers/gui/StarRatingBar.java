/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.happydroids.droidtowers.TowerAssetManager;

import static com.happydroids.droidtowers.ColorUtil.rgba;
import static com.happydroids.droidtowers.platform.Display.scale;

class StarRatingBar extends Widget {
  public static final Color STAR_COLOR = rgba("#ffbb33");
  private final Label valueLabel;
  private float stars;
  private int maxStars;
  private Texture starTexture;
  private Texture starTextureMask;
  private final int starTextureWidth;
  private final int starTextureHeight;

  StarRatingBar() {
    this(5, 5);
  }

  public StarRatingBar(float stars, int maxStars) {
    super();
    this.stars = stars;
    this.maxStars = maxStars;

    starTexture = TowerAssetManager.texture("hud/star.png");
    starTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    starTextureMask = TowerAssetManager.texture("hud/star-white.png");
    starTextureMask.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    starTextureWidth = starTexture.getWidth();

    valueLabel = FontManager.RobotoBold18.makeLabel("5.0");
    valueLabel.setAlignment(Align.CENTER);

    setValue(stars);
    starTextureHeight = starTexture.getHeight();
  }

  @Override
  public float getMinWidth() {
    return maxStars * starTextureWidth + valueLabel.getMinWidth() + scale(8);
  }

  @Override
  public float getMinHeight() {
    return starTextureHeight;
  }

  public void setValue(float value) {
    this.stars = MathUtils.clamp(value, 0, maxStars);

    valueLabel.setText(String.format("%.1f", stars));
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    batch.setColor(1, 1, 1, 0.35f * parentAlpha);
    batch.draw(starTextureMask,
                      x,
                      y,
                      starTextureWidth * maxStars,
                      height,
                      0, 0,
                      maxStars,
                      -1f);

    float starWidth = Math.round(stars * starTextureWidth);
    batch.setColor(1, 1, 1, 1 * parentAlpha);
    batch.draw(starTexture,
                      x,
                      y,
                      starWidth,
                      starTextureHeight,
                      0f, 0f,
                      stars, -1f);

    valueLabel.x = x + width - valueLabel.getMinWidth();
    valueLabel.y = y;
    valueLabel.draw(batch, parentAlpha);
  }

  @Override
  public void layout() {
    valueLabel.width = starTextureWidth;
    valueLabel.height = height;
  }


  public int getMaxStars() {
    return maxStars;
  }

  public void setMaxStars(int maxStars) {
    this.maxStars = maxStars;
  }
}
