package com.unhappyrobot.input;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.unhappyrobot.grid.GameGrid;

public class CameraController implements GestureDetector.GestureListener {
  private static CameraController instance;

  public static final float ZOOM_MAX = 3.0f;
  public static final float ZOOM_MIN = (Gdx.app.getType() == Application.ApplicationType.Android ? 0.4f : 0.6f);

  private OrthographicCamera camera;
  private BoundingBox cameraBounds;
  private float initialScale = 1.0f;
  private boolean flinging = false;
  private float velX;
  private float velY;
  private final int halfHeight;
  private final int halfWidth;

  public static void initialize(OrthographicCamera camera, GameGrid gameGrid) {
    instance = new CameraController(camera, gameGrid);
  }

  public static CameraController instance() {
    if (instance == null) {
      throw new RuntimeException("Must call CameraController.initialize() before!");
    }

    return instance;
  }

  private CameraController(OrthographicCamera camera, GameGrid gameGrid) {
    Vector2 worldSize = gameGrid.getWorldSize();
    this.camera = camera;
    halfWidth = Gdx.graphics.getWidth() / 2;
    halfHeight = Gdx.graphics.getHeight() / 2;
    this.cameraBounds = new BoundingBox(new Vector3(0, 0, 0), new Vector3(worldSize.x, worldSize.y, 0));
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

    camera.position.add(deltaX, deltaY, 0);
    checkBounds();
    return false;
  }

  public boolean zoom(float originalDistance, float currentDistance) {
    float ratio = originalDistance / currentDistance;
    camera.zoom = initialScale * ratio;

    checkZoom();
    checkBounds();

    return true;
  }

  public boolean pinch(Vector2 vector2, Vector2 vector21, Vector2 vector22, Vector2 vector23) {
    return false;
  }

  public boolean scrolled(int amount) {
    camera.zoom += (float) amount / 10;

    checkZoom();
    checkBounds();

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

      camera.position.add(deltaX, deltaY, 0);
      checkBounds();
    }
  }

  private void checkZoom() {
    if (camera.zoom < ZOOM_MIN) {
      camera.zoom = ZOOM_MIN;
    } else if (camera.zoom > ZOOM_MAX) {
      camera.zoom = ZOOM_MAX;
    }
  }

  private void checkBounds() {
    Vector3 min = cameraBounds.getMin().cpy().add(halfWidth * camera.zoom, halfHeight * camera.zoom, 0);
    Vector3 max = cameraBounds.getMax().cpy().sub(halfWidth * camera.zoom, halfHeight * camera.zoom, 0);

    camera.position.x = Math.max(min.x, camera.position.x);
    camera.position.x = Math.min(max.x, camera.position.x);

    camera.position.y = Math.max(min.y, camera.position.y);
    camera.position.y = Math.min(max.y, camera.position.y);
  }

  public BoundingBox getCameraBounds() {
    return cameraBounds;
  }

  public OrthographicCamera getCamera() {
    return camera;
  }
}
