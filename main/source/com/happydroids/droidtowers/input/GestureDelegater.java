/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.grid.GameGrid;

import java.util.List;

import static com.badlogic.gdx.input.GestureDetector.GestureListener;

public class GestureDelegater implements GestureListener {
  private ToolBase currentTool;
  private final CameraController cameraController;
  private Runnable beforeSwitchToolRunnable;
  private final GameGrid gameGrid;
  private final List<GameLayer> gameLayers;

  public GestureDelegater(OrthographicCamera camera, List<GameLayer> gameLayers, GameGrid gameGrid, CameraController cameraController) {
    this.gameLayers = gameLayers;
    this.gameGrid = gameGrid;
    this.cameraController = cameraController;
  }

  public void switchTool(OrthographicCamera camera, List<GameLayer> gameLayers, GestureTool tool, Runnable switchToolRunnable) {
    if (HappyDroidConsts.DEBUG) System.out.println("tool = " + tool);
    if (beforeSwitchToolRunnable != null) {
      beforeSwitchToolRunnable.run();
      beforeSwitchToolRunnable = null;
    }

    if (currentTool != null) {
      currentTool.cleanup();
    }

    currentTool = tool.newInstance(camera, gameLayers, gameGrid);
    beforeSwitchToolRunnable = switchToolRunnable;
  }

  public GestureListener getCurrentTool() {
    return currentTool;
  }

  // GO AWAY, DEMONS AHEAD!
  public boolean touchDown(int x, int y, int pointer) {
    return currentTool != null && currentTool.touchDown(x, y, pointer) || cameraController.touchDown(x, y, pointer);
  }

  public boolean tap(int x, int y, int count) {
    return currentTool != null && currentTool.tap(x, y, count) || cameraController.tap(x, y, count);
  }

  public boolean longPress(int x, int y) {
    return currentTool != null && currentTool.longPress(x, y) || cameraController.longPress(x, y);
  }

  public boolean fling(float velocityX, float velocityY) {
    return currentTool != null && currentTool.fling(velocityX, velocityY) || cameraController.fling(velocityX, velocityY);
  }

  public boolean pan(int x, int y, int deltaX, int deltaY) {
    return currentTool != null && currentTool.pan(x, y, deltaX, deltaY) || cameraController.pan(x, y, deltaX, deltaY);
  }

  public boolean zoom(float originalDistance, float currentDistance) {
    return currentTool != null && currentTool.zoom(originalDistance, currentDistance) || cameraController.zoom(originalDistance, currentDistance);
  }

  public boolean scrolled(int amount) {
    return currentTool != null && currentTool.scrolled(amount) || cameraController.scrolled(amount);
  }

  public boolean pinch(Vector2 vector2, Vector2 vector21, Vector2 vector22, Vector2 vector23) {
    return false;
  }

  public void update(float deltaTime) {
    if (currentTool != null) {
      currentTool.update(deltaTime);
    }

    cameraController.update(deltaTime);
  }

  public List<GameLayer> getGameLayers() {
    return gameLayers;
  }
}
