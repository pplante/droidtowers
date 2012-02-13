package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class GameObject extends Sprite {
  private boolean visible = true;
  private float opacity = 1f;
  private float velocityX;
  private float velocityY;

  public GameObject(TextureAtlas.AtlasRegion region) {
    super(region);
  }

  public GameObject() {
    super();
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
    this.opacity = opacity;
  }

  public float getOpacity() {
    return opacity;
  }

  public void setPosition(Vector2 position) {
    setPosition(position.x, position.y);
  }
}
