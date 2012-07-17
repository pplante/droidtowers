/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class GameLayer {
  protected List<GameObject> gameObjects;
  private final ArrayList<Object> deadObjects;
  private boolean visible;
  private boolean touchEnabled;


  public GameLayer() {
    visible = true;
    gameObjects = Lists.newLinkedList();
    deadObjects = Lists.newArrayListWithCapacity(5);
  }

  public void addChild(GameObject gameObject) {
    gameObjects.add(gameObject);
  }

  public void render(SpriteBatch spriteBatch) {
    if (!visible) return;

    spriteBatch.begin();
    for (int i = 0, gameObjectsSize = gameObjects.size(); i < gameObjectsSize; i++) {
      GameObject gameObject = gameObjects.get(i);
      gameObject.draw(spriteBatch);
    }

    spriteBatch.end();
  }

  public void update(float timeDelta) {
    for (int i = 0, gameObjectsSize = gameObjects.size(); i < gameObjectsSize; i++) {
      GameObject gameObject = gameObjects.get(i);
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
}
