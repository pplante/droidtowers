/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class GameLayer<T extends GameObject> {
  protected Array<T> gameObjects;
  private boolean visible;
  private boolean touchEnabled;

  public GameLayer() {
    visible = true;
    gameObjects = new Array<T>();
  }

  public void addChild(T gameObject) {
    gameObjects.add(gameObject);
  }

  protected Vector3 tmp = new Vector3();

  public void render(SpriteBatch spriteBatch, OrthographicCamera camera) {
    if (!visible) {
      return;
    }

    spriteBatch.begin();
    spriteBatch.enableBlending();
    for (T gameObject : gameObjects) {
      if (shouldCullObjects() && gameObject.shouldBeCulled()) {
        tmp.set(gameObject.getX(), gameObject.getY(), 0);
        if (camera.frustum.sphereInFrustum(tmp, Math.max(gameObject.getWidth(), gameObject.getHeight()))) {
          gameObject.draw(spriteBatch);
        }
      } else {
        gameObject.draw(spriteBatch);
      }
    }

    spriteBatch.end();
  }

  protected boolean shouldCullObjects() {
    return true;
  }

  public void update(float timeDelta) {
    Iterator<T> iterator = gameObjects.iterator();
    while (iterator.hasNext()) {
      T gameObject = iterator.next();
      if (gameObject.isMarkedForRemoval()) {
        iterator.remove();
      } else {
        gameObject.update(timeDelta);
      }
    }
  }

  public boolean isVisible() {
    return this.visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean pan(Vector2 worldPoint, Vector2 deltaPoint) {
    return false;
  }

  public boolean tap(Vector2 worldPoint, int count, int button) {
    return false;
  }

  public boolean touchDown(Vector2 worldPoint, int pointer) {
    return false;
  }

  public boolean longPress(Vector2 worldPoint) {
    return false;
  }

  public void setTouchEnabled(boolean touchEnabled) {
    this.touchEnabled = touchEnabled;
  }

  public boolean isTouchEnabled() {
    return touchEnabled;
  }

  protected void removeAllChildren() {
    gameObjects.clear();
  }

  protected float width() {
    float w = 0;

    for (T gameObject : gameObjects) {
      w += gameObject.getWidth();
    }

    return w;
  }

  public Array<T> getObjects() {
    return gameObjects;
  }
}
