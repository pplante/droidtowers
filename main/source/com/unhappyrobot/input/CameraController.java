package com.unhappyrobot.input;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.unhappyrobot.entities.GameGrid;

public class CameraController implements GestureDetector.GestureListener {
  public static final float ZOOM_MAX = 2.0f;
  public static final float ZOOM_MIN = (Gdx.app.getType() == Application.ApplicationType.Android ? 0.4f : 0.6f);

  private OrthographicCamera camera;
  private BoundingBox cameraBounds;
  private float initialScale = 1.0f;
  private boolean flinging = false;
  private float velX;
  private float velY;

  public CameraController(OrthographicCamera camera, GameGrid gameGrid) {
    Vector2 worldSize = gameGrid.getWorldSize();
    this.camera = camera;
    int halfWidth = Gdx.graphics.getWidth() / 2;
    int halfHeight = Gdx.graphics.getHeight() / 2;
    this.cameraBounds = new BoundingBox(new Vector3(halfWidth, halfHeight, 0), new Vector3(worldSize.x - halfWidth, worldSize.y - halfHeight, 0));
    this.camera.position.set(worldSize.x / 2, 384, 0);
  }

  public boolean touchDown(int x, int y, int pointer) {
    flinging = false;
    initialScale = camera.zoom;
    return false;
  }

  public boolean tap(int x, int y, int count) {
    return false;
  }

  public boolean longPress(int x, int y) {
    return false;
  }

  public boolean fling(float velocityX, float velocityY) {
    flinging = true;
    velX = camera.zoom * velocityX * 0.5f;
    velY = camera.zoom * velocityY * 0.5f;
    return false;
  }

  public boolean pan(int x, int y, int changeX, int changeY) {
    float deltaX = -changeX * camera.zoom;
    float deltaY = changeY * camera.zoom;

    checkBounds(deltaX, deltaY);
    return false;
  }

  public boolean zoom(float originalDistance, float currentDistance) {
    float ratio = originalDistance / currentDistance;
    camera.zoom = initialScale * ratio;

    checkZoom();

    return true;
  }

  public boolean scrolled(int amount) {
    camera.zoom += (float) amount / 10;

    checkZoom();

    return true;
  }

  public void update(float deltaTime) {
    if (flinging) {
      velX *= 0.95f;
      velY *= 0.95f;
      float deltaX = -velX * deltaTime;
      float deltaY = velY * deltaTime;
      if (Math.abs(velX) < 0.01f) velX = 0;
      if (Math.abs(velY) < 0.01f) velY = 0;

      checkBounds(deltaX, deltaY);
    }
  }

  private void checkZoom() {
    if (camera.zoom < ZOOM_MIN) {
      camera.zoom = ZOOM_MIN;
    } else if (camera.zoom > ZOOM_MAX) {
      camera.zoom = ZOOM_MAX;
    }
  }

  private void checkBounds(float deltaX, float deltaY) {
    Vector3 previousPosition = camera.position.cpy();

    if (cameraBounds.contains(previousPosition.add(deltaX, 0, 0))) {
      camera.position.add(deltaX, 0, 0);
    }

    if (cameraBounds.contains(previousPosition.add(0, deltaY, 0))) {
      camera.position.add(0, deltaY, 0);
    }
  }

  public BoundingBox getCameraBounds() {
    return cameraBounds;
  }
}
