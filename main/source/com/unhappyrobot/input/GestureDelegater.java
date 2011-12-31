package com.unhappyrobot.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.unhappyrobot.entities.GameGrid;

import static com.badlogic.gdx.input.GestureDetector.GestureListener;

class GestureDelegater implements GestureListener {
  private ToolBase currentTool;
  private CameraController cameraController;
  private Runnable beforeSwitchToolRunnable;

  public GestureDelegater(OrthographicCamera camera, GameGrid gameGrid) {
    this.cameraController = new CameraController(camera, gameGrid);
  }

  public void switchTool(GestureTool tool, Runnable switchToolRunnable) {
    if (beforeSwitchToolRunnable != null) {
      beforeSwitchToolRunnable.run();
      beforeSwitchToolRunnable = null;
    }

    if (currentTool != null) {
      currentTool.cleanup();
    }

    currentTool = tool.newInstance();
    beforeSwitchToolRunnable = switchToolRunnable;
  }

  public GestureListener getCurrentTool() {
    return currentTool;
  }

  public CameraController getCameraController() {
    return cameraController;
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

  public void update(float deltaTime) {
    cameraController.update(deltaTime);
  }
}
