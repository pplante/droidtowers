/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

public class GameLayer {
  protected List<GameObject> gameObjects;
  private boolean visible;
  private boolean touchEnabled;

  public GameLayer() {
    visible = true;
    gameObjects = Lists.newArrayList();
  }

  public void addChild(GameObject gameObject) {
    gameObjects.add(gameObject);
  }

  public void render(SpriteBatch spriteBatch) {
    if (!visible) return;

    spriteBatch.begin();
    spriteBatch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    for (GameObject gameObject : gameObjects) {
      gameObject.draw(spriteBatch);
    }

    spriteBatch.end();
  }

  public void update(float timeDelta) {
    Iterator<GameObject> gameObjectIterator = gameObjects.iterator();
    while (gameObjectIterator.hasNext()) {
      GameObject gameObject = gameObjectIterator.next();

      if (gameObject.isMarkedForRemoval()) {
        gameObjectIterator.remove();
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

    for (GameObject gameObject : gameObjects) {
      w += gameObject.getWidth();
    }

    return w;
  }
}
