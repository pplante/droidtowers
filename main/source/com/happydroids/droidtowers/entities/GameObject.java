/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class GameObject extends Sprite {
  protected boolean visible = true;
  private float velocityX;
  private float velocityY;
  private boolean markedForRemoval;

  public GameObject(TextureAtlas.AtlasRegion region) {
    super(region);
  }

  public GameObject() {
    super();
  }

  public GameObject(Texture texture) {
    super(texture);
  }

  public GameObject(Texture texture, int u, int v, int u2, int v2) {
    super(texture, u, v, u2, v2);
  }

  @Override
  public void draw(SpriteBatch spriteBatch) {
    if (visible) {
      super.draw(spriteBatch);
    }
  }

  @Override
  public void draw(SpriteBatch spriteBatch, float alphaModulation) {
    if (visible) {
      super.draw(spriteBatch, alphaModulation);
    }
  }

  public void update(float timeDelta) {
    setX(getX() + (velocityX * timeDelta));
    setY(getY() + (velocityY * timeDelta));
  }

  public void setVisible(boolean state) {
    visible = state;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVelocity(int x, int y) {
    velocityX = x;
    velocityY = y;
  }

  public void setOpacity(float opacity) {
    Color color = getColor();
    color.a = opacity;
    setColor(color);
  }

  public float getOpacity() {
    return getColor().a;
  }

  public void setPosition(Vector2 position) {
    setPosition(position.x, position.y);
  }

  public void setWrap(Texture.TextureWrap wrapX, Texture.TextureWrap wrapY) {
    Texture texture = getTexture();

    if (wrapX == Texture.TextureWrap.Repeat) {
      setU(0f);
      setU2(getWidth() / texture.getWidth());
    }

    if (wrapY == Texture.TextureWrap.Repeat) {
      setV(0f);
      setV2(getHeight() / texture.getHeight());
    }
    texture.setWrap(wrapX, wrapX);
  }

  public void markToRemove(boolean b) {
    markedForRemoval = b;
  }

  public boolean isMarkedForRemoval() {
    return markedForRemoval;
  }

  public boolean shouldBeCulled() {
    return true;
  }
}
