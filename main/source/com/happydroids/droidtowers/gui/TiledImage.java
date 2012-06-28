/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class TiledImage extends Image {
  private final Texture texture;
  private float u;
  private float v;
  private float u2;
  private float v2;

  public TiledImage(Texture texture) {
    this.texture = texture;
    texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
  }

  @Override
  public void layout() {
    u = 0;
    v = 0;

    u2 = Gdx.graphics.getWidth() / texture.getWidth();
    v2 = Gdx.graphics.getHeight() / texture.getHeight();
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    validate();

    batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
    batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), u, v, u2, v2);
  }
}
