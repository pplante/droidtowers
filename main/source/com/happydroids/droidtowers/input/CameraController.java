/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.events.GameGridResizeEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.events.CameraControllerEvent;
import com.happydroids.droidtowers.tween.TweenSystem;

public class CameraController implements GestureDetector.GestureListener {
  private static EventBus events = new EventBus(CameraController.class.getSimpleName());

  private static CameraController instance;

  public static final float ZOOM_MAX = 3f;
  public static final float ZOOM_MIN = 1f;

  private OrthographicCamera camera;
  private BoundingBox cameraBounds;
  private float initialScale = 1.0f;
  private boolean flinging = false;
  private float velX;
  private float velY;
  private Vector2 worldSize;
  private Vector3 lastCameraPosition;

  public static void initialize(OrthographicCamera camera, GameGrid gameGrid) {
    instance = new CameraController(camera, gameGrid);
  }

  public static CameraController instance() {
    if (instance == null) {
      throw new RuntimeException("Must call CameraController.initialize() before!");
    }

    return instance;
  }

  private CameraController(OrthographicCamera camera_, GameGrid gameGrid) {
    gameGrid.events().register(this);

    camera = camera_;
    worldSize = gameGrid.getWorldSize();
    camera.position.set(worldSize.x / 2, TowerConsts.GROUND_HEIGHT + (TowerConsts.GRID_UNIT_SIZE * 2), 0);
    lastCameraPosition = new Vector3(camera.position);
    updateCameraConstraints();
  }

  private void updateCameraConstraints() {
    this.cameraBounds = new BoundingBox(new Vector3(-TowerConsts.GAME_WORLD_PADDING, 0, 0), new Vector3(worldSize.x + TowerConsts.GAME_WORLD_PADDING, worldSize.y + TowerConsts.GAME_WORLD_PADDING, 0));
    checkBounds();
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
    Gdx.app.debug("camera", String.format("fling: %.2f, %.2f", velocityX, velocityY));

    if (Math.abs(velocityX) >= 300) {
      flinging = true;
      velX = camera.zoom * velocityX * 0.5f;
    }

    if (Math.abs(velocityY) >= 300) {
      flinging = true;
      velY = camera.zoom * velocityY * 0.5f;
    }

    return false;
  }

  public boolean pan(int x, int y, int changeX, int changeY) {
    float deltaX = -changeX * camera.zoom;
    float deltaY = changeY * camera.zoom;

    camera.position.add(deltaX, deltaY, 0);
    checkBounds();

    TutorialEngine.instance().moveToStepWhenReady("tutorial-zoom");

    return false;
  }

  public boolean zoom(float originalDistance, float currentDistance) {
    float ratio = originalDistance / currentDistance;
    camera.zoom = initialScale * ratio;

    checkZoom();
    checkBounds();

    TutorialEngine.instance().moveToStepWhenReady("tutorial-turn-on-population-overlay");

    return true;
  }

  public boolean pinch(Vector2 vector2, Vector2 vector21, Vector2 vector22, Vector2 vector23) {
    return false;
  }

  public boolean scrolled(int amount) {
    camera.zoom += (float) amount / 10;

    checkZoom();
    checkBounds();

    TutorialEngine.instance().moveToStepWhenReady("tutorial-turn-on-population-overlay");

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

    if (!lastCameraPosition.equals(camera.position)) {
      events.post(new CameraControllerEvent(camera.position, lastCameraPosition.sub(camera.position), camera.zoom));
      lastCameraPosition.set(camera.position);
    }
  }

  private void checkZoom() {
    camera.zoom = MathUtils.clamp(camera.zoom, ZOOM_MIN, ZOOM_MAX);
  }

  public void checkBounds() {
    float halfWidth = Gdx.graphics.getWidth() / 2 * camera.zoom;
    float halfHeight = Gdx.graphics.getHeight() / 2 * camera.zoom;

    Vector3 min = cameraBounds.getMin().cpy().add(halfWidth, halfHeight, 0);
    Vector3 max = cameraBounds.getMax().cpy().sub(halfWidth, halfHeight, 0);

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

  public static EventBus events() {
    return events;
  }

  public void panTo(float x, float y, boolean animate) {
    if (animate) {
      TweenSystem.getTweenManager().killTarget(this);
      Tween.to(this, CameraControllerAccessor.PAN, 500)
              .target(x, y)
              .start(TweenSystem.getTweenManager());
    } else {
      camera.position.set(x, y, 0f);
    }
  }

  public void panTo(Vector3 position, boolean animate) {
    panTo(position.x, position.y, animate);
  }

  @Subscribe
  public void GameGrid_onGridResize(GameGridResizeEvent event) {
    worldSize = event.gameGrid.getWorldSize();
    updateCameraConstraints();
  }
}
