package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.math.Bounds2d;

public abstract class GridObject {
  public Vector2 position;
  public Vector2 size;

  public GridObject() {
    this(new Vector2(0, 0), new Vector2(1, 1));
  }

  public GridObject(Vector2 position, Vector2 size) {
    this.position = position;
    this.size = size;
  }

  public boolean canShareSpace() {
    return true;
  }

  public Bounds2d getBounds() {
    return new Bounds2d(position, size);
  }

  public abstract Sprite getSprite();
}
