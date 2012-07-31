/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class GameLayer<T extends GameObject> {
  protected List<T> gameObjects;
  private final ArrayList<T> deadObjects;
  private boolean visible;
  private boolean touchEnabled;


  public GameLayer() {
    visible = true;
    gameObjects = Lists.newLinkedList();
    deadObjects = Lists.newArrayListWithCapacity(5);
  }

  public void addChild(T gameObject) {
    gameObjects.add(gameObject);
  }

  protected Vector3 tmp = new Vector3();

  public void render(SpriteBatch spriteBatch, OrthographicCamera camera) {
    if (!visible) return;

    spriteBatch.begin();
    spriteBatch.enableBlending();
    for (int i = 0, gameObjectsSize = gameObjects.size(); i < gameObjectsSize; i++) {
      GameObject gameObject = gameObjects.get(i);
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
    for (int i = 0, gameObjectsSize = gameObjects.size(); i < gameObjectsSize; i++) {
      T gameObject = gameObjects.get(i);
      if (gameObject.isMarkedForRemoval()) {
        if (deadObjects.size() < 5) {
          deadObjects.add(gameObject);
        }
      } else {
        gameObject.update(timeDelta);
      }
    }

    if (!deadObjects.isEmpty()) {
      gameObjects.removeAll(deadObjects);
      deadObjects.clear();
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

  public boolean tap(Vector2 worldPoint, int count) {
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

    for (int i = 0, gameObjectsSize = gameObjects.size(); i < gameObjectsSize; i++) {
      GameObject gameObject = gameObjects.get(i);
      w += gameObject.getWidth();
    }

    return w;
  }

  public List<T> getObjects() {
    return gameObjects;
  }
}
