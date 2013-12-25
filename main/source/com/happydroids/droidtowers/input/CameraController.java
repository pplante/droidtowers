/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Pools;
import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.events.SafeEventBus;
import com.happydroids.droidtowers.gui.events.CameraControllerEvent;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.tween.TweenSystem;

public class CameraController implements GestureDetector.GestureListener {
  public static final float ZOOM_MAX = 3f;
  public static final float ZOOM_MIN = 0.75f;

  private OrthographicCamera camera;
  private BoundingBox cameraBounds;
  private EventBus events = new SafeEventBus(CameraController.class.getSimpleName());
  private float initialScale = 1.0f;
  private boolean flinging = false;
  private float velX;
  private float velY;
  private Vector2 worldSize;
  private Vector3 lastCameraPosition;

  public CameraController(OrthographicCamera camera_, Vector2 worldSize) {
    camera = camera_;
    camera.position.set(worldSize.x / 2, TowerConsts.GROUND_HEIGHT + (TowerConsts.GRID_UNIT_SIZE * 2), 0);
    lastCameraPosition = new Vector3(camera.position);
    updateCameraConstraints(worldSize);
  }

  public void updateCameraConstraints(Vector2 newWorldSize) {
    worldSize = newWorldSize;
    int gameWorldPadding = Display.getBiggestScreenDimension();
    this.cameraBounds = new BoundingBox(new Vector3(-gameWorldPadding, -gameWorldPadding, 0), new Vector3(worldSize.x + gameWorldPadding, worldSize.y + gameWorldPadding, 0));
    checkBounds();
  }

  public boolean touchDown(float x, float y, int pointer) {
    flinging = false;
    initialScale = camera.zoom;
    return false;
  }

  public boolean tap(float x, float y, int count, int pointer, int button) {
    return false;
  }

  @Override public boolean touchDown(float x, float y, int pointer, int button) {
    return false;
  }

  @Override public boolean tap(float x, float y, int count, int button) {
    return false;
  }

  public boolean longPress(float x, float y) {
    return false;
  }

  public boolean fling(float velocityX, float velocityY, int button) {
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

  public boolean pan(float x, float y, float changeX, float changeY) {
    float deltaX = -changeX * camera.zoom;
    float deltaY = changeY * camera.zoom;

    camera.position.add(deltaX, deltaY, 0);
    checkBounds();

    TutorialEngine.instance().moveToStepWhenReady("tutorial-zoom");

    return false;
  }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
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
      if (Math.abs(velX) < 0.01f) {
        velX = 0;
      }
      if (Math.abs(velY) < 0.01f) {
        velY = 0;
      }

      camera.position.add(deltaX, deltaY, 0);
      checkBounds();
    }

    if (!lastCameraPosition.equals(camera.position)) {
      CameraControllerEvent event = Pools.obtain(CameraControllerEvent.class);
      event.setPosition(camera.position);
      event.setDelta(lastCameraPosition.sub(camera.position));
      event.setZoom(camera.zoom);
      events.post(event);
      Pools.free(event);
      lastCameraPosition.set(camera.position);
    }
  }

  private void checkZoom() {
    camera.zoom = MathUtils.clamp(camera.zoom, ZOOM_MIN / Display.getScaledDensity(), ZOOM_MAX / Display.getScaledDensity());
  }

  public void checkBounds() {
    float halfWidth = Display.getWidth() / 2 * camera.zoom;
    float halfHeight = Display.getHeight() / 2 * camera.zoom;

    camera.position.x = MathUtils.clamp(camera.position.x, cameraBounds.getMin().x + halfWidth, cameraBounds.getMax().x - halfWidth);
    camera.position.y = MathUtils.clamp(camera.position.y, cameraBounds.getMin().y + halfHeight, cameraBounds.getMax().y - halfHeight);
  }

  public BoundingBox getCameraBounds() {
    return cameraBounds;
  }

  public OrthographicCamera getCamera() {
    return camera;
  }

  public EventBus events() {
    return events;
  }

  public void panTo(float x, float y, boolean animate) {
    panTo(new Vector3(x, y, 0), animate);
  }

  public void panTo(Vector3 position, boolean animate) {
    if (animate) {
      TweenSystem.manager().killTarget(this);
      Tween.to(this, CameraControllerAccessor.PAN, 750)
              .target(position.x, position.y)
              .start(TweenSystem.manager());
    } else {
      camera.position.set(position.x, position.y, 0f);
      checkBounds();
    }
  }

  public void stopMovement() {
    velX = 0f;
    velY = 0f;
  }
}
