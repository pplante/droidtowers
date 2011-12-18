package com.unhappyrobot.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;

import static com.badlogic.gdx.input.GestureDetector.GestureListener;

public class PlacementTool implements GestureListener {
  private OrthographicCamera camera;
  private GameGrid gameGrid;
  private GridObject gridObject;
  private boolean isDraggingGridObject;

  public void setup(OrthographicCamera camera, GameGrid gameGrid) {
    this.camera = camera;
    this.gameGrid = gameGrid;

    makeGridObject();
  }

  private GridObject makeGridObject() {
    return new GridObject() {
      private Texture texture = new Texture(Gdx.files.internal("tiles/blank-tower.png"));

      @Override
      public Texture getTexture() {
        return texture;
      }
    };
  }

  private Vector2 findGameGridPointAtFinger() {
    Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
    Vector3 endPoint = pickRay.getEndPoint(1);
    return gameGrid.convertScreenPointToGridPoint(endPoint.x, endPoint.y);
  }

  public boolean touchDown(int x, int y, int pointer) {
    Vector2 gridPointAtFinger = findGameGridPointAtFinger();
    if (gridObject == null) {
      gridObject = makeGridObject();
      gameGrid.addObject(gridObject);
      gridObject.position.set(gridPointAtFinger);
    }

    isDraggingGridObject = gridObject.position.x == gridPointAtFinger.x && gridObject.position.y == gridPointAtFinger.y;

    return true;
  }

  public boolean tap(int x, int y, int count) {
    return false;
  }

  public boolean longPress(int x, int y) {
    return false;
  }

  public boolean fling(float velocityX, float velocityY) {
    return isDraggingGridObject;
  }

  public boolean pan(int x, int y, int deltaX, int deltaY) {
    if (isDraggingGridObject) {
      gridObject.position.set(findGameGridPointAtFinger());

      return true;
    }

    return false;
  }

  public boolean zoom(float originalDistance, float currentDistance) {
    return false;
  }
}
