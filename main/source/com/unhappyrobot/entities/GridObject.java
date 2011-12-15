package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.math.Bounds2;

public abstract class GridObject {
  public Vector2 position;
  public Vector2 size;

  public boolean canShareSpace() {
    return true;
  }

  public Bounds2 getBounds() {
    return new Bounds2(position, size);
  }

  public abstract void render(SpriteBatch spriteBatch, Camera camera);

  public abstract Texture getTexture();
}
