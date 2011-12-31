package com.unhappyrobot.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.unhappyrobot.entities.GameGrid;

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

  protected Vector2 findGameGridPointAtFinger() {
    Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
    Vector3 endPoint = pickRay.getEndPoint(1);
    return gameGrid.convertScreenPointToGridPoint(endPoint.x, endPoint.y);
  }
}
