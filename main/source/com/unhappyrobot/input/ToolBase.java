package com.unhappyrobot.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.math.GridPoint;

public class ToolBase implements GestureDetector.GestureListener {
  protected final OrthographicCamera camera;
  protected final GameGrid gameGrid;

  public ToolBase(OrthographicCamera camera, GameGrid gameGrid) {
    this.camera = camera;
    this.gameGrid = gameGrid;
  }

  public boolean touchDown(int x, int y, int pointer) {
    return false;
  }

  public boolean tap(int x, int y, int count) {
    return false;
  }

  public boolean longPress(int x, int y) {
    return false;
  }

  public boolean fling(float velocityX, float velocityY) {
    return false;
  }

  public boolean pan(int x, int y, int deltaX, int deltaY) {
    return false;
  }

  public boolean zoom(float originalDistance, float currentDistance) {
    return false;
  }

  public void cleanup() {

  }

  protected GridPoint gridPointAtFinger() {
    return screenToGameGridPoint(Gdx.input.getX(), Gdx.input.getY());
  }

  protected GridPoint screenToGameGridPoint(int x, int y) {
    Ray pickRay = camera.getPickRay(x, y);
    Vector3 endPoint = pickRay.getEndPoint(1);

    float gridX = (float) Math.floor(endPoint.x / gameGrid.unitSize.x);
    float gridY = (float) Math.floor(endPoint.y / gameGrid.unitSize.y);

    gridX = Math.max(0, Math.min(gridX, gameGrid.gridSize.x - 1));
    gridY = Math.max(0, Math.min(gridY, gameGrid.gridSize.y - 1));

    return new GridPoint(gridX, gridY);
  }
}
