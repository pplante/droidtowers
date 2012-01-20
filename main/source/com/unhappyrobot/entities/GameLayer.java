package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class GameLayer {
  protected List<GameObject> gameObjects;
  private boolean visible;
  private boolean touchEnabled;

  public GameLayer() {
    gameObjects = new ArrayList<GameObject>();
    visible = true;
  }

  public void addChild(GameObject gameObject) {
    gameObjects.add(gameObject);
  }

  public void render(SpriteBatch spriteBatch, Camera camera) {
    if (!visible) return;

    spriteBatch.begin();
    spriteBatch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    for (GameObject gameObject : gameObjects) {
      gameObject.render(spriteBatch);
    }

    spriteBatch.end();
  }

  public void update(float timeDelta) {
    for (GameObject gameObject : gameObjects) {
      gameObject.update(timeDelta);
    }
  }

  public boolean isVisible() {
    return this.visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean pan(Vector3 worldPoint, Vector3 deltaPoint) {
    return false;
  }

  public boolean tap(Vector3 worldPoint, int count) {
    return false;
  }

  public boolean touchDown(Vector3 worldPoint, int pointer) {
    return false;
  }

  public boolean longPress(Vector3 worldPoint) {
    return false;
  }

  public void setTouchEnabled(boolean touchEnabled) {
    this.touchEnabled = touchEnabled;
  }

  public boolean isTouchEnabled() {
    return touchEnabled;
  }
}
